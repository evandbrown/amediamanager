package com.amediamanager.config.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3ConfigurationProvider extends com.amediamanager.config.S3ConfigurationProvider {
	private static final Logger LOG = LoggerFactory.getLogger(S3ConfigurationProvider.class);
	
	@Override
	public void loadProperties() {
		/**
		 * - Use super.getBucket() and super.getKey() for the S3 bucket and key.
		 * - AmazonS3Client does not need credentials
		 * - Call super.setProperties with your result. 
		 */
		super.loadProperties();
	}
}
