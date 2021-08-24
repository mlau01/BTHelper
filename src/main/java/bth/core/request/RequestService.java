package bth.core.request;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bth.BTHelper;
import bth.core.datasource.DatasourceException;
import bth.core.datasource.sql.SQLManager;
import bth.core.exception.RequestException;
import bth.core.options.OptionException;
import bth.core.options.OptionService;

public class RequestService {
	
	private OptionService optionService;
	private SQLManager sqlService;
	
	public RequestService(OptionService p_optionService, SQLManager p_sqlService) throws RequestException, OptionException
	{
		optionService = p_optionService;
		this.sqlService = p_sqlService;
			
	}
	
	
	/*
	public final ArrayList<String> getQueryList()
	{
		final ArrayList<String> out =  new ArrayList<String>();
		for(final Object o : queryFile.keySet())
		{
			out.add((String)o);
		}
		
		return out;
	}
	
	public final void writeQuery(final String name, final String query) throws RequestException
	{
		if(queryFile != null)
		{
			queryFile.setProperty(name, query);
			write();
		}
	}
	public final void delQuery(final String name) throws RequestException
	{
		if(queryFile == null) return;
		
		queryFile.remove(name);
		write();
	}
	public final String getQuery(final String name)
	{
		if(queryFile != null) return queryFile.getProperty(name);
		
		return null;
	}
*/
	public ResultSet execQuery(String query) throws DatasourceException {
		return sqlService.getResultSet(query);
	}


	public List<String> getQueryList() throws OptionException {
		ArrayList<String> queryNames = new ArrayList<String>();
		
		String queriesAsString = optionService.get(BTHelper.Queries);
		if(queriesAsString.isEmpty()) {
			return queryNames;
		}
		
		String[] parsedQueries = queriesAsString.split(";");
		for(String query : parsedQueries) {
			String queryName = query.split("=")[0];
			queryNames.add(queryName);
		}
		
		return queryNames;
	}

}
