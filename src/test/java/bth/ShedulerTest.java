package bth;

import bth.core.bt.Sheduler;
import org.junit.Test;

import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class ShedulerTest {

    @Test
    public void getTimeTableAssignTest_shouldReturnCorrectTimetable(){
        Sheduler sheduler = new Sheduler();

        String timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 7, 31, 5,6),
                Sheduler.SHEDULEMODE.SUPER,
                true);

        assertEquals("SM", timetable);

        timetable = sheduler.getTimeTableAssign("T2",
                new GregorianCalendar(2021, 7, 30, 8,0),
                Sheduler.SHEDULEMODE.NORMAL,
                false);

        assertEquals("M2", timetable);
    }
}
