package bth;

import bth.core.bt.Sheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;


public class ShedulerTest {

    private Sheduler sheduler = new Sheduler();

    @Test
    public void isWeekendDay_shouldReturnTrueForWeekendDay(){

        assertTrue(sheduler.isWeekend(new GregorianCalendar(2021, 6, 31, 5,6)));
        assertFalse(sheduler.isWeekend(new GregorianCalendar(2021, 6, 30, 5,6)));
        assertTrue(sheduler.isWeekend(new GregorianCalendar(2021, 7, 1, 5,6)));
    }

    @Test
    public void getTimeTableAssignTest_shouldReturnAssignSMForT2SuperMorning() {

        String timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 6, 31, 5, 6),
                Sheduler.SHEDULEMODE.SUPER,
                true);

        assertEquals("SM", timetable);
    }

    @Test
    public void getTimeTableAssignTest_shouldReturnAssignM2ForT2NormalMorning() {
        String timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 6, 30, 8, 0),
                Sheduler.SHEDULEMODE.NORMAL,
                false);

        assertEquals("M2", timetable);
    }

    @Test
    public void getTimeTableAssignTest_shouldReturnAssignSSForT2SuperEvening() {
        String timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 6, 31, 19, 18),
                Sheduler.SHEDULEMODE.SUPER,
                true);

        assertEquals("SS", timetable);
    }

    @Test
    public void getTimeTableAssignTest_shouldReturnAssignSSForT1EveningAfter20() {
        String timetable = sheduler.getTimeTableAssign("T1",
                new GregorianCalendar(2021, 7, 1, 22, 18),
                Sheduler.SHEDULEMODE.SUPER,
                true);

        assertEquals("SS", timetable);
    }
    @Test
    public void getTimeTableAssignTest_shouldReturnAssignM2ForT1MorningBefore7() {
        String timetable = sheduler.getTimeTableAssign("T1",
                new GregorianCalendar(2021, 6, 23, 5,18),
                Sheduler.SHEDULEMODE.NORMAL,
                false);

        assertEquals("M2", timetable);
    }

    @Test
    public void getTimeTableAssignTest_shouldReturnAssignAForT2Afternoon() {
        String timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 6, 28, 14,8),
                Sheduler.SHEDULEMODE.NORMAL,
                false);

        assertEquals("A", timetable);
    }

    @Test
    public void getTimeTableAssignTest_shouldReturnAssignSSForT2SuperAfternoon() {
        String timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 6, 28, 14,8),
                Sheduler.SHEDULEMODE.SUPER,
                false);

        assertEquals("SS", timetable);
    }
}
