package one.lindegaard.Core.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import one.lindegaard.Core.BagOfGoldCore;

public class MySQLDataStore extends DatabaseDataStore {

	private BagOfGoldCore plugin;

	public MySQLDataStore(BagOfGoldCore plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	// *******************************************************************************
	// SETUP / INITIALIZE
	// *******************************************************************************

	@Override
	protected Connection setupConnection() throws DataStoreException {
		try {
			Locale.setDefault(new Locale("us", "US"));
			Class.forName("com.mysql.jdbc.Driver");
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(plugin.getConfigManager().databaseUsername);
			dataSource.setPassword(plugin.getConfigManager().databasePassword);
			if (plugin.getConfigManager().databaseHost.contains(":")) {
				dataSource.setServerName(plugin.getConfigManager().databaseHost.split(":")[0]);
				dataSource.setPort(Integer.valueOf(plugin.getConfigManager().databaseHost.split(":")[1]));
			} else {
				dataSource.setServerName(plugin.getConfigManager().databaseHost);
			}
			dataSource.setDatabaseName(plugin.getConfigManager().databaseName + "?autoReconnect=true");
			Connection c = dataSource.getConnection();
			Statement statement = c.createStatement();
			statement.executeUpdate("SET NAMES 'utf8'");
			statement.executeUpdate("SET CHARACTER SET 'utf8'");
			statement.close();
			c.setAutoCommit(false);
			return c;
		} catch (ClassNotFoundException classNotFoundEx) {
			throw new DataStoreException("MySQL not present on the classpath", classNotFoundEx);
		} catch (SQLException sqlEx) {
			throw new DataStoreException("Error creating sql connection", sqlEx);
		}
	}

	@Override
	protected void openPreparedStatements(Connection connection, PreparedConnectionType preparedConnectionType)
			throws SQLException {
		switch (preparedConnectionType) {
		case GET_PLAYER_UUID:
			mGetPlayerUUID = connection.prepareStatement("SELECT UUID FROM mh_PlayerSettings WHERE NAME=?;");
			break;
		case GET_PLAYER_SETTINGS:
			mGetPlayerSettings = connection.prepareStatement("SELECT * FROM mh_PlayerSettings WHERE UUID=?;");
			break;
		case INSERT_PLAYER_SETTINGS:
			mInsertPlayerSettings = connection.prepareStatement(
					"REPLACE INTO mh_PlayerSettings (UUID,NAME,LAST_WORLDGRP,LEARNING_MODE,MUTE_MODE) "
							+ "VALUES(?,?,?,?,?);");
			break;
		}

	}

	// *******************************************************************************
	// V1 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV1Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = plugin.getConfigManager().learningMode ? "1" : "0";
		plugin.getMessages().debug("MySQLDatastore: create mh_PlayerSettings");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_PlayerSettings "//
				+ "(UUID CHAR(40),"//
				+ " NAME VARCHAR(20),"//
				+ " LAST_WORLDGRP VARCHAR(20) NOT NULL DEFAULT 'default'," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + ","//
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0,"//
				+ " PRIMARY KEY (UUID))");
		connection.commit();

		create.close();
		plugin.getMessages().debug("MySQLDatastore: commit transactions");
		connection.commit();
	}

}
