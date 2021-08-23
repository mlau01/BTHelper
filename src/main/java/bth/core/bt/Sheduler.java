package bth.core.bt;

import java.util.GregorianCalendar;
import java.util.Hashtable;

import bth.core.exception.TimetableException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Enumeration;

public class Sheduler {
	public enum SHEDULEMODE {
		NORMAL,
		SUPER
	}
	
	private static final Logger logger = LogManager.getLogger();
	
	private Hashtable<Timetable, String> timeTableT1 = new Hashtable<Timetable, String>();
	private Hashtable<Timetable, String> timeTableT1W = new Hashtable<Timetable, String>();
	private Hashtable<Timetable, String> timeTableT1S = new Hashtable<Timetable, String>();
	private Hashtable<Timetable, String> timeTableT2 = new Hashtable<Timetable, String>();
	private Hashtable<Timetable, String> timeTableT2W = new Hashtable<Timetable, String>();
	private Hashtable<Timetable, String> timeTableT2S = new Hashtable<Timetable, String>();
	
	public Sheduler()
	{
		logger.trace("INIT");
		loadTimeTable();
	}

	/**
	 * Should return the correct timetable for the date and terminal given
	 * @param terminal can be T1 or T2
	 * @param btDate The date of the bt, time in date is user to find correct timetable
	 * @param sheduleMode if this is a Super timetable or Normal time table
	 * @param forceWeekend set to true if you want to force a week end timetable, this is used as fallback when no technician was founds with the other ways
	 * @return  timetable selected by the logic
	 * @throws TimetableException when params matches to no logic
	 */
	public Hashtable<Timetable, String> getCorrectTimetable(String terminal, GregorianCalendar btDate, SHEDULEMODE sheduleMode, boolean forceWeekend) throws TimetableException {

		logger.info("getTimeTable for terminal: {}", terminal);
		if(terminal.equals("T1") && (isWeekend(btDate) || forceWeekend) && sheduleMode == SHEDULEMODE.NORMAL) {
			logger.info("Timetable selected: T1 Weekend");
			return timeTableT1W;
		}
		else if(terminal.equals("T1") && !isWeekend(btDate) && sheduleMode == SHEDULEMODE.NORMAL) {
			logger.info("Timetable selected: T1 Normal");
			return timeTableT1;
		}
		else if(terminal.equals("T1")  && sheduleMode == SHEDULEMODE.SUPER) {
			logger.info("Timetable selected: T1 Super");
			return timeTableT1S;
		}
		else if(terminal.equals("T2") && (isWeekend(btDate) || forceWeekend) && sheduleMode == SHEDULEMODE.NORMAL) {
			logger.info("Timetable selected: T2 Weekend");
			return timeTableT2W;
		}
		else if(terminal.equals("T2") && !isWeekend(btDate) && sheduleMode == SHEDULEMODE.NORMAL) {
			logger.info("Timetable selected: T2 Normal");
			return timeTableT2;
		}
		else if(terminal.equals("T2") && sheduleMode == SHEDULEMODE.SUPER) {
			logger.info("Timetable selected: T2 Super");
			return timeTableT2S;
		}

		else {
			throw new TimetableException("Cannot find timetable for params: " + terminal + ", weekend: " + isWeekend(btDate) + ", Shedule mode: " + sheduleMode.toString());
		}
	}

	/**
	 * Get the acronym of the assignment selected by getCorrectTimetable method
	 * @param terminal
	 * @param btDate
	 * @param sheduleMode
	 * @param forceWeekend
	 * @return
	 */
	public String getTimeTableAssign(String terminal, GregorianCalendar btDate, SHEDULEMODE sheduleMode, boolean forceWeekend)
	{
		Hashtable<Timetable, String> timeTable = null;
		try {
			timeTable = getCorrectTimetable(terminal, btDate, sheduleMode, forceWeekend);
		} catch (TimetableException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}

		for(Timetable terminalTimeTable : timeTable.keySet()){
			if(terminalTimeTable.isIn(btDate)) {
				return timeTable.get(terminalTimeTable);
			}
		}

		return null;
	}

	/**
	 * Check if a date day is a weekend day
	 * @param cal Date to check
	 * @return true if the day is a weekend day
	 */
	public boolean isWeekend(GregorianCalendar cal)
	{
		int day = cal.get(GregorianCalendar.DAY_OF_WEEK);
		if( (day == 7) || (day == 1) ) {
			return true;
		}
		
		return false;
	}
	
