package bth.core.bt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.core.MONTH;
import bth.core.Utils;
import bth.core.planning.PlanningException;
import bth.core.planning.Planning;
import bth.core.planning.PlanningManager;
import bth.core.planning.Technician;

public class BtAssignator {
	
	private static final Logger logger = LogManager.getLogger();
	
	private static final Sheduler sheduler = new Sheduler();
	
	/**
	 * Start the research for a technician with date and terminal
	 * @param p_btDate GreorianCalendar date
	 * @param p_terminal String terminal
	 * @return Technician object
	 * @throws BTHException
	 */
	public static final Technician searchTech(final PlanningManager pMan, final GregorianCalendar p_btDate, final String p_terminal) throws BTException
	{	
		logger.info("searchTech -> Search technician for bt date: " + new SimpleDateFormat("dd/MM/YYYY HH:mm").format(p_btDate.getTime()));
		//Retrieve the month corresponding to the bt date
		final int btMonthNum = p_btDate.get(GregorianCalendar.MONTH);
		final MONTH btMonth = MONTH.getByIndex(btMonthNum);
		
		//Trying to get witch assignation match to the terminal and BT date
		String assign = null;
		assign = sheduler.getTimeTableAssign(p_terminal, p_btDate, Sheduler.SHEDULEMODE.NORMAL, false);
		
		//Trying to get tech
		String tech = getTech(pMan, btMonth, p_btDate, assign);
		
		if(tech.equals("NOT FOUND"))
		//If tech was not found with normal assignation, trying with SUPER assignation...
		{
			assign = sheduler.getTimeTableAssign(p_terminal, p_btDate, Sheduler.SHEDULEMODE.SUPER, false);
			tech = getTech(pMan, btMonth, p_btDate, assign);
		}
		
		if(tech.equals("NOT FOUND"))
		//If tech was always not found, try to process like a weedend day, this case can happen in holyday 
		{
			assign = sheduler.getTimeTableAssign(p_terminal, p_btDate, Sheduler.SHEDULEMODE.NORMAL, true);
			tech = getTech(pMan, btMonth, p_btDate, assign);
			
		}
		
		//Return technician model based on the finded String
		return pMan.getTechnicianManager().getTechnician(tech);
	}
	
	private static final String getTech(final PlanningManager pMan, final MONTH btMonth, final GregorianCalendar btDate, final String assign)
			throws BTException
	{	
		//Get the planning corresponding to the date of BT
		Planning planning;
		try {
			planning = pMan.get(btMonth);
		} catch (PlanningException e) {
			throw new BTException(e.getMessage());
		}
	
		
		//Start searching at the end of planning if the bt data >= 15
		String tech = null;
		MONTH targetMonth;
		SEARCH searchIn;
		// 2016-05-19 Modifying if below by: >= before: >
		if(btDate.get(GregorianCalendar.DAY_OF_MONTH) > 15)
			searchIn = SEARCH.END;
		else
			searchIn = SEARCH.START;
		
		tech = getTechAt(btDate, searchIn, assign, planning.getArray());	
		logger.info("Looking tech assignation: {} in month {} for date {}", assign, btMonth, new SimpleDateFormat("dd/MM/YYYY HH:mm").format(btDate.getTime()));
		if(tech.equals("NFD"))
		{
			logger.info("getTech(...): receive NFD code, trying to get day in next planning...");
			if(btDate.get(GregorianCalendar.DAY_OF_MONTH) < 15)
			{
				targetMonth = MONTH.getPrevMonth(btMonth);
				 searchIn = SEARCH.END;
			}
			else
			{
				targetMonth = MONTH.getNextMonth(btMonth);
				searchIn = SEARCH.START;
			}
			
			try {
				planning = pMan.get(targetMonth);
			} catch (PlanningException e) {
				throw new BTException(e.getMessage());
			}

			
			tech = getTechAt(btDate, searchIn, assign, planning.getArray());
		}
		
		
		return (tech);
	}
	
	private static final String getTechAt(final GregorianCalendar btCalDate, final SEARCH search, final String assign, final ArrayList<ArrayList<String>> dstMonthArrayList)
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
	
	private static final int getDayColumnNumber(final GregorianCalendar cal, final SEARCH search, final ArrayList<ArrayList<String>> dstMonthArrayList)
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
	
	private static final String findAssignedTech(final int col, final String assign, final ArrayList<ArrayList<String>> dstMonthArrayList)
	{
		String tech = "NOT FOUND";
		if(dstMonthArrayList != null)
		{
			for(int i = 0; i < dstMonthArrayList.size(); i++)
			{
				ArrayList<String> line = dstMonthArrayList.get(i);
				String cell = line.get(col);
				logger.trace("Searching assignation {} in col {} for tech line {} ", assign, col, line);
				if(cell.equals(assign))
				{
					tech = line.get(1);
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
}
