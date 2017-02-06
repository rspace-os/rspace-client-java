package com.researchspace.api.examples.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCase1c {
	
	public static long docID = 24407;
	
	public static void main(String[] args) throws URISyntaxException, JsonProcessingException, IOException {
		
		ArrayList<String> fileURLs = new ArrayList<String>();
		String data = Library.makeQuery(Library.getAPIDocumentUrl(docID));
		//System.out.println(data);
		
		ObjectMapper mapper = new ObjectMapper();
		// data here is returned from /documents
		JsonNode document = mapper.readTree(data);
		JsonNode fields = document.path("fields");
		JsonNode files = fields.path(0).path("files");
		System.out.println(files);
		
		for(JsonNode file: files){
			String filelink = file.path("_links").path(0).path("link").asText();
			URL fileSource = new URL(filelink);
			System.out.println(fileSource);
			fileURLs.add(filelink);
			System.out.println(filelink);
		}
		
		int i = 0;
		for(String filelink: fileURLs){
			String fileSource = filelink + "/file";
			InputStream content = (Request.Get(fileSource)
					.addHeader("apiKey", Library.getConfigProperty("apiKey"))
					.connectTimeout(10000)
					.socketTimeout(10000)
					.execute().returnContent().asStream());
			File file = new File("output" + i);
			FileUtils.copyInputStreamToFile(content, file);
			i++;
			
			try {
			    Thread.sleep(100);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			//System.out.println(output);
		} 
	}

}

