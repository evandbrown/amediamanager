package com.amediamanager.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="tags")
public class Tag {
	private String tagId;
	private String name;
	private Set<Video> videos;
	
	public Tag() {}
	public Tag(String name) {
		this.tagId = name;
		this.name = name;
	}
	
	@Id
	@Column(name = "tagId", unique = true, nullable = false)
	public String getTagId() {
		return this.tagId;
	}
	
	@Column(name = "name")
	public String getName() {
		return this.name;
	}
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
	public Set<Video> getVideos() {
		return this.videos;
	}
	
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setVideos(Set<Video> videos) {
		this.videos = videos;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
