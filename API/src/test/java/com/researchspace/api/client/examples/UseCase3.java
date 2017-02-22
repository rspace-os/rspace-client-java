package com.researchspace.api.client.examples;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import org.junit.Test;

import com.researchspace.api.client.AdvancedQuery;
import com.researchspace.api.client.AdvancedQueryElem;
import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.model.ApiDocumentInfo;
import com.researchspace.api.client.model.ApiDocumentSearchResult;

/** 
 * Iterate through documents and put their content into CSV file.
 */
public class UseCase3 {
	
	@Test
	public void downloadDocsAsCSV() throws IOException, URISyntaxException {
		
		ApiConnector apiConnector = new ApiConnector();
		
		// search for Basic Documents 
		AdvancedQueryElem basicFormQuery = new AdvancedQueryElem("Basic Document", "form");
		AdvancedQuery advQuery = new AdvancedQuery(basicFormQuery);
		ApiDocumentSearchResult paginatedDocs = apiConnector.makeDocumentSearchRequest(advQuery);
		
		for(ApiDocumentInfo apiDocInfo : paginatedDocs.getDocuments()) {
			long docId = apiDocInfo.getId();
			String output1 = apiConnector.makeSingleCSVDocumentRequest(docId);
			try(PrintWriter out = new PrintWriter("UseCase3-" + docId + ".csv")){
			    out.println(output1);
			}
		}
	}

}

