package bth.core.planning;

import java.util.ArrayList;

import bth.core.bt.Bt;


public class Technician {
	private final String name;
	private final ArrayList<Bt> btList;
	
	public Technician(final String pName)
	{
		name = pName;
		btList = new ArrayList<Bt>();
	}
	
	public final ArrayList<Bt> getBtList()
	{
		return btList;
	}
	
	public void resetBt()
	{
		btList.clear();
	}
	public String getName()
	{
		return this.name;
	}

}
