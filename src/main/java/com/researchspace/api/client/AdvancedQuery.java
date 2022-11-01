package com.researchspace.api.client;

/**
 * This class builds correctly formatted advanced queries, given the required
 * operand, queries and query types
 */
public class AdvancedQuery {
    
    public static final String OPERATOR_AND = "and";
    public static final String OPERATOR_OR = "or";

    private String operator = OPERATOR_AND;
    private final AdvancedQueryElem[] queries;

    /** constructor for setting multiple query terms and operand */
    public AdvancedQuery (String operator, AdvancedQueryElem... queries){
        this.operator = operator;
        this.queries = queries;
    }
    
    /** constructor for setting multiple query terms with default operand */
    public AdvancedQuery (AdvancedQueryElem... queries){
        this(OPERATOR_AND, queries);
    }
    
    protected String toJSON() {
        StringBuilder queryJSON = new StringBuilder();
        String[] queryTerms = new String[queries.length];
        
        // format individual queries into JSON
        for (int i = 0; i < queries.length; i++) {
            queryTerms[i] = "{\"query\": \"" + queries[i].getQuery()
                    + "\", \"queryType\": \"" + queries[i].getQueryType()
                    + "\" }";
        }
        
        // add commas between multiple queries
        for (int i = 0; i < queryTerms.length - 1; i++) {
            queryTerms[i] = queryTerms[i] + ", ";
            queryJSON.append(queryTerms[i]);
        }
        queryJSON.append(queryTerms[queryTerms.length - 1]);
        
        // finally add square brackets
        queryJSON = new StringBuilder("\"terms\": [ " + queryJSON + " ]");
        
        // add operand if it is valid
        if (queryTerms.length > 1 && (operator.equals("and") || operator.equals("or"))) {
            queryJSON = new StringBuilder("{ \"operator\": \"" + operator + "\", " + queryJSON + " }");
        } else {
            queryJSON = new StringBuilder("{ " + queryJSON + " }");
        }

        return queryJSON.toString();
    }

}
