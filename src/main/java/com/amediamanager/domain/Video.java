package com.amediamanager.domain;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class Video {

	private String owner;
	private String s3Key;
	private String title;
	private String description;
	private Date uploadedDate;
	private Date createdDate;
	private Privacy privacy = Privacy.PRIVATE;
	private TagSet<String> tags;
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

	public void setTags(TagSet<String> tags) {
		this.tags = tags;
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;		
	}

	protected void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;		
	}

	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public TagSet<String> getTags() {
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
	
	public String getS3Key() {
		return s3Key;
	}

}
