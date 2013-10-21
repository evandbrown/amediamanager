package com.amediamanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read S3 bucket and key from env
 */
public class S3EnvConfigurationProvider extends com.amediamanager.config.challenge.S3ConfigurationProvider {
	private static final Logger LOG = LoggerFactory.getLogger(S3EnvConfigurationProvider.class);
	public S3EnvConfigurationProvider() {
		try {
			LOG.debug("Attempting to create S3ConfigurationProvider with bucket and key from env");
			super.setBucket(System.getProperty("S3_CONFIG_BUCKET"));
			super.setKey(System.getProperty("S3_CONFIG_KEY"));
		} catch (Exception ex) {
			LOG.debug("No S3 configuration information found in environment.");
		}
	}
}
