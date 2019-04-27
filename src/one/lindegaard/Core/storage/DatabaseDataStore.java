package one.lindegaard.Core.storage;

import java.sql.*;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import one.lindegaard.Core.Core;
import one.lindegaard.Core.PlayerSettings;

public abstract class DatabaseDataStore implements IDataStore {

	private Core plugin;

	public DatabaseDataStore(Core plugin) {
		this.plugin = plugin;
	}

	/**
	 * Connection to the Database
	 */
	// protected Connection mConnection;

	/**
	 * Args: player name
	 */
	protected PreparedStatement mGetPlayerUUID;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mGetPlayerSettings;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mInsertPlayerSettings;

	/**
	 * Establish initial connection to Database
	 */
	protected abstract Connection setupConnection() throws SQLException, DataStoreException;

	/**
	 * Setup / Create database version 1 tables for BagOfGold
	 */
	protected abstract void setupV1Tables(Connection connection) throws SQLException;

	/**
	 * Open a connection to the Database and prepare a statement for executing.
	 * 
	 * @param connection
	 * @param preparedConnectionType
	 * @throws SQLException
	 */
	protected abstract void openPreparedStatements(Connection connection, PreparedConnectionType preparedConnectionType)
			throws SQLException;

	public enum PreparedConnectionType {
		GET_PLAYER_UUID, GET_PLAYER_SETTINGS, INSERT_PLAYER_SETTINGS
	};

