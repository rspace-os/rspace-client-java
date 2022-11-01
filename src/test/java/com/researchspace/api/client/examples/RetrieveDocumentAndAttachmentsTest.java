package com.researchspace.api.client.examples;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;


import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.clientmodel.ApiFile;
import com.researchspace.api.clientmodel.Document;
import com.researchspace.api.clientmodel.Field;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Example code that retrieves a document, it's content (in json or cvs format)
 * and files attached to document's text fields.
 */
class RetrieveDocumentAndAttachmentsTest extends FixedIntervalTest {


    private static final Logger log = LoggerFactory
            .getLogger(RetrieveDocumentAndAttachmentsTest.class);

    /** 
     * Retrieve document and print its content.
     */
    @Test
    void printDocumentsContent() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        Document document = apiConnector.retrieveDocument(getTestDocId());
        
        log.info("Printing content of '{}' (globalId: {}).\n", document.getName(), document.getGlobalId());
        for (Field field : document.getFields()) {
            log.info("{}: {}", field.getName(), field.getContent());
        }
    }

    /** 
     * Download document in CSV format and save it as a file.
     */
    @Test
    void saveDocumentInCsvFormat() throws IOException, URISyntaxException {
        
        ApiConnector apiConnector = createApiConnector();
        String contentAsCsv = apiConnector.retrieveDocumentAsCSV(getTestDocId());
        
        String outputFileName = getTestDocId() + ".csv";
        try (PrintWriter out = new PrintWriter(outputFileName)) {
            out.println(contentAsCsv);
        }
        log.info("Document {} saved into file '{}'. \n", getTestDocId(), outputFileName);
    }

    /**
     * Save document's attachments. 
     */
    @Test
    void saveDocumentsAttachments() throws URISyntaxException, IOException {
        
        ApiConnector apiConnector = createApiConnector();
        Document document = apiConnector.retrieveDocument(getTestDocId());

        List<Field> fields = document.getFields();
        List<ApiFile> attachments = new ArrayList<>();
        for (Field f : fields) {
            attachments.addAll(f.getFiles());
        }

        log.info("Retrieved document '{}' (globalId: {}), which contains {} attachment(s).\n",
                document.getName(), document.getGlobalId(), attachments.size());
        
        for (ApiFile File : attachments) {
            InputStream content = apiConnector.retrieveFileData(File);
            File file = new File(File.getName());
            FileUtils.copyInputStreamToFile(content, file);
            System.out.println("Saved attachment: " + File.getName());
        }
    }
    
    private long getTestDocId() {
        return Long.parseLong(configReader.getConfigProperty("testDocId"));
    }

}

