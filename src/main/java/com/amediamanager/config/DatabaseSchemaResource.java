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
			if(this.doesDataSourceExist(TABLE_NAME)) {
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
		this.provisionDataSource(DROP_TABLE, CREATE_TABLE);
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
	
	/** Table name **/
	public static final String TABLE_NAME = "videos";

	/** Column names **/
	private static final String COLUMN_NAME_KEY = "s3key";
	private static final String COLUMN_NAME_OWNER = "owner";
	private static final String COLUMN_NAME_UPLOADED_DATE = "uploaded";
	private static final String COLUMN_NAME_PRIVACY = "privacy";
	private static final String COLUMN_NAME_TITLE = "title";
	private static final String COLUMN_NAME_DESCRIPTION = "description";
	private static final String COLUMN_NAME_TAGS = "tags";
	private static final String COLUMN_NAME_THUMBNAIL_KEY = "thumbnail";
	private static final String COLUMN_NAME_CREATED_DATE = "created";
	
	private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" 
			+ COLUMN_NAME_KEY + " VARCHAR(255) NOT NULL, "
			+ COLUMN_NAME_OWNER + " VARCHAR(255) NOT NULL, " 
			+ COLUMN_NAME_UPLOADED_DATE + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
			+ COLUMN_NAME_PRIVACY + " VARCHAR(8) NOT NULL DEFAULT '" + Video.Privacy.Private.name() + "', "
			+ COLUMN_NAME_TITLE + " VARCHAR(255), "
			+ COLUMN_NAME_DESCRIPTION + " VARCHAR(255), "
			+ COLUMN_NAME_TAGS + " VARCHAR(255), "
			+ COLUMN_NAME_THUMBNAIL_KEY + " VARCHAR(255), "
			+ COLUMN_NAME_CREATED_DATE + " DATE, "
			+ "UNIQUE ("+ COLUMN_NAME_KEY + ") "
			+ ") ENGINE = InnoDB DEFAULT CHARACTER SET = utf8";
	
	public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

}
