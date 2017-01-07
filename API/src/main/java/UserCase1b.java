import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
@JsonIgnoreProperties(ignoreUnknown = true)

public class UserCase1b {
	public static ArrayList<String> requestURLs = new ArrayList<String>();
	public static ArrayList<Float> fieldValues = new ArrayList<Float>();
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		String output1 = makeQuery(uriString());
//		System.out.println(makeQuery(uriString()));
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode results = mapper.readTree(output1);
		for(JsonNode document : results.path("documents")) {
			String requestURL = document.path("_links").path(0).path("link").asText();
//			System.out.println(requestURL);
			
			String output2 = makeQuery(requestURL);
			
			ObjectMapper mapper2 = new ObjectMapper();
			JsonNode results2 = mapper2.readTree(output2);
			JsonNode fieldArray = results2.path("fields");
			String fieldValue = fieldArray.path(1).path("content").asText();
			fieldValues.add(Float.parseFloat(fieldValue));
			System.out.println(fieldValue);
			
			try {
			    Thread.sleep(1000);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
		}
	}
	
	//Method to set property (API key or host URL) from input file
	public static String setProperties(String property) {

	Properties prop = new Properties();
	InputStream input = null;
	String propertyValue = "";

	try {
		input = new FileInputStream("config.properties");
		// load a properties file
		prop.load(input);
		// get the property value and print it out
//		System.out.println(prop.getProperty(property));
		propertyValue = prop.getProperty(property);
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}
//	System.out.println(propertyValue);
	return propertyValue;
	}
	
	//This method makes the HTTP query and returns the results as a JSON string
	public static String makeQuery(String uriString) throws URISyntaxException {
		
		String output = "";
		
		try {
			output = (Request.Get(uriString)
					.addHeader("apiKey", setProperties("apiKey"))
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
		return output;
	}
	
	//This method builds the URI String from your Query
	public static String uriString() throws URISyntaxException {
		
		String uriString = "";
		
		AdvancedQuery advQuery = new AdvancedQuery(new Query ("TestForm", "form"));
	    
	    URIBuilder builder = new URIBuilder()
	    		.setScheme("https")
	            .setHost(setProperties("hostURL"))
	            .setParameter("advancedQuery", advQuery.advancedQuery2JSON());
	    
	    URI uri = builder.build();
	    uriString = uri.toString();
	    
//	    System.out.println(uriString);
	    return uriString;
	    
	}
}
	


