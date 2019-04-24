package one.lindegaard.Core;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import one.lindegaard.Core.config.ConfigManager;
import one.lindegaard.Core.rewards.BagOfGoldItems;
import one.lindegaard.Core.storage.DataStoreException;
import one.lindegaard.Core.storage.DataStoreManager;
import one.lindegaard.Core.storage.IDataStore;
import one.lindegaard.Core.storage.MySQLDataStore;
import one.lindegaard.Core.storage.SQLiteDataStore;

public class BagOfGoldCore extends JavaPlugin {

	private static BagOfGoldCore instance;
	private File mFile = new File(getDataFolder(), "config.yml");

	private ConfigManager mConfig;
	private Messages mMessages;
	private PlayerSettingsManager mPlayerSettingsManager;
	private IDataStore mStore;
	private DataStoreManager mStoreManager;
	private WorldGroup mWorldGroupManager;
	private BagOfGoldItems mBagOfGoldItems;

	private boolean mInitialized = false;

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {

		instance = this;

		mMessages = new Messages(this);
		mConfig = new ConfigManager(this, mFile);

		if (mConfig.loadConfig()) {
			if (mConfig.backup)
				mConfig.backupConfig(mFile);
			mConfig.saveConfig();
		} else
			throw new RuntimeException(instance.getMessages().getString("bagofgold.config.fail"));

		mWorldGroupManager = new WorldGroup(this);
		mWorldGroupManager.load();

		if (mConfig.databaseType.equalsIgnoreCase("mysql"))
			mStore = new MySQLDataStore(this);
		else
			mStore = new SQLiteDataStore(this);

		try {
			mStore.initialize();
		} catch (DataStoreException e) {
			e.printStackTrace();
			try {
				mStore.shutdown();
			} catch (DataStoreException e1) {
				e1.printStackTrace();
			}
			setEnabled(false);
			return;
		}

		mStoreManager = new DataStoreManager(this, mStore);

		mPlayerSettingsManager = new PlayerSettingsManager(this);

		mInitialized = true;

	}

	@Override
	public void onDisable() {
		if (!mInitialized)
			return;

		try {
			getMessages().debug("Shutdown StoreManager");
			mStoreManager.shutdown();
			getMessages().debug("Shutdown Store");
			mStore.shutdown();
		} catch (DataStoreException e) {
			e.printStackTrace();
		}

		instance.getMessages().debug("BagOfGold disabled.");
	}

	// ************************************************************************************
	// Managers and handlers
	// ************************************************************************************
	public static BagOfGoldCore getInstance() {
		return instance;
	}

	public static BagOfGoldCore getAPI() {
		return instance;
	}

	@Deprecated
	public static BagOfGoldCore getApi() {
		return instance;
	}

	public ConfigManager getConfigManager() {
		return mConfig;
	}

	/**
	 * Get the MessagesManager
	 * 
	 * @return
	 */
	public Messages getMessages() {
		return mMessages;
	}

	/**
	 * setMessages
	 * 
	 * @param messages
	 */
	public void setMessages(Messages messages) {
		mMessages = messages;
	}

	/**
	 * Gets the Store Manager
	 * 
	 * @return
	 */
	public IDataStore getStoreManager() {
		return mStore;
	}

	/**
	 * Gets the Database Store Manager
	 * 
	 * @return
	 */
	public DataStoreManager getDataStoreManager() {
		return mStoreManager;
	}

	/**
	 * Get the PlayerSettingsManager
	 * 
	 * @return
	 */
	public PlayerSettingsManager getPlayerSettingsManager() {
		return mPlayerSettingsManager;
	}

	/**
	 * Get all WorldGroups and their worlds
	 * 
	 * @return
	 */
	public WorldGroup getWorldGroupManager() {
		return mWorldGroupManager;
	}

	public BagOfGoldItems getBagOfGoldItems() {
		return mBagOfGoldItems;
	}

}