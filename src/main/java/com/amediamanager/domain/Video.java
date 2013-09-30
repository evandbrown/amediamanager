package com.amediamanager.domain;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class Video {

	public enum Privacy {
		Public, Private, Shared
	};
	
	private String s3Key;
	private String owner;
	private Date uploadedDate;
	private String title;
	private Privacy privacy = Privacy.Private;;
	private String description;
	private Date createdDate;
	private String tags;
	private String thumbnailKey;
	private String previewKey;

	public Video() {
	}

	public void setTitle(String title) {
		this.title = StringUtils.stripToNull(title);
	}

	public void setDescription(String description) {
		this.description = StringUtils.stripToNull(description);
	}

	public void setTags(String tags) {
		this.tags = StringUtils.stripToNull(tags);
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;		
	}

	protected void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;		
	}
	
	public String getKey() {
		return s3Key;
	}

	public String getOwner() {
		return owner;
	}

	public Date getUploadedDate() {
		return uploadedDate;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getTags() {
		return tags;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setThumbnailKey(String thumbnailKey) {
		this.thumbnailKey = thumbnailKey; 		
	}
	
	public String getThumbnailKey() {
		return thumbnailKey;
	}
	
	public void setPreviewKey(String previewKey) {
		this.previewKey = previewKey; 		
	}
	
	public String getPreviewKey() {
		return previewKey;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
	}

}
