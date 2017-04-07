package com.researchspace.api.client.examples;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.ApiConnectorImpl;
import com.researchspace.api.client.model.ApiDocument;
import com.researchspace.api.client.model.ApiField;
import com.researchspace.api.client.model.ApiFile;

/** 
 * Example code that retrieves a document, it's content (in json or cvs format)
 * and files attached to document's text fields.
 */
public class RetrieveDocumentAndAttachments  extends FixedIntervalTest {
    
    /** ID of an example document stored on API Test User account on RSpace Community. */
    private static final long TEST_DOC_ID = 1117;

    /** 
     * Retrieve document and print its content.
     */
    @Test
    public void printDocumentsContent() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        ApiDocument document = apiConnector.makeSingleDocumentRequest(TEST_DOC_ID);
        
        System.out.printf("Printing content of '%s' (globalId: %s).\n", document.getName(), document.getGlobalId());
        for (ApiField field : document.getFields()) {
            System.out.println(field.getName() + ": " + field.getContent());
        }
    }

    /** 
     * Download document in CSV format and save it as a file.
     */
    @Test
    public void saveDocumentInCsvFormat() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        String contentAsCsv = apiConnector.makeSingleCSVDocumentRequest(TEST_DOC_ID);
        
        String outputFileName = TEST_DOC_ID + ".csv";
        try (PrintWriter out = new PrintWriter(outputFileName)) {
            out.println(contentAsCsv);
        }
        System.out.printf("Document %d saved into file '%s'. \n", TEST_DOC_ID, outputFileName);
    }

	
    /**
     * Save document's attachments. 
     */
    @Test
    public void saveDocumentsAttachments() throws URISyntaxException, IOException {
        
        ApiConnector apiConnector = createApiConnector();
        ApiDocument document = apiConnector.makeSingleDocumentRequest(TEST_DOC_ID);

        List<ApiField> fields = document.getFields();
        List<ApiFile> attachments = new ArrayList<>();
        for (ApiField f : fields) {
            attachments.addAll(f.getFiles());
        }

        System.out.printf("Retrieved document '%s' (globalId: %s), which contains %d attachment(s).\n",
                document.getName(), document.getGlobalId(), attachments.size());
        
        for (ApiFile apiFile : attachments) {
            InputStream content = apiConnector.makeFileDataRequest(apiFile);
            File file = new File(apiFile.getName());
            FileUtils.copyInputStreamToFile(content, file);
            System.out.println("Saved attachment: " + apiFile.getName());
        }
    }

}

