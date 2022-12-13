package com.researchspace.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.researchspace.api.clientmodel.ApiFile;
import com.researchspace.api.clientmodel.Document;
import com.researchspace.api.clientmodel.DocumentPost;
import com.researchspace.api.clientmodel.DocumentSearchResult;
import com.researchspace.api.clientmodel.FilePost;
import com.researchspace.api.clientmodel.FileSearchResult;
import com.researchspace.api.clientmodel.FormInfo;
import com.researchspace.api.clientmodel.FormPost;
import com.researchspace.api.clientmodel.LinkItem;
import com.researchspace.api.clientmodel.User;
import org.apache.commons.lang.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Main helper class providing methods for connecting to RSpace API.
 */
public class ApiConnectorImpl implements ApiConnector {

    private static final Logger log = LoggerFactory
            .getLogger(ApiConnectorImpl.class);

    private static final String API_DOCUMENTS_ENDPOINT = "/api/v1/documents";
    private static final String API_FILES_ENDPOINT = "/api/v1/files";
    private static final String API_FORMS_ENDPOINT = "/api/v1/forms";

    private static final int SOCKET_TIMEOUT = 15000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final String USER_NAME_AND_KEY_ENDPOINT = "/api/v1/syadmin/userDetails/apiKeyInfo/all";
    private static final String USER_DETAILS_ENDPOINT = "/api/v1/userDetails";
    private final String serverUrl;

    /**
     * Constructor that reads serverURL and apiKey properties from config.properties file.  
     */
    public ApiConnectorImpl() throws IOException {
        ConfigPropertiesReader configReader = new ConfigPropertiesReader();
        serverUrl = configReader.getConfigProperty("serverURL");
    }
    
    /**
     * Constructor that allows passing your serverUrl and apiKey 
     */
    public ApiConnectorImpl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public User getUserByUsername(String username, String apiKey) throws Exception {
        Map<String, String> usersAndAPIKeys = getUserNamesAndApiKeys(apiKey);
        URIBuilder builder = new URIBuilder(getUserDetailsEndpoint() + "/whoami");
        String uri = builder.build().toString();
        String userWhoAmIResponse = makeApiGetRequest(uri,ContentType.APPLICATION_JSON.toString() , usersAndAPIKeys.get(username)).asString();
        ObjectMapper mapper = createObjectMapper();

        return mapper.readValue(userWhoAmIResponse, User.class);
    }

    public Map<String, String> getUserNamesAndApiKeys(String apiKey) throws Exception {
        return makeUserNameAndKeyRequest(apiKey);
    }


    /* (non-Javadoc)
     * @see com.researchspace.api.client.ApiConnector#makeDocumentSearchRequest(java.lang.String, java.util.Map)
     */
    @Override
    public DocumentSearchResult searchDocuments(String searchQuery,
            Map<String, String> searchParams,String apiKey) throws URISyntaxException, IOException {
        return makeDocSearchRequest(searchQuery, null, searchParams, apiKey);
    }

    /* (non-Javadoc)
     * @see com.researchspace.api.client.ApiConnector#makeDocumentSearchRequest(com.researchspace.api.client.AdvancedQuery, java.util.Map)
     */
    @Override
    public DocumentSearchResult searchDocuments(AdvancedQuery advQuery, 
            Map<String, String> searchParams, String apiKey) throws URISyntaxException, IOException {

        return makeDocSearchRequest(null, advQuery, searchParams, apiKey);
    }

