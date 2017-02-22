package com.researchspace.api.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import com.researchspace.api.client.model.ApiDocumentInfo;
import com.researchspace.api.client.model.ApiDocumentSearchResult;

/** 
 * Iterate through documents and put their content into CSV file.
 */
public class UseCase3 {
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		
		ApiConnector apiConnector = new ApiConnector();
		
		// search for Basic Documents 
		AdvancedQueryElem basicFormQuery = new AdvancedQueryElem("Basic Document", "form");
		AdvancedQuery advQuery = new AdvancedQuery(basicFormQuery);
		ApiDocumentSearchResult paginatedDocs = apiConnector.makeDocumentSearchRequest(advQuery);
		
		for(ApiDocumentInfo apiDocInfo : paginatedDocs.getDocuments()) {
			long docId = apiDocInfo.getId();
			String output1 = apiConnector.makeSingleCSVDocumentApiRequest(docId);
			try(PrintWriter out = new PrintWriter("UseCase3-" + docId + ".csv")){
			    out.println(output1);
			}
		}
	}

}

