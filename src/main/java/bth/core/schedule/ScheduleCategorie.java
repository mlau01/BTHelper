package bth.core.schedule;

import java.util.List;

import bth.core.model.Assignation;

public enum ScheduleCategorie {
	T1,
	T1W,
	T1S,
	T2,
	T2W,
	T2S;
	
	private List<Assignation> assignation;

	public List<Assignation> getAssignation() {
		return assignation;
	}

	public void setAssignation(List<Assignation> assignation) {
		this.assignation = assignation;
	}

		
	
}