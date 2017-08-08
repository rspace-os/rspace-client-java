package com.researchspace.api.client.examples;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.ApiFile;
import com.researchspace.api.clientmodel.FileSearchResult;

/** 
 * Example code that lists files uploaded to the Gallery. 
 * That includes documents exported to Word/PDF. 
 */
public class SearchGallery extends FixedIntervalTest {

    /**
     * Prints files from User's Gallery.
     */
    @Test
    public void printPageOfGalleryItems() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        FileSearchResult galleryImages = apiConnector.searchFiles("", null);

        /* search results are paginated, printing only the first page */
        System.out.println("User has " + galleryImages.getTotalHits() + " file(s) in their Gallery.");
        System.out.println("Printing first " + galleryImages.getFiles().size() + " file(s):");

        for(ApiFile apiFile : galleryImages.getFiles()) {
            printOutFileDetails(apiFile);
        }
    }

    private void printOutFileDetails(ApiFile fileInfo) {
        String details = String.format("Name: %s, globalId: %s, size: %d, contentType: %s", 
                fileInfo.getName(), fileInfo.getGlobalId(), fileInfo.getSize(), fileInfo.getContentType());
        System.out.println(details);
    }
    
}

