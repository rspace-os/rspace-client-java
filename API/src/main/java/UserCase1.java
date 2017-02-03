import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCase1 {

	public static String output, output2, resultsString;
	public static ArrayList<Long> docIDs = new ArrayList<Long>();
	public static ArrayList<Float> fieldValues = new ArrayList<Float>();
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		
		String output1 = Query.makeQuery(uriString());
		System.out.println(output1);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode results = mapper.readTree(output1);
		for(JsonNode document : results.path("documents")) {
			String documentID = document.path("id").asText();
			docIDs.add(Long.parseLong(documentID));
			System.out.println(documentID);
		}
		
		for(long docID: docIDs){
			String requestURL = Library.getAPIDocumentUrl(docID);
			output2 = Query.makeQuery(requestURL);
			
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
		
		//UriBuilder.contruct_uri_parameters_apache();
	}
	
	//This method builds the URI String from your Query
	public static String uriString() throws URISyntaxException {
		
		AdvancedQuery advQuery = new AdvancedQuery(new Query ("TestForm", "form"));
	    URIBuilder builder = new URIBuilder(Library.getAPIDocumentsUrl())
	            .setParameter("advancedQuery", advQuery.advancedQuery2JSON());
	    
	    URI uri = builder.build();
	    return uri.toString();
	    
	}
}

