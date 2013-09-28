package com.amediamanager.dao;

import java.util.List;

import com.amazonaws.services.rds.model.Endpoint;

public interface DbEndpointRetriever {
	/**
	 * Retrieve the DNS endpoint for the specified DB
	 * @param dbInstanceId
	 * @return
	 */
	public Endpoint getMasterDbEndpoint(String dbInstanceId);
	
	/**
	 * Retrieve a list of DNS endpoints for any Read Replicas associated with
	 * the specified DB
	 * @param dbInstanceId
	 * @return
	 */
	public List<Endpoint> getReadReplicaEndpoints(String dbInstanceId);
}
