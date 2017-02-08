package com.researchspace.api.examples.java;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

/**
 * This class builds correctly formatted advanced queries, given the required
 * operand, queries and query types
 *
 */
public class AdvancedQuery {
	
	//set the operand of the advanced search (can be "and" or "or", default is "and")
	public String operand = "and";
	//set the query terms you wish to search and the query types
	public Query queries[];
	public Query query;
	
	//constructor for multiple Advanced Queries
	public AdvancedQuery (String operand, Query... queries){
		this.operand = operand;
		this.queries = queries;
	}
	
	//constructor for a single Advanced Query
	public AdvancedQuery (Query... queries){
		this.queries = queries;
	}

	private String advancedQuery2JSON() {
		String queryJSON = "";
		String queryTerms[] = new String[queries.length];
		
		//format individual queries into JSON
		for(int i=0; i<queries.length; i++){
			queryTerms[i] = "{\"query\": \"" + queries[i].getQuery() + "\", \"queryType\": \"" + queries[i].getQueryType() + "\" }";
		}
		
		//add commas between multiple queries
		for(int i=0; i<queryTerms.length-1; i++){
			queryTerms[i] = queryTerms[i] + ", ";
			queryJSON = queryJSON + queryTerms[i];
		}
		queryJSON = queryJSON + queryTerms[queryTerms.length-1];
		
		//finally add square brackets
		queryJSON = "\"terms\": [ " + queryJSON + " ]";
		
		//Add operand if it is valid
		if(queryTerms.length > 1 && (operand.equals("and")||operand.equals("or"))){
			queryJSON = "{ \"operand\": " + operand + ", " + queryJSON + " }";
		}
		else {
			queryJSON = "{ " + queryJSON + " }";
		}
//		System.out.println(queryJSON);
		return queryJSON;
	}

	
	public static String advancedQueryUriString(String apiDocumentsUrl) throws URISyntaxException {
		URIBuilder builder = getAdvancedQueryURIBuilder(apiDocumentsUrl);
	    URI uri = builder.build();
	    return uri.toString();
	    
	}

	public static String advancedQueryOneDocPerPageUriString(String apiDocumentsUrl) throws URISyntaxException {
		URIBuilder builder = getAdvancedQueryURIBuilder(apiDocumentsUrl)
				.setParameter("pageNumber", "0")
	    		.setParameter("pageSize", "1")
	    		.setParameter("orderBy", "created asc");
    
		URI uri = builder.build();
		return uri.toString();
	} 

	private static URIBuilder getAdvancedQueryURIBuilder(String apiDocumentsUrl) throws URISyntaxException {
		AdvancedQuery advQuery = new AdvancedQuery(new Query ("Basic Document", "form"));
	    return new URIBuilder(apiDocumentsUrl)
	            .setParameter("advancedQuery", advQuery.advancedQuery2JSON());
	}
	
}
