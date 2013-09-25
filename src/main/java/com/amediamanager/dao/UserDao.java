package com.amediamanager.dao;

import com.amediamanager.domain.User;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;
import com.amediamanager.exceptions.UserDoesNotExistException;
import com.amediamanager.exceptions.UserExistsException;

public interface UserDao {

	public void save(User user) throws UserExistsException, DataSourceTableDoesNotExistException;

	public void update(User user) throws UserDoesNotExistException, DataSourceTableDoesNotExistException;
	
	public User find (String email) throws DataSourceTableDoesNotExistException;

}
