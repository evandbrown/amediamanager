package com.amediamanager.dao;

import com.amediamanager.domain.Video;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;

public interface VideoDao {
	public void save(Video video) throws DataSourceTableDoesNotExistException;
}
