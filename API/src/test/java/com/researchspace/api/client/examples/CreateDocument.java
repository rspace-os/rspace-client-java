package com.researchspace.api.client.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.ApiFile;
import com.researchspace.api.clientmodel.Document;
import com.researchspace.api.clientmodel.FilePost;

/** 
 * Example code for creating simple and complex documents.
 */
public class CreateDocument extends FixedIntervalTest {

    @Test
    public void createUpdateSimplestDoc() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        Document document = new Document();
        document.setName("created with API");

        Document createdDoc = apiConnector.createDocument(document);
        assertNotNull(createdDoc);
        assertNotNull(createdDoc.getId());
        assertEquals("created with API", createdDoc.getName());
        assertNotNull(createdDoc.getForm());
        assertEquals("Basic Document", createdDoc.getForm().getName());

        createdDoc.setName("updated with API");
        Document updatedDoc = apiConnector.updateDocument(createdDoc);
        assertEquals(createdDoc.getId(), updatedDoc.getId());
        assertEquals("updated with API", updatedDoc.getName());
    }

    @Test
    public void createDocumentWithAttachment() throws IOException {
        
        ApiConnector apiConnector = createApiConnector();
        File file = new File("src/test/resources/2017-05-10_1670091041_CNVts.csv");
        String caption = "test caption from API";
        //Integer folderId = 2060; // FIXME should be long?

        FilePost filePost = FilePost.builder()
                .file(file)
                .caption(caption)
                //.folderId(folderId)
                .build(); 
        
        ApiFile uploadFile = apiConnector.uploadFile(filePost);
        assertNotNull(uploadFile);
        assertNotNull(uploadFile.getId());
        assertEquals(caption, uploadFile.getCaption());
        
        System.out.println("Document successfully uploaded to RSpace Gallery");
    }
    
}

