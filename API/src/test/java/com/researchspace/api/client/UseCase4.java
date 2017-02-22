package com.researchspace.api.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.researchspace.api.client.model.ApiDocument;
import com.researchspace.api.client.model.ApiField;
import com.researchspace.api.client.model.ApiFile;
import com.researchspace.api.client.model.ApiLinkItem;

/**
 * This use case saves document's attachment. 
 */
public class UseCase4 {
	
	private static final long TEST_DOC_ID = 21609;
	
	@Test
	public void downloadAttachments() throws URISyntaxException, IOException {
		
		ApiConnector apiConnector = new ApiConnector();
		ApiDocument document = apiConnector.makeSingleDocumentApiRequest(TEST_DOC_ID);
		
		List<ApiField> fields = document.getFields();
		List<ApiFile> files = fields.get(0).getFiles();

		for (ApiFile apiFile : files) {
			String fileData = apiFile.getLinkByType(ApiLinkItem.ENCLOSURE_REL);
			System.out.println(fileData);
			
			InputStream content = apiConnector.makeApiRequest(fileData).asStream();
			File file = new File("UseCase4-" + apiFile.getName());
			FileUtils.copyInputStreamToFile(content, file);
		}
		
		System.out.println("done.");
	}

}

