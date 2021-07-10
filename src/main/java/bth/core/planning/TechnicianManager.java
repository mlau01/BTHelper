package bth.core.planning;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TechnicianManager {
	
	private final ArrayList<Technician> techList;
	private final static Logger logger = LogManager.getLogger();
	
	public TechnicianManager()
	{
		logger.trace("INIT");
		techList = new ArrayList<Technician>();
		add("All");
		add("NOT FOUND");
	}
	
	public void add(final ArrayList<String> tecList)
	{
		for(final String tec : tecList)
			add(tec);
	}
	
	private void add(final String tec)
	{
		
		if(getTechnician(tec) != null) {
			logger.trace("add(String name) : " + tec + " is already in the list");
		}
		else {
			techList.add(new Technician(tec));
			logger.info("User " + tec + " added");
		}
	}
	
	public ArrayList<Technician> getTechList()
	{
		return techList;
	}
	
	public final Technician getTechnician(final String name)
	{
		for(final Technician tech : techList)
		{
			//logger.trace("getTechnician(...) : compare {} to {}",name, tech.getName());
			if(tech.getName().equals(name))
				return (tech);
		}
		
		return null;
	
	}

	/*
	public void resetAllBts()
	{
		for(final Technician tec : techList) tec.resetBt();
			
	}
	*/
	public void clear()
	{
		techList.clear();
		add("All");
		add("NOT FOUND");
	}

}
