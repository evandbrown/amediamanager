package com.amediamanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public void update(Video video) throws DataSourceTableDoesNotExistException {
		// TODO Auto-generated method stub
		videoDao.update(video);
	}

	@Override
	public Video findById(String videoId)
			throws DataSourceTableDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List findByUserId(String email) {
		return videoDao.findByUserId(email);
		/*Video v = new Video();
		v.setCreatedDate(new Date());
		v.setUploadedDate(new Date());
		v.setDescription("I took this video with my iPhone!");
		v.setThumbnailKey("https://amm.s3.amazonaws.com/output/evbrown/web/eb-console-cap-00001.png");
		v.setPrivacy(Privacy.SHARED);
		v.setPreviewKey("https://amm.s3.amazonaws.com/output/evbrown/web/eb-console-cap.mp4");
		v.setS3Key("output/evbrown/web/eb-console-cap.mp4");
		v.setTitle("My cool video");
		v.setId(UUID.randomUUID().toString());
		
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
		
		return videos;*/
	}

	@Override
	public List<Video> findAllPublic(int limit, int start, int end)
			throws DataSourceTableDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

}
