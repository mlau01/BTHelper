package bth.core.planning;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import bth.core.exception.PlanningDeserializeException;
import bth.core.exception.PlanningSerializeException;

public class JsonPlanningService implements ISerializePlanningService {
	
	private final static Logger logger = LogManager.getLogger();
	
	@Override
	public void serialize(Planning planning, String path) throws PlanningSerializeException {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(new File(path), planning);
		} catch (IOException e) {
			logger.error("Serialization error: {}", e.getMessage());
			throw new PlanningSerializeException(e.getMessage());
		}
		
	}

	@Override
	public Planning deserialize(String path) throws PlanningDeserializeException {
		ObjectMapper objectMapper = new ObjectMapper();
		Planning planning;
		try {
			planning = objectMapper.readValue(new File(path), Planning.class);
		} catch (IOException e) {
			logger.error("Deserialization error: {}", e.getMessage());
			throw new PlanningDeserializeException(e.getMessage());
		}
		
		return planning;
	}

}
