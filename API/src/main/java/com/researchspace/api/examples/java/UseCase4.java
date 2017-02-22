package com.researchspace.api.examples.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3
import com.researchspace.api.examples.java.model.ApiFile;

/**
 * This use case saves document's attachment. 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UseCase4 {
	
	private static final long TEST_DOC_ID = 21609;
	
	public static void main(String[] args) throws URISyntaxException, JsonProcessingException, IOException {
		
		ApiConnector apiConnector = new ApiConnector();
		String data = apiConnector.makeSingleDocumentApiRequest(TEST_DOC_ID);
		
		ObjectMapper mapper = new ObjectMapper();
		// data here is returned from /documents
		JsonNode document = mapper.readTree(data);
		JsonNode fields = document.path("fields");
		JsonNode files = fields.path(0).path("files");

		for (JsonNode fileNode : files) {
			ApiFile apiFile = mapper.treeToValue(fileNode, ApiFile.class);
			String fileData = apiFile.getLinks().get(1).getLink();
			System.out.println(fileData);
			
			InputStream content = apiConnector.makeApiRequest(fileData).asStream();
			File file = new File("UseCase4-" + apiFile.getName());
			FileUtils.copyInputStreamToFile(content, file);
		}
		
		System.out.println("done.");
	}

}

