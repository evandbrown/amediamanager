package com.amediamanager.dao;

import java.util.List;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.Endpoint;

import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.service.AwsClientService;

/**
 * @author evbrown
 *
 */
@Component
public class RdsDbEndpointRetriever implements DbEndpointRetriever {

	@Autowired
	private ConfigurationSettings config;
	
	@Autowired
	private AwsClientService awsClientService;
	
	private AmazonRDSClient rds;
	
	@PostConstruct
	public void init() {
		rds = awsClientService.getAmazonRdsClient();
	}

	/* (non-Javadoc)
	 * @see com.aMediaManager.DataAccess.DbEndpointRetriever#getMasterDbEndpoint(java.lang.String)
	 */
	@Override
	public Endpoint getMasterDbEndpoint(String dbInstanceId) {
		Endpoint endpoint = null;
		
		DescribeDBInstancesResult result = rds.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier(dbInstanceId));
		
		if(result.getDBInstances().size() == 1) {
			endpoint = result.getDBInstances().get(0).getEndpoint();
		}
		
		return endpoint;
	}

	/* (non-Javadoc)
	 * @see com.aMediaManager.DataAccess.DbEndpointRetriever#getReadReplicaEndpoints(java.lang.String)
	 */
	@Override
	public List<Endpoint> getReadReplicaEndpoints(String dbInstanceId) {
		List<Endpoint> endpoints = null;
		
		DescribeDBInstancesResult result = rds.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier(dbInstanceId));
		
		// If the master exist and has any read replicas
		if(result.getDBInstances().size() == 1 && result.getDBInstances().get(0).getReadReplicaDBInstanceIdentifiers().size() > 0) {
			endpoints = new ArrayList<Endpoint>();
			for(String readReplicaId : result.getDBInstances().get(0).getReadReplicaDBInstanceIdentifiers()) {
				DBInstance rrInstance = rds.describeDBInstances(new DescribeDBInstancesRequest().withDBInstanceIdentifier(readReplicaId)).getDBInstances().get(0);
				if (rrInstance.getDBInstanceStatus().equals("available")) {
					endpoints.add(rrInstance.getEndpoint());
				}
			}
			
		}
		
		return endpoints;
	}

	/*
	# If the master DB identifier were available in the constructor, this would be a more efficient way to retrieve
	# the master and read replica endpoints as it limits the number of API requests. 
	
	String marker = null;			
	do {
		DescribeDBInstancesResult result = rds.describeDBInstances(new DescribeDBInstancesRequest().withMarker(marker));
		
		for (DBInstance instance : result.getDBInstances()) {
			if (masterId.equals(instance.getDBInstanceIdentifier())) {
				master = instance.getEndpoint();
			} else if (masterId.equals(instance.getReadReplicaSourceDBInstanceIdentifier())) {
				if (instance.getDBInstanceStatus().equals("available")) {
					replicas.add(instance.getEndpoint());
				}
			}
		}			
		marker = result.getMarker();
		
	} while (marker != null);
	*/
	
}
