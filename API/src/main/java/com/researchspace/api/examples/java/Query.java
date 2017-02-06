package com.researchspace.api.examples.java;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

//constructor for a query object comprised of the query term and the query type

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
	
	//This method makes the HTTP query and returns the results as a JSON string
	public static String makeQuery(String uriString) throws URISyntaxException {
		
		String output = "";
		try {
			output = (Request.Get(uriString)
					.addHeader("apiKey", Library.getConfigProperty("apiKey"))
					.connectTimeout(10000)
					.socketTimeout(10000)
					.execute().returnContent().asString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

}
