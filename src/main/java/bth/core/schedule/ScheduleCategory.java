package bth.core.schedule;

import java.util.List;

import bth.core.model.Assignment;

public enum ScheduleCategory {
	T1("SheduleT1"),
	T1W("SheduleT1W"),
	T1S("SheduleT1S"),
	T2("SheduleT2"),
	T2W("SheduleT2W"),
	T2S("SheduleT2S");
	
	/*
	 * public static final String sheduleT1W = ("SheduleT1W");
	public static final String sheduleT1S = ("SheduleT1S");
	public static final String sheduleT2 = ("SheduleT2");
	public static final String sheduleT2W = ("SheduleT2W");
	public static final String sheduleT2S = ("SheduleT2S");
	 */
	
	private ScheduleCategory(String p_optionName) {
		this.optionName = p_optionName;
	}
	
	private List<Assignment> assignment;
	private final String optionName;

	public List<Assignment> getAssignment() {
		return assignment;
	}

	public void setAssignment(List<Assignment> assignment) {
		this.assignment = assignment;
	}
	
	public String getOptionName() {
		return this.optionName;
	}

		
	
}