    private DocumentSearchResult makeDocSearchRequest(String searchQuery, AdvancedQuery advQuery, 
            Map<String, String> searchParams, String apiKey) throws URISyntaxException, IOException {

        if (searchParams == null) {
            searchParams = new HashMap<>();
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
        String docSearchResponse = makeApiGetRequest(uri, ContentType.APPLICATION_JSON.toString(), apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
       
        return mapper.readValue(docSearchResponse, DocumentSearchResult.class);
    }


    private Map<String, String> makeUserNameAndKeyRequest(String apiKey) throws Exception {
        URIBuilder builder = new URIBuilder(getUserNameAndKeyUrl());
        String uri = builder.build().toString();
        String docSearchResponse = makeApiGetRequest(uri, ContentType.APPLICATION_JSON.toString(), apiKey).asString();
        ObjectMapper mapper = createObjectMapper();

        return mapper.readValue(docSearchResponse, new TypeReference<Map<String, String>>(){});
    }
    
    @Override
    public Document createDocument(DocumentPost documentPost, String apiKey) throws IOException {
        String docAsString = makeDocumentApiPostRequest(documentPost, apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(docAsString, Document.class);
    }
    
    /* (non-Javadoc)
     * @see com.researchspace.api.client.ApiConnector#makeSingleDocumentRequest(long)
     */
    @Override
    public Document retrieveDocument(long docID, String apiKey) throws IOException {
        String docAsString = makeApiGetRequest(getApiSingleDocumentUrl(docID),"application/json", apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(docAsString, Document.class);
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        return mapper;
    }

    /* (non-Javadoc)
     * @see com.researchspace.api.client.ApiConnector#makeSingleCSVDocumentRequest(long)
     */
    @Override
    public String retrieveDocumentAsCSV(long docID, String apiKey) throws IOException {
        return makeApiGetRequest(getApiSingleDocumentUrl(docID), "text/csv", apiKey).asString();
    }
    
    /* (non-Javadoc)
     * @see com.researchspace.api.client.ApiConnector#makeLinkedObjectRequest(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T retrieveLinkedObject(String link, Class<T> objectType, String apiKey) throws IOException {
        String objectAsString = makeApiGetRequest(link, "application/json", apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(objectAsString, objectType);
    }

    @Override
    public Document updateDocument(Long docId, DocumentPost documentPost, String apiKey) throws IOException {
        String docAsString = makeDocumentApiPutRequest(docId, documentPost, apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(docAsString, Document.class);
    }

    /* (non-Javadoc)
	 * @see com.researchspace.api.client.ApiConnector#makeFileSearchRequest(java.lang.String, java.util.Map)
	 */
    @Override
	public FileSearchResult searchFiles(String mediaType, 
            Map<String, String> searchParams, String apiKey) throws URISyntaxException, IOException {

        if (searchParams == null) {
            searchParams = new HashMap<>();
        }
        if (mediaType != null && mediaType.length() > 0) {
            searchParams.put("mediaType", mediaType);
        }
        
        URIBuilder builder = new URIBuilder(getApiFilesUrl());
        for (Entry<String, String> param : searchParams.entrySet()) {
            builder.setParameter(param.getKey(), param.getValue());
        }
        String uri = builder.build().toString();
        String docSearchResponse = makeApiGetRequest(uri,"application/json", apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(docSearchResponse, FileSearchResult.class);
    }

    @Override
    public ApiFile retrieveFileById(long fileId, String apiKey) throws IOException {
        String docSearchResponse = makeApiGetRequest(getApiSingleFileUrl(fileId), "application/json", apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(docSearchResponse, ApiFile.class);
    }

    /* (non-Javadoc)
     * @see com.researchspace.api.client.ApiConnector#makeFileDataRequest(com.researchspace.api.client.model.ApiFile)
     */
    @Override
    public InputStream retrieveFileData(ApiFile apiFile, String apiKey) throws IOException {
        String fileDataLink = apiFile.getLinkByType(LinkItem.ENCLOSURE_REL);
        return makeApiGetRequest(fileDataLink, "application/json", apiKey).asStream();
    }

    @Override
    public ApiFile uploadFile(FilePost filePost, String apiKey) throws IOException {
        String fileUploadResponse = makeFileUploadApiRequest(filePost, apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(fileUploadResponse, ApiFile.class);
    }

    @Override
    public FormInfo createForm(FormPost.Form formPost, String apiKey) throws IOException {
        String formCreationResponse = makeFormCreationRequest(formPost, apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(formCreationResponse, FormInfo.class);
    }

    @Override
    public FormInfo updateForm(FormPost.Form formPost, String apiKey) throws IOException {
        String formEditResponse = makeFormEditRequest(formPost, apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(formEditResponse, FormInfo.class);
    }

    @Override
    public boolean deleteForm(FormInfo form, String apiKey) throws IOException {
        int statuscode = makeFormDeleteRequest(form, apiKey);
        return statuscode == 204;
    }

    @Override
    public FormInfo publishForm(FormInfo form, String apiKey) throws IOException {
        String formPublishResponse = makeFormPublishRequest(form, apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(formPublishResponse, FormInfo.class);
    }

    @Override
    public FormInfo globalShareForm(FormInfo form, String apiKey) throws IOException {
        String formSharedResponse = makeFormGloballySharedRequest(form, apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(formSharedResponse, FormInfo.class);
    }

    @Override
    public FormInfo groupShareForm(FormInfo form, String apiKey) throws IOException {
        String formSharedResponse = makeFormGroupSharedRequest(form, apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(formSharedResponse, FormInfo.class);
    }

    @Override
    public FormInfo setIconOnForm(FormInfo form, File iconFile, String apiKey) throws IOException {
        String formEditResponse = makeFormIconSetRequest(form, iconFile, apiKey).asString();
        ObjectMapper mapper = createObjectMapper();
        return mapper.readValue(formEditResponse, FormInfo.class);
    }

    private Content makeFormCreationRequest(FormPost.Form formPForm, String apiKey) throws IOException {
        ObjectMapper mapper = createObjectMapper();
        String formAsJson = mapper.writeValueAsString(formPForm);
        Response response = Request.Post(getApiFormsUrl())
                .addHeader("apiKey", apiKey)
                .bodyString(formAsJson, ContentType.APPLICATION_JSON)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }
    private Content makeFormEditRequest(FormPost.Form formPForm, String apiKey) throws IOException {
        ObjectMapper mapper = createObjectMapper();
        String formAsJson = mapper.writeValueAsString(formPForm);
        Response response = Request.Put(getApiFormsUrl()+"/"+formPForm.getId())
                .addHeader("apiKey", apiKey)
                .bodyString(formAsJson, ContentType.APPLICATION_JSON)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }

    private Content makeFormIconSetRequest(FormInfo form, File iconFile, String apiKey) throws IOException {
        HttpEntity fileUploadEntity = MultipartEntityBuilder
                .create()
                .addBinaryBody("file", iconFile)
                .build();
        Response response = Request.Post(getApiFormsUrl()+"/"+form.getId()+"/icon")
                .addHeader("apiKey", apiKey)
                .body(fileUploadEntity)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }

    private int makeFormDeleteRequest(FormInfo form, String apiKey) throws IOException {
        Response response = Request.Delete(getApiFormsUrl()+"/"+form.getId())
                .addHeader("apiKey", apiKey)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnResponse().getStatusLine().getStatusCode();
    }

    private Content makeFormPublishRequest (FormInfo form, String apiKey) throws IOException {
        Response response = Request.Put(getApiFormsUrl()+"/"+form.getId()+"/publish")
                .addHeader("apiKey", apiKey)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }

    private Content makeFormGloballySharedRequest (FormInfo form, String apiKey) throws IOException {
        Response response = Request.Put(getApiFormsUrl()+"/"+form.getId()+"/shareglobal")
                .addHeader("apiKey", apiKey)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }
    private Content makeFormGroupSharedRequest (FormInfo form, String apiKey) throws IOException {
        Response response = Request.Put(getApiFormsUrl()+"/"+form.getId()+"/share")
                .addHeader("apiKey", apiKey)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }

    private Content makeFileUploadApiRequest(FilePost filePost, String apiKey) throws IOException {
        HttpEntity fileUploadEntity = MultipartEntityBuilder
                .create()
                .addBinaryBody("file", filePost.getFile())
                .addTextBody("folderId", filePost.getFolderId() == null ? "" : "" + filePost.getFolderId())
                .addTextBody("caption", filePost.getCaption())
                .build();
        Response response = Request.Post(getApiFilesUrl())
                .addHeader("apiKey", apiKey)
                .body(fileUploadEntity)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }

    private Content makeDocumentApiPostRequest(DocumentPost document, String apiKey) throws IOException {
        Validate.notNull(document);
        ObjectMapper mapper = createObjectMapper();
        String documentAsJson = mapper.writeValueAsString(document);
        Response response = Request.Post(getApiDocumentsUrl())
                .addHeader("apiKey", apiKey)
                .bodyString(documentAsJson, ContentType.APPLICATION_JSON)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }

    private Content makeDocumentApiPutRequest(Long docId, DocumentPost document, String apiKey) throws IOException {
        Validate.notNull(docId);
        String docUpdateUrl = getApiSingleDocumentUrl(docId);
        log.info("updating document url: {}", docUpdateUrl);
        ObjectMapper mapper = createObjectMapper();
        String documentAsJson = mapper.writeValueAsString(document);
        log.info("updating document with json: {}", documentAsJson);
        Response response = Request.Put(docUpdateUrl)
                .addHeader("apiKey", apiKey)
                .bodyString(documentAsJson, ContentType.APPLICATION_JSON)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }

    /* makes the HTTP query and returns the results as a Content object */
    protected Content makeApiGetRequest(String uriString, String responseContentType, String apiKey) throws IOException {
        Response response = Request.Get(uriString)
                .addHeader("Accept", responseContentType)
                .addHeader("apiKey", apiKey)
                .connectTimeout(CONNECT_TIMEOUT)
                .socketTimeout(SOCKET_TIMEOUT)
                .execute();
        return response.returnContent();
    }

    protected String getApiDocumentsUrl() {
        return serverUrl + API_DOCUMENTS_ENDPOINT;
    }

    private String getUserNameAndKeyUrl() {
        return serverUrl + USER_NAME_AND_KEY_ENDPOINT;
    }

    private String getUserDetailsEndpoint() {
        return serverUrl + USER_DETAILS_ENDPOINT;
    }

    protected String getApiSingleDocumentUrl(long docId) {
        return getApiDocumentsUrl() + "/" + docId;
    }

    protected String getApiFilesUrl() {
        return serverUrl + API_FILES_ENDPOINT;
    }

    protected String getApiFormsUrl() {
        return serverUrl + API_FORMS_ENDPOINT;
    }

    protected String getApiSingleFileUrl(long fileId) {
        return getApiFilesUrl() + "/" + fileId;
    }

}
