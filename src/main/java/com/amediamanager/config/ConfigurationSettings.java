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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;

/**
 * The ConfigurationSettings class is a singleton class that retrieves the settings from the 
 * aMediaManager.properties file or from the EC2 Metadata URL or Elastic Beanstalk Environment Metadata.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ConfigurationSettings {
	
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
	private final Properties props;
	private final ConfigurationProviderChain configProviderChain;

	@Autowired
	public ConfigurationSettings(final AWSCredentialsProvider credsProvider) throws IOException {
		this.credsProvider = credsProvider;
		this.configProviderChain = new ConfigurationProviderChain(
				new S3ConfigurationProvider(),
				new ClassResourceConfigurationProvider("/aMediaManager.properties")
				);
		
		// Get configuration from the provider chain
		props = configProviderChain.getProperties();
		
		System.out.println("Config provider: " + configProviderChain.getTheProvider().getClass().getSimpleName());
		System.out.println("---------------------");
        System.out.println("Effective config:");
        props.list(System.out);
        System.out.println("---------------------");
        System.out.println("Effective AWS credential config:");
        System.out.println("Access Key=" + this.getAWSCredentialsProvider().getCredentials().getAWSAccessKeyId());
        System.out.println("Secret Key=" + this.getObfuscatedSecretKey());
		
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
		return props.getProperty(property_name.name());
	}
	
	/**
	 * This method returns aMediaManager configuration settings as a string of key-value pairs.
	 * @return	aMediaManager configuration parameters from running environment.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Enumeration<?> e = this.props.propertyNames();
		while (e.hasMoreElements()){ 
			String key = (String) e.nextElement();
			sb.append(key);
			sb.append("=");
			sb.append(props.getProperty(key));
			sb.append("\n");
		}
		
		return sb.toString();
	}
}