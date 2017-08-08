package com.researchspace.api.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import com.researchspace.api.clientmodel.ApiFile;
import com.researchspace.api.clientmodel.Document;
import com.researchspace.api.clientmodel.DocumentSearchResult;
import com.researchspace.api.clientmodel.FilePost;
import com.researchspace.api.clientmodel.FileSearchResult;

public interface ApiConnector {

	/** 
	 * General search for a particular phrase. Corresponds to Workspace 'All' search.
	 * 
	 * @param searchQuery (optional) to limit search results
	 * @param searchParams (optional) additional search parameters i.e. pageNumber, pageSize, orderBy, filter
	 * @returns a page of search results, paginated and order according to the search parameters
	 *         (by default: 20 results per page, "last modified desc" order)
	 */
	DocumentSearchResult searchDocuments(String searchQuery, Map<String, String> searchParams)
			throws URISyntaxException, IOException;

	/** 
	 * Make advanced document search query, with additional parameters.
	 * Results are paginated, with 20 elements on a page by default.
	 *  
	 * @param advQuery (optional) advanced query to limit search results
	 * @param searchParams (optional) additional search parameters i.e. pageNumber, pageSize, orderBy, filter
	 * @returns a page of search results, paginated and order according to the search parameters
	 *         (by default: 20 results per page, "last modified desc" order) 
	 */
	DocumentSearchResult searchDocuments(AdvancedQuery advQuery, Map<String, String> searchParams)
			throws URISyntaxException, IOException;

	/**
	 * Create new document in RSpace, either "Basic Document" or based on a specific form. 
	 * @param document to create
	 * @return created document
	 */
    Document createDocument(Document document) throws IOException;

	/**
	 * Returns representation of a single document (with fields).
	 */
	Document retrieveDocument(long docID) throws IOException;

	/** 
	 * Returns content of a single document in CSV format 
	 */
	String retrieveDocumentAsCSV(long docID) throws IOException;

	/** retrieves the object behind the link */
	<T> T retrieveLinkedObject(String link, Class<T> objectType) throws IOException;

	/**
     * Update existing RSpace document. 
     * @param document to create, id field cannot be null
     * @return updated document
     */
    Document updateDocument(Document document) throws IOException;

    /**
     * Search for Gallery files, based on various criteria. 
     */
    FileSearchResult searchFiles(String mediaType, Map<String, String> searchParams)
            throws URISyntaxException, IOException;

    ApiFile retrieveFileById(long fileId) throws IOException;

	/** returns input stream to file data */
	InputStream retrieveFileData(ApiFile apiFile) throws IOException;
	
	ApiFile uploadFile(FilePost file) throws IOException;

}
