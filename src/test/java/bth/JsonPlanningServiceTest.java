package bth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import bth.core.MONTH;
import bth.core.exception.PlanningDeserializeException;
import bth.core.exception.PlanningSerializeException;
import bth.core.planning.ISerializePlanningService;
import bth.core.planning.JsonPlanningService;
import bth.core.planning.Planning;

public class JsonPlanningServiceTest {
	
	@Test
	public void serializeTest_shouldCreatePlanningJsonFile() throws PlanningSerializeException, IOException {
		ISerializePlanningService jsonPlanningService = new JsonPlanningService();
		ArrayList<ArrayList<String>> planningArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> planningRow = new ArrayList<String>();
		planningArray.add(planningRow);
		planningRow.add("test");

		Planning planning = new Planning(MONTH.AOUT, planningArray, 23, Calendar.getInstance());
		String path = BTHelper.CONF_DIRECTORY + "/" + planning.getMonth().toString() + ".json";
		
		jsonPlanningService.serialize(planning, path);
		
		assertTrue(Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS));
		Files.delete(Paths.get(path));
	}
	
	@Test
	public void deserializeTest_shouldConstructPlanningCorrecly() throws PlanningSerializeException, PlanningDeserializeException, IOException {
		
		ISerializePlanningService jsonPlanningService = new JsonPlanningService();
		ArrayList<ArrayList<String>> planningArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> planningRow = new ArrayList<String>();
		planningArray.add(planningRow);
		planningRow.add("test");

		Planning planning = new Planning(MONTH.AOUT, planningArray, 23, Calendar.getInstance());
		String path = BTHelper.CONF_DIRECTORY + "/" + planning.getMonth().toString() + ".json";
		
		jsonPlanningService.serialize(planning, path);
		
		Planning planningDeserialized = jsonPlanningService.deserialize(path);
		
		assertEquals(MONTH.AOUT, planningDeserialized.getMonth());
		assertEquals(23, planningDeserialized.getLastModified());
		
		assertTrue(Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS));
		Files.delete(Paths.get(path));
		
	}

}
