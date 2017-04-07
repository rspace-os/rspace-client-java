package com.researchspace.api.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.researchspace.api.client.model.ApiDocument;
import com.researchspace.api.client.model.ApiDocumentSearchResult;
import com.researchspace.api.client.model.ApiFile;
import com.researchspace.api.client.model.ApiFileSearchResult;
import com.researchspace.api.client.model.ApiLinkItem;

/**
 * Main helper class providing methods for connecting to RSpace API.
 * The connection code uses serverURL and apiKey properties set in config file.  
 */
public class ApiConnectorImpl implements ApiConnector {
    
    private static final String API_DOCUMENTS_ENDPOINT = "/api/v1/documents";
    private static final String API_FILES_ENDPOINT = "/api/v1/files"; 
    
    private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

    private static final int SOCKET_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;

    private final String serverURL;
    private final String apiKey;
    
    public ApiConnectorImpl() throws IOException {
        serverURL = getConfigProperty("serverURL");
        apiKey = getConfigProperty("apiKey");
    }
    
    /* (non-Javadoc)
	 * @see com.researchspace.api.client.ApiConnector#makeDocumentSearchRequest(java.lang.String, java.util.Map)
	 */
    @Override
	public ApiDocumentSearchResult makeDocumentSearchRequest(String searchQuery, 
            Map<String, String> searchParams) throws URISyntaxException, IOException {

        return makeDocSearchRequest(searchQuery, null, searchParams);
    }

    /* (non-Javadoc)
	 * @see com.researchspace.api.client.ApiConnector#makeDocumentSearchRequest(com.researchspace.api.client.AdvancedQuery, java.util.Map)
	 */
    @Override
	public ApiDocumentSearchResult makeDocumentSearchRequest(AdvancedQuery advQuery, 
            Map<String, String> searchParams) throws URISyntaxException, IOException {

        return makeDocSearchRequest(null, advQuery, searchParams);
    }

    private ApiDocumentSearchResult makeDocSearchRequest(String searchQuery, AdvancedQuery advQuery, 
            Map<String, String> searchParams) throws URISyntaxException, IOException {

        if (searchParams == null) {
            searchParams = new HashMap<String, String>();
        }
        if (searchQuery != null && searchQuery.length() > 0) {
            searchParams.put("query", searchQuery);
        }
        if (advQuery != null) {
            searchParams.put("advancedQuery", advQuery.toJSON());
        }
        
        URIBuilder builder = new URIBuilder(getApiDocumentsUrl());
        for (Entry<String, String> param : searchParams.entrySet()) {
            builder.setParameter(param.getKey(), param.getValue());
        }
        String uri = builder.build().toString();
        String docSearchResponse = makeApiRequest(uri).asString();
        ObjectMapper mapper = createObjectMapper();
       
        return mapper.readValue(docSearchResponse, ApiDocumentSearchResult.class);
    };
    
    /* (non-Javadoc)
	 * @see com.researchspace.api.client.ApiConnector#makeSingleDocumentRequest(long)
	 */
    @Override
	public ApiDocument makeSingleDocumentRequest(long docID) throws IOException {
        String docAsString = makeApiRequest(getApiSingleDocumentUrl(docID)).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(docAsString, ApiDocument.class);
    }

	private ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

    /* (non-Javadoc)
	 * @see com.researchspace.api.client.ApiConnector#makeSingleCSVDocumentRequest(long)
	 */
    @Override
	public String makeSingleCSVDocumentRequest(long docID) throws IOException {
        return makeApiRequest(getApiSingleDocumentUrl(docID), "text/csv").asString();
    }
    
    /* (non-Javadoc)
	 * @see com.researchspace.api.client.ApiConnector#makeLinkedObjectRequest(java.lang.String, java.lang.Class)
	 */
    @Override
	public <T> T makeLinkedObjectRequest(String link, Class<T> objectType) throws IOException {
        String objectAsString = makeApiRequest(link, "application/json").asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(objectAsString, objectType);
    }

    /* (non-Javadoc)
	 * @see com.researchspace.api.client.ApiConnector#makeFileDataRequest(com.researchspace.api.client.model.ApiFile)
	 */
    @Override
	public InputStream makeFileDataRequest(ApiFile apiFile) throws IOException {
        String fileDataLink = apiFile.getLinkByType(ApiLinkItem.ENCLOSURE_REL);
        return makeApiRequest(fileDataLink).asStream();
    }
    
    /* (non-Javadoc)
	 * @see com.researchspace.api.client.ApiConnector#makeFileSearchRequest(java.lang.String, java.util.Map)
	 */
    @Override
	public ApiFileSearchResult makeFileSearchRequest(String mediaType, 
            Map<String, String> searchParams) throws URISyntaxException, IOException {

        if (searchParams == null) {
            searchParams = new HashMap<String, String>();
        }
        if (mediaType != null && mediaType.length() > 0) {
            searchParams.put("mediaType", mediaType);
        }
        
        URIBuilder builder = new URIBuilder(getApiFilesUrl());
        for (Entry<String, String> param : searchParams.entrySet()) {
            builder.setParameter(param.getKey(), param.getValue());
        }
        String uri = builder.build().toString();
        String docSearchResponse = makeApiRequest(uri).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(docSearchResponse, ApiFileSearchResult.class);
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

    protected String getApiFilesUrl() {
        return serverURL + API_FILES_ENDPOINT;
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
