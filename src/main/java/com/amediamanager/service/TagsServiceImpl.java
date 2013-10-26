package com.amediamanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amediamanager.dao.TagDao;
import com.amediamanager.dao.TagDao.TagCount;
import com.amediamanager.domain.Video;

@Service
public class TagsServiceImpl implements TagsService {

	@Autowired private TagDao tagDao; 

	@Override
	public List<TagCount> getTagsForUser(String user) {
		return tagDao.getTagsForUser(user);
	}

	@Override
	public List<Video> getVideosForUserByTag(String user, String tagId) {
		return tagDao.getVideosForUserByTag(user, tagId);
	}

	
}
