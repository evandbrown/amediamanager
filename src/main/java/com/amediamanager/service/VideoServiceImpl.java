package com.amediamanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amediamanager.dao.VideoDao;
import com.amediamanager.domain.TagSet;
import com.amediamanager.domain.Video;
import com.amediamanager.domain.Privacy;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	VideoDao videoDao;
	
	@Override
	public void save(Video video) throws DataSourceTableDoesNotExistException {
		videoDao.save(video);
	}

	@Override
	public void update(Video user) throws DataSourceTableDoesNotExistException {
		// TODO Auto-generated method stub

	}

	@Override
	public Video findById(String videoId)
			throws DataSourceTableDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Video> findByUserEmail(String email)
			throws DataSourceTableDoesNotExistException {
		Video v = new Video();
		v.setCreatedDate(new Date());
		v.setDescription("I took this video with my iPhone!");
		v.setThumbnailKey("https://amm.s3.amazonaws.com/output/evbrown/web/eb-console-cap-00001.png");
		v.setPrivacy(Privacy.SHARED);
		v.setPreviewKey("https://amm.s3.amazonaws.com/output/evbrown/web/eb-console-cap.mp4");
		v.setS3Key("output/evbrown/web/eb-console-cap.mp4");
		v.setTitle("My cool video");
		
		TagSet<String> tags = new TagSet<String>();
		tags.add("vacation");
		tags.add("paris");
		tags.add("yolo");
		tags.add("callmemaybe");
		v.setTags(tags);
		
		List<Video> videos = new ArrayList<Video>();
		videos.add(v);
		videos.add(v);
		videos.add(v);
		videos.add(v);
		videos.add(v);
		
		return videos;
	}

	@Override
	public List<Video> findAllPublic(int limit, int start, int end)
			throws DataSourceTableDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

}
