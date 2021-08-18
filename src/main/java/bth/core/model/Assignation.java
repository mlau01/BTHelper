package bth.core.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Assignation {
	
	private String assignation;
	private LocalTime beginTime;
	private LocalTime endTime;
	
	public Assignation(String assignation, LocalTime beginTime, LocalTime endTime) {
		super();
		this.assignation = assignation;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	public Assignation() {
		super();
	}

	public String getAssignation() {
		return assignation;
	}

	public void setAssignation(String assignation) {
		this.assignation = assignation;
	}

	public LocalTime getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(LocalTime beginTime) {
		this.beginTime = beginTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}
	
	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		
		return assignation + "=(" + beginTime.format(formatter) + "," + endTime.format(formatter) + ")";
	}
}
