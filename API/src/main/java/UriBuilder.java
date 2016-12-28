//This class builds the correctly formatted search URI given a JSON query,
//a host URL and the search parameters

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;


public class UriBuilder {
	
	public static String uriString() throws URISyntaxException {
		
		String uriString = "";
		
		AdvancedQuery advQuery = new AdvancedQuery("and", "protein", "term", "pcr", "term");
	    
	    URIBuilder builder = new URIBuilder()
	        //    .setScheme("http")
	            .setHost("https://pangolin.researchspace.com:8085/api/v1/documents")
	            .setParameter("advancedQuery", advQuery.advancedQueryJSON());
	    
	    URI uri = builder.build();
	    uriString = uri.toString();
	    
	    return uriString;
	    
	}

}
