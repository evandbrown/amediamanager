package com.amediamanager.dao;

import java.util.List;

import com.amediamanager.domain.Video;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;

public interface VideoDao {
    public void save(Video video) throws DataSourceTableDoesNotExistException;
    public void update(Video video) throws DataSourceTableDoesNotExistException;
    public List<Video> findByUserId(String userId);
    public Video findByTranscodeJobId(String jobId);
    public Video findById(String id);
}
