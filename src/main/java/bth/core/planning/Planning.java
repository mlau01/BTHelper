package bth.core.planning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.core.MONTH;

public class Planning implements Serializable{
	
	private final ArrayList<ArrayList<String>> array;
	private final MONTH month;
	private final long lastModified;
	private final int maxCol;
	private final int maxRow;
	private final ArrayList<String> technicians;
	private final Calendar gettedDate;
	private boolean localMode = false;
	private final static Logger logger = LogManager.getLogger();
	
	Planning(final MONTH pMonth, final ArrayList<ArrayList<String>> p_array, final long p_lastModified, final Calendar p_gettedDate)
	{
		month = pMonth;
		lastModified = p_lastModified;
		gettedDate = p_gettedDate;
		array = p_array;
		technicians = new ArrayList<String>();
		
		int rowNbr = 0;
		int colNbr = 0;
		
		//Extract user list
		for(ArrayList<String> row : p_array)
		{
			String technician = PlanningParser.extractTechnicians(row);
			if(technician != null) {
				technicians.add(technician);
			}
			
			int rowCol = 0;
			while(rowCol < row.size())
			{
				rowCol++;
				if(rowCol > colNbr)
					colNbr = rowCol;
			}
			rowNbr++;
		}
		
		this.maxCol = colNbr;
		this.maxRow = rowNbr;
		
	}
	
	//--Getters
	public final MONTH getMonth() { return this.month; }
	public final int getMaxCol() 	{ return this.maxCol; }
	public final int getMaxRow() 	{ return this.maxRow; }
	public final ArrayList<String> getTechnicians() { return this.technicians; }
	public final ArrayList<ArrayList<String>> getArray() { return this.array; }
	public final long getLastModified() { return lastModified; }
	public final Calendar getGettedDate() { return gettedDate; }
	public final boolean isLocal() { return localMode; }
	
	// ---- Setters ----
	public void setLocalMode()
	{
		localMode = true;
	}
}
