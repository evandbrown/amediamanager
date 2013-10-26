package com.amediamanager.dao;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.amazonaws.services.rds.model.Endpoint;
import com.amediamanager.config.ConfigurationSettings;

/**
 * This custom DriverManagerDataSource retrieves DB connection information from
 * the ConfigurationSettings class. Most im
 * @author evbrown
 *
 */
@Component
public class RdsDriverManagerDataSource extends DriverManagerDataSource {
    private static final Logger LOG = LoggerFactory.getLogger(RdsDriverManagerDataSource.class);

    @Autowired
    ConfigurationSettings config;

    @Autowired
    com.amediamanager.dao.challenge.RdsDbEndpointRetriever dbEndpointRetriever;

    @PostConstruct
    public void init() {
        initializeDataSource();
    }

    @Override
    public String getUsername() {
        return config.getProperty(ConfigurationSettings.ConfigProps.RDS_USERNAME);
    }

    @Override
    public String getPassword() {
        return config.getProperty(ConfigurationSettings.ConfigProps.RDS_PASSWORD);
    }

    private void initializeDataSource() {
        // Use the RDS DB and the dbEndpointRetriever to discover the URL of the
        // database. If there
        // are read replicas, set the correct driver and use them.
        final String masterId = config
                .getProperty(ConfigurationSettings.ConfigProps.RDS_INSTANCEID);

        try {
            Endpoint master = dbEndpointRetriever.getMasterDbEndpoint(masterId);
            List<Endpoint> replicas = dbEndpointRetriever
                    .getReadReplicaEndpoints(masterId);

            if (master != null) {
                StringBuilder builder = new StringBuilder();
                builder.append("jdbc:mysql:");
                if (replicas != null) {
                    builder.append("replication:");
                    super.setDriverClassName("com.mysql.jdbc.ReplicationDriver");
                } else {
                    super.setDriverClassName("com.mysql.jdbc.Driver");
                }

                builder.append("//" + master.getAddress() + ":"
                        + master.getPort());
                if (replicas != null) {
                    for (Endpoint endpoint : replicas) {
                        builder.append("," + endpoint.getAddress() + ":"
                                + endpoint.getPort());
                    }
                }
                builder.append("/"
                        + config.getProperty(ConfigurationSettings.ConfigProps.RDS_DATABASE));
                String connectionString = builder.toString();

                super.setUrl(connectionString);
            }
        } catch (Exception e) {
            LOG.warn("Failed to initialize datasource.", e);
        }
    }
}
