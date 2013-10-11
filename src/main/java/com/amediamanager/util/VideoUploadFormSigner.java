package com.amediamanager.util;

import java.util.UUID;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amediamanager.domain.User;

/**
 * 
 * @author evbrown
 *
 */
public class VideoUploadFormSigner extends S3FormSigner {
	private String s3Bucket;
	private String objectKey;
	private String keyPrefix;
	private String successActionRedirect;
	private String encodedPolicy;
	private String signature;
	private String uuid;
	private User user;
	private AWSCredentialsProvider credsProvider;
	
	public VideoUploadFormSigner(String s3Bucket, String keyPrefix, User user, AWSCredentialsProvider credsProvider,
			String successActionRedirect) {
		this.s3Bucket = s3Bucket;
		this.keyPrefix = keyPrefix;
		this.successActionRedirect = successActionRedirect;
		this.user = user;
		this.credsProvider = credsProvider;
		this.uuid =  UUID.randomUUID().toString();
		
		String policy = super.generateUploadPolicy(s3Bucket, keyPrefix, credsProvider, successActionRedirect);
		String[] policyAndSig = super.signRequest(credsProvider, policy);
		
		// Create object key
		generateVideoObjectKey(this.uuid);
		
		// Create policy
		this.encodedPolicy = policyAndSig[0];
		this.signature = policyAndSig[1];
	}
	
	/**
	 * Generate a unique object key for this upload
	 */
	private void generateVideoObjectKey(String uuid) {
		this.objectKey = this.keyPrefix + "/original/" + user.getId() + "/" + uuid;
	}
	public AWSCredentialsProvider getCredsProvider() {
		return credsProvider;
	}
	public String getS3Bucket() {
		return s3Bucket;
	}
	public String getS3BucketUrl() {
		return "https://" + s3Bucket + ".s3.amazonaws.com/";
	}
	public void setS3BucketUrl(String s3BucketUrl) {
		this.s3Bucket = s3BucketUrl;
	}
	public String getObjectKey() {
		return objectKey;
	}
	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}
	public String getKeyPrefix() {
		return keyPrefix;
	}
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
	public Boolean getIsToken() {
		return credsProvider.getCredentials() instanceof BasicSessionCredentials;
	}
	public String getSuccessActionRedirect() {
		return successActionRedirect;
	}
	public void setSuccessActionRedirect(String successActionRedirect) {
		this.successActionRedirect = successActionRedirect;
	}
	public String getEncodedPolicy() {
		return encodedPolicy;
	}
	public void setPolicy(String policy) {
		this.encodedPolicy = policy;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
