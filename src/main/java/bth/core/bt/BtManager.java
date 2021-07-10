package bth.core.bt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.BTHelper;
import bth.Observable;
import bth.Observer;
import bth.core.datasource.DatasourceException;
import bth.core.datasource.Datasource;
import bth.core.planning.PlanningManager;
import bth.core.planning.Technician;

public class BtManager implements Observable{
	
	private ArrayList<Bt> bts;
	private final Properties prop;
	private final Datasource DBMan;
	private final ArrayList<Observer> observers;
	private final static Logger logger = LogManager.getLogger();
	
	public BtManager(final Properties p_prop, final ArrayList<Observer> p_observers, final Datasource p_DBMan)
	{
		logger.trace("INIT");
		
		prop = p_prop;
		observers = p_observers;
		DBMan = p_DBMan;
	}
	
	public final ArrayList<Bt> getRawBt(final String dbFilepath) throws DatasourceException
	{	
		String params = null;
		if(prop.getProperty(BTHelper.FileUsed).equals("true")) params = dbFilepath;
		else if(prop.getProperty(BTHelper.SqlUsed).equals("true")) params = prop.getProperty(BTHelper.SqlRequest);
	
		//Get datas and create bts
		bts = DBMan.getBts(params);

		return bts;
	}
	
	public final void assign(final PlanningManager pMan, final String dbFilepath) throws BTException
	{
		//Clear planning cache
		clear(pMan);

		//Assign all bts to virtual technician "All"
		ArrayList<Bt> btList;
		try {
			btList = getRawBt(dbFilepath);
		} catch (DatasourceException e) {
			e.printStackTrace();
			throw new BTException(e.getMessage());
		}
		Technician all = pMan.getTechnicianManager().getTechnician("All");
		all.getBtList().addAll(btList);
		
		//Assign bt to the target Technician
		for(final Bt bt : btList) {
			logger.info(" **** Searching tech for BT {} ... ***", bt.getWonum());
			Technician tech = BtAssignator.searchTech(pMan, parseDate(bt.getDate()), getTermFromDesc(bt.getDesc()));
			if(tech != null) logger.debug("Tech found: {}", tech.getName());
			if(tech == null) {
				logger.debug("tech not found for wonum= {}, turn in 'NOT FOUND'", bt.getWonum());
				tech = pMan.getTechnicianManager().getTechnician("NOT FOUND");
			}
	
			tech.getBtList().add(bt);		
		}
		notifyObserver("List at: " + new SimpleDateFormat("dd MMMM (HH:mm:ss)").format(Calendar.getInstance().getTime()));

	}
	
	private final void clear(final PlanningManager pMan)
	{
		//pMan.getTechnicianManager().resetAllBts();
		pMan.clear();
	}
	
	private final String getTermFromDesc (String btDesc)
	{
		if(btDesc == null)
			return "NF";
		
		btDesc = btDesc.toLowerCase();
		String res = null;
		if(btDesc.contains("t1"))
			res = "T1";
		else if(btDesc.contains("t2"))
			res = "T2";
		else if(btDesc.contains("e2"))
			res = "T2";
		else if(btDesc.contains("t21"))
			res = "T2";
		else if(btDesc.contains("t2.1"))
			res = "T2";
		else if(btDesc.contains("t22"))
			res = "T2";
		else  {
			res = "NFT";
			logger.error("getTermFromDesc : cannot find terminal in desc '{}', return NFT error", btDesc);
		}
		return res;
	}
	
	private final GregorianCalendar parseDate(String date) throws BTException
	{
		logger.trace("parseDate(" + date + ")");
	
		final GregorianCalendar cal = new GregorianCalendar();
		try {
			cal.setTime(new SimpleDateFormat(DBMan.getDateFormat()).parse(date));
		} catch (ParseException e) {
			throw new BTException(e.getMessage());
		}

		logger.trace("result: {}", new SimpleDateFormat("dd/MM/YYYY").format(cal.getTime()));
		return cal;
	}

	@Override
	public void addObserver(Observer obs) {
		this.observers.add(obs);
	}

	@Override
	public void notifyObserver(String notification) {
		for(Observer obs : observers)
			obs.notify(notification);
		
	}

	@Override
	public void removeObserver(Observer obs) {
		this.observers.remove(obs);
	}

}
