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
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.amazonaws.auth.*;

/**
 * The ConfigurationSettings class is a singleton class that retrieves the settings from the 
 * aMediaManager.properties file or from the EC2 Metadata URL or Elastic Beanstalk Environment Metadata.
 */
@Component
public class ConfigurationSettings {
	
	/** Constants **/
	private static final String PROPS_FILE_PATH_ENV_VAR = "APP_CONFIG_FILE";
	private static final String DEFAULT_CONFIG_FILE_PATH = "/aMediaManager.properties";
	
	/** Available config properties **/
	public static enum ConfigProps {
		S3_UPLOAD_BUCKET,
		S3_UPLOAD_PREFIX,
		S3_PROFILE_PIC_PREFIX,
		DEFAULT_PROFILE_PIC_KEY,
		CF_STATIC_URL,
		CF_STREAMING_URL,
		RDS_DATABASE,
		RDS_USERNAME,
		RDS_PASSWORD,
		RDS_INSTANCEID,
		DDB_USERS_TABLE,
		SNS_NEW_CONTENT_TOPIC,
		AWS_REGION
	}
	
	/** Where config vals came frome **/
	public static enum ConfigSource {
		FROM_FILE,
		FROM_WAR
	}
	
	private ConfigSource configSource;
	
	/** Provider for AWS credentials (Access Key and Secret Key) **/
	private AWSCredentialsProvider credsProvider = new AWSCredentialsProviderChain(
			new SystemPropertiesCredentialsProvider(),
			new InstanceProfileCredentialsProvider()
			);
	
	private Properties props = null;
	
	/**
	 * Private constructor that is called by getInstance if the singleton does not exist.
	 */
	private ConfigurationSettings() {
		
		// Get any system properties
		Properties sysProps = System.getProperties();
		
		// Prepare to get other properties from a file
		Properties fileProps = new Properties();
		
        try {
        	// Determine path of properties file to load
        	String configFileToLoad = this.getAppConfigFilePath();
        	
        	// If a aMediaManager.properties file is present then use those properties
        	InputStream resourceFile = null;
        	
        	// If we're loading the default configuration, it must be retrieved as a resource
        	// from the classloader. Otherwise, load from a standard file
        	if(configFileToLoad.equals(DEFAULT_CONFIG_FILE_PATH)) {
        		resourceFile = this.getClass().getResourceAsStream(configFileToLoad);
        		this.configSource = ConfigSource.FROM_WAR;
        	} else {
        		resourceFile = new java.io.FileInputStream(configFileToLoad);
        		this.configSource = ConfigSource.FROM_FILE;
        	}
        	
        	fileProps = new Properties();
        	fileProps.load(resourceFile);
			resourceFile.close();
        } catch(NullPointerException e) {
        	System.err.println("Error loading configuration file: " + this.getAppConfigFilePath() + ". Confirm that this file exists.");
        } catch(UnknownHostException e) {
        	System.err.println(e.getMessage());
        } catch (IOException e) {
			System.err.println(e.getMessage());
		}
        
        // Merge the properties, with system properties taking precedence. This will allow file-based
        // config to be overridden with system props.
        Properties merged = new Properties();
        merged.putAll(fileProps);
        merged.putAll(sysProps);
        
        props = new Properties();
        
        // Remove any props we don't call out in the enumeration
        for(ConfigProps val : ConfigProps.values()) {
        	if(merged.containsKey(val.name())) {
        		props.put(val.name(), merged.getProperty(val.name()));
        	}
        }
        
        System.out.println("Effective app config (loaded from " + this.getAppConfigFilePath() + "):");
        props.list(System.out);
        System.out.println("---------------------");
        System.out.println("Effective AWS credential config:");
        System.out.println("Access Key=" + this.getAWSCredentials().getAWSAccessKeyId());
        System.out.println("Secret Key=" + this.getObfuscatedSecretKey());
		
	}
	
	/**
	 * Get the full path of the properties file that contains application
	 * configuration. The default config file bundled with the WAR will be
	 * used unless an OS env var is set with a different path.
	 * @return
	 */
	private String getAppConfigFilePath() {
		String filePath = (System.getProperty(PROPS_FILE_PATH_ENV_VAR) != null) ? System.getProperty(PROPS_FILE_PATH_ENV_VAR) : DEFAULT_CONFIG_FILE_PATH;
		
		return filePath;
	}
	
	/**
	 * Indicates where configuration for this application came from (either
	 * a file bundled with the app WAR or a file elsewhere on the FS)
	 * @return
	 */
	public ConfigSource getConfigSource() {
		return this.configSource;
	}
	
	/**
	 * Print readable ini-style config
	 * @return
	 */
	public String getReadableConfigSource() {
		switch(this.configSource) {
		case FROM_FILE: return "file (" + System.getProperty(PROPS_FILE_PATH_ENV_VAR) + ")";
		case FROM_WAR: return "WAR (" + DEFAULT_CONFIG_FILE_PATH + ")";
		default: return "Unknown";
		}
		
	}
	
	/**
	 * This method returns the AWS credentials object.
	 * @return	AWS credentials taken from the properties and user-data.
	 */
	public AWSCredentials getAWSCredentials() {
		return credsProvider.getCredentials();
	}
	
	public String getObfuscatedSecretKey() {
		return this.getAWSCredentials().getAWSSecretKey().substring(0, 4) + "******************" + this.getAWSCredentials().getAWSSecretKey().substring(this.getAWSCredentials().getAWSSecretKey().length()-4, this.getAWSCredentials().getAWSSecretKey().length()-1);
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
	 * Print effective config as a key=values\n string
	 * @return
	 */
	public String getPropertiesAsString(Properties props) {
		StringBuilder sb = new StringBuilder();
		Enumeration<?> e = props.propertyNames();
		while (e.hasMoreElements()){ 
			String key = (String) e.nextElement();
			sb.append(key);
			sb.append("=");
			sb.append(props.getProperty(key));
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * This method returns aMediaManager configuration settings as a string of key-value pairs.
	 * @return	aMediaManager configuration parameters from running environment.
	 */
	public String toString() {
		return getPropertiesAsString(this.props);
	}
}