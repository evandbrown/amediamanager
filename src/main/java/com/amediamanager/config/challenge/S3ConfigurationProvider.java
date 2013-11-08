package com.amediamanager.config.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3ConfigurationProvider extends com.amediamanager.config.S3ConfigurationProvider {
	private static final Logger LOG = LoggerFactory.getLogger(S3ConfigurationProvider.class);
	
	@Override
	public void loadProperties() {
		/**
		 * ** CHALLENGE **
		 * - Create an AmazonS3Client (does not need creds or a region)
		 * - Retrieve the object stored in bucket super.getBucket() with key super.getKey()
		 * - Call super.setProperties with your result. 
		 */
		super.loadProperties();
	}
}
