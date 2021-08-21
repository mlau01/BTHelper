package bth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bth.core.exception.AssignmentAcronymException;
import bth.core.exception.AssignmentScheduleOverlapException;
import bth.core.model.Assignment;
import bth.core.schedule.ScheduleService;

public class SheduleServiceTest {
	
	@Test
	public void parseFromStringTest_shouldReturnAssignmentObjectCorreclyParsed() {
		ScheduleService sheduleService = new ScheduleService();
		
		List<Assignment> assignations = sheduleService.parseFromString("M2=(04:15:00,06:59:59);M1=(07:00:00,13:30:00);S1=(13:30:00,19:59:59);S2=(20:00:00,23:30:00)");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		
		assertEquals("M2", assignations.get(0).getAssignment());
		assertEquals("04:15:00", assignations.get(0).getBeginTime().format(formatter));
		assertEquals("06:59:59", assignations.get(0).getEndTime().format(formatter));
		assertEquals("M2=(04:15:00,06:59:59)", assignations.get(0).toString());
		
		assertEquals("M1", assignations.get(1).getAssignment());
		assertEquals("07:00:00", assignations.get(1).getBeginTime().format(formatter));
		assertEquals("13:30:00", assignations.get(1).getEndTime().format(formatter));
		assertEquals("M1=(07:00:00,13:30:00)", assignations.get(1).toString());
		
		assertEquals("S1", assignations.get(2).getAssignment());
		assertEquals("13:30:00", assignations.get(2).getBeginTime().format(formatter));
		assertEquals("19:59:59", assignations.get(2).getEndTime().format(formatter));
		assertEquals("S1=(13:30:00,19:59:59)", assignations.get(2).toString());
		
		assertEquals("S2", assignations.get(3).getAssignment());
		assertEquals("20:00:00", assignations.get(3).getBeginTime().format(formatter));
		assertEquals("23:30:00", assignations.get(3).getEndTime().format(formatter));
		assertEquals("S2=(20:00:00,23:30:00)", assignations.get(3).toString());
	}
	
	@Test
	public void parseFromStringWithEmptyStringTest_shouldReturnEmptyList() {
		ScheduleService sheduleService = new ScheduleService();
		
		List<Assignment> assignations = sheduleService.parseFromString("");
		
		assertNotNull(assignations);
		assertEquals(0, assignations.size());
	}
	
	@Test
	public void toStringTest_shouldReturnCorreclyBuildedString() {
		ScheduleService sheduleService = new ScheduleService();
		
		Assignment assignation1 = new Assignment("A", LocalTime.of(11, 34, 42), LocalTime.of(11, 57, 59));
		Assignment assignation2 = new Assignment("S1", LocalTime.of(9, 1, 2), LocalTime.of(23, 14, 12));
		Assignment assignation3 = new Assignment("M2", LocalTime.of(4, 15, 0), LocalTime.of(10, 30, 49));
		List<Assignment> assignations = new ArrayList<Assignment>();
		assignations.add(assignation1);
		assignations.add(assignation2);
		assignations.add(assignation3);
		
		String rawAssignments = sheduleService.toString(assignations);
		
		assertEquals("A=(11:34:42,11:57:59);S1=(09:01:02,23:14:12);M2=(04:15:00,10:30:49)", rawAssignments);
	}
	
	@Test
	public void assignationIsConflictTest_shouldThrowExceptionAssignmentAcronymException() {
		ScheduleService sheduleService = new ScheduleService();
		Assignment assignation1 = new Assignment("A", LocalTime.of(11, 34, 42), LocalTime.of(11, 57, 59));
		Assignment assignation2 = new Assignment("A", LocalTime.of(11, 34, 42), LocalTime.of(11, 57, 59));
		
		assertThrows(AssignmentAcronymException.class, () -> sheduleService.testConflict(assignation1, assignation2));
	}
	
	@Test
	public void testConflictTest_shouldThrowExceptionForAssignmentWithFullOverlapingTimePeriod() {
		ScheduleService sheduleService = new ScheduleService();

		Assignment assignation1 = new Assignment("A", LocalTime.of(11, 34, 42), LocalTime.of(11, 57, 59));
		Assignment assignation2 = new Assignment("S1", LocalTime.of(11, 34, 42), LocalTime.of(11, 57, 58));
		
		assertThrows(AssignmentScheduleOverlapException.class, () -> sheduleService.testConflict(assignation1, assignation2));
	}
	
	@Test
	public void testConflictTest_shouldThrowExceptionForAssignmentWithEndTimeOverlapBeginTime() {
		ScheduleService sheduleService = new ScheduleService();
		
		Assignment assignation1 = new Assignment("A", LocalTime.of(9, 00, 00), LocalTime.of(17, 00, 00));
		Assignment assignation2 = new Assignment("M1", LocalTime.of(4, 15, 00), LocalTime.of(9, 01, 58));
		
		assertThrows(AssignmentScheduleOverlapException.class, () -> sheduleService.testConflict(assignation1, assignation2));
	}
	
	@Test
	public void testConflictTest_shouldThrowExceptionForAssignmentWithBeginTimeOverlapEndTime() {
		ScheduleService sheduleService = new ScheduleService();
		
		Assignment assignation1 = new Assignment("A", LocalTime.of(9, 00, 00), LocalTime.of(17, 00, 00));
		Assignment assignation2 = new Assignment("S1", LocalTime.of(17, 00, 00), LocalTime.of(23, 01, 58));
		
		assertThrows(AssignmentScheduleOverlapException.class, () -> sheduleService.testConflict(assignation1, assignation2));
	}
	
	@Test
	public void testConflictTest_shouldReturnNoConflict() throws AssignmentAcronymException, AssignmentScheduleOverlapException {
		ScheduleService sheduleService = new ScheduleService();
		
		Assignment assignation1 = new Assignment("A", LocalTime.of(9, 00, 00), LocalTime.of(17, 00, 00));
		Assignment assignation2 = new Assignment("S1", LocalTime.of(17, 00, 01), LocalTime.of(23, 01, 58));
		
		assertFalse(sheduleService.testConflict(assignation1, assignation2));
	}

}
