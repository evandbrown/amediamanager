package com.amediamanager.config.challenge;

public class S3ConfigurationProvider extends com.amediamanager.config.S3ConfigurationProvider {
	@Override
	public void loadProperties() {
		/**
		 * - Use super.getBucket() and super.getKey() for the S3 bucke and key.
		 * - AmazonS3Client does not need credentials
		 * - Call super.setProperties with your result. 
		 */
		super.loadProperties();
	}
}
