package com.amediamanager.dao.challenge;

import java.util.List;

import org.springframework.stereotype.Component;

import com.amazonaws.services.rds.model.Endpoint;

@Component
public class RdsDbEndpointRetriever extends com.amediamanager.dao.RdsDbEndpointRetriever {
	/**
	 * Return the Endpoint for the dbInstanceId 
	 */
	@Override
	public Endpoint getMasterDbEndpoint(String dbInstanceId) {
		return super.getMasterDbEndpoint(dbInstanceId);
	}
	
	/**
	 * First locate the master DB for the provided dbInstanceId, then
	 * locate and return any Read Replicas it may have. Be sure the
	 * replicas are in the 'available' state before returning 
	 */
	@Override
	public List<Endpoint> getReadReplicaEndpoints(String dbInstanceId) {
		return super.getReadReplicaEndpoints(dbInstanceId);
	}
}
