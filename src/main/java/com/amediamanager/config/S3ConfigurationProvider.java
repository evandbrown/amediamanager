package com.amediamanager.config;

import java.io.IOException;
import java.util.Properties;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class S3ConfigurationProvider implements ConfigurationProvider {

	private final String s3ConfigBucketEnvVarName = "S3_CONFIG_BUCKET";
	private String s3ConfigKeyEnvVarName = "S3_CONFIG_KEY";
	private String bucket;
	private String key;
	private Properties properties;

	public S3ConfigurationProvider() {
		this.bucket = System.getProperty(this.s3ConfigBucketEnvVarName);
		this.key = System.getProperty(this.s3ConfigKeyEnvVarName);
	}

	public Properties getProperties() {
		if (bucket != null && key != null) {
			AmazonS3 s3Client = new AmazonS3Client();
			try {
				S3Object object = s3Client.getObject(this.bucket, this.key);
				if (object != null) {
					properties = new Properties();
					try {
						properties.load(object.getObjectContent());
					} catch (IOException e) {
						properties = null;
						e.printStackTrace();
					} finally {
						try {
							object.close();
						} catch (IOException e) {
							// Don't care
						}
					}
				}
			} catch (AmazonS3Exception ase) {
				System.err.println("Error loading config from s3://" + this.bucket + "/" + this.key);
			}
		}
		return properties;
	}

	@Override
	public void refresh() {

	}

	@Override
	public String getPrettyName() {
		String source = "s3://" + this.bucket + "/" + this.key;
		return this.getClass().getSimpleName() + " (" + source + ")";
	}

}
