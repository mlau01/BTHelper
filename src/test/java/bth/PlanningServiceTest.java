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
public class PlanningServiceTest {

	@Mock
	OptionService optionService;
	
    @Test
    public void buildUrlWithHttpProtocolTest_shouldBuildUrlCorrecly() throws OptionException{
    	Mockito.doReturn("http://test").when(optionService).get(BTHelper.HttpUrl);
    	
        PlanningService planningService = new PlanningService(optionService);
        
        String urlWithSlash = planningService.buildUrl("http://www.test.com/", MONTH.AOUT);
        String urlWithoutSlash = planningService.buildUrl("http://www.test.com", MONTH.AOUT);

        assertEquals("http://www.test.com/AOUT.htm", urlWithoutSlash);
        assertEquals("http://www.test.com/AOUT.htm", urlWithSlash);
    }
    
    @Test
    public void buildUrlWithFileProtocolTest_shouldBuildUrlCorrecly() throws OptionException{
    	Mockito.doReturn("file://test").when(optionService).get(BTHelper.HttpUrl);
    	
        PlanningService planningService = new PlanningService(optionService);
        
        String urlWithSlash = planningService.buildUrl("file://C:/test/", MONTH.AOUT);
        String urlWithoutSlash = planningService.buildUrl("file://C:/test", MONTH.AOUT);

        assertEquals("file://C:/test/AOUT.htm", urlWithoutSlash);
        assertEquals("file://C:/test/AOUT.htm", urlWithSlash);
    }
}
