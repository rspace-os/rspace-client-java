import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)

public class UserCase1a {
	public static ArrayList<String> requestURLs = new ArrayList<String>();
	public static ArrayList<Float> fieldValues = new ArrayList<Float>();
	public static String query = "TestForm";
	public static String queryType = "form";
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		//Create an advanced query for all documents made from a specified form template
		//Return the results by retrieving the first page of results and then linking to the
		//next page... until the last page is reached
		String output1 = Query.makeQuery(uriString());
		System.out.println(output1);
	}

	
	//This method builds the URI String from your Query
	public static String uriString() throws URISyntaxException {
		
		String uriString = "";
		
		AdvancedQuery advQuery = new AdvancedQuery(new Query (query, queryType));
	    
	    URIBuilder builder = new URIBuilder()
	    		.setScheme("https")
	            .setHost(Query.setProperties("hostURL"))
	            .setParameter("advancedQuery", advQuery.advancedQuery2JSON())
	    		.setParameter("pageNumber", "1")
	    		.setParameter("pageSize", "1")
	    		.setParameter("orderBy", "created asc");
	    
	    URI uri = builder.build();
	    uriString = uri.toString();
	    
//	    System.out.println(uriString);
	    return uriString;
	    
	} 
}
	



