package com.researchspace.api.client.examples;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.researchspace.api.client.ApiConnector;
import com.researchspace.api.client.model.ApiDocument;
import com.researchspace.api.client.model.ApiField;
import com.researchspace.api.client.model.ApiFile;

/**
 * This use case saves document's attachment. 
 */
public class RetrieveDocumentAttachments {
	
	private static final long TEST_DOC_ID = 21609;
	
	@Test
	public void downloadAttachments() throws URISyntaxException, IOException {
		
		ApiConnector apiConnector = new ApiConnector();
		ApiDocument document = apiConnector.makeSingleDocumentRequest(TEST_DOC_ID);
		
		List<ApiField> fields = document.getFields();
		List<ApiFile> files = fields.get(0).getFiles();

		for (ApiFile apiFile : files) {
			InputStream content = apiConnector.makeFileDataRequest(apiFile);
			File file = new File("UseCase4-" + apiFile.getName());
			FileUtils.copyInputStreamToFile(content, file);
		}
		
		System.out.println("done.");
	}

}

