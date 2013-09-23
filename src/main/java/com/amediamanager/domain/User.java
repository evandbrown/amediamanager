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

package com.amediamanager.domain;

/**
 * DefaultUser is an implementation of the User interface. The password property
 * for this type of user should be MD5 hashed before setting the value. This
 * class provides a static method to perform the hash.
 */

public class User {

	private String id;
	private String email;
	private String password;
	private String nickname;
	private String tagline;
	private String profilePicKey;
	private Boolean alertOnNewContent;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * If the Tagline is null, return an empty string.
	 */
	public String getTagline() {
		return this.tagline == null ? "" : this.tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;

	}

	public String getProfilePicKey() {
		return this.profilePicKey;
	}

	public void setProfilePicKey(String profilePicKey) {
		this.profilePicKey = profilePicKey;

	}

	private Boolean passwordMatches(String plainTextPassword) {
		return MD5HashPassword(plainTextPassword).equals((this.getPassword()));
	}

	/**
	 * Convert a plain-text password string to an MD5 hex string
	 * 
	 * @param plainTextPassword
	 *            The plain-text password to be encoded as an MD5 hex string
	 * 
	 * @return MD5-encoded hex string of the provided password.
	 */
	public static String MD5HashPassword(String plainTextPassword) {
		return org.apache.commons.codec.digest.DigestUtils
				.md5Hex(plainTextPassword);
	}
}
