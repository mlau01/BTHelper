package bth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import bth.core.MONTH;
import bth.core.options.OptionException;
import bth.core.options.OptionService;
import bth.core.planning.PlanningService;

@ExtendWith(MockitoExtension.class)
public class PlanningManagerTest {

	@Mock
	OptionService optionService;
	
    @Test
    public void buildUrlTest() throws OptionException{
    	Mockito.doReturn("http://test").when(optionService).get(BTHelper.HttpUrl);
        PlanningService planningManager = new PlanningService(optionService);
        
        String urlWithSlash = planningManager.buildUrl("http://www.test.com/", MONTH.AOUT);
        String urlWithoutSlash = planningManager.buildUrl("http://www.test.com", MONTH.AOUT);

        assertEquals("http://www.test.com/AOUT.htm", urlWithoutSlash);
        assertEquals("http://www.test.com/AOUT.htm", urlWithSlash);
    }
}
