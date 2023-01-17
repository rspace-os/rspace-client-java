package com.researchspace.api.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import com.researchspace.api.clientmodel.*;

public interface ApiConnector {
    User getUserByUsername(String username, String apiKey) throws Exception;
    Map<String,String> getUserNamesAndApiKeys(String apiKey) throws Exception;

    /**
     * General search for a particular phrase. Corresponds to Workspace 'All'
     * search.
     * 
     * @param searchQuery
     *            (optional) to limit search results
     * @param searchParams
     *            (optional) additional search parameters i.e. pageNumber,
     *            pageSize, orderBy, filter
     * @returns a page of search results, paginated and order according to the
     *          search parameters (by default: 20 results per page, "last
     *          modified desc" order)
     */
    DocumentSearchResult searchDocuments(String searchQuery, Map<String, String> searchParams, String apiKey)
            throws URISyntaxException, IOException;

    /**
     * Make advanced document search query, with additional parameters. Results
     * are paginated, with 20 elements on a page by default.
     * 
     * @param advQuery
     *            (optional) advanced query to limit search results
     * @param searchParams
     *            (optional) additional search parameters i.e. pageNumber,
     *            pageSize, orderBy, filter
     * @returns a page of search results, paginated and order according to the
     *          search parameters (by default: 20 results per page, "last
     *          modified desc" order)
     */
    DocumentSearchResult searchDocuments(AdvancedQuery advQuery, Map<String, String> searchParams, String apiKey)
            throws URISyntaxException, IOException;

    /**
     * Create new document in RSpace, either "Basic Document" or based on a
     * specific form.
     * 
     * @param document 
     *      document to create
     * @return created document
     */
    Document createDocument(DocumentPost document, String apiKey) throws IOException;
    /**
     * Returns representation of a single document (with fields).
     */
    Document retrieveDocument(long docId, String apiKey) throws IOException;

    /**
     * Returns content of a single document in CSV format
     */
    String retrieveDocumentAsCSV(long docId, String apiKey) throws IOException;

    /** retrieves the object behind the link */
    <T> T retrieveLinkedObject(String link, Class<T> objectType, String apiKey) throws IOException;

    /**
     * Update existing RSpace document.
     * 
     * @param docId 
     *            id of a document to update
     * @param document
     *            object containing changes to the document
     * @return updated document
     */
    Document updateDocument(Long docId, DocumentPost document, String apiKey) throws IOException;


    /**
     * Creates a folder or notebook (if isNotebook is true)
     * in the folder related to parentFolderId if specified, else in the users home folder
     * @param folderPost {@link FolderPost} the new folder to create
     * @return the new {@link Folder} object
     * @throws IOException
     */
    Folder createFolder(FolderPost folderPost, String apiKey) throws IOException;

    Folder getFolder(long folderId, String apiKey) throws IOException;

    /**
     * Search for Gallery files, based on various criteria.
     */
    FileSearchResult searchFiles(String mediaType, Map<String, String> searchParams, String apiKey)
            throws URISyntaxException, IOException;

    /**
     * Retrieve details of a particular file from RSpace Gallery.
     * @param fileId
     */
    ApiFile retrieveFileById(long fileId, String apiKey) throws IOException;

    /** returns input stream to file data */
    InputStream retrieveFileData(ApiFile apiFile, String apiKey) throws IOException;

    /**
     * Upload a file to RSpace Gallery.
     * 
     * @param file
     *          details of file to upload
     */
    ApiFile uploadFile(FilePost file, String apiKey) throws IOException;


    FormSearchResult getForms(String apiKey, Map<String, String> searchParams, String query) throws IOException, URISyntaxException ;

    FormInfo createForm(FormPost.Form formPost, String apiKey) throws IOException;

    FormInfo updateForm(FormPost.Form formPost, String apiKey) throws IOException;

    /**
     *
     * @param form
     * @param apiKey
     * @return true if deletion was successful
     */
    boolean deleteForm(FormInfo form, String apiKey) throws IOException;

    FormInfo setIconOnForm(FormInfo form, File iconFile,  String apiKey) throws IOException;

    FormInfo publishForm(FormInfo createdForm, String configuredApiKey) throws IOException;
    /**
     * Globally shares a form with READ permission, ONLY SYSADMINS can use this API
     * @param createdForm, an existing form
     * @param apiKey
     * @return the shared form which has set group and global permissions to READ
     */
    FormInfo globalShareForm(FormInfo createdForm, String configuredApiKey) throws IOException;
    /**
     * Shares a form with owners groups with READ permission, FORM owner can use this API
     * @param createdForm, an existing form
     * @param apiKey
     * @return the shared form which has set group and global permissions to READ
     */
    FormInfo groupShareForm(FormInfo createdForm, String configuredApiKey) throws IOException;

    UserGroupInfo createUser(UserPost userToCreate, String configuredApiKey) throws IOException;

    GroupInfo createGroup(GroupPost groupPost, String configuredApiKey) throws IOException;
}
