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
import bth.core.exception.SheduleServiceException;
import bth.core.options.OptionException;
import bth.core.options.OptionService;
import bth.core.planning.PlanningManager;
import bth.core.planning.Technician;
import bth.core.request.RequestException;
import bth.core.request.RequestManager;
import bth.core.schedule.ScheduleService;
import mack.MackConnection;
import mack.exception.MaximoConnectionException;
import bth.core.planning.PlanningException;
import bth.core.planning.HttpConnectionException;
import bth.core.planning.Planning;

public class CoreManager implements Observable {

	private final Properties properties;
	private final PlanningManager planningService;
	private final BtService btMan;
	private Datasource DBMan;
	private final RequestManager reqMan;
	private final ArrayList<Observer> observers;
	private final OptionService optionService;
	private final ScheduleService scheduleService;
	
	public CoreManager() throws Exception
	{

		this.optionService = new OptionService(BTHelper.CONF_DIRECTORY + "/" + BTHelper.CONF_NAME);
		optionService.loadConfig();

		properties = optionService.getCurrentProperties();
		observers = new ArrayList<Observer>();
		planningService = new PlanningManager(properties);
		
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
		btMan = new BtService(properties, observers, DBMan, planningService, scheduleService);
		
		reqMan = new RequestManager();
		
	}
	// ---- Request methods ----
	
	public final ResultSet request_execQuery(final String params) throws DatasourceException
	{
		if(DBMan instanceof SQLManager)
			return ((SQLManager) DBMan).getResultSet(params);
		return null;
	}
	public final ArrayList<String> request_getList()
	{
		return reqMan.getQueryList();
	}
	public final void request_writeQuery(final String name, final String query) throws RequestException
	{
		reqMan.writeQuery(name, query);
	}
	public final String request_getQuery(final String name)
	{
		return reqMan.getQuery(name);
	}
	public final void request_delQuery(final String name) throws RequestException
	{
		reqMan.delQuery(name);
	}
	
	// ---- Options methods ----
	
	public final Properties getProperties()
	{
		return optionService.getCurrentProperties();
	}
	
	public final OptionService getOptionService() {
		return optionService;
	}
	
	// ---- Planning methods ----
	
	public final ArrayList<String> planning_get_cacheList()
	{
		final ArrayList<String> list = new ArrayList<String>();
		for(final Planning plan : planningService.getCache()) list.add(plan.getMonth().toString());
		return list;
	}
	
	public final ArrayList<ArrayList<String>> planning_get_array(final String sMonth)
			throws HttpConnectionException, IOException, PlanningException
	{
		MONTH month = MONTH.getByName(sMonth);
		final Planning plan = planningService.get(month);

		return plan.getArray();
	}
	
	public final long planning_get_lastModified(final String sMonth) 
			throws HttpConnectionException, IOException, PlanningException
	{
		final MONTH month = MONTH.getByName(sMonth);
		final Planning plan = planningService.get(month);

		return plan.getLastModified();
	}
	
	
	public final boolean planning_isLocal(final String sMonth) 
			throws HttpConnectionException, IOException, PlanningException
	{
		final MONTH month = MONTH.getByName(sMonth);
		final Planning plan = planningService.get(month);
		
		return plan.isLocal();
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
	
	// ---- Bt methods ----
	
	public final ArrayList<String[]> bt_get_rawList(final String dbFilepath) throws DatasourceException
	{	
		final ArrayList<Bt> btList = btMan.getRawBt(dbFilepath);

		final ArrayList<String[]> btArray = new ArrayList<String[]>();
		for(final Bt bt : btList)
		{
			final String[] btLine = {bt.getWonum(), bt.getDate(), bt.getDesc()};
			btArray.add(btLine);
		}
		
		return btArray;
	}
	
	public final void bt_assign(final String dbFilepath) throws BTException, SheduleServiceException
	{
		btMan.assign(planningService, dbFilepath);
	}
	
	public void w(ArrayList<Bt> btList) throws MaximoConnectionException, IOException, InterruptedException
	{
		MackConnection max = new MackConnection(
				properties.getProperty(BTHelper.MaximoUrl),
				properties.getProperty(BTHelper.MaximoLogin),
				properties.getProperty(BTHelper.MaximoPassword)
		);
		
		HttpURLConnection login = max.login();
		max.extractLoginInformations(login);
	
		for(Bt bt : btList)
		{
			if(bt.isW()) {
				max.loadBtContext();
				System.out.println("Start filling BT:" + bt.getWonum());
				max.fillBt(bt.getWonum(),
					bt.getGear(),
					bt.getDate(),
					bt.getDesc(),
					bt.getNewDesc(), 
					bt.getTravelTime(),
					bt.getDuration(),
					bt.getIssue(),
					bt.getComment());
				bt.setW(false);
			}
		}
	}
	public void p(String[] args) throws MaximoConnectionException, IOException, InterruptedException
	{
		MackConnection max = new MackConnection(
				properties.getProperty(BTHelper.MaximoUrl),
				properties.getProperty(BTHelper.MaximoLogin),
				properties.getProperty(BTHelper.MaximoPassword)
		);
		
		HttpURLConnection login = max.login();
		max.extractLoginInformations(login);
		max.loadBtContext();
		max.doPrev(args);
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
}
