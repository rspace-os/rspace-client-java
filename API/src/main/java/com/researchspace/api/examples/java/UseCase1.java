package com.researchspace.api.examples.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3
import com.researchspace.api.examples.java.model.ApiDocument;
import com.researchspace.api.examples.java.model.ApiDocumentInfo;
import com.researchspace.api.examples.java.model.ApiField;

/**
 * UseCase1 - print content of all documents belonging to the user into results file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UseCase1 {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		ApiConnector apiConnector = new ApiConnector();

		String allDocsOutput = apiConnector.makeAllDocsApiRequest();
		//System.out.println(allDocsOutput);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode allDocsNode = mapper.readTree(allDocsOutput);
		
		String resultString = "";
		for(JsonNode document : allDocsNode.path("documents")) {
			
			ApiDocumentInfo apiDocInfo = mapper.treeToValue(document, ApiDocumentInfo.class);
			resultString += apiDocInfo.getName() + ":\n";
			
			ApiDocument apiDocWithFields = apiConnector.makeSingleDocumentApiRequest(apiDocInfo.getId());
			for (ApiField field : apiDocWithFields.getFields()) {
				String fieldName = field.getName();
				String fieldValue = field.getContent();
				resultString += fieldName + ": " + fieldValue + "\n";
			}
		}
		
		try(PrintWriter out = new PrintWriter("UseCase1.txt")){
		    out.println(resultString);
		}
		System.out.println("done.");
	}

}

