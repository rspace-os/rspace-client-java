package com.researchspace.api.client.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.ApiFile;
import com.researchspace.api.clientmodel.Document;
import com.researchspace.api.clientmodel.DocumentPost;
import com.researchspace.api.clientmodel.FieldPost;
import com.researchspace.api.clientmodel.FilePost;

/** 
 * Example code for creating simple and complex documents.
 */
public class CreateDocument extends FixedIntervalTest {

    @Test
    public void createUpdateSimplestDoc() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        DocumentPost docToCreate = DocumentPost.builder().build();
        docToCreate.setName("created with API");

        Document createdDoc = apiConnector.createDocument(docToCreate);
        assertNotNull(createdDoc);
        assertNotNull(createdDoc.getId());
        assertEquals("created with API", createdDoc.getName());
        assertNotNull(createdDoc.getForm());
        assertEquals("Basic Document", createdDoc.getForm().getName());
        assertEquals(1, createdDoc.getFields().size());
        assertEquals("", createdDoc.getFields().get(0).getContent());

        System.out.println("Document successfully created");
        
        DocumentPost docToUpdate = DocumentPost.builder().build();
        docToUpdate.setName("updated with API");
        Document updatedDoc = apiConnector.updateDocument(createdDoc.getId(), docToUpdate);
        assertEquals(createdDoc.getId(), updatedDoc.getId());
        assertEquals("updated with API", updatedDoc.getName());
        assertEquals("", updatedDoc.getFields().get(0).getContent());
        
        System.out.println("Document successfully updated");
    }

    @Test
    public void createDocumentWithAttachment() throws IOException {
        
        ApiConnector apiConnector = createApiConnector();

        File file = new File("src/test/resources/2017-05-10_1670091041_CNVts.csv");
        String caption = "csv results file";
        FilePost fileToUpload = FilePost.builder()
                .file(file)
                .caption(caption)
                .build(); 
        ApiFile uploadedFile = apiConnector.uploadFile(fileToUpload);
        assertNotNull(uploadedFile);
        assertNotNull(uploadedFile.getId());
        assertEquals(caption, uploadedFile.getCaption());
        
        System.out.println("File successfully uploaded to RSpace Gallery");
        
        FieldPost fieldToCreate = new FieldPost();
        fieldToCreate.appendContent("here are the results: <br/>");
        fieldToCreate.appendFileReference(uploadedFile.getId());
        DocumentPost docToCreate = DocumentPost.builder()
                .name("document with attachment")
                .field(fieldToCreate)
                .build();

        Document createdDoc = apiConnector.createDocument(docToCreate);
        assertNotNull(createdDoc.getId());
        String expectedAttachmentLink = "/Streamfile/" + uploadedFile.getId();
        assertTrue(createdDoc.getFields().get(0).getContent().contains(expectedAttachmentLink));
        
        System.out.println("Document with attachment successfully created");
    }

}

