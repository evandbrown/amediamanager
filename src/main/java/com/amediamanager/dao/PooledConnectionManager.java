package com.amediamanager.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.commons.dbcp.BasicDataSource;

import com.amazonaws.services.rds.model.Endpoint;

import com.amediamanager.config.ConfigurationSettings;;

@Component
public class PooledConnectionManager implements ConnectionManager {

	@Autowired
	private DbEndpointRetriever dbEndpointRetriever;
	
	@Autowired
	private ConfigurationSettings config;
	
	private Lock connectionLock = new ReentrantLock();
	private BasicDataSource dataSource = new BasicDataSource();
	
	@PostConstruct
	public void init() {
		refreshDataSource();	
		
		// Uncomment to auto-detect Read Replicas every 60 seconds.
		/*new Thread(new Runnable() {
            public void run() {
                while(true) {
        			try {
            			// Once every 60 seconds
						Thread.sleep(60000); 
            			refreshDataSource();
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
            }
        }).start();	*/
	}
	
	public Connection getConnection() throws SQLException {
		if (dataSource == null)
			return null;

		Connection conn = null;

		connectionLock.lock();
		try {
			conn = dataSource.getConnection();
		} finally {
			connectionLock.unlock();
		}

		return conn;
	}
	
	private void refreshDataSource() {	
		
		final String masterId  = config.getProperty(ConfigurationSettings.ConfigProps.RDS_INSTANCEID);

		try {
			Endpoint master = dbEndpointRetriever.getMasterDbEndpoint(masterId);
			List<Endpoint> replicas = dbEndpointRetriever.getReadReplicaEndpoints(masterId);
			
			if (master != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("jdbc:mysql:");
				if (replicas != null) {
					builder.append("replication:");
				}
				builder.append("//" + master.getAddress() + ":" + master.getPort());
				if (replicas != null) {
					for (Endpoint endpoint : replicas) {
						builder.append("," + endpoint.getAddress() + ":" + endpoint.getPort());
					}			
				}
				builder.append("/" + config.getProperty(ConfigurationSettings.ConfigProps.RDS_DATABASE));
				String connectionString = builder.toString();
				
				if (!connectionString.equals(dataSource.getUrl())) {
					System.out.println("Creating new DB Connection pool: " + connectionString);
			
					BasicDataSource basicDataSource = new BasicDataSource();
					basicDataSource.setUrl(connectionString);
					if (replicas != null) {
						basicDataSource.setDriverClassName("com.mysql.jdbc.ReplicationDriver");
					} else {
						basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");			
					}
					basicDataSource.setDefaultReadOnly(false);
					basicDataSource.setUsername(config.getProperty(ConfigurationSettings.ConfigProps.RDS_USERNAME));
					basicDataSource.setPassword(config.getProperty(ConfigurationSettings.ConfigProps.RDS_PASSWORD));
					basicDataSource.setMaxWait(10);
					basicDataSource.setMaxActive(100);
					basicDataSource.setMaxIdle(25);
					basicDataSource.setInitialSize(10);
					basicDataSource.setValidationQuery("SELECT 1");
					
					this.dataSource = basicDataSource;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}