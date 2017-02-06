package com.researchspace.api.examples.java;

/** 
 * constructor for a query object comprised of the query term and the query type
 */
public class Query {
	
	public String query;
	public String queryType;
	
	public Query (String query, String queryType) {
		this.query = query;
		this.queryType = queryType;
	}
	
	public String getQuery() {
		return query;
	}
	
	public String getQueryType() {
		return queryType;
	}
	
}
