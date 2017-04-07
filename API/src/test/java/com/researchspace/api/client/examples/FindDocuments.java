package com.researchspace.api.client.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.researchspace.api.client.AdvancedQuery;
import com.researchspace.api.client.AdvancedQueryElem;
import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.ApiConnectorImpl;
import com.researchspace.api.client.model.ApiDocumentInfo;
import com.researchspace.api.client.model.ApiDocumentSearchResult;
import com.researchspace.api.client.model.ApiLinkItem;

/** 
 * Example code that finds documents using various criteria and search types,
 * also demonstrates how to navigate paginated results returned by the API.
 */
public class FindDocuments  extends FixedIntervalTest {

    /**
     * Prints 20 last modified documents that user has access to.
     * The result corresponds to 'View All' filter.  
     */
    @Test
    public void printRecentlyUpdatedDocs() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        ApiDocumentSearchResult allDocs = apiConnector.makeDocumentSearchRequest("", null);

        /* search results are paginated, printing only the first page */
        System.out.println("User has " + allDocs.getTotalHits() + " document(s) in their Workspace.");
        System.out.println("Printing first " + allDocs.getDocuments().size() + " document(s).");
        
        for(ApiDocumentInfo apiDocInfo : allDocs.getDocuments()) {
            printOutDocDetails(apiDocInfo);
        }
    }

    /** 
     * Print all documents that user has access to, ordered by creation date.
     *
     * Document search results are paginated (with 20 elements per page by default),
     * and the code below uses 'next' links to go through all pages.
     */
    @Test
    public void printAllUserDocs() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();

        /* get all documents, sort by creation date in ascending order */
        Map<String, String> extraSearchParams = new HashMap<>();
        extraSearchParams.put("orderBy", "created asc");

        ApiDocumentSearchResult paginatedDocs = apiConnector.makeDocumentSearchRequest("", extraSearchParams);
        String nextLink = paginatedDocs.getLinkByType(ApiLinkItem.NEXT_REL);

        /* go through all search result pages */ 
        while (nextLink != null) {
            System.out.println("at page: " + paginatedDocs.getPageNumber());
            System.out.println("next link: " + nextLink);

            for (ApiDocumentInfo doc : paginatedDocs.getDocuments()) {
                printOutDocDetails(doc);
            }
            nextLink = paginatedDocs.getLinkByType(ApiLinkItem.NEXT_REL);
            if (nextLink != null) {
                paginatedDocs = apiConnector.makeLinkedObjectRequest(nextLink, ApiDocumentSearchResult.class);
            }
        } 
    }

    /** 
     * General search for a particular phrase. Corresponds to Workspace 'All' search.
     */
    @Test
    public void simpleSearch() throws IOException, URISyntaxException {

        String searchQuery = "api*";
        ApiConnector apiConnector =createApiConnector();
        ApiDocumentSearchResult searchResult = apiConnector.makeDocumentSearchRequest(searchQuery, null);

        /* search results are paginated, printing only the first page */
        System.out.printf("Found %d document(s) matching the '%s' query: \n", searchResult.getTotalHits(), searchQuery);
        System.out.println("Printing first " + searchResult.getDocuments().size() + " document(s).");
        
        for (ApiDocumentInfo doc : searchResult.getDocuments()) {
            printOutDocDetails(doc);
        }
    }

    /**
     * Search for documents with a particular tag and name.
     */
    @Test
    public void advancedSearch() throws IOException, URISyntaxException {

        /** search terms */
        String searchedName = "doc*";
        String searchedTag = "apiSearchTag";
        
        AdvancedQueryElem nameSearchTerm = new AdvancedQueryElem(searchedName, "name");
        AdvancedQueryElem tagSearchTerm = new AdvancedQueryElem(searchedTag, "tag");
        AdvancedQuery advQuery = new AdvancedQuery(AdvancedQuery.OPERATOR_AND, nameSearchTerm, tagSearchTerm);

        ApiConnector apiConnector = createApiConnector();
        ApiDocumentSearchResult searchResult = apiConnector.makeDocumentSearchRequest(advQuery, null);

        /* search results are paginated, printing only the first page */
        System.out.printf("Found %s document(s) with name '%s' or tag '%s': \n", 
                searchResult.getTotalHits(), searchedName, searchedTag);
        System.out.println("Printing first " + searchResult.getDocuments().size() + " document(s).");
        for(ApiDocumentInfo apiDocInfo : searchResult.getDocuments()) {
            printOutDocDetails(apiDocInfo);
        }
    }
    
    private void printOutDocDetails(ApiDocumentInfo docInfo) {
        String details = String.format("Document: %s, form: %s, globalId: %s, createdAt: %s, lastModified: %s", 
                docInfo.getName(), docInfo.getForm().getName(), docInfo.getGlobalId(), docInfo.getCreated(), 
                docInfo.getLastModified());
        System.out.println(details);
    }
    
}

