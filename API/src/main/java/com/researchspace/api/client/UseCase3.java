package com.researchspace.api.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * Iterate through documents and put their content into CSV file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UseCase3 {
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		ApiConnector apiConnector = new ApiConnector();
		String allDocsOutput = apiConnector.makeAllDocsApiRequest();
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode allDocsNode = mapper.readTree(allDocsOutput);

		for(JsonNode document : allDocsNode.path("documents")) {
			long docId = Long.parseLong(document.path("id").asText());
			String output1 = apiConnector.makeSingleCSVDocumentApiRequest(docId);
			try(PrintWriter out = new PrintWriter("UseCase3-" + docId + ".csv")){
			    out.println(output1);
			}
		}
	}

}

