package com.researchspace.api.examples.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

/**
 * Main helper class providing methods for connecting to RSpace API.
 * The connection code uses serverURL and apiKey properties set in config file.  
 */
public class ApiConnector {
	
	private static final String API_DOCUMENTS_ENDPOINT = "/api/v1/documents"; 
	
	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

	public String makeAllDocsApiRequest() throws URISyntaxException, IOException {
		return makeApiRequest(AdvancedQuery.uriStringForDefaultQuery(getApiDocumentsUrl()))
				.asString();
	}
	
	public String makeOneDocPerPageApiRequest() throws URISyntaxException, IOException {
		return makeApiRequest(AdvancedQuery.uriStringForOneDocPerPageDefaultQuery(getApiDocumentsUrl()))
				.asString();
	}

	public String makeSingleDocumentApiRequest(long docID) throws IOException {
		return makeApiRequest(getApiSingleDocumentUrl(docID)).asString();
	}
	
	/** This method makes the HTTP query and returns the results as a Content object */
	public Content makeApiRequest(String uriString) throws IOException {
		return Request.Get(uriString)
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
