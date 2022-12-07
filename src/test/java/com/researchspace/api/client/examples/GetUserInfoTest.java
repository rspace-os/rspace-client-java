package com.researchspace.api.client.examples;

import com.researchspace.api.client.AdvancedQuery;
import com.researchspace.api.client.AdvancedQueryElem;
import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.DocumentInfo;
import com.researchspace.api.clientmodel.DocumentSearchResult;
import com.researchspace.api.clientmodel.LinkItem;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example code that finds documents using various criteria and search types,
 * also demonstrates how to navigate paginated results returned by the API.
 */
class GetUserInfoTest extends FixedIntervalTest {

    private static final Logger log = LoggerFactory
            .getLogger(GetUserInfoTest.class);


    /**
     * General search for a particular phrase. Corresponds to Workspace 'All' search.
     */
    @Test
    void getsAllUserInfoWhenRunAsSysadmin() throws Exception {

        ApiConnector apiConnector = createApiConnector();

        Map<String, String> userAndKey = apiConnector.getUserNamesAndApiKeys();

        assertTrue(userAndKey.size() > 0);
    }

    @Test
    void getsUnauthorisedErrorWhenNotRunAsSysadmin() throws IOException, URISyntaxException {

        String searchQuery = "_pcr_";
        ApiConnector apiConnector = createApiConnector();
        DocumentSearchResult searchResult = apiConnector.searchDocuments(searchQuery, null);

        /* search results are paginated, printing only the first page */
        log.info("Found {} document(s) matching the '{}' query: \n", searchResult.getTotalHits(), searchQuery);
        log.info("Printing first {} document(s).", searchResult.getDocuments().size());

        for (DocumentInfo doc : searchResult.getDocuments()) {
            printOutDocDetails(doc);
        }
    }


    private void printOutDocDetails(DocumentInfo docInfo) {
        String details = String.format("Document: %s, form: %s, globalId: %s, createdAt: %s, lastModified: %s",
                docInfo.getName(), docInfo.getForm().getName(), docInfo.getGlobalId(), docInfo.getCreated(),
                docInfo.getLastModified());
        log.info(details);
    }

}

