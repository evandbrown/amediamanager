package com.amediamanager.springconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amediamanager.config.ClassResourceConfigurationProvider;
import com.amediamanager.config.ConfigurationProviderChain;
import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.config.S3ConfigurationProvider;

@Configuration
public class ServerConfig {
	
	@Bean
	@Scope(WebApplicationContext.SCOPE_APPLICATION)
	public AWSCredentialsProvider credentials() {
		return new AWSCredentialsProviderChain(
				new SystemPropertiesCredentialsProvider(),
				new InstanceProfileCredentialsProvider()
				);
	}
	
	@Bean
	@Scope(WebApplicationContext.SCOPE_APPLICATION)
	public Region region(final ConfigurationSettings settings) {
		return Region.getRegion(Regions.fromName(settings.getProperty(ConfigurationSettings.ConfigProps.AWS_REGION)));

	}

	@Bean
	@Scope(WebApplicationContext.SCOPE_APPLICATION)
	public AmazonElasticTranscoder transcodeClient(final AWSCredentialsProvider creds,
												   final Region region) {
		return region.createClient(AmazonElasticTranscoderClient.class, creds, null);
	}

	@Bean
	@Scope(WebApplicationContext.SCOPE_APPLICATION)
	public AmazonS3 s3Client(final AWSCredentialsProvider creds,
							 final Region region) {
		return region.createClient(AmazonS3Client.class, creds, null);
	}

	@Bean
	@Scope(WebApplicationContext.SCOPE_APPLICATION)
	public AmazonRDS rdsClient(final AWSCredentialsProvider creds,
							   final Region region) {
		return region.createClient(AmazonRDSClient.class, creds, null);
	}

	@Bean
	@Scope(WebApplicationContext.SCOPE_APPLICATION)
	public AmazonDynamoDB dynamoClient(final AWSCredentialsProvider creds,
							   final Region region) {
		return region.createClient(AmazonDynamoDBClient.class, creds, null);
	}

	@Bean
	@Scope(WebApplicationContext.SCOPE_APPLICATION)
	public AmazonCloudWatch cloudwatchClient(final AWSCredentialsProvider creds,
							   				 final Region region) {
		return region.createClient(AmazonCloudWatchClient.class, creds, null);
	}
	
}
