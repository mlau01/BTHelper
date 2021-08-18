package bth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import bth.core.model.Assignation;
import bth.core.schedule.SheduleService;

public class SheduleServiceTest {
	
	@Test
	public void parseFromStringTest_shouldReturnAssignationObjectCorreclyParsed() {
		SheduleService sheduleService = new SheduleService();
		
		List<Assignation> assignations = sheduleService.parseFromString("A=(09:00:00,17:00:00);S1=(14:00:00,23:39:59)");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		assertEquals("A", assignations.get(0).getAssignation());
		assertEquals("09:00:00", assignations.get(0).getBeginTime().format(formatter));
		assertEquals("17:00:00", assignations.get(0).getEndTime().format(formatter));
		assertEquals("S1", assignations.get(1).getAssignation());
		assertEquals("14:00:00", assignations.get(1).getBeginTime().format(formatter));
		assertEquals("23:39:59", assignations.get(1).getEndTime().format(formatter));
	}

}
