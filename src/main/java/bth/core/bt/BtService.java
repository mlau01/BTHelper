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
import bth.core.MONTH;
import bth.core.Utils;
import bth.core.datasource.Datasource;
import bth.core.datasource.DatasourceException;
import bth.core.exception.BTException;
import bth.core.exception.BtAssignmentException;
import bth.core.exception.PlanningException;
import bth.core.exception.SheduleServiceException;
import bth.core.options.OptionException;
import bth.core.options.OptionService;
import bth.core.planning.Planning;
import bth.core.planning.PlanningManager;
import bth.core.planning.PlanningParser;
import bth.core.planning.Technician;
import bth.core.schedule.ScheduleService;
import bth.core.schedule.ScheduleService.SHEDULEMODE;

public class BtService implements Observable{
	
	private ArrayList<Bt> bts;
	private final Datasource DBMan;
	private final ArrayList<Observer> observers;
	private PlanningManager planningService;
	private ScheduleService scheduleService;
	private OptionService optionService;
	private final static Logger logger = LogManager.getLogger();
	
	public BtService(final OptionService p_optionService, final ArrayList<Observer> p_observers, final Datasource p_DBMan, PlanningManager p_planningService, ScheduleService p_scheduleService)
	{
		logger.trace("INIT");
		this.optionService = p_optionService;
		observers = p_observers;
		DBMan = p_DBMan;
		this.planningService = p_planningService;
		this.scheduleService = p_scheduleService;
	}
	
	/**
	 * Browse all bts to assign them to a technician
	 * By default all bts are assigned to virtual technician "All"
	 * All bts that can't be assigned to a technician are assigned to virtual "NOT FOUND"
	 * @param dbFilepath
	 * @throws OptionException
	 * @throws BTException
	 * @throws DatasourceException
	 * @throws ParseException
	 */
	public final void assign(final String dbFilepath) throws OptionException, BTException, DatasourceException, ParseException
	{
		//Clear planning cache
		planningService.clear();
	
		//Assign all bts to virtual technician "All"
		ArrayList<Bt> btList = getRawBt(dbFilepath);

		Technician all = planningService.getTechnicianManager().getTechnician("All");
		all.getBtList().addAll(btList);
		
		//Assign bt to the target Technician
		for(final Bt bt : btList) {
			logger.info(" **** Searching tech for BT {} ... ***", bt.getWonum());
			
			Technician tech = null;
			try {
				tech = searchTech(bt.getDate(), bt.getDesc());
				logger.info("Tech found: {}", tech.getName());
			} catch (SheduleServiceException | PlanningException | BtAssignmentException e) {
				logger.info("tech not found for wonum= {}, turn in 'NOT FOUND'", bt.getWonum());
				tech = planningService.getTechnicianManager().getTechnician("NOT FOUND");
			} finally {
				tech.getBtList().add(bt);
			}
				
		}
		notifyObserver("List at: " + new SimpleDateFormat("dd MMMM (HH:mm:ss)").format(Calendar.getInstance().getTime()));
	
	}

	/**
	 * Start the research for a technician with date and terminal
	 * @param p_btDate GreorianCalendar date
	 * @param p_terminal String terminal
	 * @return Technician object
	 * @throws SheduleServiceException 
	 * @throws PlanningException 
	 * @throws ParseException 
	 * @throws BtAssignmentException 
	 */
	public final Technician searchTech(String btDateString, final String btDesc) throws SheduleServiceException, PlanningException, ParseException, BtAssignmentException
	{	
		//Retrieve the month corresponding to the bt date
		final GregorianCalendar btDate = new GregorianCalendar();
		btDate.setTime(new SimpleDateFormat(DBMan.getDateFormat()).parse(btDateString));
		
		logger.info("searchTech -> Search technician for bt date: " + new SimpleDateFormat("dd/MM/YYYY HH:mm").format(btDate.getTime()));
		
		final int btMonthNum = btDate.get(GregorianCalendar.MONTH);
		final MONTH btMonth = MONTH.getByIndex(btMonthNum);
		
		String terminal = getTermFromDesc(btDesc);
		
		//Trying to get witch assignation match to the terminal and BT date
		String assign = scheduleService.getCorrectSheduleAcronym(terminal, btDate, SHEDULEMODE.NORMAL, false);
		
		//Trying to get tech
		String tech = getTech(planningService, btMonth, btDate, assign);
		
		if(tech.equals("NOT FOUND"))
		//If tech was not found with normal assignation, trying with SUPER assignation...
		{
			assign = scheduleService.getCorrectSheduleAcronym(terminal, btDate, SHEDULEMODE.SUPER, false);
			tech = getTech(planningService, btMonth, btDate, assign);
		}
		
		if(tech.equals("NOT FOUND"))
		//If tech was always not found, try to process like a weedend day, this case can happen in holyday 
		{
			assign = scheduleService.getCorrectSheduleAcronym(terminal, btDate, SHEDULEMODE.NORMAL, true);
			tech = getTech(planningService, btMonth, btDate, assign);
			
		}
		
		//Return technician model based on the finded String
		return planningService.getTechnicianManager().getTechnician(tech);
	}
	
