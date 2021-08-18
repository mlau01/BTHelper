package bth.core.schedule;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import bth.core.model.Assignation;

public class SheduleService {
	
	private List<Assignation> assignationT1;
	private List<Assignation> assignationT1W;
	private List<Assignation> assignationT1S;
	private List<Assignation> assignationT2;
	private List<Assignation> assignationT2W;
	private List<Assignation> assignationT2S;
	
	public SheduleService() {
		// TODO Auto-generated constructor stub
	}
	
	public void loadT1fromString(String T1Assignation) {
		
	}
	
	/**
	 * Parse a raw assignment string to an AssignationModel object
	 * String had to respect pattern: PlanningAcronym=(HH:mm:ss,HH:mm:ss);PlanningAcronym=(HH:mm:ss,HH:mm:ss);
	 * Example: S1=(14:00:00,21:00:00);S2=(16:30:00,23:30:00)
	 * @param rawAssignation
	 * @return
	 */
	public List<Assignation> parseFromString(String rawAssignations) {
		List<Assignation> assignations = new ArrayList<Assignation>();
		
		String[] rawAssignationArray = rawAssignations.split(";");
		
		for(String rawAssignation : rawAssignationArray) {
			Assignation assignation = new Assignation();
			String acronym = rawAssignation.split("=")[0];
			String timePeriod = rawAssignation.split("=")[1];
			timePeriod = timePeriod.replace("(", "");
			timePeriod = timePeriod.replace(")", "");
			String beginTimeString = timePeriod.split(",")[0];
			String endTimeString = timePeriod.split(",")[1];
			
			assignation.setAssignation(acronym);
			
			LocalTime beginTime = LocalTime.parse(beginTimeString);
			assignation.setBeginTime(beginTime);
			
			LocalTime endTime = LocalTime.parse(endTimeString);
			assignation.setEndTime(endTime);
			
			assignations.add(assignation);
		}

		return assignations;
	}
	

}
