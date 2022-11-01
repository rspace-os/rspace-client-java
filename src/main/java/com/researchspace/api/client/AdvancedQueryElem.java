package com.researchspace.api.client;

/**
 * constructor for a query object comprised of the query term and the query type
 */
public class AdvancedQueryElem {

    private final String query;
    private final String queryType;

    public AdvancedQueryElem (String query, String queryType) {
        this.query = query;
        this.queryType = queryType;
    }

    public String getQuery() {
        return query;
    }

    public String getQueryType() {
        return queryType;
    }

}