	public final String getTech(final PlanningManager planningService, final MONTH btMonth, final GregorianCalendar btDate, final String assign)
			throws PlanningException
	{	
		//Get the planning corresponding to the date of BT
		Planning planning = planningService.get(btMonth);

		//Start searching at the end of planning if the bt data >= 15
		String tech = null;
		MONTH targetMonth;
		SEARCH searchIn;
		// 2016-05-19 Modifying if below by: >= before: >
		if(btDate.get(GregorianCalendar.DAY_OF_MONTH) > 15) {
			searchIn = SEARCH.END; 
		}
		else {
			searchIn = SEARCH.START;
		}
		
		tech = getTechAt(btDate, searchIn, assign, planning.getArray());
		
		logger.info("Looking tech assignation: {} in month {} for date {}", assign, btMonth, new SimpleDateFormat("dd/MM/YYYY HH:mm").format(btDate.getTime()));
		
		if(tech.equals("NFD"))
		{
			logger.info("getTech(...): receive NFD code, trying to get day in next planning...");
			if(btDate.get(GregorianCalendar.DAY_OF_MONTH) < 15) {
				targetMonth = MONTH.getPrevMonth(btMonth);
				 searchIn = SEARCH.END;
			}
			else {
				targetMonth = MONTH.getNextMonth(btMonth);
				searchIn = SEARCH.START;
			}
			
			planning = planningService.get(targetMonth);
			
			tech = getTechAt(btDate, searchIn, assign, planning.getArray());
		}
		
		
		return (tech);
	}
	
	public final String getTechAt(final GregorianCalendar btCalDate, final SEARCH search, final String assign, final ArrayList<ArrayList<String>> dstMonthArrayList)
	{
		int col = 0;
		String tech = null;
		
		col = getDayColumnNumber(btCalDate, search, dstMonthArrayList);
		if(col == -1)
		{
			logger.error("day {} not found in array {}", btCalDate.get(GregorianCalendar.DAY_OF_MONTH), dstMonthArrayList);
			return ("NFD");
		}
		
		tech = findAssignedTech(col, assign, dstMonthArrayList);
		
		return (tech);
	}
	
	public final int getDayColumnNumber(final GregorianCalendar cal, final SEARCH search, final ArrayList<ArrayList<String>> dstMonthArrayList)
	{
		if(dstMonthArrayList == null)
		{
			return -10;
		}
		ArrayList<String> days = dstMonthArrayList.get(0);
		//TODO CONSTANT FOR NUMBER OF DAY POSITION IN TABLE
		int day = 0;
		int dayColIndex = 0;
		int startSearch = 0, endSearch = 0;
		int dayToFind = cal.get(GregorianCalendar.DAY_OF_MONTH);
		
		switch(search)
		{
			case ENTIRE :
				startSearch = 0;
				endSearch = days.size();
			break;
			case START :
				startSearch = 0;
				endSearch = 24;			//2016-07-14: previous value: 20, modified for fixing bug "loop was not able to reach value 14 in the array"
			break;
			case END :
				startSearch = 10;
				endSearch = days.size();
				
			break;
		}

		for(int i = startSearch; i < endSearch; i++)
		{
			day = Utils.parseInt(days.get(i));
			logger.trace("Parsing planning day: " + days.get(i));
			
			if(dayToFind == day) {
				
				return (startSearch + dayColIndex);
			}
			else
				dayColIndex++;
		}
		
		logger.info("getDayColumnNumber(...): Failed to retrieve column index for this date");
		return -1;
	}
	
	public final String findAssignedTech(final int col, final String assign, final ArrayList<ArrayList<String>> dstMonthArrayList)
	{
		String tech = "NOT FOUND";
		if(dstMonthArrayList != null)
		{
			for(int i = 0; i < dstMonthArrayList.size(); i++)
			{
				ArrayList<String> line = dstMonthArrayList.get(i);
				String cell = line.get(col);
				logger.trace("Searching assignation {} in col {} for tech line {} ", assign, col, line);
				if(cell.equals(assign)) {
					tech = PlanningParser.extractTechnicians(line);
				}
			}
		}
		else
		{
			logger.info("findAssignedTech(...) -> Month table is null, return NO PLANNING");
			return "NO PLANNING";
		}
		return tech;
	}
	
	public final ArrayList<Bt> getRawBt(final String dbFilepath) throws DatasourceException, OptionException
	{	
		String params = null;
		if(optionService.get(BTHelper.FileUsed).equals("true")) {
			params = dbFilepath;
		}
		else if(optionService.get(BTHelper.SqlUsed).equals("true")) {
			params = optionService.get(BTHelper.SqlRequest);
		}
	
		//Get datas and create bts
		bts = DBMan.getBts(params);

		return bts;
	}
	
	/**
	 * Get the terminal from the bt description
	 * @param btDesc
	 * @return
	 * @throws BtAssignmentException when failed
	 */
	public final String getTermFromDesc (String btDesc) throws BtAssignmentException 
	{
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
			logger.error("cannot find terminal in desc {}", btDesc);
			throw new BtAssignmentException("cannot find terminal in desc " + btDesc);
		}
		
		return res;
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
