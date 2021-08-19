package bth.core.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Assignment {
	
	private String assignment;
	private LocalTime beginTime;
	private LocalTime endTime;
	
	public Assignment(String assignment, LocalTime beginTime, LocalTime endTime) {
		super();
		this.assignment = assignment;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	public Assignment() {
		super();
	}

	public String getAssignment() {
		return assignment;
	}

	public void setAssignment(String assignment) {
		this.assignment = assignment;
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
		
		return assignment + "=(" + beginTime.format(formatter) + "," + endTime.format(formatter) + ")";
	}
}
