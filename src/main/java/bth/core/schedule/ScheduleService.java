package bth.core.schedule;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.BTHelper;
import bth.core.model.Assignation;

public class ScheduleService {
	
	private static final Logger logger = LogManager.getLogger();
	
	private Properties bthOptions;
	
	public ScheduleService(Properties p_bthOptions) {
		bthOptions = p_bthOptions;
	}
	
	public void loadfromOptions(Properties properties) {
		ScheduleCategorie.T1.setAssignation(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT1))));
		ScheduleCategorie.T1W.setAssignation(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT1W))));
		ScheduleCategorie.T1S.setAssignation(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT1S))));
		ScheduleCategorie.T2.setAssignation(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT2))));
		ScheduleCategorie.T2W.setAssignation(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT2W))));
		ScheduleCategorie.T2S.setAssignation(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT2S))));
	}
	
	public void addAssignation(ScheduleCategorie targetCategorie, String acronym, LocalTime beginTime, LocalTime endTime) {
		Assignation newAssignation = new Assignation(acronym, beginTime, endTime);
		List<Assignation> assignationList = targetCategorie.getAssignation();
		for(Assignation assignation : assignationList) {
			//TODO
		}
		
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
	
	

}
