package com.amediamanager.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import org.apache.commons.lang.StringUtils;



@Entity
@Table(name="videos")
public class Video {

	private String id;
	private String owner;
	private String s3Key;
	private String title;
	private String description;
	private Date uploadedDate;
	private Date createdDate;
	private Privacy privacy = Privacy.PRIVATE;
	private Set<String> tag;
	private String thumbnailKey;
	private String previewKey;

	public Video() {
	}

	@Column
	@Id
	public String getId() {
		return id;
	}
	
	@Column
	public String getS3Key() {
		return s3Key;
	}

	@Column
	public String getOwner() {
		return owner;
	}

	@Column
	public Date getUploadedDate() {
		return uploadedDate;
	}

	@Column
	public Privacy getPrivacy() {
		return privacy;
	}

	@Column
	public String getTitle() {
		return title;
	}

	@Column
	public String getDescription() {
		return description;
	}

	@Column
	@ElementCollection
	@CollectionTable(name="tags", joinColumns=@JoinColumn(name="videoId"))
	public Set<String> getTag() {
		return tag;
	}

	@Column
	public Date getCreatedDate() {
		return createdDate;
	}
	
	@Column
	public String getPreviewKey() {
		return previewKey;
	}
	
	@Column
	public String getThumbnailKey() {
		return thumbnailKey;
	}
	
	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}
	
	public void setTitle(String title) {
		this.title = StringUtils.stripToNull(title);
	}

	public void setDescription(String description) {
		this.description = StringUtils.stripToNull(description);
	}

	public void setTag(Set<String> tag) {
		this.tag = tag;
	}
	
	public void setTags(HashSet<String> tags) {
		if(this.tag == null) {
			this.tag = new TagSet<String>();
		}
		for(String tag : tags) {
			this.tag.add(tag.replaceAll("\\s+",""));
		}
	}
	
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;		
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;		
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setThumbnailKey(String thumbnailKey) {
		this.thumbnailKey = thumbnailKey; 		
	}
	
	public void setPreviewKey(String previewKey) {
		this.previewKey = previewKey; 		
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
	}
}
