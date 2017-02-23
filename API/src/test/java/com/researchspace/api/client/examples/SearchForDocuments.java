package com.researchspace.api.client.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.researchspace.api.client.AdvancedQuery;
import com.researchspace.api.client.AdvancedQueryElem;
import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.model.ApiDocument;
import com.researchspace.api.client.model.ApiDocumentInfo;
import com.researchspace.api.client.model.ApiDocumentSearchResult;
import com.researchspace.api.client.model.ApiField;
import com.researchspace.api.client.model.ApiLinkItem;

/** 
 * TODO: a test using basic search
 * TODO: a test using advanced search with more than one search term
 */
public class SearchForDocuments {

	/**
	 * Print names, types and content of every document in user's workspace
	 */
	@Test
	public void printAllDocs() throws IOException, URISyntaxException {
		
		ApiConnector apiConnector = new ApiConnector();
		ApiDocumentSearchResult allDocs = apiConnector.makeDocumentSearchRequest(null);
		
		String resultString = "";
		for(ApiDocumentInfo apiDocInfo : allDocs.getDocuments()) {
			
			resultString += apiDocInfo.getGlobalId() + " - " + apiDocInfo.getName() 
				+ " (" + apiDocInfo.getForm().getName() + "):\n";
			
			ApiDocument apiDocWithFields = apiConnector.makeSingleDocumentRequest(apiDocInfo.getId());
			for (ApiField field : apiDocWithFields.getFields()) {
				String fieldName = field.getName();
				String fieldValue = field.getContent();
				resultString += fieldName + ": " + fieldValue + "\n";
			}
		}
		
		System.out.println(resultString);
		System.out.println("done.");
	}

	/** 
	 * This test navigates through paginated document results by using 
	 * the _links property within documents.
	 * 
	 * The _links property contains pre-created links to the next, previous,
	 * first or last page of results, as appropriate.
	 */
	@Test
	public void printPaginatedResults() throws IOException, URISyntaxException {
		
		ApiConnector apiConnector = new ApiConnector();

		//Create an advanced query for all Basic Documents
		AdvancedQueryElem basicFormQuery = new AdvancedQueryElem("Basic Document", "form");
		AdvancedQuery advQuery = new AdvancedQuery(basicFormQuery);

		//Return the results by retrieving the first page of results and then linking to the
		// next page... until the last page is reached
		Map<String, String> extraSearchParams = new HashMap<>();
		extraSearchParams.put("pageNumber", "0");
		extraSearchParams.put("pageSize", "1");
		extraSearchParams.put("orderBy", "created asc");
		
		ApiDocumentSearchResult paginatedDocs = apiConnector.makeDocumentSearchRequest(advQuery, extraSearchParams);
		String nextLink = paginatedDocs.getLinkByType(ApiLinkItem.NEXT_REL);

		System.out.println("at page: " + paginatedDocs.getPageNumber());
		System.out.println("next link: " + nextLink);
		
		//Select document ID of all documents returned using "next" link
		ArrayList<ApiDocumentInfo> allDocs = new ArrayList<>();
		allDocs.addAll(paginatedDocs.getDocuments());
		
		while (nextLink != null){
			paginatedDocs = apiConnector.makeLinkedObjectRequest(nextLink, ApiDocumentSearchResult.class);
			allDocs.addAll(paginatedDocs.getDocuments());
			nextLink = paginatedDocs.getLinkByType(ApiLinkItem.NEXT_REL);

			System.out.println("at page: " + paginatedDocs.getPageNumber());
			System.out.println("next link: " + nextLink);
		}
		
		for (ApiDocumentInfo doc : allDocs) {
			System.out.println(doc.getGlobalId() + " - " + doc.getName());
		}
	}
	
}

