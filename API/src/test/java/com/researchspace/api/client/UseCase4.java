package com.researchspace.api.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.researchspace.api.client.model.ApiDocument;
import com.researchspace.api.client.model.ApiField;
import com.researchspace.api.client.model.ApiFile;

/**
 * This use case saves document's attachment. 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UseCase4 {
	
	private static final long TEST_DOC_ID = 21609;
	
	public static void main(String[] args) throws URISyntaxException, JsonProcessingException, IOException {
		
		ApiConnector apiConnector = new ApiConnector();
		ApiDocument document = apiConnector.makeSingleDocumentApiRequest(TEST_DOC_ID);
		
		List<ApiField> fields = document.getFields();
		List<ApiFile> files = fields.get(0).getFiles();

		for (ApiFile apiFile : files) {
			String fileData = apiFile.getLinks().get(1).getLink();
			System.out.println(fileData);
			
			InputStream content = apiConnector.makeApiRequest(fileData).asStream();
			File file = new File("UseCase4-" + apiFile.getName());
			FileUtils.copyInputStreamToFile(content, file);
		}
		
		System.out.println("done.");
	}

}

