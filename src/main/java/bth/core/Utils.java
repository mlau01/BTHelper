package bth.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {
	
	private static short verboseLevel = 0;
	
	public static int parseInt(final String str)
	{
		int number = 0;
		try
		{
			number = Integer.parseInt(str);
		} catch (NumberFormatException e)
		{
			if(verboseLevel >= 1) System.out.println("Utils: parseInt(...) -> " + e.getMessage());
		}
		
		return (number);
	}
	
	public static String getCurrentTime()
	{
		String currentTime = new SimpleDateFormat("dd MMMM (HH:mm:ss)").format(Calendar.getInstance().getTime());
		
		return currentTime;
	}
	

}
