import java.util.ArrayList;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		ArrayList<Query> queries = new ArrayList<Query>();
		queries.add(new Query("protein","fulltext"));
		queries.add(new Query("Protocol","name"));
		
		AdvancedQuery2 formattedQuery = new AdvancedQuery2("and", queries);
		formattedQuery.advancedQuery2JSON();

	}

}
