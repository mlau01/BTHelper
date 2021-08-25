package bth.core.planning;

import bth.core.exception.HttpConnectionException;
import bth.core.exception.PlanningConnectionException;

public interface IPlanningConnection {
	public PlanningContent getTargetContent(final String url, final String user, final String password, final String proxyhost) throws PlanningConnectionException;
}
