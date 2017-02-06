package com.researchspace.api.examples.java;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3

/* This program returns the specified number of results for a query 
 * and pages through them using the _links property within documents/.
 * The _links property contains pre-created links to the next, previous,
 * first or last page of results, as appropriate. 
 * This helps the client since it doesn’t have to figure out pagination. 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCase1a {

	public static ArrayList<String> requestURLs = new ArrayList<String>();
	public static ArrayList<Integer> docIDs = new ArrayList<Integer>();
	public static String nextLinkNotNull;
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		//Create an advanced query for all documents made from a specified form template
		//Return the results by retrieving the first page of results and then linking to the
		//next page... until the last page is reached
		String data = AdvancedQuery.makeOneDocPerPageQuery();
		
		ObjectMapper mapper = new ObjectMapper();
		// data here is returned from /documents
		JsonNode results = mapper.readTree(data);
		Optional<String> nextLink = getLink(results, "next");
		System.err.println(nextLink.orElse("No next link"));
		Optional<String> prevLink = getLink(results, "last");
		System.err.println(prevLink.orElse("No last link"));
		
		//Select document ID of all documents returned using "next" link
		while (nextLink.isPresent()){
		nextLinkNotNull = nextLink.get();
		data = Library.makeQuery(nextLinkNotNull);
		docIDs.addAll(getDocID(results));
		
		mapper = new ObjectMapper();
		results = mapper.readTree(data);
		nextLink = getLink(results, "next");
		System.err.println(nextLink.orElse("No next link"));
		prevLink = getLink(results, "last");
		System.err.println(prevLink.orElse("No last link"));
		}
		
		docIDs.addAll(getDocID(results));
		
		for(int docID: docIDs){
			System.out.println(docID);
		}
	}
	
	static Optional<String> getLink (JsonNode results, String relType){
	    String nextLink = null;
	    Iterator<JsonNode> it = results.get("_links").elements();
	    while(it.hasNext()) {
		JsonNode node = it.next();
		if (relType.equals(node.get("rel").textValue())){
		    nextLink=  node.get("link").asText();
		    break;
		};
	    } 
	    return Optional.ofNullable(nextLink);
	}
	
	static ArrayList<Integer> getDocID (JsonNode results){
		ArrayList<Integer> theseDocIDs = new ArrayList<Integer>();
		for(JsonNode document : results.path("documents")){
			int docID = document.path("id").asInt();
			theseDocIDs.add(docID);
		}
		return theseDocIDs;
	}

}

