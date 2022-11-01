package com.researchspace.api.client.examples;

import java.io.IOException;
import java.net.URISyntaxException;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.ApiConnectorImpl;
import com.researchspace.api.clientmodel.ApiFile;
import com.researchspace.api.clientmodel.FileSearchResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Example code that lists files uploaded to the Gallery. 
 * That includes documents exported to Word/PDF. 
 */
class SearchGalleryTest extends FixedIntervalTest {

    private static final Logger log = LoggerFactory
            .getLogger(SearchGalleryTest.class);

    /**
     * Prints files from User's Gallery.
     */
    @Test
    void printPageOfGalleryItems() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        FileSearchResult galleryImages = apiConnector.searchFiles("", null);

        /* search results are paginated, printing only the first page */
        log.info("User has {} file(s) in their Gallery.", galleryImages.getTotalHits() );
        log.info("Printing first {} file(s):", galleryImages.getFiles().size());

        for(ApiFile apiFile : galleryImages.getFiles()) {
            printOutFileDetails(apiFile);
        }
    }

    private void printOutFileDetails(ApiFile fileInfo) {
        String details = String.format("Name: %s, globalId: %s, size: %d, contentType: %s", 
                fileInfo.getName(), fileInfo.getGlobalId(), fileInfo.getSize(), fileInfo.getContentType());
        log.info(details);
    }
    
}

