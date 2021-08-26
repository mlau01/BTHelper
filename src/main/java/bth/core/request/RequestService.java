package bth.core.request;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bth.BTHelper;
import bth.core.datasource.DatasourceException;
import bth.core.datasource.sql.SQLManager;
import bth.core.exception.RequestException;
import bth.core.model.Query;
import bth.core.options.OptionException;
import bth.core.options.OptionService;

public class RequestService {
	
	private OptionService optionService;
	private SQLManager sqlService;
	private ArrayList<Query> queries;
	
	public RequestService(OptionService p_optionService, SQLManager p_sqlService) throws RequestException, OptionException
	{
		optionService = p_optionService;
		this.sqlService = p_sqlService;
		queries = new ArrayList<Query>();
		loadQueries();
	}
	
	public void loadQueries() throws OptionException {
		String queriesAsString = optionService.get(BTHelper.Queries);
		if(queriesAsString.isEmpty()) {
			return;
		}
		
		String[] parsedQueries = queriesAsString.split(";");
		for(String queryFull : parsedQueries) {
			String[] querySplit = queryFull.split("#");
			String queryTitle = querySplit[0];
			String queryValue = querySplit[1];
			queries.add(new Query(queryTitle, queryValue));
		}
	}
	
	public ResultSet execQuery(String query) throws DatasourceException {
		return sqlService.getResultSet(query);
	}


	/**
	 * Get a the queries title list
	 * @return empty list if no queries was registered yet
	 * @throws OptionException if something goes wrong with the queries recovering process
	 */
	public List<String> getQueriesTitle() throws OptionException {
		ArrayList<String> queriesTitle = new ArrayList<String>();
		
		for(Query query : queries) {
			queriesTitle.add(query.getTitle());
		}
		
		return queriesTitle;
	}
	
	/**
	 * Get a query
	 * @param title of the query to get
	 * @return query value if found
	 * @throws OptionException
	 * @throws RequestException
	 */
	public String getQueryValue(String title) throws OptionException, RequestException {
		for(Query query : queries) {
			if(query.getTitle().equals(title)) {
				return query.getValue();
			}
		}
		
		throw new RequestException("Not found query title: " + title);
	}

	/**
	 * Write a query to persist it
	 * @param title
	 * @param value
	 * @throws RequestException if a query with this title already exist
	 * @throws OptionException
	 */
	public void writeQuery(String title, String value) throws RequestException, OptionException {
		Query newQuery = new Query(title, value);
		if(value.contains("#") || value.contains(";")) {
			throw new RequestException("forbidden characters found: ';' or '#'");
		}
		for(Query query : queries) {
			if(query.getTitle().equals(title)) {
				throw new RequestException("A query with this title already exists");
			}
		}
		queries.add(newQuery);
		saveQueries();
		
	}
	
	/**
	 * Persist all queries using OptionService
	 * @throws OptionException
	 */
	public void saveQueries() throws OptionException {
		String queriesAsString = "";
		
		for(Query query : queries) {
			if( ! queriesAsString.isEmpty()) {
				queriesAsString += ";";
			}
			
			queriesAsString += query.getTitle();
			queriesAsString += "#";
			queriesAsString += query.getValue();
		}
		
		optionService.set(BTHelper.Queries, queriesAsString);
	}

	public void delQuery(String queryTitle) throws RequestException, OptionException {
		Query queryToDelete = null;
		for(Query query : queries) {
			if(query.getTitle().equals(queryTitle)) {
				queryToDelete = query;
			}
		}
		if(queryToDelete == null) {
			throw new RequestException("No query found with this title");
		}
		
		queries.remove(queryToDelete);
		saveQueries();
		
	}

}
