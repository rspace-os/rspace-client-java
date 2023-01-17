package com.researchspace.api.client.examples;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.Folder;
import com.researchspace.api.clientmodel.FolderPost;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FoldersTest extends FixedIntervalTest {

    private static final Logger log = LoggerFactory
            .getLogger(CreateDocumentTest.class);


    @Test
    void testCreateAndGetFolder() throws IOException {
        ApiConnector apiConnector = createApiConnector();
        FolderPost folderToCreate = FolderPost.builder().name("created with API client").notebook(false).build();

        Folder createdFolder = apiConnector.createFolder(folderToCreate, configuredApiKey);
        assertNotNull(createdFolder);
        assertNotNull(createdFolder.getId());
        assertFalse(createdFolder.isNotebook());
        assertEquals("created with API client", createdFolder.getName());

        Folder folderRetrieved = apiConnector.getFolder(createdFolder.getId(), configuredApiKey);
        assertNotNull(folderRetrieved);
        assertEquals(createdFolder.getId(), folderRetrieved.getId());
        assertFalse(folderRetrieved.isNotebook());
        assertEquals("created with API client", folderRetrieved.getName());

        log.info("Folder successfully created");
    }
}