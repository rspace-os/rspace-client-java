package com.researchspace.api.client.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.Document;

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

}

