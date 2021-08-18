package bth.core.model;

import java.time.LocalTime;

public class AssignationModel {
	
	private String assignation;
	private LocalTime beginTime;
	private LocalTime endTime;
	
	public AssignationModel(String assignation, LocalTime beginTime, LocalTime endTime) {
		super();
		this.assignation = assignation;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}
}
