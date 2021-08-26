package bth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import bth.core.exception.PlanningCharsetException;
import bth.core.exception.PlanningConnectionException;
import bth.core.planning.PlanningContent;
import bth.core.planning.PlanningFileConnection;

public class PlanningFileConnectionTest {
	
	@Test
	public void getTargetContentTest_shouldGetPlanningFile() throws PlanningConnectionException, PlanningCharsetException {
		PlanningFileConnection planningFileConnection = new PlanningFileConnection();
		
		PlanningContent content = planningFileConnection.getTargetContent("file://C:/Windows/System32/drivers/etc/hosts", null, null, null);
		
		Assertions.assertNotNull(content.getContent());
		Assertions.assertNotNull(content.getLastModified());
	}
	
	@Test
	public void cleanPathTest_shouldCleanProtocol() {
		PlanningFileConnection planningFileConnection = new PlanningFileConnection();
		
		String test = "file://C:/test";
		String cleaned = planningFileConnection.cleanPath(test);
		assertEquals("C:/test", cleaned);
	
		String test2 = "file:/C:/test";
		String cleaned2 = planningFileConnection.cleanPath(test2);
		assertEquals("C:/test", cleaned2);
		
		String test3 = "file:\\C:/test";
		String cleaned3 = planningFileConnection.cleanPath(test3);
		assertEquals("C:/test", cleaned3);
	}

}
