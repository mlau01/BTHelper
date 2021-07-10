package bth.core;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public enum MONTH {
	JANVIER,
	FEVRIER,
	MARS,
	AVRIL,
	MAI,
	JUIN,
	JUILLET,
	AOUT,
	SEPTEMBRE,
	OCTOBRE,
	NOVEMBRE,
	DECEMBRE;
	
	public static MONTH getCurrent()
	{
		int intMonth = Integer.valueOf(new SimpleDateFormat("MM").format(GregorianCalendar.getInstance().getTime()));
		return MONTH.values()[intMonth - 1];
	}
	
	public static MONTH getByName(final String name)
	{
		for(MONTH month : MONTH.values())
		{
			if(name.equals(month.toString()))
				return month;
		}
		
		return null;
	}
	
	public static MONTH getByIndex(int index)
	{
		return MONTH.values()[index];
	}
	
	public static MONTH getNextMonth(MONTH ref)
	{
		int nextI = toInteger(ref) + 1;
		if(nextI > 11)
			nextI = 0;
		
		return MONTH.values()[nextI];
	}
	
	public static MONTH getPrevMonth(MONTH ref)
	{
		int prevI = toInteger(ref) - 1;
		if(prevI < 0)
			prevI = 11;
		
		return MONTH.values()[prevI];
	}
	
	public static int toInteger(MONTH month)
	{
		int i = 0;
		for(MONTH tMonth : MONTH.values())
		{
			if(tMonth == month)
				return i;
			i++;
		}
		
		return (-1);
	}
}
