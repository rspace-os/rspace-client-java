package com.researchspace.api.client.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.researchspace.api.client.AdvancedQuery;
import com.researchspace.api.client.AdvancedQueryElem;
import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.DocumentInfo;
import com.researchspace.api.clientmodel.DocumentSearchResult;
import com.researchspace.api.clientmodel.LinkItem;

/** 
 * Example code that finds documents using various criteria and search types,
 * also demonstrates how to navigate paginated results returned by the API.
 */
public class FindDocuments extends FixedIntervalTest {

    /**
     * Prints 20 last modified documents that user has access to.
     * The result corresponds to 'View All' filter.  
     */
    @Test
    public void printRecentlyUpdatedDocs() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        DocumentSearchResult allDocs = apiConnector.searchDocuments("", null);

        /* search results are paginated, printing only the first page */
        System.out.println("User has " + allDocs.getTotalHits() + " document(s) in their Workspace.");
        System.out.println("Printing first " + allDocs.getDocuments().size() + " document(s).");
        
        for(DocumentInfo DocInfo : allDocs.getDocuments()) {
            printOutDocDetails(DocInfo);
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

        DocumentSearchResult paginatedDocs = apiConnector.searchDocuments("", extraSearchParams);
        String nextLink = paginatedDocs.getLinkByType(LinkItem.NEXT_REL);
        int maxPages =10;
        int currPage=0;
        /* go through all search result pages */ 
        while (nextLink != null && currPage < maxPages) {
        	currPage++;
            System.out.println("at page: " + paginatedDocs.getPageNumber());
            System.out.println("next link: " + nextLink);

            for (DocumentInfo doc : paginatedDocs.getDocuments()) {
                printOutDocDetails(doc);
            }
            nextLink = paginatedDocs.getLinkByType(LinkItem.NEXT_REL);
            if (nextLink != null) {
                paginatedDocs = apiConnector.retrieveLinkedObject(nextLink, DocumentSearchResult.class);
            }
        } 
    }

    /** 
     * General search for a particular phrase. Corresponds to Workspace 'All' search.
     */
    @Test
    public void simpleSearch() throws IOException, URISyntaxException {

        String searchQuery = "pcr";
        ApiConnector apiConnector =createApiConnector();
        DocumentSearchResult searchResult = apiConnector.searchDocuments(searchQuery, null);

        /* search results are paginated, printing only the first page */
        System.out.printf("Found %d document(s) matching the '%s' query: \n", searchResult.getTotalHits(), searchQuery);
        System.out.println("Printing first " + searchResult.getDocuments().size() + " document(s).");
        
        for (DocumentInfo doc : searchResult.getDocuments()) {
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
        String searchedTag = "SearchTag";
        
        AdvancedQueryElem nameSearchTerm = new AdvancedQueryElem(searchedName, "name");
        AdvancedQueryElem tagSearchTerm = new AdvancedQueryElem(searchedTag, "tag");
        AdvancedQuery advQuery = new AdvancedQuery(AdvancedQuery.OPERATOR_AND, nameSearchTerm, tagSearchTerm);

        ApiConnector apiConnector = createApiConnector();
        DocumentSearchResult searchResult = apiConnector.searchDocuments(advQuery, null);

        /* search results are paginated, printing only the first page */
        System.out.printf("Found %s document(s) with name '%s' or tag '%s': \n", 
                searchResult.getTotalHits(), searchedName, searchedTag);
        System.out.println("Printing first " + searchResult.getDocuments().size() + " document(s).");
        for(DocumentInfo DocInfo : searchResult.getDocuments()) {
            printOutDocDetails(DocInfo);
        }
    }
    
    private void printOutDocDetails(DocumentInfo docInfo) {
        String details = String.format("Document: %s, form: %s, globalId: %s, createdAt: %s, lastModified: %s", 
                docInfo.getName(), docInfo.getForm().getName(), docInfo.getGlobalId(), docInfo.getCreated(), 
                docInfo.getLastModified());
        System.out.println(details);
    }
    
}

