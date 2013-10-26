package com.amediamanager.service;

import java.util.List;

import com.amediamanager.dao.TagDao.TagCount;
import com.amediamanager.domain.Video;

public interface TagsService {

	List<TagCount> getTagsForUser(String user);
	List<Video> getVideosForUserByTag(String user, String tagId); 
}
