package com.amediamanager.service;

import java.util.List;

import com.amediamanager.domain.Video;
import com.amediamanager.exceptions.*;

public interface VideoService {

	public void save(Video user) throws DataSourceTableDoesNotExistException;;

	public void update(Video user) throws DataSourceTableDoesNotExistException;;

	public Video findById (String videoId) throws DataSourceTableDoesNotExistException;
	
	public List<Video> findByUserEmail (String email) throws DataSourceTableDoesNotExistException;
	
	public List<Video> findAllPublic (int limit, int start, int end) throws DataSourceTableDoesNotExistException;
}
