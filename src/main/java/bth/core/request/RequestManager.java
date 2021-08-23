package bth.core.request;

import java.util.ArrayList;
import java.util.Properties;

import bth.BTHelper;
import bth.core.options.OptionException;
import bth.core.options.OptionService;

public class RequestManager {
	
	//private final CoreManager corma;
	private Properties queryFile;
	private OptionService optionService;
	
	public RequestManager() throws RequestException, OptionException
	{
		optionService = new OptionService(BTHelper.CONF_DIRECTORY + "/query.conf");
		optionService.loadConfig();
		
			try {
				optionService.loadConfig();
			} catch (OptionException e) {
				throw new RequestException(e.getMessage());
			}
	
		if(queryFile == null) {
			queryFile = new Properties();
			write();
		}
			
	}
	
	private final void write() throws RequestException {
		try {
			optionService.writePropertiesFile(optionService.getCurrentProperties());
		} catch (OptionException e) {
			throw new RequestException(e.getMessage());
		}
	}
	
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

}
