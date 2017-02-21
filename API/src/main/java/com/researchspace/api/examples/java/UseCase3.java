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

/** 
 * Iterate through documents and put their content into CSV file.
 * 
 * TODO: should demonstrate retrieving CSV content from the server, which is supported by the API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UseCase3 {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		ApiConnector apiConnector = new ApiConnector();

		//Create a query for all documents made from a specified form template
		String output1 = apiConnector.makeAllDocsApiRequest();
		
		//Process results
		ArrayList<Float> fieldValues = new ArrayList<Float>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode results = mapper.readTree(output1);
		for(JsonNode document : results.path("documents")) {

			//extract links for documents made from desired form
			String linkURL = document.path("_links").path(0).path("link").asText();
			String output2 = apiConnector.makeApiRequest(linkURL).asString();
			
			//iterate over all documents extracting data from desired field
			ObjectMapper mapper2 = new ObjectMapper();
			JsonNode results2 = mapper2.readTree(output2);
			System.out.println(output2);
			JsonNode fieldArray = results2.path("fields");
			String fieldValue = fieldArray.path(1).path("content").asText();
			fieldValues.add(Float.parseFloat(fieldValue));
			System.out.println(fieldValue);
			
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
		}
		
		//output results into CSV file
		String resultsString = "";
		for (float fieldValue : fieldValues) {
			resultsString = resultsString + Float.toString(fieldValue) + "," + "\n";
		}
		
		try(PrintWriter out = new PrintWriter("UseCase1b.csv")){
		    out.println(resultsString);
		}
	}

}

