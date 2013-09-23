package com.amediamanager.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

import com.amediamanager.config.ConfigurationSettings;
import com.amediamanager.domain.User;
import com.amediamanager.exceptions.*;

@Repository
public class DynamoDbUserDaoImpl implements UserDao {

	private ConfigurationSettings configs;
	private AmazonDynamoDBClient client;

	/** DynamoDB config **/
	public static final String HASH_KEY_NAME = "EMail";
	public static final String EMAIL_ATTR = HASH_KEY_NAME;
	public static final String PASSWORD_ATTR = "Password";
	public static final String NICKNAME_ATTR = "Nickname";
	public static final String TAGLINE_ATTR = "Tagline";
	public static final String PROFILE_PIC_KEY_ATTR = "ProfilePicKey";
	public static final String ALERT_ON_NEW_CONTENT_ATTR = "AlertOnNewContent";

	public DynamoDbUserDaoImpl() {
		configs = ConfigurationSettings.getInstance();
		// Get a DynamoDB client
		client = new AmazonDynamoDBClient(configs.getAWSCredentials());
	}

	@Override
	public void save(User user)
			throws UserExistsException, DataSourceTableDoesNotExistException {
		try {
			// See if User item exists
			User existing = this.find(user.getEmail());
			
			// If the user exists, throw an exception
			if(existing != null) {
				throw new UserExistsException();
			}
			
			// Convert the User object to a Map. The DynamoDB PutItemRequest object
			// requires the Item to be in the Map<String, AttributeValue> structure
			Map<String, AttributeValue> userItem = getMapFromUser(user);
			
			// Create a request to save and return the user
			PutItemRequest putItemRequest = new PutItemRequest()
												.withTableName(configs.getProperty(ConfigurationSettings.ConfigProps.DDB_USERS_TABLE))
												.withItem(userItem);
			
			// Save user
			client.putItem(putItemRequest);
		} catch (ResourceNotFoundException rnfe) {
			throw new DataSourceTableDoesNotExistException();
		} catch (AmazonServiceException ase) {
			throw ase;
		}
	}

	@Override
	public void update(User user) throws UserDoesNotExistException, DataSourceTableDoesNotExistException {
		try {			
			// Make sure the user exists
			User existing = this.find(user.getEmail());
			
			// If the user does not exist, throw an exception
			if(null == existing) {
				throw new UserDoesNotExistException();
			}
			
			// Convert the User object to a Map
			Map<String, AttributeValue> userItem = getMapFromUser(user);
			
			// Create a request to save and return the user
			PutItemRequest putItemRequest = new PutItemRequest()
												.withItem(userItem)
												.withTableName(configs.getProperty(ConfigurationSettings.ConfigProps.DDB_USERS_TABLE));
			
			// Save user
			client.putItem(putItemRequest);
		} catch (ResourceNotFoundException rnfe) {
			throw new DataSourceTableDoesNotExistException();
		} catch (AmazonServiceException ase) {
			throw ase;
		}

	}

	@Override
	public User find(String email) throws DataSourceTableDoesNotExistException {
		try {

			User user = null;

			// Create a request to find a User by email address
			GetItemRequest getItemRequest = new GetItemRequest()
					.withTableName(
							configs.getProperty(ConfigurationSettings.ConfigProps.DDB_USERS_TABLE))
					.addKeyEntry(HASH_KEY_NAME, new AttributeValue(email));

			// Issue the request to find the User in DynamoDB
			GetItemResult getItemResult = client.getItem(getItemRequest);

			// If an item was found
			if (getItemResult.getItem() != null) {
				// Marshal the Map<String, AttributeValue> structure returned in
				// the
				// GetItemResult to a User object
				user = getUserFromMap(getItemResult.getItem());
			}

			return user;

		} catch (ResourceNotFoundException rnfe) {

			// The ResourceNotFoundException method is thrown by the getItem()
			// method
			// if the DynamoDB table doesn't exist. This exception is re-thrown
			// as a
			// custom, more specific DataSourceTableDoesNotExistException that
			// users
			// of this DAO understand.
			throw new DataSourceTableDoesNotExistException();
		}
	}

	/**
	 * Marshal a Map<String, AttributeValue> object (representing an Item
	 * retrieved from DynamoDB) to a User.
	 * 
	 * @return
	 */
	private User getUserFromMap(Map<String, AttributeValue> userItem) {
		// Create a new user from the minimum required values. As the password
		// is stored
		// hashed, the last parameter is false to indicate.
		User user = new User();
		
		user.setEmail(userItem.get(EMAIL_ATTR).getS());
		user.setPassword(userItem.get(PASSWORD_ATTR).getS());
		user.setId(userItem.get(EMAIL_ATTR).getS());

		// Look for other optional attributes and set them for the object if
		// they exist.
		if (null != userItem.get(NICKNAME_ATTR))
			user.setNickname(userItem.get(NICKNAME_ATTR).getS());

		if (null != userItem.get(TAGLINE_ATTR))
			user.setTagline(userItem.get(TAGLINE_ATTR).getS());

		if (null != userItem.get(PROFILE_PIC_KEY_ATTR))
			user.setProfilePicKey((userItem.get(PROFILE_PIC_KEY_ATTR).getS()));

		return user;
	}
	
	/**
     * Marshal a User object to a Map<String, AttributeValue> suitable for inserting
     * as an item into a DynamoDB table.
     * 
     * @param user
     * @return
     */
    private Map<String, AttributeValue> getMapFromUser(User user) {
		// Create a Map object from the User
		Map<String, AttributeValue> userItem = new HashMap<String, AttributeValue>();
		
		// Add items to the Map
		userItem.put(EMAIL_ATTR, new AttributeValue(user.getEmail()));
		userItem.put(PASSWORD_ATTR, new AttributeValue(user.getPassword()));
		
		// Ensure User properties are neither null nor empty strings
		if(null != user.getNickname() && true != user.getNickname().isEmpty())
			userItem.put(NICKNAME_ATTR, new AttributeValue(user.getNickname()));
		
		if(null != user.getTagline() && true != user.getTagline().isEmpty())
			userItem.put(TAGLINE_ATTR, new AttributeValue(user.getTagline()));
		
		if(null != user.getProfilePicKey() && true != user.getProfilePicKey().isEmpty())
			userItem.put(PROFILE_PIC_KEY_ATTR, new AttributeValue(user.getProfilePicKey()));
		
		return userItem;
    }
}
