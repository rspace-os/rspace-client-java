package com.researchspace.api.examples.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCase1 {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		String output1 = AdvancedQuery.makeDocsQuery();
		System.out.println(output1);
		
		ArrayList<Long> docIDs = new ArrayList<Long>();
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode results = mapper.readTree(output1);
		for(JsonNode document : results.path("documents")) {
			String documentID = document.path("id").asText();
			docIDs.add(Long.parseLong(documentID));
			System.out.println(documentID);
		}
		
		ArrayList<Float> fieldValues = new ArrayList<Float>();
		for(long docID: docIDs){
			String requestURL = Library.getAPIDocumentUrl(docID);
			String output2 = Library.makeQuery(requestURL);
			
			ObjectMapper mapper2 = new ObjectMapper();
			JsonNode results2 = mapper2.readTree(output2);
			JsonNode fieldArray = results2.path("fields");
			String fieldValue = fieldArray.path(1).path("content").asText();
			fieldValues.add(Float.parseFloat(fieldValue));
//			System.out.println(fieldValue);
			
			try {
			    Thread.sleep(1000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
		}
		
		String resultsString = "\n";
		for(float fieldValue: fieldValues){
			resultsString = resultsString + Float.toString(fieldValue) + "\n";
		}
		
		try(PrintWriter out = new PrintWriter("UserCase1.txt")){
		    out.println(resultsString);
		}
	}

}