	private void loadTimeTable()
	{
		//Revoir le système en cas de jour ferier
		
		/*
		timeTableT1.put(new Timetable("04:15:00", "11:39:00"), "M1");
		timeTableT1.put(new Timetable("14:21:00", "21:45:00"), "S1");
		timeTableT1.put(new Timetable("11:39:01", "14:20:59"), "A");
		
		timeTableT1W.put(new Timetable("04:15:00", "13:24:00"), "M1");
		timeTableT1W.put(new Timetable("13:21:00", "21:45:00"), "S1");
		
		timeTableT1S.put(new Timetable("04:15:00", "13:24:00"), "SM");
		timeTableT1S.put(new Timetable("13:21:00", "21:45:00"), "SS");
		
		timeTableT2.put(new Timetable("04:15:00", "11:54:00"), "M2");
		timeTableT2.put(new Timetable("11:54:01", "20:00:00"), "S2");
		timeTableT2.put(new Timetable("20:00:01", "21:45:00"), "S1");
		timeTableT2.put(new Timetable("13:00:00", "21:00:00"), "S");
		
		timeTableT2W.put(new Timetable("04:15:00", "12:39:00"), "M2");
		timeTableT2W.put(new Timetable("12:36:00", "20:00:00"), "S2");
		timeTableT2W.put(new Timetable("20:00:01", "21:45:00"), "S1");
		timeTableT2W.put(new Timetable("13:00:00", "21:00:00"), "S");
		
		timeTableT2S.put(new Timetable("20:00:01", "21:45:00"), "SS");
		*/

		timeTableT1.put(new Timetable("04:15:00", "06:59:59"), "M2");
		timeTableT1.put(new Timetable("07:00:00", "13:30:00"), "M1");
		timeTableT1.put(new Timetable("13:30:00", "19:59:59"), "S1");
		timeTableT1.put(new Timetable("20:00:00", "23:30:00"), "S2");

		timeTableT1W.put(new Timetable("04:15:00", "06:59:59"), "M2");
		timeTableT1W.put(new Timetable("07:00:00", "13:30:00"), "M1");
		timeTableT1W.put(new Timetable("13:30:00", "20:00:00"), "S1");
		timeTableT1W.put(new Timetable("20:00:00", "23:30:00"), "S2");

		timeTableT1S.put(new Timetable("04:15:00", "06:59:59"), "SM");
		timeTableT1S.put(new Timetable("20:00:00", "23:30:00"), "SS");

		timeTableT2.put(new Timetable("04:30:00", "11:29:59"), "M2");
		timeTableT2.put(new Timetable("11:30:00", "16:29:59"), "A");
		timeTableT2.put(new Timetable("16:30:00", "23:30:00"), "S2");

		timeTableT2W.put(new Timetable("04:30:00", "13:59:00"), "SM");
		timeTableT2W.put(new Timetable("14:00:00", "23:30:00"), "SS");
		timeTableT2W.put(new Timetable("04:30:00", "13:59:00"), "M2");
		timeTableT2W.put(new Timetable("14:00:00", "23:30:00"), "S2");

		timeTableT2S.put(new Timetable("04:30:00", "13:59:00"), "SM");
		timeTableT2S.put(new Timetable("14:00:00", "23:30:00"), "SS");


		/*
		timeTableT2.put(new Timetable("04:30:00", "12:59:59"), "M");
		timeTableT2W.put(new Timetable("04:30:00", "12:59:59"), "M");
		timeTableT2S.put(new Timetable("04:30:00", "12:59:59"), "M");
		timeTableT2.put(new Timetable("13:00:00", "21:30:00"), "S");
		timeTableT2W.put(new Timetable("13:00:00", "21:30:00"), "S");
		timeTableT2S.put(new Timetable("13:00:00", "21:30:00"), "S");
		
		timeTableT1.put(new Timetable("04:30:00", "12:59:59"), "M");
		timeTableT1W.put(new Timetable("04:30:00", "12:59:59"), "M");
		timeTableT1S.put(new Timetable("04:30:00", "12:59:59"), "M");
		timeTableT1.put(new Timetable("13:00:00", "21:30:00"), "S");
		timeTableT1W.put(new Timetable("13:00:00", "21:30:00"), "S");
		timeTableT1S.put(new Timetable("13:00:00", "21:30:00"), "S");

		 */
	}
	
}
