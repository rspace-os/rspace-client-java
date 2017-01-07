//This class builds correctly formatted advanced queries, given the required
//operand, queries and query types

public class AdvancedQuery {
	
	//set the operand of the advanced search (can be "and" or "or", default is "and")
	public String operand = "and";
	//set the query terms you wish to search and the query types
	public Query queries[];
	public Query query;
	
	//constructor for multiple Advanced Queries
	public AdvancedQuery (String operand, Query... queries){
		this.operand = operand;
		this.queries = queries;
	}
	
	//constructor for a single Advanced Query
	public AdvancedQuery (Query... queries){
		this.queries = queries;
	}
	
	public String advancedQuery2JSON() {
		String queryJSON = "";
		String queryTerms[] = new String[queries.length];
		
		//format individual queries into JSON
		for(int i=0; i<queries.length; i++){
			queryTerms[i] = "{\"query\": \"" + queries[i].getQuery() + "\", \"queryType\": \"" + queries[i].getQueryType() + "\" }";
		}
		
		//add commas between multiple queries
		for(int i=0; i<queryTerms.length-1; i++){
			queryTerms[i] = queryTerms[i] + ", ";
			queryJSON = queryJSON + queryTerms[i];
		}
		queryJSON = queryJSON + queryTerms[queryTerms.length-1];
		
		//finally add square brackets
		queryJSON = "\"terms\": [ " + queryJSON + " ]";
		
		//Add operand if it is valid
		if(queryTerms.length > 1 && (operand.equals("and")||operand.equals("or"))){
			queryJSON = "{ \"operand\": " + operand + ", " + queryJSON + " }";
		}
		else {
			queryJSON = "{ " + queryJSON + " }";
		}
//		System.out.println(queryJSON);
		return queryJSON;
	}
}
