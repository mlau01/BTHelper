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
import bth.core.exception.PlanningDeserializeException;
import bth.core.exception.PlanningException;
import bth.core.exception.PlanningSerializeException;
import bth.core.options.OptionException;
import bth.core.options.OptionService;

public class PlanningService {
	
	private final ArrayList<Planning> planList;
	
	private final TechnicianManager tecMan;
	private OptionService optionService;
	private final static Logger logger = LogManager.getLogger();
	private IPlanningConnection planningConnection;
	private ISerializePlanningService serializePlanningService;
	
	public PlanningService(OptionService p_optionService) throws OptionException, PlanningException
	{
		optionService = p_optionService;
		planList = new ArrayList<Planning>();
		tecMan = new TechnicianManager();
		String path = optionService.get(BTHelper.HttpUrl);
		if(path.startsWith("http")) {
			logger.debug("Protocol http detected, using PlanningHttpConnection");
			planningConnection = new PlanningHttpConnection();
		} else if (path.startsWith("file")){
			logger.debug("Protocol file detected, using PlanningFileConnection");
			planningConnection = new PlanningFileConnection();
		}
		else {
			logger.error("Unknown protocol used in address: {}", path);
		}
		
		serializePlanningService = new JsonPlanningService();
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
	
	public Planning get(final MONTH month) throws PlanningException, OptionException, PlanningDeserializeException
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
			logger.error(e.getMessage());
			newPlan = serializePlanningService.deserialize(BTHelper.CONF_DIRECTORY + "/" + month.toString() + ".json");
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
			try {
				serializePlanningService.serialize(newPlan, BTHelper.CONF_DIRECTORY + "/" + month.toString() + ".json");
			} catch (PlanningSerializeException e) {
				logger.error("Serialization error: {}", e.getMessage());
			}
		} else
		{
			throw new PlanningException();
		}
	
		return (newPlan);
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
