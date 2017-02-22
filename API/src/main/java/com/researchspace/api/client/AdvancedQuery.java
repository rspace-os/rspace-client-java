package com.researchspace.api.client;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

/**
 * This class builds correctly formatted advanced queries, given the required
 * operand, queries and query types
 *
 */
public class AdvancedQuery {
	
	public static final String OPERAND_AND = "and";
	public static final String OPERAND_OR = "or";

	private String operand = OPERAND_AND;
	private AdvancedQueryElem queries[];

	/** constructor for setting multiple query terms and operand */
	public AdvancedQuery (String operand, AdvancedQueryElem... queries){
		this.operand = operand;
		this.queries = queries;
	}
	
	/** constructor for setting multiple query terms with default operand */
	public AdvancedQuery (AdvancedQueryElem... queries){
		this(OPERAND_AND, queries);
	}
	
	private String toJSON() {
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

	
	public static String uriStringForDefaultQuery(String apiDocumentsUrl) throws URISyntaxException {
		URIBuilder builder = new URIBuilder(apiDocumentsUrl)
				.setParameter("advancedQuery", (new AdvancedQuery()).toJSON());
		return builder.build().toString();
	}

	public static String uriStringForOneDocPerPageDefaultQuery(String apiDocumentsUrl) throws URISyntaxException {
		URIBuilder builder = new URIBuilder(apiDocumentsUrl)
				.setParameter("advancedQuery",  (new AdvancedQuery()).toJSON())
				.setParameter("pageNumber", "0")
	    		.setParameter("pageSize", "1")
	    		.setParameter("orderBy", "created asc");
		return builder.build().toString();
	}

}
