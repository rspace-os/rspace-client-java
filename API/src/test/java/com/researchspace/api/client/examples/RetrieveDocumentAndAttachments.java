package com.researchspace.api.client.examples;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.ApiFile;
import com.researchspace.api.clientmodel.Document;
import com.researchspace.api.clientmodel.Field;

/** 
 * Example code that retrieves a document, it's content (in json or cvs format)
 * and files attached to document's text fields.
 */
public class RetrieveDocumentAndAttachments extends FixedIntervalTest {
    
   long  getTestDocId (){
	   return Long.parseLong(getConfigProperty("testDocId"));
	   
   }

    /** 
     * Retrieve document and print its content.
     */
    @Test
    public void printDocumentsContent() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        Document document = apiConnector.makeSingleDocumentRequest(getTestDocId ());
        
        System.out.printf("Printing content of '%s' (globalId: %s).\n", document.getName(), document.getGlobalId());
        for (Field field : document.getFields()) {
            System.out.println(field.getName() + ": " + field.getContent());
        }
    }

    /** 
     * Download document in CSV format and save it as a file.
     */
    @Test
    public void saveDocumentInCsvFormat() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        String contentAsCsv = apiConnector.makeSingleCSVDocumentRequest(getTestDocId ());
        
        String outputFileName = getTestDocId () + ".csv";
        try (PrintWriter out = new PrintWriter(outputFileName)) {
            out.println(contentAsCsv);
        }
        System.out.printf("Document %d saved into file '%s'. \n", getTestDocId (), outputFileName);
    }

    /**
     * Save document's attachments. 
     */
    @Test
    public void saveDocumentsAttachments() throws URISyntaxException, IOException {
        
        ApiConnector apiConnector = createApiConnector();
        Document document = apiConnector.makeSingleDocumentRequest(getTestDocId ());

        List<Field> fields = document.getFields();
        List<ApiFile> attachments = new ArrayList<>();
        for (Field f : fields) {
            attachments.addAll(f.getFiles());
        }

        System.out.printf("Retrieved document '%s' (globalId: %s), which contains %d attachment(s).\n",
                document.getName(), document.getGlobalId(), attachments.size());
        
        for (ApiFile File : attachments) {
            InputStream content = apiConnector.makeFileDataRequest(File);
            File file = new File(File.getName());
            FileUtils.copyInputStreamToFile(content, file);
            System.out.println("Saved attachment: " + File.getName());
        }
    }

}

