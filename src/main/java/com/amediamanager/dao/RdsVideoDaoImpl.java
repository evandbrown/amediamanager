package com.amediamanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amediamanager.domain.Video;
import com.amediamanager.exceptions.DataSourceTableDoesNotExistException;

@Repository
public class RdsVideoDaoImpl implements VideoDao {

	@Autowired
	ConnectionManager connectionManager;
	
	@Override
	public void save(Video video) throws DataSourceTableDoesNotExistException {
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = connectionManager.getConnection();
			statement = connection.prepareStatement(QUERY_INSERT);

			// Required parameters
			statement.setString(1, video.getKey());
			statement.setString(2, video.getOwner());
			statement.setDate(3, new java.sql.Date(video.getUploadedDate().getTime()));
			statement.setString(4, video.getPrivacy().name().toString());
			
			// Optional parameters
			statement.setString(5, video.getTitle());
			statement.setString(6, video.getDescription());
			statement.setString(7, video.getTags().toString());
			if(video.getCreatedDate() != null) {
				statement.setDate(8, new java.sql.Date(video.getCreatedDate().getTime()));
			}
			statement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (Exception x) {
			}
		}						
	}

	/** Table name **/
	public static final String TABLE_NAME = "videos";

	/** Column names **/
	public static final String COLUMN_NAME_KEY = "s3key";
	public static final String COLUMN_NAME_OWNER = "owner";
	public static final String COLUMN_NAME_UPLOADED_DATE = "uploaded";
	public static final String COLUMN_NAME_PRIVACY = "privacy";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_DESCRIPTION = "description";
	public static final String COLUMN_NAME_TAGS = "tags";
	public static final String COLUMN_NAME_THUMBNAIL_KEY = "thumbnail";
	public static final String COLUMN_NAME_CREATED_DATE = "created";

	/** Queries **/
	public static final String QUERY_INSERT = "INSERT INTO " + TABLE_NAME + " ( " 
			+ COLUMN_NAME_KEY + ", "
			+ COLUMN_NAME_OWNER + ", "
			+ COLUMN_NAME_UPLOADED_DATE + ", "
			+ COLUMN_NAME_PRIVACY + ", "
			+ COLUMN_NAME_TITLE + ", "
			+ COLUMN_NAME_DESCRIPTION + ", "
			+ COLUMN_NAME_TAGS + ", "
			+ COLUMN_NAME_CREATED_DATE + ") "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";	
}
