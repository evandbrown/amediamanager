package com.amediamanager.dao.challenge;

import java.util.List;

import org.springframework.stereotype.Component;

import com.amazonaws.services.rds.model.Endpoint;

@Component
public class RdsDbEndpointRetriever extends com.amediamanager.dao.RdsDbEndpointRetriever {
	@Override
	public Endpoint getMasterDbEndpoint(String dbInstanceId) {
		return super.getMasterDbEndpoint(dbInstanceId);
	}
	
	@Override
	public List<Endpoint> getReadReplicaEndpoints(String dbInstanceId) {
		return super.getReadReplicaEndpoints(dbInstanceId);
	}
}
