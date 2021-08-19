package bth.core.schedule;

import java.util.List;

import bth.core.model.Assignment;

public enum ScheduleCategorie {
	T1,
	T1W,
	T1S,
	T2,
	T2W,
	T2S;
	
	private List<Assignment> assignment;

	public List<Assignment> getAssignment() {
		return assignment;
	}

	public void setAssignment(List<Assignment> assignment) {
		this.assignment = assignment;
	}

		
	
}