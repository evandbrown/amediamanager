package com.amediamanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.dao.VideoDao;
import com.amediamanager.domain.Video;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	VideoDao videoDao;

	@Autowired
	AWSCredentialsProvider credentials;

	@Autowired
	AmazonS3 s3Client;

	@Autowired
	ConfigurationSettings config;

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
		return videoDao.findById(videoId);
	}

	@Override
	public List<Video> findByUserId(String email) {
		return videoDao.findByUserId(email);
	}

	@Override
	public Video findByTranscodeJobId(final String jobId)
			throws DataSourceTableDoesNotExistException {
		return videoDao.findByTranscodeJobId(jobId);
	}

	@Override
	public List<Video> findAllPublic(int limit, int start, int end)
			throws DataSourceTableDoesNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Video> generateExpiringUrls(List<Video> videos, long expirationInMillis) {
		List<Video> newVideos = null;
		if(null != videos) {
			newVideos = new ArrayList<Video>();
			for(Video video : videos) {
				newVideos.add(generateExpiringUrl(video, expirationInMillis));
			}
		}
		
		return newVideos;
	}
	@Override
	public Video generateExpiringUrl(Video video, long expirationInMillis) {

		Date expiration = new java.util.Date();
		long msec = expiration.getTime();
		msec += expirationInMillis;
		expiration.setTime(msec);

		// Expiring URL for original video
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
				video.getBucket(), video.getOriginalKey());
		generatePresignedUrlRequest.setMethod(HttpMethod.GET);
		generatePresignedUrlRequest.setExpiration(expiration);
		video.setExpiringUrl(s3Client
				.generatePresignedUrl(generatePresignedUrlRequest));

		// Expiring URL for preview video
		if (video.getPreviewKey() != null) {
			generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
					video.getBucket(), video.getPreviewKey());
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);
			generatePresignedUrlRequest.setExpiration(expiration);
			video.setExpiringPreviewKey(s3Client
					.generatePresignedUrl(generatePresignedUrlRequest));
		}

		// Expiring URL for original video
		if (video.getThumbnailKey() != null) {
			generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
					video.getBucket(), video.getThumbnailKey());
			generatePresignedUrlRequest.setMethod(HttpMethod.GET);
			generatePresignedUrlRequest.setExpiration(expiration);
			video.setExpiringThumbnailKey(s3Client
					.generatePresignedUrl(generatePresignedUrlRequest));
		}

		return video;
	}

	/**
	 * Default placeholder image for profile pic
	 * 
	 * @return
	 */
	@Override
	public String getDefaultVideoPosterKey() {
		return "https://"
				+ config.getProperty(ConfigurationSettings.ConfigProps.S3_UPLOAD_BUCKET)
				+ ".s3.amazonaws.com/"
				+ config.getProperty(ConfigurationSettings.ConfigProps.DEFAULT_VIDEO_POSTER_KEY);
	}

}
