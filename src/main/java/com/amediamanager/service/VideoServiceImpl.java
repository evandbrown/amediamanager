package com.amediamanager.service;

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
    public Video findByTranscodeJobId(final String jobId) throws DataSourceTableDoesNotExistException {
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
        if(null != videos) {
            for(Video video : videos) {

                Date expiration = new java.util.Date();
                long msec = expiration.getTime();
                msec += expirationInMillis;
                expiration.setTime(msec);

                GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(video.getBucket(), video.getOriginalKey());
                generatePresignedUrlRequest.setMethod(HttpMethod.GET);
                generatePresignedUrlRequest.setExpiration(expiration);

                video.setExpiringUrl(s3Client.generatePresignedUrl(generatePresignedUrlRequest));
            }
        }

        return videos;
    }

    /**
     * Default placeholder image for profile pic
     * @return
     */
    @Override
    public String getDefaultVideoPosterKey() {
        return "https://" + config.getProperty(ConfigurationSettings.ConfigProps.S3_UPLOAD_BUCKET) + ".s3.amazonaws.com/" + config.getProperty(ConfigurationSettings.ConfigProps.DEFAULT_VIDEO_POSTER_KEY);
    }

}
