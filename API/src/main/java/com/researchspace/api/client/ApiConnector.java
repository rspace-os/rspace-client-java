package com.researchspace.api.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.researchspace.api.client.model.ApiDocument;

/**
 * Main helper class providing methods for connecting to RSpace API.
 * The connection code uses serverURL and apiKey properties set in config file.  
 */
public class ApiConnector {
	
	private static final String API_DOCUMENTS_ENDPOINT = "/api/v1/documents"; 
	
	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

	/** make advanced document search query */
	public String makeDocumentSearchRequest(AdvancedQuery advQuery) throws URISyntaxException, IOException {
		return makeDocumentSearchRequest(advQuery, null);
	}

	/** make advanced document search query, with additional parameters */
	public String makeDocumentSearchRequest(AdvancedQuery advQuery, Map<String, String> searchParams) 
			throws URISyntaxException, IOException {
		String uri = getURIBuilderForAdvancedDocSearch(advQuery, searchParams)
				.build().toString();
		return makeApiRequest(uri).asString();
	}

	private URIBuilder getURIBuilderForAdvancedDocSearch(AdvancedQuery advQuery, Map<String, String> searchParams) 
			throws URISyntaxException, IOException {

		URIBuilder builder = new URIBuilder(getApiDocumentsUrl());
		builder.setParameter("advancedQuery", advQuery.toJSON());
		
		if (searchParams != null) {
			for (Entry<String, String> param : searchParams.entrySet()) {
				builder.setParameter(param.getKey(), param.getValue());
			}
		}
		return builder;
	};
	
	/**
	 * Returns representation of a single document (with fields).
	 */
	public ApiDocument makeSingleDocumentApiRequest(long docID) throws IOException {
		String docAsString = makeApiRequest(getApiSingleDocumentUrl(docID)).asString();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(docAsString, ApiDocument.class);
	}

	/** 
	 * Returns CSV content of a single document 
	 * 
	 * @param docID
	 * @return document content, in CSV format
	 * @throws IOException
	 */
	public String makeSingleCSVDocumentApiRequest(long docID) throws IOException {
		return makeApiRequest(getApiSingleDocumentUrl(docID), "text/csv").asString();
	}
	
	public Content makeApiRequest(String uriString) throws IOException {
		return makeApiRequest(uriString, "application/json");
	}
	
	/** This method makes the HTTP query and returns the results as a Content object */
	protected Content makeApiRequest(String uriString, String responseContentType) throws IOException {
		return Request.Get(uriString)
				.addHeader("Accept", responseContentType)
				.addHeader("apiKey", getConfigProperty("apiKey"))
				.connectTimeout(10000)
				.socketTimeout(10000)
				.execute().returnContent();
	}

	/* returns property value from config file */
	protected String getConfigProperty(String propertyName) throws IOException {
		Properties prop = new Properties();
		String propertyValue = "";
	
		try (InputStream input = new FileInputStream(CONFIG_PROPERTIES_FILENAME)) {
			prop.load(input);
			propertyValue = prop.getProperty(propertyName);
		}
		return propertyValue;
	}
	
	protected String getApiDocumentsUrl() throws IOException {
		return getConfigProperty("serverURL") + API_DOCUMENTS_ENDPOINT;
	}

	protected String getApiSingleDocumentUrl(long docID) throws IOException {
		return getApiDocumentsUrl() + "/" + docID;
	}
	
}
