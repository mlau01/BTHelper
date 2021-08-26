package bth.core.planning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.core.MONTH;

public class Planning implements Serializable{
	
	private ArrayList<ArrayList<String>> array;
	private MONTH month;
	private long lastModified;
	private int maxCol;
	private int maxRow;
	private ArrayList<String> technicians;
	private Calendar gettedDate;
	private boolean localMode = false;
	
	

	public Planning() {
		super();
	}

	/**
	 * Construct a planning
	 * @param pMonth Month of the planning
	 * @param p_array Planning as bidimensional array of string
	 * @param p_lastModified as timestamp
	 * @param p_gettedDate date of getting
	 */
	public Planning(final MONTH pMonth, final ArrayList<ArrayList<String>> p_array, final long p_lastModified, final Calendar p_gettedDate)
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
	public final boolean isLocalMode() { return localMode; }
	
	// ---- Setters ----
	public void setLocalMode()
	{
		localMode = true;
	}

	public void setArray(ArrayList<ArrayList<String>> array) {
		this.array = array;
	}

	public void setMonth(MONTH month) {
		this.month = month;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public void setMaxCol(int maxCol) {
		this.maxCol = maxCol;
	}

	public void setMaxRow(int maxRow) {
		this.maxRow = maxRow;
	}

	public void setTechnicians(ArrayList<String> technicians) {
		this.technicians = technicians;
	}

	public void setGettedDate(Calendar gettedDate) {
		this.gettedDate = gettedDate;
	}
	
	
}
