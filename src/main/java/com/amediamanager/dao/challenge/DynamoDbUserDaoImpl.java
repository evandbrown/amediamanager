package com.amediamanager.dao.challenge;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.domain.User;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;

@Repository
public class DynamoDbUserDaoImpl extends com.amediamanager.dao.DynamoDbUserDaoImpl {
	@Override
	public User find(String email) throws DataSourceTableDoesNotExistException {
		/**
		 * Use config.getProperty(ConfigurationSettings.ConfigProps.DDB_USERS_TABLE)) to get the DDB table name
		 * 
		 * See super.HASH_KEY_NAME for name of table's hash key
		 * 
		 * 
		 */
		return super.find(email);
	}

}
