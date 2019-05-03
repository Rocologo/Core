package one.lindegaard.BagOfGoldCore.storage;

import java.sql.SQLException;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGoldCore.PlayerSettings;

public interface IDataStore {
	/**
	 * Initialize - opening a connection to the Database and initialize the
	 * connection.
	 * 
	 * @throws DataStoreException
	 */
	void initialize() throws DataStoreException;

	/**
	 * Closing all connections to the Database
	 * 
	 * @throws DataStoreException
	 */
	void shutdown() throws DataStoreException;

	/**
	 * Get the players Settings from the Database
	 * 
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws SQLException
	 */
	PlayerSettings loadPlayerSettings(OfflinePlayer player) throws UserNotFoundException, DataStoreException;

	/**
	 * Update the players Settings in the Database
	 * 
	 * @param playerDataSet
	 * @throws DataStoreException
	 */
	void savePlayerSettings(Set<PlayerSettings> ps, boolean cleanCache) throws DataStoreException;

	/**
	 * Insert all PlayerData for one player into the Database
	 * 
	 * @param ps
	 * @throws DataStoreException
	 */
	void insertPlayerSettings(PlayerSettings ps) throws DataStoreException;

	/**
	 * Get the player by his name from the Database. ings @param name
	 * 
	 * @return
	 * @throws DataStoreException
	 */
	OfflinePlayer getPlayerByName(String name) throws DataStoreException;
	
}
