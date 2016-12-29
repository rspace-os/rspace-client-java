import java.util.ArrayList;

//This class builds correctly formatted advanced queries, given the required
//operand, queries and query types

public class AdvancedQuery2 {
	
	//set the operand of the advanced search (can be "and" or "or")
	public String operand;
	//set the query terms you wish to search and the query types
	public ArrayList<Query> queries = new ArrayList<Query>();
	public Query query;
	
	public AdvancedQuery2 (String operand, ArrayList<Query> queries){
		this.operand = operand;
		this.queries = queries;
	}
	
	public AdvancedQuery2 (Query query){
		this.query = query;
	}
	
	public String advancedQuery2JSON() {
		String queryJSON = "";
		ArrayList<String> queryTerms = new ArrayList<String>();
		
		for(Query query: queries){
			queryTerms.add("{\"query\": " + query.getQuery() + ", \"queryType\": " + query.getQueryType() + "}");
		}
		
		for(int i=0; i<queryTerms.size() - 1; i++){
			queryTerms.set(i, queryTerms.get(i) + ", ");
			queryJSON = queryJSON + queryTerms.get(i);
		}
		queryJSON = queryJSON + queryTerms.get(queryTerms.size()-1);
		
		queryJSON = "\"terms\": [ " + queryJSON + " ]";
		
		if(queryTerms.size() > 1){
			queryJSON = "{ \"operand\": " + operand + ", " + queryJSON + " }";
		}
		
		System.out.println(queryJSON);
		return queryJSON;
		
	}
}
