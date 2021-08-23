package bth.core.schedule;

import java.io.InvalidObjectException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bth.BTHelper;
import bth.core.exception.AssignmentAcronymException;
import bth.core.exception.AssignmentScheduleOverlapException;
import bth.core.model.Assignment;
import bth.core.options.OptionService;

public class ScheduleService {
	
	private static final Logger logger = LogManager.getLogger();
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	private OptionService optionService;
	
	public ScheduleService(OptionService p_optionService) {
		optionService = p_optionService;
		ScheduleCategory.T1.setAssignment(new ArrayList<Assignment>());
		ScheduleCategory.T1W.setAssignment(new ArrayList<Assignment>());
		ScheduleCategory.T1S.setAssignment(new ArrayList<Assignment>());
		ScheduleCategory.T2.setAssignment(new ArrayList<Assignment>());
		ScheduleCategory.T2W.setAssignment(new ArrayList<Assignment>());
		ScheduleCategory.T2S.setAssignment(new ArrayList<Assignment>());
	}
	
	public void load() throws Exception {
		ScheduleCategory.T1.setAssignment(parseFromString(String.valueOf(optionService.get(BTHelper.sheduleT1))));
		ScheduleCategory.T1W.setAssignment(parseFromString(String.valueOf(optionService.get(BTHelper.sheduleT1W))));
		ScheduleCategory.T1S.setAssignment(parseFromString(String.valueOf(optionService.get(BTHelper.sheduleT1S))));
		ScheduleCategory.T2.setAssignment(parseFromString(String.valueOf(optionService.get(BTHelper.sheduleT2))));
		ScheduleCategory.T2W.setAssignment(parseFromString(String.valueOf(optionService.get(BTHelper.sheduleT2W))));
		ScheduleCategory.T2S.setAssignment(parseFromString(String.valueOf(optionService.get(BTHelper.sheduleT2S))));
	}
	
	/**
	 * Add a new assignment in a schedule category list
	 * The new assignment is tested for conflict with others in the assignment list
	 * Save the assignment using OptionService
	 * @param targetCategory The category to add the assignment
	 * @param acronym Assignment acronym (S1,M2,etc..)
	 * @param beginTime LocalTime of the Assignment begin
	 * @param endTime LocalTime of the Assignment end
	 * @return The assignment list updated if succeed
	 * @throws Exception if conflicts was detected
	 */
	public List<Assignment> addAssignment(ScheduleCategory targetCategory, String acronym, LocalTime beginTime, LocalTime endTime) throws Exception {
		Assignment newAssignment = new Assignment(acronym, beginTime, endTime);
		List<Assignment> assignmentList = targetCategory.getAssignment();
		testConflict(newAssignment, assignmentList);
		assignmentList.add(newAssignment);
		optionService.set(targetCategory.getOptionName(), getAssignmentListAsString(assignmentList));
		
		return assignmentList;
	}
	
	public List<Assignment> addAssignment(ScheduleCategory targetCategory, String acronym, String beginTime, String endTime) throws Exception {
		LocalTime beginTimeObject = LocalTime.parse(beginTime, formatter);
		LocalTime endTimeObject = LocalTime.parse(endTime, formatter);
		
		return addAssignment(targetCategory, acronym, beginTimeObject, endTimeObject);
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
	 * @throws Exception 
	 */
	public List<Assignment> parseFromString(String rawassignments) throws Exception {
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
			
			LocalTime beginTime = LocalTime.parse(beginTimeString, formatter);
			assignment.setBeginTime(beginTime);
			
			LocalTime endTime = LocalTime.parse(endTimeString, formatter);
			assignment.setEndTime(endTime);
			
			testConflict(assignment, assignments);
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
			
			string += assign.getSaveableString();
		}
		return string;
	}
	
	/**
	 * Test conflict between two assignment
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
	
	/**
	 * Test conflict between an assignment and all assignment in a list
	 * @param assignmentToTest
	 * @param assignmentList
	 * @return false if no conflict detected
	 * @throws Exception When a conflict has been detected
	 */
	public boolean testConflict(Assignment assignmentToTest, List<Assignment> assignmentList) throws Exception {
		for(Assignment assignment : assignmentList) {
			try {
				testConflict(assignment, assignmentToTest);
			} catch (AssignmentAcronymException | AssignmentScheduleOverlapException e) {
				logger.error(e);
				throw e;
			}
		}
		
		return false;
	}

	public List<Assignment> getAssignementList(ScheduleCategory scheduleCategory) {
		return scheduleCategory.getAssignment();
	}
	
	public void setAssignmentList(List<Assignment> assignmentList, ScheduleCategory scheduleCategory) {
		scheduleCategory.setAssignment(assignmentList);
	}
	
	public DateTimeFormatter getDateTimeFormatter() {
		return formatter;
	}
	
}
