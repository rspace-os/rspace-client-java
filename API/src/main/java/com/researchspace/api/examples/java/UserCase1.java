package com.researchspace.api.examples.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3

/**
 * UseCase1 - print content of all documents belonging to the user into results file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCase1 {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		String allDocsOutput = AdvancedQuery.makeDocsQuery();
		//System.out.println(allDocsOutput);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode allDocsNode = mapper.readTree(allDocsOutput);
		
		String resultString = "";
		for(JsonNode document : allDocsNode.path("documents")) {
			String docName = document.path("name").asText();
			resultString += docName + ":\n";
			
			long docId = Long.parseLong(document.path("id").asText());
			String singleDocOutput = Library.makeSingleDocumentQuery(docId);
			ObjectMapper mapper2 = new ObjectMapper();
			JsonNode singleDocNode = mapper2.readTree(singleDocOutput);
			for (JsonNode fieldNode : singleDocNode.path("fields")) {
				String fieldName = fieldNode.path("name").asText();
				String fieldValue = fieldNode.path("content").asText();
				resultString += fieldName + ": " + fieldValue + "\n";
			}
		}
		
		try(PrintWriter out = new PrintWriter("UseCase1.txt")){
		    out.println(resultString);
		}
		System.out.println("done.");
	}

}