	/**
	 * Initialize the connection. Must be called after Opening of initial
	 * connection. Open Prepared statements for batch processing large selections of
	 * players. Batches will be performed in batches of 10,5,2,1
	 */
	@Override
	public void initialize() throws DataStoreException {
		plugin.getMessages().debug("Initialize database");
		try {

			Connection mConnection = setupConnection();

			//Update database if needed
			//if (plugin.getConfigManager().databaseVersion == 0) {
			//Statement statement = mConnection.createStatement();
			//try {
			// Check if Database exists at all?
			//	ResultSet rs = statement.executeQuery("SELECT UUID FROM mh_Players LIMIT 0");
			//		rs.close();
			//		plugin.getConfigManager().databaseVersion = 0;
			//		plugin.getConfigManager().saveConfig();
			//	} catch (SQLException e2) {
			//		// Database v1 does not exist. Create V2
			//		setupV2Tables(mConnection);
			//		plugin.getConfigManager().databaseVersion = 2;
			//		plugin.getConfigManager().saveConfig();
			//	}
			//	statement.close();
			//}

			switch (plugin.getConfigManager().databaseVersion) {
			case 1:
				Bukkit.getLogger().info(
						"[BagOfGoldCore] Database version " + plugin.getConfigManager().databaseVersion + " detected.");
				setupV1Tables(mConnection);
			}

			plugin.getConfigManager().databaseVersion = 1;
			plugin.getConfigManager().saveConfig();

			// Enable FOREIGN KEY for Sqlite database
			if (!plugin.getConfigManager().databaseType.equalsIgnoreCase("MySQL")) {
				Statement statement = mConnection.createStatement();
				statement.execute("PRAGMA foreign_keys = ON");
				statement.close();
			}
			mConnection.close();

		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * Rollback of last transaction on Database.
	 * 
	 * @throws DataStoreException
	 */
	protected void rollback(Connection mConnection) throws DataStoreException {

		try {
			mConnection.rollback();
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * Shutdown: Commit and close database connection completely.
	 */
	@Override
	public void shutdown() throws DataStoreException {
		int n = 0;
		do {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			n++;
		} while (plugin.getDataStoreManager().isRunning() && n < 40);
		System.out.println("[BagOfGold] Closing database connection.");
	}

	// ******************************************************************
	// Player Settings
	// ******************************************************************

	/**
	 * getPlayerSettings
	 * 
	 * @param offlinePlayer :OfflinePlayer
	 * @return PlayerData
	 * @throws DataStoreException
	 * @throws SQLException
	 * 
	 */
	@Override
	public PlayerSettings loadPlayerSettings(OfflinePlayer offlinePlayer)
			throws UserNotFoundException, DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_SETTINGS);
			mGetPlayerSettings.setString(1, offlinePlayer.getUniqueId().toString());
			ResultSet result;
			result = mGetPlayerSettings.executeQuery();
			if (result.next()) {
				PlayerSettings ps = new PlayerSettings(offlinePlayer, result.getString("LAST_WORLDGRP"),
						result.getBoolean("LEARNING_MODE"), result.getBoolean("MUTE_MODE"), result.getString("TEXTURE"), result.getString("SIGNATURE"));
				result.close();
				//plugin.getMessages().debug("Reading from Database: %s", ps.toString());
				mGetPlayerSettings.close();
				mConnection.close();
				return ps;
			}
			mGetPlayerSettings.close();
			mConnection.close();
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
		throw new UserNotFoundException("User " + offlinePlayer.toString() + " is not present in database");
	}

	/**
	 * insertPlayerSettings to database
	 */
	@Override
	public void insertPlayerSettings(PlayerSettings playerSettings) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				openPreparedStatements(mConnection, PreparedConnectionType.INSERT_PLAYER_SETTINGS);
				mInsertPlayerSettings.setString(1, playerSettings.getPlayer().getUniqueId().toString());
				mInsertPlayerSettings.setString(2, playerSettings.getPlayer().getName());
				mInsertPlayerSettings.setString(3, playerSettings.getLastKnownWorldGrp());
				mInsertPlayerSettings.setInt(4, playerSettings.isLearningMode() ? 1 : 0);
				mInsertPlayerSettings.setInt(5, playerSettings.isMuted() ? 1 : 0);
				mInsertPlayerSettings.setString(6, playerSettings.getTexture());
				mInsertPlayerSettings.setString(7, playerSettings.getSignature());
				mInsertPlayerSettings.addBatch();
				mInsertPlayerSettings.executeBatch();
				mInsertPlayerSettings.close();
				mConnection.commit();
				mConnection.close();
			} catch (SQLException e) {
				rollback(mConnection);
				mConnection.close();
				throw new DataStoreException(e);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void savePlayerSettings(Set<PlayerSettings> playerDataSet, boolean cleanCache) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				openPreparedStatements(mConnection, PreparedConnectionType.INSERT_PLAYER_SETTINGS);
				for (PlayerSettings playerSettings : playerDataSet) {
					mInsertPlayerSettings.setString(1, playerSettings.getPlayer().getUniqueId().toString());
					mInsertPlayerSettings.setString(2, playerSettings.getPlayer().getName());
					mInsertPlayerSettings.setString(3, playerSettings.getLastKnownWorldGrp());
					mInsertPlayerSettings.setInt(4, playerSettings.isLearningMode() ? 1 : 0);
					mInsertPlayerSettings.setInt(5, playerSettings.isMuted() ? 1 : 0);
					mInsertPlayerSettings.setString(6, playerSettings.getTexture());
					mInsertPlayerSettings.setString(7, playerSettings.getSignature());
					mInsertPlayerSettings.addBatch();
				}
				mInsertPlayerSettings.executeBatch();
				mInsertPlayerSettings.close();
				mConnection.commit();
				mConnection.close();

				plugin.getMessages().debug("PlayerSettings saved.");

				if (cleanCache)
					for (PlayerSettings playerData : playerDataSet) {
						if (plugin.getPlayerSettingsManager().containsKey(playerData.getPlayer())
								&& !playerData.getPlayer().isOnline() && playerData.getPlayer().hasPlayedBefore())
							plugin.getPlayerSettingsManager().removePlayerSettings(playerData.getPlayer());
					}

			} catch (SQLException e) {
				rollback(mConnection);
				mConnection.close();
				throw new DataStoreException(e);
			}
		} catch (SQLException e1) {
			throw new DataStoreException(e1);
		}
	}

	/**
	 * getPlayerByName - get the player
	 * 
	 * @param name
	 *            : String
	 * @return player
	 */
	@Override
	public OfflinePlayer getPlayerByName(String name) throws DataStoreException {
		if (name.equals("Random Bounty"))
			return null; // used for Random Bounties
		try {
			Connection mConnection = setupConnection();

			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_UUID);
			mGetPlayerUUID.setString(1, name);
			ResultSet set = mGetPlayerUUID.executeQuery();

			if (set.next()) {
				UUID uid = UUID.fromString(set.getString(1));
				set.close();
				mGetPlayerUUID.close();
				mConnection.close();
				return Bukkit.getOfflinePlayer(uid);
			}
			mGetPlayerUUID.close();
			mConnection.close();
			throw new UserNotFoundException("[MobHunting] User " + name + " is not present in database");
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

}
