/*
 * Copyright 2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License
 * is located at
 *
 *      http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amediamanager.config;

import java.io.IOException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;

/**
 * The ConfigurationSettings class is a singleton class that retrieves the settings from the
 * aMediaManager.properties file or from the EC2 Metadata URL or Elastic Beanstalk Environment Metadata.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ConfigurationSettings {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationSettings.class);

	/** Available config properties **/
	public static enum ConfigProps {
		S3_UPLOAD_BUCKET,
		S3_UPLOAD_PREFIX,
		S3_PROFILE_PIC_PREFIX,
		DEFAULT_PROFILE_PIC_KEY,
		DEFAULT_VIDEO_POSTER_KEY,
		RDS_DATABASE,
		RDS_USERNAME,
		RDS_PASSWORD,
		RDS_INSTANCEID,
		DDB_USERS_TABLE,
		AWS_REGION,
		TRANSCODE_TOPIC,
		TRANSCODE_QUEUE,
		TRANSCODE_ROLE
	}

	private final AWSCredentialsProvider credsProvider;
	private final ConfigurationProviderChain configProviderChain;

	@Autowired
	public ConfigurationSettings(final AWSCredentialsProvider credsProvider) throws IOException {
		this.credsProvider = credsProvider;
		this.configProviderChain = new ConfigurationProviderChain(
				new S3ConfigurationProvider(),
				new ClassResourceConfigurationProvider("/aMediaManager.properties")
				);

		LOG.info("Config provider: " + this.configProviderChain.getTheProvider().getClass().getSimpleName());
		LOG.info("---------------------");
        LOG.info("Effective config:");
        this.configProviderChain.getProperties().list(System.out);
        LOG.info("---------------------");
        LOG.info("Effective AWS credential config:");
        LOG.info("Access Key=" + this.getAWSCredentialsProvider().getCredentials().getAWSAccessKeyId());
        LOG.info("Secret Key=" + this.getObfuscatedSecretKey());

	}

	/**
	 * This method returns the AWS credentials object.
	 * @return	AWS credentials taken from the properties and user-data.
	 */
	public AWSCredentialsProvider getAWSCredentialsProvider() {
		return credsProvider;
	}

	/**
	 * Thsi method returns the ConfigurationProvider
	 * @return
	 */
	public ConfigurationProvider getConfigurationProvider() {
		return configProviderChain.getTheProvider();
	}

	@Scheduled(fixedDelay=60000)
	public void refreshConfigurationProvider() {
		this.configProviderChain.refresh();
	}

	public String getObfuscatedSecretKey() {
		return this.getAWSCredentialsProvider().getCredentials().getAWSSecretKey().substring(0, 4) + "******************" + this.getAWSCredentialsProvider().getCredentials().getAWSSecretKey().substring(this.getAWSCredentialsProvider().getCredentials().getAWSSecretKey().length()-4, this.getAWSCredentialsProvider().getCredentials().getAWSSecretKey().length()-1);
	}

	/**
	 * Accessor for the various properties in the configuration.
	 *
	 * @param propertyName	the name of the property key.  The static strings on this class can also be used.
	 * @return	the value of the property.
	 */
	public String getProperty(ConfigurationSettings.ConfigProps property_name) {
		return configProviderChain.getProperties().getProperty(property_name.name());
	}

	/**
	 * This method returns aMediaManager configuration settings as a string of key-value pairs.
	 * @return	aMediaManager configuration parameters from running environment.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Enumeration<?> e = this.configProviderChain.getProperties().propertyNames();
		while (e.hasMoreElements()){
			String key = (String) e.nextElement();
			sb.append(key);
			sb.append("=");
			sb.append(configProviderChain.getProperties().getProperty(key));
			sb.append("\n");
		}

		return sb.toString();
	}
}