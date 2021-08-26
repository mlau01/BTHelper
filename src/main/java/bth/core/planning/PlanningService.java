package bth.core.planning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.BTHelper;
import bth.core.MONTH;
import bth.core.exception.HttpConnectionException;
import bth.core.exception.PlanningCharsetException;
import bth.core.exception.PlanningConnectionException;
import bth.core.exception.PlanningException;
import bth.core.options.OptionException;
import bth.core.options.OptionService;

public class PlanningService {
	
	private final ArrayList<Planning> planList;
	
	private final TechnicianManager tecMan;
	private OptionService optionService;
	private final static Logger logger = LogManager.getLogger();
	private IPlanningConnection planningConnection;
	
	public PlanningService(OptionService p_optionService) throws OptionException
	{
		optionService = p_optionService;
		planList = new ArrayList<Planning>();
		tecMan = new TechnicianManager();
		if(optionService.get(BTHelper.HttpUrl).startsWith("http")) {
			logger.debug("Protocol http detected, using PlanningHttpConnection");
			planningConnection = new PlanningHttpConnection();
		} else if (optionService.get(BTHelper.HttpUrl).startsWith("file")){
			logger.debug("Protocol file detected, using PlanningFileConnection");
			planningConnection = new PlanningFileConnection();
		}
	}

	public String buildUrl(final String hostname, final MONTH month) {
		String extension = ".htm";
		if(hostname.endsWith("/")){
			return hostname + month.toString() + extension;
		}
		else {
			return hostname + "/" + month.toString() + extension;
		}
	}
	
	public Planning get(final MONTH month) throws PlanningException, OptionException
	{
		logger.info("get(...) -> Request for month: " + month + "...");
		//Search existing planning in memory
		for(final Planning planning : planList) {
			if(planning.getMonth() == month) return planning;
		}
		
		//At this stage, no planning was founded in memory, so we try to get it by other way
		logger.info("Getting new planning : " + month);
		
		PlanningContent targetData = null;
		Planning newPlan = null;
		final String user = optionService.get(BTHelper.HttpUser);
		final String passwd = optionService.get(BTHelper.HttpPasswd);
		final String hostname = optionService.get(BTHelper.HttpUrl);
		final String useProxy = optionService.get(BTHelper.HttpUseProxy);
		String proxyHost = null;
		if(useProxy.equals("true")) {
			proxyHost = optionService.get(BTHelper.HttpProxyHost);
		}
		
		try {
			targetData = planningConnection.getTargetContent(buildUrl(hostname, month), user, passwd, proxyHost);
		} catch (PlanningConnectionException | PlanningCharsetException e)
		{
			logger.error(e.getClass().getName(), e.getMessage());
			newPlan = deserialize(month);
			if(newPlan != null) { 
				newPlan.setLocalMode();
			}
		}

		if(targetData != null)
		{
			logger.trace("Planning {} import success", month);
			logger.trace("Raw data:");
			logger.trace("{}", targetData.getContent());
			final ArrayList<ArrayList<String>> shedule = PlanningParser.getParsedArray(targetData.getContent());
			newPlan = new Planning(month, shedule, targetData.getLastModified(), Calendar.getInstance());
		}
		
		if(newPlan != null)
		{
			tecMan.add(newPlan.getTechnicians());
			planList.add(newPlan);
			serialize(newPlan);
		} else
		{
			throw new PlanningException();
		}
	
		return (newPlan);
	}
	
	private void serialize(final Planning planning) throws PlanningException
	{
		ObjectOutputStream oos = null;

		try {
			final FileOutputStream fichier = new FileOutputStream(BTHelper.CONF_DIRECTORY + "/" + planning.getMonth().toString() + ".ser");
			oos = new ObjectOutputStream(fichier);
			oos.writeObject(planning);
			oos.flush();
		} catch (final IOException e)
		{
			throw new PlanningException(e.getMessage());
		} finally {
		
			try {
			if (oos != null) {
				oos.flush();
				oos.close();
			}
			} catch (IOException e)
			{
				throw new PlanningException(e.getMessage());
			}
		}
	}
	
	/**
	 * Read the last local save of the month provided
	 * @param month
	 * @return Return a Planning object or null if the file is missing
	 */
	private final Planning deserialize(final MONTH month) throws PlanningException
	{
		String path = BTHelper.CONF_DIRECTORY + "/" + month.toString() + ".ser";
		logger.debug("Try deserialize " + path);
		Planning planning = null;
		ObjectInputStream ois = null;
		
		try {	
			final FileInputStream file = new FileInputStream(path);
			ois = new ObjectInputStream(file);
			planning = (Planning)ois.readObject();
		} catch (IOException | ClassNotFoundException e)
		{
			throw new PlanningException(e.getMessage());
		} finally
		{
			try {
				if(ois != null) ois.close();
			} catch (IOException e)
			{
				throw new PlanningException(e.getMessage());
			}
		}
	
		logger.debug("Deserialization complete of: " + path);
		return planning;
		
	}
	
	
	public TechnicianManager getTechnicianManager()
	{
		return tecMan;
	}
	
	
	public void clear()
	{
		planList.clear();
		tecMan.clear();
	}
	
	public final ArrayList<Planning> getCache()
	{
		return planList;
	}
}
