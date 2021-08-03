package bth.core.bt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Timetable {
	
	private short verboseLevel = 0;
	private Date beg = null;
	private Date end = null;
	
	public Timetable(final String pBeg, final String pEnd)
	{
		try {
			beg = new SimpleDateFormat("HH:mm:ss").parse(pBeg);
			end = new SimpleDateFormat("HH:mm:ss").parse(pEnd);
		} catch (ParseException e)
		{
			if(verboseLevel >= 1) System.out.println(this.getClass().getName() + " -> contructor(...): " + e.getMessage());
		}
	}

	/**
	 * Check if the time in the calendar given is in this timetable
	 * @param cal
	 * @return true if the time is inner this timetable, false otherwise
	 */
	public boolean isIn(GregorianCalendar cal)
	{
		try {
			Date pres = new SimpleDateFormat("HH:mm:ss")
			.parse(cal.get(GregorianCalendar.HOUR_OF_DAY) + ":" + cal.get(GregorianCalendar.MINUTE) + ":" + cal.get(GregorianCalendar.SECOND));
			if(pres.after(beg) && pres.before(end))
			{
				return true;
			}
		} catch (ParseException e)
		{
			if(verboseLevel >= 1) System.out.println(this.getClass().getName() + " -> isIn(...): " + e.getMessage());
		}	
		return false;
	}
}
