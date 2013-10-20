package com.amediamanager.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read S3 bucket and key from file
 */
public class S3FileConfigurationProvider extends S3ConfigurationProvider {
	private static final Logger LOG = LoggerFactory.getLogger(S3FileConfigurationProvider.class);
	private static final String S3_CONFIG_FILE = "/s3config.properties";
	public S3FileConfigurationProvider() {
		InputStream stream = getClass().getResourceAsStream(S3_CONFIG_FILE);
        try {
            Properties properties = new Properties();
            properties.load(stream);
            super.setBucket(properties.getProperty("S3_CONFIG_BUCKET"));
            super.setKey(properties.getProperty("S3_CONFIG_KEY"));
        } catch (Exception e) {
            LOG.info("No S3 configuration information found in file.");
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // Don't care
            }
        }
	}
}
