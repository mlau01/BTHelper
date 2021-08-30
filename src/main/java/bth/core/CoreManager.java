package bth.core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;

import bth.BTHelper;
import bth.Observable;
import bth.Observer;
import bth.core.bt.Bt;
import bth.core.bt.BtService;
import bth.core.datasource.DatasourceException;
import bth.core.datasource.Datasource;
import bth.core.datasource.file.FileManager;
import bth.core.datasource.sql.SQLManager;
import bth.core.exception.BTException;
import bth.core.exception.HttpConnectionException;
import bth.core.exception.PlanningDeserializeException;
import bth.core.exception.PlanningException;
import bth.core.exception.RequestException;
import bth.core.exception.SheduleServiceException;
import bth.core.options.OptionException;
import bth.core.options.OptionService;
import bth.core.planning.PlanningService;
import bth.core.planning.Technician;
import bth.core.request.RequestService;
import bth.core.schedule.ScheduleService;
import mack.MackConnection;
import mack.exception.MaximoConnectionException;
import bth.core.planning.Planning;

public class CoreManager implements Observable {

	private final Properties properties;
	private final PlanningService planningService;
	private final BtService btService;
	private Datasource DBMan;
	private final RequestService requestService;
	private final ArrayList<Observer> observers;
	private final OptionService optionService;
	private final ScheduleService scheduleService;
	
	public CoreManager() throws Exception
	{

		this.optionService = new OptionService(BTHelper.CONF_DIRECTORY + "/" + BTHelper.CONF_NAME);
		optionService.loadConfig();

		properties = optionService.getCurrentProperties();
		observers = new ArrayList<Observer>();
		planningService = new PlanningService(optionService);
		
		if(Boolean.parseBoolean(properties.getProperty(BTHelper.SqlUsed))) {
			DBMan = new SQLManager(this, observers, properties);
		}
		else if(Boolean.parseBoolean(properties.getProperty(BTHelper.FileUsed))) {
			DBMan = new FileManager(this, observers, properties);
		}
		else if(Boolean.parseBoolean(properties.getProperty(BTHelper.MaximoUsed))) {
			//DBMan = new MaximoManager(this, observers, properties);
		}
		scheduleService = new ScheduleService(optionService);
		scheduleService.load();
		btService = new BtService(optionService, observers, DBMan, planningService, scheduleService);
		
		requestService = new RequestService(optionService, new SQLManager(this, observers, properties));
		
	}
	
	// ---- Options methods ----
	
	public final Properties getProperties()
	{
		return optionService.getCurrentProperties();
	}
	
	public final OptionService getOptionService() {
		return optionService;
	}
	
	// ---- Technician methods ----
	
	public final ArrayList<String> technician_get_list()
	{
		final ArrayList<String> list = new ArrayList<String>();
		for(final Technician tec : planningService.getTechnicianManager().getTechList()) list.add(tec.getName());
		return list;
	}

	public final ArrayList<Bt> technician_get_bts(final String userName)
	{
		final Technician tec = planningService.getTechnicianManager().getTechnician(userName);
		
		return tec.getBtList();
	}
	
	public final String technician_get_name(final String userName)
	{
		final Technician tec = planningService.getTechnicianManager().getTechnician(userName);
		
		return tec.getName();
	}
	
	
	// ---- Interfaces methods ----

	@Override
	public void addObserver(Observer obs) {
		observers.add(obs);
		
	}

	@Override
	public void notifyObserver(final String notification) {
		for(Observer obs : observers)
		{
			//obs.notify(value);
		}
		
	}

	@Override
	public void removeObserver(Observer obs) {
		observers.remove(obs);
		
	}

	public ScheduleService getScheduleService() {
		return scheduleService;
	}

	public RequestService getRequestService() {
		return requestService;
	}
	
	public PlanningService getPlanningService() {
		return planningService;
	}
	
	public BtService getBtService() {
		return btService;
	}
}
