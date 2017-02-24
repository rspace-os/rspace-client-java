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
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.researchspace.api.client.model.ApiDocument;
import com.researchspace.api.client.model.ApiDocumentSearchResult;
import com.researchspace.api.client.model.ApiFile;
import com.researchspace.api.client.model.ApiLinkItem;

/**
 * Main helper class providing methods for connecting to RSpace API.
 * The connection code uses serverURL and apiKey properties set in config file.  
 */
public class ApiConnector {
	
	private static final String API_DOCUMENTS_ENDPOINT = "/api/v1/documents"; 
	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

	private static final int SOCKET_TIMEOUT = 10000;
	private static final int CONNECT_TIMEOUT = 10000;

	private final String serverURL;
	private final String apiKey;
	
	public ApiConnector() throws IOException {
		serverURL = getConfigProperty("serverURL");
		apiKey = getConfigProperty("apiKey");
	}
	
	/** 
	 * Makes advanced document search query. 
	 *
	 * @param advQuery advanced query to limit search results, may be null
	 * @returns a page of search results, with default pagination (20 results per page)
	 *  		and ordering ("last modified desc")
	 */
	public ApiDocumentSearchResult makeDocumentSearchRequest(AdvancedQuery advQuery) 
			throws URISyntaxException, IOException {
		return makeDocumentSearchRequest(advQuery, null);
	}

	/** 
	 * Make advanced document search query, with additional parameters.
	 * Results are paginated, with 20 elements on a page by default.
	 *  
	 * @param advQuery advanced query to limit search results. may be null
	 * @param searchParams additional search parameters i.e. pageNumber, pageSize, orderBy, filter
	 * @returns a page of search results, paginated and order according to the search parameters
	 * 		(by default: 20 results per page, "last modified desc" order) 
	 */
	public ApiDocumentSearchResult makeDocumentSearchRequest(AdvancedQuery advQuery, 
			Map<String, String> searchParams) throws URISyntaxException, IOException {
		
		String uri = getURIBuilderForDocSearch(advQuery, searchParams).build().toString();
		String docSearchResponse = makeApiRequest(uri).asString();
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(docSearchResponse, ApiDocumentSearchResult.class);
	}

	private URIBuilder getURIBuilderForDocSearch(AdvancedQuery advQuery, 
			Map<String, String> searchParams) throws URISyntaxException {

		URIBuilder builder = new URIBuilder(getApiDocumentsUrl());
		if (advQuery != null) {
			builder.setParameter("advancedQuery", advQuery.toJSON());
		}
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
	public ApiDocument makeSingleDocumentRequest(long docID) throws IOException {
		String docAsString = makeApiRequest(getApiSingleDocumentUrl(docID)).asString();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(docAsString, ApiDocument.class);
	}

	/** 
	 * Returns content of a single document in CSV format 
	 */
	public String makeSingleCSVDocumentRequest(long docID) throws IOException {
		return makeApiRequest(getApiSingleDocumentUrl(docID), "text/csv").asString();
	}
	
	/** retrieves the object behind the link */
	public <T> T makeLinkedObjectRequest(String link, Class<T> objectType) throws IOException {
		String objectAsString = makeApiRequest(link, "application/json").asString();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(objectAsString, objectType);
	}

	/** returns input stream to file data */
	public InputStream makeFileDataRequest(ApiFile apiFile) throws IOException {
		String fileDataLink = apiFile.getLinkByType(ApiLinkItem.ENCLOSURE_REL);
		return makeApiRequest(fileDataLink).asStream();
	}
	
	protected Content makeApiRequest(String uriString) throws IOException {
		return makeApiRequest(uriString, "application/json");
	}
	
	/* makes the HTTP query and returns the results as a Content object */
	protected Content makeApiRequest(String uriString, String responseContentType) throws IOException {
		Response response = Request.Get(uriString)
				.addHeader("Accept", responseContentType)
				.addHeader("apiKey", apiKey)
				.connectTimeout(CONNECT_TIMEOUT)
				.socketTimeout(SOCKET_TIMEOUT)
				.execute();
		return response.returnContent();
	}
	
	protected String getApiDocumentsUrl() {
		return serverURL + API_DOCUMENTS_ENDPOINT;
	}

	protected String getApiSingleDocumentUrl(long docID) {
		return getApiDocumentsUrl() + "/" + docID;
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

}
