package bth;

import bth.core.MONTH;
import bth.core.planning.PlanningManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;

public class PlanningManagerTest {

    @Test
    public void buildUrlTest(){
        PlanningManager planningManager = new PlanningManager(new Properties());
        String urlWithSlash = planningManager.buildUrl("http://www.test.com/", MONTH.AOUT);
        String urlWithoutSlash = planningManager.buildUrl("http://www.test.com", MONTH.AOUT);

        assertEquals("http://www.test.com/AOUT.htm", urlWithoutSlash);
        assertEquals("http://www.test.com/AOUT.htm", urlWithSlash);
    }
}
