package com.amediamanager.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amediamanager.dao.ConnectionManager;
import com.amediamanager.domain.Privacy;
import com.amediamanager.domain.Video;

@Component
@Scope("prototype")
public class DatabaseSchemaResource implements ProvisionableResource {
	
	@Autowired
	private ConnectionManager connectionManager;
	
	private static final String name = "RDS Database Schema";
	private ProvisionState provisionState;
	
	@PostConstruct
	public void init() {
		try {
			if(this.doesDataSourceExist(VIDEO_TABLE_NAME) && this.doesDataSourceExist(TAGS_TABLE_NAME)) {
				provisionState = ProvisionableResource.ProvisionState.PROVISIONED;
			} else {
				provisionState = ProvisionableResource.ProvisionState.UNPROVISIONED;
			}
		} catch (Exception e) {
			throw new RuntimeException("Error connecting to database");
		}
	}
	
	@Override
	public ProvisionState getState() {
		return provisionState;
	}

	@Override
	public String getName() {
		return DatabaseSchemaResource.name;
	}
	
	@Override
	public void provision() {
		this.provisionDataSource(VIDEO_DROP_TABLE, VIDEO_CREATE_TABLE);
		this.provisionDataSource(TAGS_DROP_TABLE, TAGS_CREATE_TABLE);
	}
	
	private Boolean doesDataSourceExist(final String tableName) throws Exception {
		boolean dataSourceExists = false;

		Connection connection = null;
		ResultSet results = null;
		DatabaseMetaData metadata;

		try {
			connection = connectionManager.getConnection();
			metadata = connection.getMetaData();
			results = metadata.getTables(null, null, tableName, null);

			dataSourceExists = results.next();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				results.close();
				connection.close();
			} catch (Exception x) {
			}
		}

		return dataSourceExists;
	}

	private void provisionDataSource(final String dropTableQuery, final String createTableQuery) {

		Connection connection = null;
		Statement statement = null;
		try {
			connection = connectionManager.getConnection();
			statement = connection.createStatement();
			
			statement.executeUpdate(dropTableQuery);
			statement.executeUpdate(createTableQuery);

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
	
	/** Video table stuff**/
	public static final String TAGS_TABLE_NAME = "tags";

	private static final String TAGS_CREATE_TABLE = "create table tags (" + 
			"tag varchar(255) NOT NULL," +
			"videoId varchar(255) NOT NULL" +
		") ENGINE = InnoDB DEFAULT CHARACTER SET = utf8";
	
	public static final String TAGS_DROP_TABLE = "DROP TABLE IF EXISTS " + TAGS_TABLE_NAME;
	
	
	
	/** Video table stuff**/
	public static final String VIDEO_TABLE_NAME = "videos";

	/** Column names **/
	private static final String VIDEO_COLUMN_NAME_ID = "id";
	private static final String VIDEO_COLUMN_NAME_KEY = "originalKey";
	private static final String VIDEO_COLUMN_NAME_BUCKET = "bucket";
	private static final String VIDEO_COLUMN_NAME_OWNER = "owner";
	private static final String VIDEO_COLUMN_NAME_UPLOADED_DATE = "uploadedDate";
	private static final String VIDEO_COLUMN_NAME_PRIVACY = "privacy";
	private static final String VIDEO_COLUMN_NAME_TITLE = "title";
	private static final String VIDEO_COLUMN_NAME_DESCRIPTION = "description";
	private static final String VIDEO_COLUMN_NAME_THUMBNAIL_KEY = "thumbnailKey";
	private static final String VIDEO_COLUMN_NAME_CREATED_DATE = "createdDate";
	private static final String VIDEO_COLUMN_NAME_PREVIEW_KEY = "previewKey";
	
	private static final String VIDEO_CREATE_TABLE = "CREATE TABLE " + VIDEO_TABLE_NAME + "(" 
			+ VIDEO_COLUMN_NAME_ID + " VARCHAR(255) NOT NULL PRIMARY KEY, "
			+ VIDEO_COLUMN_NAME_KEY + " VARCHAR(255) NOT NULL, "
			+ VIDEO_COLUMN_NAME_BUCKET + " VARCHAR(255) NOT NULL, " 
			+ VIDEO_COLUMN_NAME_OWNER + " VARCHAR(255) NOT NULL, " 
			+ VIDEO_COLUMN_NAME_UPLOADED_DATE + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
			+ VIDEO_COLUMN_NAME_PRIVACY + " VARCHAR(8) NOT NULL DEFAULT '" + Privacy.PRIVATE.name() + "', "
			+ VIDEO_COLUMN_NAME_TITLE + " VARCHAR(255), "
			+ VIDEO_COLUMN_NAME_DESCRIPTION + " VARCHAR(255), "
			+ VIDEO_COLUMN_NAME_THUMBNAIL_KEY + " VARCHAR(255), "
			+ VIDEO_COLUMN_NAME_PREVIEW_KEY + " VARCHAR(255), "
			+ VIDEO_COLUMN_NAME_CREATED_DATE + " DATE, "
			+ "UNIQUE ("+ VIDEO_COLUMN_NAME_KEY + ") "
			+ ") ENGINE = InnoDB DEFAULT CHARACTER SET = utf8";
	
	public static final String VIDEO_DROP_TABLE = "DROP TABLE IF EXISTS " + VIDEO_TABLE_NAME;

	private static final String[] tables = {VIDEO_TABLE_NAME, TAGS_TABLE_NAME};
	
}
