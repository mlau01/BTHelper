package bth;

import bth.core.bt.Sheduler;
import org.junit.Test;

import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class ShedulerTest {

    @Test
    public void isWeekendDay_shouldReturnTrueForWeekendDay(){
        Sheduler sheduler = new Sheduler();

        assertTrue(sheduler.isWeekend(new GregorianCalendar(2021, 6, 31, 5,6)));
        assertFalse(sheduler.isWeekend(new GregorianCalendar(2021, 6, 30, 5,6)));
        assertTrue(sheduler.isWeekend(new GregorianCalendar(2021, 7, 1, 5,6)));
    }

    @Test
    public void getTimeTableAssignTest_shouldReturnCorrectTimetable(){
        Sheduler sheduler = new Sheduler();

        String timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 6, 31, 5,6),
                Sheduler.SHEDULEMODE.SUPER,
                true);

        assertEquals("SM", timetable);

        timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 6, 30, 8,0),
                Sheduler.SHEDULEMODE.NORMAL,
                false);

        assertEquals("M2", timetable);

        timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 6, 31, 19,18),
                Sheduler.SHEDULEMODE.SUPER,
                true);

        assertEquals("SS", timetable);

        timetable = sheduler.getTimeTableAssign("T1",
                new GregorianCalendar(2021, 7, 1, 22,18),
                Sheduler.SHEDULEMODE.SUPER,
                true);

        assertEquals("SS", timetable);
    }
}
