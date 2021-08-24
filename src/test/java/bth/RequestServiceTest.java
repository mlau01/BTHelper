package bth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import bth.core.datasource.sql.SQLManager;
import bth.core.exception.RequestException;
import bth.core.options.OptionException;
import bth.core.options.OptionService;
import bth.core.request.RequestService;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
	
	@Mock
	OptionService optionService;
	
	@Mock
	SQLManager sqlService;
	
	@Test
	public void getQueryListTest_shouldCorrectlyGetParsedQueries() throws OptionException, RequestException {
		Mockito.doReturn("Test=(SELECT * FROM hello WHERE things='hello');Test2=(COUNT * FROM ertty where date='2019-34-45'").when(optionService).get("queries");
		
		RequestService requestService = new RequestService(optionService, sqlService);
		
		List<String> queryList = requestService.getQueryList();
		
		assertEquals("Test", queryList.get(0));
		assertEquals("Test2", queryList.get(1));
		
		Mockito.verify(optionService).get("queries");
	}

}
