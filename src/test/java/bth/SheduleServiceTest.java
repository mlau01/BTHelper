package bth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bth.core.model.Assignation;
import bth.core.schedule.SheduleService;

public class SheduleServiceTest {
	
	@Test
	public void parseFromStringTest_shouldReturnAssignationObjectCorreclyParsed() {
		SheduleService sheduleService = new SheduleService(null);
		
		List<Assignation> assignations = sheduleService.parseFromString("M2=(04:15:00,06:59:59);M1=(07:00:00,13:30:00);S1=(13:30:00,19:59:59);S2=(20:00:00,23:30:00)");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		
		assertEquals("M2", assignations.get(0).getAssignation());
		assertEquals("04:15:00", assignations.get(0).getBeginTime().format(formatter));
		assertEquals("06:59:59", assignations.get(0).getEndTime().format(formatter));
		assertEquals("M2=(04:15:00,06:59:59)", assignations.get(0).toString());
		
		assertEquals("M1", assignations.get(1).getAssignation());
		assertEquals("07:00:00", assignations.get(1).getBeginTime().format(formatter));
		assertEquals("13:30:00", assignations.get(1).getEndTime().format(formatter));
		assertEquals("M1=(07:00:00,13:30:00)", assignations.get(1).toString());
		
		assertEquals("S1", assignations.get(2).getAssignation());
		assertEquals("13:30:00", assignations.get(2).getBeginTime().format(formatter));
		assertEquals("19:59:59", assignations.get(2).getEndTime().format(formatter));
		assertEquals("S1=(13:30:00,19:59:59)", assignations.get(2).toString());
		
		assertEquals("S2", assignations.get(3).getAssignation());
		assertEquals("20:00:00", assignations.get(3).getBeginTime().format(formatter));
		assertEquals("23:30:00", assignations.get(3).getEndTime().format(formatter));
		assertEquals("S2=(20:00:00,23:30:00)", assignations.get(3).toString());
	}
	
	@Test
	public void toStringTest_shouldReturnCorreclyBuildedString() {
		SheduleService sheduleService = new SheduleService(null);
		
		Assignation assignation1 = new Assignation("A", LocalTime.of(11, 34, 42), LocalTime.of(11, 57, 59));
		Assignation assignation2 = new Assignation("S1", LocalTime.of(9, 1, 2), LocalTime.of(23, 14, 12));
		Assignation assignation3 = new Assignation("M2", LocalTime.of(4, 15, 0), LocalTime.of(10, 30, 49));
		List<Assignation> assignations = new ArrayList<Assignation>();
		assignations.add(assignation1);
		assignations.add(assignation2);
		assignations.add(assignation3);
		
		String rawAssignations = sheduleService.toString(assignations);
		
		assertEquals("A=(11:34:42,11:57:59);S1=(09:01:02,23:14:12);M2=(04:15:00,10:30:49)", rawAssignations);
	}

}
