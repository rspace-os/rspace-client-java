import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

//constructor for a query object comprised of the query term and the query type

public class Query {
	
	public String query;
	public String queryType;
	
	public Query (String query, String queryType) {
		this.query = query;
		this.queryType = queryType;
	}
	
	public String getQuery() {
		return query;
	}
	
	public String getQueryType() {
		return queryType;
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
}
