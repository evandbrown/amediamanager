	package com.amediamanager.domain;

import java.net.URL;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

@Entity
@Table(name="videos")
public class Video {

	private String id;
	private String owner;
	private String bucket;
	private String originalKey;
	private String thumbnailKey;
	private String previewKey;
	private String title;
	private String description;
	private Date uploadedDate;
	private Date createdDate;
	private Privacy privacy = Privacy.PRIVATE;
	private Set<Tag> tags;
	private URL expiringUrl;

	public Video() {
	}

	@Id
	@Column(name = "videoId", unique = true, nullable = false)
	public String getId() {
		return id;
	}
	
	@Column
	public String getOriginalKey() {
		return originalKey;
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
	@Enumerated(EnumType.STRING)
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
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "videos_tags", joinColumns = { 
			@JoinColumn(name = "videoId", nullable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "tagId", nullable = false) })
	public Set<Tag> getTags() {
		return tags;
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
	
	@Column
	public String getBucket() {
		return bucket;
	}
	
	@Transient
	public URL getExpiringUrl() {
		return expiringUrl;
	}
	
	public void setExpiringUrl(URL expiringUrl) {
		this.expiringUrl = expiringUrl;
	}
	
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	
	public void setOriginalKey(String originalKey) {
		this.originalKey = originalKey;
	}
	
	public void setTitle(String title) {
		this.title = StringUtils.stripToNull(title);
	}

	public void setDescription(String description) {
		this.description = StringUtils.stripToNull(description);
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
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
