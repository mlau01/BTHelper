package bth.core.schedule;

import java.io.InvalidObjectException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.BTHelper;
import bth.core.exception.AssignmentAcronymException;
import bth.core.exception.AssignmentScheduleOverlapException;
import bth.core.model.Assignment;
import bth.core.options.OptionsException;
import bth.core.options.OptionsService;

public class ScheduleService {
	
	private static final Logger logger = LogManager.getLogger();
	
	public ScheduleService() {
	}
	
	public void loadfromOptions(OptionsService optionsService) throws OptionsException {
		ScheduleCategory.T1.setAssignment(parseFromString(String.valueOf(optionsService.get(BTHelper.sheduleT1))));
		ScheduleCategory.T1W.setAssignment(parseFromString(String.valueOf(optionsService.get(BTHelper.sheduleT1W))));
		ScheduleCategory.T1S.setAssignment(parseFromString(String.valueOf(optionsService.get(BTHelper.sheduleT1S))));
		ScheduleCategory.T2.setAssignment(parseFromString(String.valueOf(optionsService.get(BTHelper.sheduleT2))));
		ScheduleCategory.T2W.setAssignment(parseFromString(String.valueOf(optionsService.get(BTHelper.sheduleT2W))));
		ScheduleCategory.T2S.setAssignment(parseFromString(String.valueOf(optionsService.get(BTHelper.sheduleT2S))));
	}
	
	/**
	 * Add a new assignment in a schedule category list
	 * The new assignment is tested for conflict with one other in the list
	 * @param targetCategory The category to add the assignment
	 * @param acronym Assignment acronym (S1,M2,etc..)
	 * @param beginTime LocalTime of the Assignment begin
	 * @param endTime LocalTime of the Assignment end
	 * @return The assignment added if succeed
	 * @throws Exception if conflicts was detected
	 */
	public Assignment addAssignment(ScheduleCategory targetCategory, String acronym, LocalTime beginTime, LocalTime endTime) throws Exception {
		Assignment newAssignment = new Assignment(acronym, beginTime, endTime);
		List<Assignment> assignmentList = targetCategory.getAssignment();
		for(Assignment assignment : assignmentList) {
			try {
				testConflict(assignment, newAssignment);
			} catch (AssignmentAcronymException | AssignmentScheduleOverlapException e) {
				logger.error(e);
				throw e;
			}
			
			assignmentList.add(newAssignment);
		}
		return newAssignment;
	}
	
	/**
	 * Remove an assignment from the desired category
	 * @param targetCategory
	 * @param assignmentToRemove
	 * @return Assignment removed if succeed
	 * @throws InvalidObjectException If the assignment was not found in the target category
	 */
	public Assignment removeAssignment(ScheduleCategory targetCategory, Assignment assignmentToRemove) throws InvalidObjectException {
		if(targetCategory.getAssignment().remove(assignmentToRemove)) {
			return assignmentToRemove;
		}
		else {
			throw new InvalidObjectException("Assignment not found in target list");
		}
	}
	
	/**
	 * Parse a raw assignment string to an assignmentModel object
	 * String had to respect pattern: PlanningAcronym=(HH:mm:ss,HH:mm:ss);PlanningAcronym=(HH:mm:ss,HH:mm:ss);
	 * Example: S1=(14:00:00,21:00:00);S2=(16:30:00,23:30:00)
	 * @param rawassignment
	 * @return List<Assignment> parsed, can return empty list if the string does not contains any parsable assignment
	 */
	public List<Assignment> parseFromString(String rawassignments) {
		List<Assignment> assignments = new ArrayList<Assignment>();
		if(rawassignments.isEmpty()) {
			logger.debug("empty rawAssignments string, return empty array");
			return assignments;
		}
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
	public String getAssignmentListAsString(List<Assignment> assignments) {
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
	 * @return false if no conflict was detected
	 * @throws assignmentAcronymException If the acronym is the same
	 * @throws assignmentScheduleOverlapException If the first assignment beginTime or endTime over lap or equals beginTime or endTime of the second Assignment
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

	public List<Assignment> getAssignement(ScheduleCategory scheduleCategory) {
		return scheduleCategory.getAssignment();
	}
	
}
