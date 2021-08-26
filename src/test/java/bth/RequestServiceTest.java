package bth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Disabled;
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
	
	private final String TEST_QUERIES_AS_STRING = "Test#SELECT * FROM hello WHERE things='hello';Test2#COUNT * FROM ertty where date='2019-34-45'";
	
	@Mock
	OptionService optionService;
	
	@Mock
	SQLManager sqlService;
	
	@Test
	public void getQueriesTitle_shouldCorrectlyGetParsedQueriesTitle() throws OptionException, RequestException {
		Mockito.doReturn(TEST_QUERIES_AS_STRING).when(optionService).get("queries");
		
		RequestService requestService = new RequestService(optionService, sqlService);
		
		List<String> queriesTitle = requestService.getQueriesTitle();
		
		assertEquals("Test", queriesTitle.get(0));
		assertEquals("Test2", queriesTitle.get(1));
		
		Mockito.verify(optionService).get("queries");
	}
	
	@Test
	public void getQueryTest_shouldCorrectlyGetParsedQuery() throws OptionException, RequestException {
		Mockito.doReturn(TEST_QUERIES_AS_STRING).when(optionService).get("queries");
		
		RequestService requestService = new RequestService(optionService, sqlService);
		
		String query1 = requestService.getQueryValue("Test");
		String query2 = requestService.getQueryValue("Test2");
		
		assertEquals("SELECT * FROM hello WHERE things='hello'", query1);
		assertEquals("COUNT * FROM ertty where date='2019-34-45'", query2);
		
		Mockito.verify(optionService, Mockito.times(1)).get("queries");
	}
	
	@Test
	public void saveQueriesTest_shouldCallOptionServiceWithRightParams() throws OptionException, RequestException {
		Mockito.doReturn(TEST_QUERIES_AS_STRING).when(optionService).get("queries");
		RequestService requestService = new RequestService(optionService, sqlService);
		
		requestService.saveQueries();
		
		Mockito.verify(optionService).set("queries", TEST_QUERIES_AS_STRING);
	}
	
	@Test
	public void writeQueryTest_shouldCorrectlyWriteQuery() throws RequestException, OptionException {
		Mockito.doReturn(TEST_QUERIES_AS_STRING).when(optionService).get("queries");
		RequestService requestService = new RequestService(optionService, sqlService);
		String test3 = "SELECT * FROM hey WHERE tupo='solid'";
		
		requestService.writeQuery("test3", test3);
		
		String query1 = requestService.getQueryValue("test3");
		
		assertEquals("SELECT * FROM hey WHERE tupo='solid'", query1);
		
		Mockito.verify(optionService).get("queries");
		Mockito.verify(optionService).set(BTHelper.Queries,TEST_QUERIES_AS_STRING + ";" + "test3#" + test3);
	}
	
	@Test
	public void delQueryTest_shouldCorrectlyDeleteQuery() throws RequestException, OptionException {
		Mockito.doReturn(TEST_QUERIES_AS_STRING).when(optionService).get("queries");
		RequestService requestService = new RequestService(optionService, sqlService);
		
		String test3 = "SELECT * FROM hey WHERE tupo='solid'";
		requestService.writeQuery("test3", test3);
		String query1 = requestService.getQueryValue("test3");
		assertEquals("SELECT * FROM hey WHERE tupo='solid'", query1);
		
		requestService.delQuery("test3");

		assertThrows(RequestException.class, () -> requestService.getQueryValue("test3"));
		
		Mockito.verify(optionService).get("queries");
		
		Mockito.verify(optionService).set("queries", TEST_QUERIES_AS_STRING);
	}

}
