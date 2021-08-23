package bth.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import bth.BTHelper;
import bth.core.exception.SheduleServiceException;
import bth.core.model.Assignment;
import bth.core.options.OptionException;
import bth.core.options.OptionService;
import bth.core.schedule.ScheduleCategory;
import bth.core.schedule.ScheduleService;

public class ScheduleServiceIT {
	
	private static OptionService optionService;
	private static String testConfPath = System.getenv("LOCALAPPDATA") + "\\.BTHelperTest\\test.conf";
	
	@BeforeAll
	public static void setUp() throws OptionException {
		 Path path = Paths.get(testConfPath);
		 optionService = new OptionService(path.toString());
		 optionService.loadConfig();
	}
	
	@AfterAll
	public static void cleanUp() throws IOException {
		Path path = Paths.get(testConfPath);
		Files.deleteIfExists(path);
		Files.deleteIfExists(path.getParent());
		
	}
	
	@Test
	public void getAssignmentListTest_shouldBeDefaultAssignmentSetInBTHelperConstantClass() throws Exception {
		ScheduleService scheduleService = new ScheduleService(optionService);
		scheduleService.load();
		List<Assignment> t1AssignmentList = scheduleService.getAssignementList(ScheduleCategory.T1);
		String t1AssignmentListString = scheduleService.getAssignmentListAsString(t1AssignmentList);
		
		assertEquals(BTHelper.defaultSheduleT1, t1AssignmentListString);
	}
	
   @Test
    public void getCorrectSheduleAcronymTest_shouldReturnAssignSMForT2SuperMorning() throws Exception {
    	ScheduleService scheduleService = new ScheduleService(optionService);
    	scheduleService.load();
        String timetable = scheduleService.getCorrectSheduleAcronym("T2",
                new GregorianCalendar(2021, 6, 31, 5, 6, 0),
                ScheduleService.SHEDULEMODE.SUPER,
                true);

        assertEquals("SM", timetable);
    }

    @Test
    public void getCorrectSheduleAcronymTest_shouldReturnAssignM2ForT2NormalMorning() throws Exception {
    	ScheduleService scheduleService = new ScheduleService(optionService);
    	scheduleService.load();
        String timetable = scheduleService.getCorrectSheduleAcronym("T2",
                new GregorianCalendar(2021, 6, 30, 8, 0, 0),
                ScheduleService.SHEDULEMODE.NORMAL,
                false);

        assertEquals("M2", timetable);
    }

    @Test
    public void getCorrectSheduleAcronymTest_shouldReturnAssignSSForT2SuperEvening() throws Exception {
    	ScheduleService scheduleService = new ScheduleService(optionService);
    	scheduleService.load();
        String timetable = scheduleService.getCorrectSheduleAcronym("T2",
                new GregorianCalendar(2021, 6, 31, 19, 18,0),
                ScheduleService.SHEDULEMODE.SUPER,
                true);

        assertEquals("SS", timetable);
    }

    @Test
    public void getCorrectSheduleAcronymTest_shouldReturnAssignSSForT1EveningAfter20() throws Exception {
    	ScheduleService scheduleService = new ScheduleService(optionService);
    	scheduleService.load();
    	String timetable = scheduleService.getCorrectSheduleAcronym("T1",
                new GregorianCalendar(2021, 7, 1, 22, 18,0),
                ScheduleService.SHEDULEMODE.SUPER,
                true);

        assertEquals("SS", timetable);
    }
    @Test
    public void getCorrectSheduleAcronymTest_shouldReturnAssignM2ForT1MorningBefore7() throws Exception {
    	ScheduleService scheduleService = new ScheduleService(optionService);
    	scheduleService.load();
        String timetable = scheduleService.getCorrectSheduleAcronym("T1",
                new GregorianCalendar(2021, 6, 23, 5,18,0),
                ScheduleService.SHEDULEMODE.NORMAL,
                false);

        assertEquals("M2", timetable);
    }

    @Test
    public void getCorrectSheduleAcronymTest_shouldReturnAssignAForT2Afternoon() throws Exception {
    	ScheduleService scheduleService = new ScheduleService(optionService);
    	scheduleService.load();
        String timetable = scheduleService.getCorrectSheduleAcronym("T2",
                new GregorianCalendar(2021, 6, 28, 14,8,0),
                ScheduleService.SHEDULEMODE.NORMAL,
                false);

        assertEquals("A", timetable);
    }

    @Test
    public void getCorrectSheduleAcronymTest_shouldReturnAssignSSForT2SuperAfternoon() throws Exception {
    	ScheduleService scheduleService = new ScheduleService(optionService);
    	scheduleService.load();
        String timetable = scheduleService.getCorrectSheduleAcronym("T2",
                new GregorianCalendar(2021, 6, 28, 14,8,0),
                ScheduleService.SHEDULEMODE.SUPER,
                false);

        assertEquals("SS", timetable);
    }

}
