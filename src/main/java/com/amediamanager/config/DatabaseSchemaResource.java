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
			if(this.doesDataSourceExist(VIDEO_TABLE_NAME) && 
					this.doesDataSourceExist(TAGS_TABLE_NAME) &&
					this.doesDataSourceExist(VIDEOS_TAGS_TABLE_NAME)) {
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
		this.provisionDataSource(VIDEOS_TAGS_DROP_TABLE, VIDEOS_TAGS_CREATE_TABLE);
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
	
	/** Tags table**/
	public static final String TAGS_TABLE_NAME = "tags";

	private static final String TAGS_CREATE_TABLE = "CREATE TABLE `tags` (" +
			"`tagId` varchar(255) NOT NULL," +  
			"`name` varchar(255) NOT NULL," +
			  "PRIMARY KEY (`tagId`)," +
			  "KEY `ix_tag` (`tagId`)" +
			") ENGINE=InnoDB DEFAULT CHARSET=utf8";
	
	public static final String TAGS_DROP_TABLE = "DROP TABLE IF EXISTS " + TAGS_TABLE_NAME;

	
	/** Video table stuff**/
	public static final String VIDEO_TABLE_NAME = "videos";
	
	private static final String VIDEO_CREATE_TABLE = "CREATE TABLE `videos` (" +
			  "`videoId` varchar(255) NOT NULL," +
			  "`originalKey` varchar(255) NOT NULL," +
			  "`bucket` varchar(255) NOT NULL," +
			  "`owner` varchar(255) NOT NULL," +
			  "`uploadedDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
			  "`privacy` varchar(8) NOT NULL DEFAULT 'PRIVATE'," +
			  "`title` varchar(255) DEFAULT NULL," +
			  "`description` varchar(255) DEFAULT NULL," +
			  "`thumbnailKey` varchar(255) DEFAULT NULL," +
			  "`previewKey` varchar(255) DEFAULT NULL," +
			  "`createdDate` date DEFAULT NULL," +
			  "PRIMARY KEY (`videoId`)," +
			  "KEY `ix_tag` (`videoId`)," +
			  "UNIQUE KEY `originalKey` (`originalKey`)" +
			") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
	
	public static final String VIDEO_DROP_TABLE = "DROP TABLE IF EXISTS " + VIDEO_TABLE_NAME;

	
	/** Videos_Tags join tablef**/
	public static final String VIDEOS_TAGS_TABLE_NAME = "videos_tags";

	private static final String VIDEOS_TAGS_CREATE_TABLE = "CREATE TABLE `videos_tags` (" +
			  "`tagId` varchar(255) NOT NULL," +
			  "`videoId` varchar(255) NOT NULL," +
			  "PRIMARY KEY (`tagId`,`videoId`)," +
			  "CONSTRAINT FOREIGN KEY (`tagId`) REFERENCES `tags` (`tagId`) ON DELETE CASCADE ON UPDATE CASCADE," +
			  "CONSTRAINT FOREIGN KEY (`videoId`) REFERENCES `videos` (`videoId`) ON DELETE CASCADE ON UPDATE CASCADE" +
			") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

	
	public static final String VIDEOS_TAGS_DROP_TABLE = "DROP TABLE IF EXISTS " + VIDEOS_TAGS_TABLE_NAME;
	
	private static final String[] tables = {VIDEO_TABLE_NAME, TAGS_TABLE_NAME, VIDEOS_TAGS_TABLE_NAME};
	
}
