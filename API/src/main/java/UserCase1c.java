import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;


import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;// in play 2.3

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)

public class UserCase1c {
	
	public static long docID = 24407;
	
	public static void main(String[] args) throws URISyntaxException, JsonProcessingException, IOException {
		
		
		String data = Query.makeQuery(uriString(docID));
		//System.out.println(data);
		
		ObjectMapper mapper = new ObjectMapper();
		// data here is returned from /documents
		JsonNode document = mapper.readTree(data);
		JsonNode fields = document.path("fields");
		JsonNode files = fields.path(0).path("files");
		System.out.println(files);
		
		for(JsonNode file: files){
			String filelink = file.path("_links").path(0).path("link").asText();
			System.out.println(filelink);
		}
	}
	
	public static String uriString(long docID) throws URISyntaxException {
		
		String uriString = "";
    
		URIBuilder builder = new URIBuilder()
    		.setScheme("https")
            .setHost(Query.setProperties("hostURL"));
    
		URI uri = builder.build();
		uriString = uri.toString() + "/" + docID;
    
		return uriString;
	} 

}
