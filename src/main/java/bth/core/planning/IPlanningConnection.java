package bth.core.planning;

import bth.core.exception.HttpConnectionException;

public interface IPlanningConnection {
	public PlanningContent getTargetContent(final String url, final String user, final String password, final String proxyhost) throws HttpConnectionException;
}
