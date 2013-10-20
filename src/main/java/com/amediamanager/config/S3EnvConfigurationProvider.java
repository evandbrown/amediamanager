package com.amediamanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read S3 bucket and key from env
 */
public class S3EnvConfigurationProvider extends S3ConfigurationProvider {
	private static final Logger LOG = LoggerFactory.getLogger(S3EnvConfigurationProvider.class);
	public S3EnvConfigurationProvider() {
		try {
			super.setBucket(System.getProperty("S3_CONFIG_BUCKET"));
			super.setKey(System.getProperty("S3_CONFIG_KEY"));
		} catch (Exception ex) {
			LOG.info("No S3 configuration information found in environment.");
		}
	}
}
