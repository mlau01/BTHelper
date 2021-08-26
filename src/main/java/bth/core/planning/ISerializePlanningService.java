package bth.core.planning;

import bth.core.exception.PlanningDeserializeException;
import bth.core.exception.PlanningSerializeException;

public interface ISerializePlanningService {
	
	public void serialize(final Planning planning, String path) throws PlanningSerializeException;
	public Planning deserialize(String path) throws PlanningDeserializeException;

}
