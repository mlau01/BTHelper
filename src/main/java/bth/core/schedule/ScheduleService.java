package bth.core.schedule;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.BTHelper;
import bth.core.exception.AssignmentAcronymException;
import bth.core.exception.AssignmentScheduleOverlapException;
import bth.core.model.Assignment;

public class ScheduleService {
	
	private static final Logger logger = LogManager.getLogger();
	
	private Properties bthOptions;
	
	public ScheduleService(Properties p_bthOptions) {
		bthOptions = p_bthOptions;
	}
	
	public void loadfromOptions(Properties properties) {
		ScheduleCategorie.T1.setAssignment(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT1))));
		ScheduleCategorie.T1W.setAssignment(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT1W))));
		ScheduleCategorie.T1S.setAssignment(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT1S))));
		ScheduleCategorie.T2.setAssignment(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT2))));
		ScheduleCategorie.T2W.setAssignment(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT2W))));
		ScheduleCategorie.T2S.setAssignment(parseFromString(String.valueOf(properties.get(BTHelper.sheduleT2S))));
	}
	
	public void addassignment(ScheduleCategorie targetCategorie, String acronym, LocalTime beginTime, LocalTime endTime) {
		Assignment newassignment = new Assignment(acronym, beginTime, endTime);
		List<Assignment> assignmentList = targetCategorie.getAssignment();
		for(Assignment assignment : assignmentList) {
			//TODO
		}
		
	}
	
	/**
	 * Parse a raw assignment string to an assignmentModel object
	 * String had to respect pattern: PlanningAcronym=(HH:mm:ss,HH:mm:ss);PlanningAcronym=(HH:mm:ss,HH:mm:ss);
	 * Example: S1=(14:00:00,21:00:00);S2=(16:30:00,23:30:00)
	 * @param rawassignment
	 * @return
	 */
	public List<Assignment> parseFromString(String rawassignments) {
		List<Assignment> assignments = new ArrayList<Assignment>();
		
		String[] rawassignmentArray = rawassignments.split(";");
		
		for(String rawassignment : rawassignmentArray) {
			Assignment assignment = new Assignment();
			String acronym = rawassignment.split("=")[0];
			String timePeriod = rawassignment.split("=")[1];
			timePeriod = timePeriod.replace("(", "");
			timePeriod = timePeriod.replace(")", "");
			String beginTimeString = timePeriod.split(",")[0];
			String endTimeString = timePeriod.split(",")[1];
			
			assignment.setAssignment(acronym);
			
			LocalTime beginTime = LocalTime.parse(beginTimeString);
			assignment.setBeginTime(beginTime);
			
			LocalTime endTime = LocalTime.parse(endTimeString);
			assignment.setEndTime(endTime);
			
			assignments.add(assignment);
		}

		return assignments;
	}

	/**
	 * Create a string with assignment.toString() of each assignment separate by semicolon
	 * @param assignments
	 * @return String builded
	 */
	public String toString(List<Assignment> assignments) {
		String string = "";
		for(Assignment assign : assignments) {
			if( ! string.isEmpty()) {
				string += ";";
			}
			
			string += assign.toString();
		}
		return string;
	}
	
	/**
	 * Compare two Assignment
	 * @param inPlaceassignment
	 * @param toCompareassignment
	 * @return
	 * @throws assignmentAcronymException
	 * @throws assignmentScheduleOverlapException
	 */
	public boolean testConflict(Assignment inPlaceassignment, Assignment toCompareassignment) throws AssignmentAcronymException, AssignmentScheduleOverlapException {
		String acronym1 = inPlaceassignment.getAssignment();
		String acronym2 = toCompareassignment.getAssignment();
		if(acronym1.equals(acronym2)) {
			throw new AssignmentAcronymException("Duplicate acronym");
		}
		
		LocalTime beginTime1 = inPlaceassignment.getBeginTime();
		LocalTime beginTime2 = toCompareassignment.getBeginTime();
		LocalTime endTime1 = inPlaceassignment.getEndTime();
		LocalTime endTime2 = toCompareassignment.getEndTime();
		
		if(beginTime2.isAfter(beginTime1) && beginTime2.isBefore(endTime1)) {
			throw new AssignmentScheduleOverlapException("Shedule overlap");
		}
		
		if(beginTime2.equals(beginTime1) || beginTime2.equals(endTime1)) {
			throw new AssignmentScheduleOverlapException("Shedule overlap");
		}
		
		if(endTime2.isAfter(beginTime1) && endTime2.isBefore(endTime1))
		{
			throw new AssignmentScheduleOverlapException("Shedule overlap");
		}
		
		if(endTime2.equals(beginTime1) || endTime2.equals(endTime1)) {
			throw new AssignmentScheduleOverlapException("Shedule overlap");
		}
		
		return false;
	}
	
	

}
