package bth.core.schedule;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bth.BTHelper;
import bth.core.model.Assignation;

public class SheduleService {
	
	private List<Assignation> assignationT1;
	private List<Assignation> assignationT1W;
	private List<Assignation> assignationT1S;
	private List<Assignation> assignationT2;
	private List<Assignation> assignationT2W;
	private List<Assignation> assignationT2S;
	
	public SheduleService(Properties bthOptions) {
		loadfromOptions(bthOptions);
	}
	
	public void loadfromOptions(Properties properties) {
		assignationT1 = parseFromString(String.valueOf(properties.get(BTHelper.sheduleT1)));
		assignationT1W = parseFromString(String.valueOf(properties.get(BTHelper.sheduleT1W)));
		assignationT1S = parseFromString(String.valueOf(properties.get(BTHelper.sheduleT1S)));
		assignationT2 = parseFromString(String.valueOf(properties.get(BTHelper.sheduleT2)));
		assignationT2W = parseFromString(String.valueOf(properties.get(BTHelper.sheduleT2W)));
		assignationT2S = parseFromString(String.valueOf(properties.get(BTHelper.sheduleT2S)));
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

	/**
	 * Create a string with assignation.toString() of each assignation separate by semicolon
	 * @param assignations
	 * @return String builded
	 */
	public String toString(List<Assignation> assignations) {
		String string = "";
		for(Assignation assign : assignations) {
			if( ! string.isEmpty()) {
				string += ";";
			}
			
			string += assign.toString();
		}
		return string;
	}

	public List<Assignation> getAssignationT1() {
		return assignationT1;
	}

	public List<Assignation> getAssignationT1W() {
		return assignationT1W;
	}

	public List<Assignation> getAssignationT1S() {
		return assignationT1S;
	}

	public List<Assignation> getAssignationT2() {
		return assignationT2;
	}

	public List<Assignation> getAssignationT2W() {
		return assignationT2W;
	}

	public List<Assignation> getAssignationT2S() {
		return assignationT2S;
	}
	
	
	

}
