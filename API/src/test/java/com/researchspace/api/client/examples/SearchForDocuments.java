package com.researchspace.api.client.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.model.ApiDocumentInfo;
import com.researchspace.api.client.model.ApiDocumentSearchResult;
import com.researchspace.api.client.model.ApiLinkItem;

/** 
 * TODO: a test using basic search
 * TODO: a test using advanced search with more than one search term
 */
public class SearchForDocuments {

	/**
	 * Prints 20 last modified documents that user has access to.
	 * The result corresponds to 'View All' filter.  
	 */
	@Test
	public void printRecentlyUpdatedDocs() throws IOException, URISyntaxException {
		
		ApiConnector apiConnector = new ApiConnector();
		ApiDocumentSearchResult allDocs = apiConnector.makeDocumentSearchRequest(null);

		Long totalHits = allDocs.getTotalHits();
		System.out.println("User has " + totalHits + " document(s) in their Workspace.");
		System.out.println("Printing first " + allDocs.getDocuments().size() + " document(s).");
		
		for(ApiDocumentInfo apiDocInfo : allDocs.getDocuments()) {
			printDocDetailsToSysout(apiDocInfo);
		}
	}

	/** 
	 * Print all documents that user has access to, ordered by creation date.
	 *
	 * Document search results are always paginated (with 20 elements per page by default),
	 * and the code below uses 'next' links to go through all pages.
	 */
	@Test
	public void printAllUserDocs() throws IOException, URISyntaxException {
		
		ApiConnector apiConnector = new ApiConnector();

		/* get all documents, sort by creation date in ascending order */
		Map<String, String> extraSearchParams = new HashMap<>();
		extraSearchParams.put("orderBy", "created asc");

		ApiDocumentSearchResult paginatedDocs = apiConnector.makeDocumentSearchRequest(null, extraSearchParams);
		String nextLink = paginatedDocs.getLinkByType(ApiLinkItem.NEXT_REL);

		while (nextLink != null) {
			System.out.println("at page: " + paginatedDocs.getPageNumber());
			System.out.println("next link: " + nextLink);

			for (ApiDocumentInfo doc : paginatedDocs.getDocuments()) {
				printDocDetailsToSysout(doc);
			}
			nextLink = paginatedDocs.getLinkByType(ApiLinkItem.NEXT_REL);
			if (nextLink != null) {
				paginatedDocs = apiConnector.makeLinkedObjectRequest(nextLink, ApiDocumentSearchResult.class);
			}
		} 
	}

	private void printDocDetailsToSysout(ApiDocumentInfo docInfo) {
		String docDetailsLine = String.format("Document: %s, form: %s, globalId: %s, createdAt: %s", 
				docInfo.getName(), docInfo.getForm().getName(), docInfo.getGlobalId(), docInfo.getCreated());
		System.out.println(docDetailsLine);
	}
	
}

