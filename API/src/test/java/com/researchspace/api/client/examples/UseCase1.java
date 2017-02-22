package com.researchspace.api.client.examples;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.model.ApiDocument;
import com.researchspace.api.client.model.ApiDocumentInfo;
import com.researchspace.api.client.model.ApiDocumentSearchResult;
import com.researchspace.api.client.model.ApiField;

/**
 * UseCase1 - print names, types and content of every document in user's workspace
 */
public class UseCase1 {

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

}

