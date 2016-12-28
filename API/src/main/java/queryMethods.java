import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

public class queryMethods {
	

	public String makeQuery() {
		
		String output = "";
		
		try {
			output = (Request.Get("https://pangolin.researchspace.com:8085/api/v1/documents?orderBy=lastModified%20desc&advancedQuery=%7B%22terms%22%3A%20%5B%20%7B%22query%22%3A%20%22TestForm%22%2C%20%22queryType%22%3A%20%22form%22%7D%5D%7D")
					.addHeader("apiKey",UserCase1.apiKey)
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
	
/*	public ArrayList<Long> loopThroughResults() {
	
		ArrayList<Long> docIDs = new ArrayList<Long>();
	
		ObjectMapper mapper = new ObjectMapper();
		JsonNode results = mapper.readTree(output);
		for(JsonNode document : results.path("documents")) {
			String documentID = document.path("id").asText();
			docIDs.add(Long.parseLong(documentID));
			//	System.out.println(documentID);
		}
		return docIDs;
	}*/
	
	

}
