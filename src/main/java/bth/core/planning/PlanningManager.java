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
import bth.core.CoreManager;
import bth.core.MONTH;

public class PlanningManager {
	
	private short verboseLevel = 0;
	private final ArrayList<Planning> planList;
	
	private final TechnicianManager tecMan;
	private final CoreManager interact;
	private final static Logger logger = LogManager.getLogger();
	
	public PlanningManager(final CoreManager p_interact)
	{
		if(verboseLevel >= 2) System.out.println(this.getClass().getName() + " -> INIT");
		
		interact = p_interact;
		
		planList = new ArrayList<Planning>();
		tecMan = new TechnicianManager();
	}
	
	public Planning get(final MONTH month) throws PlanningException
	{
		logger.info("get(...) -> Request for month: " + month + "...");
		//Search existing planning in memory
		for(final Planning planning : planList) {
			if(planning.getMonth() == month) return planning;
		}
		
		//At this stage, no planning was founded in memory, so we try to get it by other way
		logger.info("Getting new planning : " + month);
		
		final Properties conf = interact.getProperties();
		
		HttpContent targetData = null;
		Planning newPlan = null;
		try {
			final String user = conf.getProperty(BTHelper.HttpUser);
			final String passwd = conf.getProperty(BTHelper.HttpPasswd); 
			final String site = conf.getProperty(BTHelper.HttpUrl);
			final String useProxy = conf.getProperty(BTHelper.HttpUseProxy);
			String proxyHost;
			if(useProxy.equals("true")) proxyHost = conf.getProperty(BTHelper.HttpProxyHost);
			else proxyHost = null;
			
			String url = site + month.toString() + ".htm";
			
			targetData = HttpConnection.getTargetContent(url, user, passwd, proxyHost);
		} catch (HttpConnectionException e)
		{
			logger.error("Cannot reach target: ", e.getMessage());
			newPlan = deserialize(month);
			if(newPlan != null) newPlan.setLocalMode();
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
		
		Planning planning = null;
		ObjectInputStream ois = null;
		
		try {	
			final FileInputStream file = new FileInputStream(BTHelper.CONF_DIRECTORY + "/" + month.toString() + ".ser");
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
