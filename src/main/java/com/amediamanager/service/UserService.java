package com.amediamanager.service;

import com.amediamanager.domain.User;
import com.amediamanager.exceptions.*;

public interface UserService {

	public void save(User user) throws UserExistsException, DataSourceTableDoesNotExistException;;

	public void update(User user) throws UserDoesNotExistException, DataSourceTableDoesNotExistException;;

	public User find (String email) throws DataSourceTableDoesNotExistException;
}
