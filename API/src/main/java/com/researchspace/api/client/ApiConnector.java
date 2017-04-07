package com.researchspace.api.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import com.researchspace.api.client.model.ApiDocument;
import com.researchspace.api.client.model.ApiDocumentSearchResult;
import com.researchspace.api.client.model.ApiFile;
import com.researchspace.api.client.model.ApiFileSearchResult;

public interface ApiConnector {

	/** 
	 * General search for a particular phrase. Corresponds to Workspace 'All' search.
	 * 
	 * @param searchQuery (optional) to limit search results
	 * @param searchParams (optional) additional search parameters i.e. pageNumber, pageSize, orderBy, filter
	 * @returns a page of search results, paginated and order according to the search parameters
	 *         (by default: 20 results per page, "last modified desc" order)
	 */
	ApiDocumentSearchResult makeDocumentSearchRequest(String searchQuery, Map<String, String> searchParams)
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
	ApiDocumentSearchResult makeDocumentSearchRequest(AdvancedQuery advQuery, Map<String, String> searchParams)
			throws URISyntaxException, IOException;

	/**
	 * Returns representation of a single document (with fields).
	 */
	ApiDocument makeSingleDocumentRequest(long docID) throws IOException;

	/** 
	 * Returns content of a single document in CSV format 
	 */
	String makeSingleCSVDocumentRequest(long docID) throws IOException;

	/** retrieves the object behind the link */
	<T> T makeLinkedObjectRequest(String link, Class<T> objectType) throws IOException;

	/** returns input stream to file data */
	InputStream makeFileDataRequest(ApiFile apiFile) throws IOException;

	ApiFileSearchResult makeFileSearchRequest(String mediaType, Map<String, String> searchParams)
			throws URISyntaxException, IOException;

}