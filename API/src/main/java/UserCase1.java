import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
@JsonIgnoreProperties(ignoreUnknown = true)

public class UserCase1 {
	public static String output, output2, resultsString;
	public static ArrayList<Long> docIDs = new ArrayList<Long>();
	public static ArrayList<Float> fieldValues = new ArrayList<Float>();
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		try {
			output = (Request.Get("https://pangolin.researchspace.com:8085/api/v1/documents?orderBy=lastModified%20desc&advancedQuery=%7B%22terms%22%3A%20%5B%20%7B%22query%22%3A%20%22TestForm%22%2C%20%22queryType%22%3A%20%22form%22%7D%5D%7D")
					.addHeader("apiKey","064sj9DP18yPGDGmdk23AfXxF2Gf5ARC")
					.connectTimeout(10000)
					.socketTimeout(10000)
					.execute().returnContent().asString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode results = mapper.readTree(output);
		for(JsonNode document : results.path("documents")) {
			String documentID = document.path("id").asText();
			docIDs.add(Long.parseLong(documentID));
		//	System.out.println(documentID);
		}
		
		for(long docID: docIDs){
			try {
				String requestURL = "https://pangolin.researchspace.com:8085/api/v1/documents/" + docID;
				output2 = (Request.Get(requestURL)
						.addHeader("apiKey","064sj9DP18yPGDGmdk23AfXxF2Gf5ARC")
						.connectTimeout(10000)
						.socketTimeout(10000)
						.execute().returnContent().asString());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ObjectMapper mapper2 = new ObjectMapper();
			JsonNode results2 = mapper2.readTree(output2);
			JsonNode fieldArray = results2.path("fields");
			String fieldValue = fieldArray.path(1).path("content").asText();
			fieldValues.add(Float.parseFloat(fieldValue));
//			System.out.println(fieldValue);
			
			try {
			    Thread.sleep(1000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
		}
		
		resultsString = "\n";
		for(float fieldValue: fieldValues){
			resultsString = resultsString + Float.toString(fieldValue) + "\n";
		}
		
		try(PrintWriter out = new PrintWriter("UserCase1.txt")){
		    out.println(resultsString);
		}
	}
	
}


