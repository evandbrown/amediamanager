package com.amediamanager.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.rds.AmazonRDSClient;

import com.amediamanager.config.ConfigurationSettings;

@Service
public class AwsClientService {

	@Autowired
	ConfigurationSettings config;
	
	private AmazonDynamoDBClient dynamoDbClient;
	private AmazonS3Client s3Client;
	private AmazonRDSClient amazonRdsClient;
	private AmazonCloudWatchClient cloudWatchClient;
	private Region region;
	
	@PostConstruct
	public void init() {
		region = Region.getRegion((Regions.fromName(config.getProperty(ConfigurationSettings.ConfigProps.AWS_REGION))));
		
		// DynamoDB
		dynamoDbClient = new AmazonDynamoDBClient();
		dynamoDbClient.setRegion(region);
		
		// S3
		s3Client = new AmazonS3Client();
		s3Client.setRegion(region);
		
		// RDS
		amazonRdsClient = new AmazonRDSClient();
		amazonRdsClient.setRegion(region);
		
		// CloudWatch
		cloudWatchClient = new AmazonCloudWatchClient();
		cloudWatchClient.setRegion(region);
	}

	public AmazonDynamoDBClient getDynamoDbClient() {
		return dynamoDbClient;
	}

	public void setDynamoDbClient(AmazonDynamoDBClient dynamoDbClient) {
		this.dynamoDbClient = dynamoDbClient;
	}

	public AmazonS3Client getS3Client() {
		return s3Client;
	}

	public void setS3Client(AmazonS3Client s3Client) {
		this.s3Client = s3Client;
	}

	public AmazonRDSClient getAmazonRdsClient() {
		return amazonRdsClient;
	}

	public void setAmazonRdsClient(AmazonRDSClient amazonRdsClient) {
		this.amazonRdsClient = amazonRdsClient;
	}
	
	public AmazonCloudWatchClient getCloudWatchClient() {
		return cloudWatchClient;
	}
	
	public void setCloudWatchClient(AmazonCloudWatchClient client) {
		this.cloudWatchClient = client;
	}
	
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}
}
