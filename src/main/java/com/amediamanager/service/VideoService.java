package com.amediamanager.service;

import java.util.List;

import com.amediamanager.domain.Video;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;

public interface VideoService {

    public void save(Video user) throws DataSourceTableDoesNotExistException;;

    public void update(Video user) throws DataSourceTableDoesNotExistException;;

    public Video findById(String videoId) throws DataSourceTableDoesNotExistException;

    public List<Video> findByUserId(String email) throws DataSourceTableDoesNotExistException;

    public Video findByTranscodeJobId(String jobId) throws DataSourceTableDoesNotExistException;

    public List<Video> findAllPublic(int limit, int start, int end) throws DataSourceTableDoesNotExistException;

    public Video generateExpiringUrl(Video video, long expirationInMillis);
    
    public List<Video> generateExpiringUrls(List<Video> video, long expirationInMillis);

    public String getDefaultVideoPosterKey();
}
