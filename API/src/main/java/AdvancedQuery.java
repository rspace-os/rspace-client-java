//This class builds correctly formatted advanced queries, given the required
//operand, queries and query types

public class AdvancedQuery {
	
	//set the operand of the advanced search (can be "and" or "or")
	public String operand;
	//set the query terms you wish to search and the query types
	public String query1;
	//Query types may be "global", "fullText", "tag", "name", "created", "lastModified", "form", "attachment", "owner"
	public String query1Type;
	public String query2;
	public String query2Type;
	
	public AdvancedQuery (String operand, String query1, String query1Type, String query2, String query2Type) {
		this.operand = operand;
		this.query1 = query1;
		this.query1Type = query1Type;
		this.query2 = query2;
		this.query2Type = query2Type;
	}
	
	public AdvancedQuery (String query1, String query1Type) {
		this.query1 = query1;
		this.query1Type = query1Type;
	}

	public String advancedQueryJSON() {
		//Formats parameters into JSON as required for running query
		String terms = "\"terms\": [ {\"query\": " + query1 + ", \"queryType\": " + query1Type + "},{\"query\": " + query2 + ", \"queryType\": " + query2Type + "} ]";
		String queryJSON = "{ \"operand\": " + operand + "," + terms + "}";
		
		return queryJSON;
	}

}
