package one.lindegaard.BagOfGoldCore;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import one.lindegaard.BagOfGoldCore.commands.CommandDispatcher;
import one.lindegaard.BagOfGoldCore.commands.DebugCommand;
import one.lindegaard.BagOfGoldCore.commands.MuteCommand;
import one.lindegaard.BagOfGoldCore.commands.ReloadCommand;
import one.lindegaard.BagOfGoldCore.commands.UpdateCommand;
import one.lindegaard.BagOfGoldCore.commands.VersionCommand;
import one.lindegaard.BagOfGoldCore.config.ConfigManager;
import one.lindegaard.BagOfGoldCore.storage.DataStoreException;
import one.lindegaard.BagOfGoldCore.storage.DataStoreManager;
import one.lindegaard.BagOfGoldCore.storage.IDataStore;
import one.lindegaard.BagOfGoldCore.storage.MySQLDataStore;
import one.lindegaard.BagOfGoldCore.storage.SQLiteDataStore;
import one.lindegaard.BagOfGoldCore.update.SpigetUpdater;

public class BagOfGoldCore extends JavaPlugin {

	private static BagOfGoldCore instance;
	private File mFile = new File(getDataFolder(), "config.yml");

	private ConfigManager mConfig;
	private Messages mMessages;
	private PlayerSettingsManager mPlayerSettingsManager;
	private CommandDispatcher mCommandDispatcher;
	private IDataStore mStore;
	private DataStoreManager mStoreManager;
	private WorldGroupManager mWorldGroupManager;
	private SpigetUpdater mSpigetUpdater;

	private boolean mInitialized = false;

	@Override
	public void onLoad() {
		instance = this;
		mMessages = new Messages(this);
	}

	@Override
	public void onEnable() {

		mConfig = new ConfigManager(this, mFile);

		if (mConfig.loadConfig()) {
			if (mConfig.backup)
				mConfig.backupConfig(mFile);
			mConfig.saveConfig();
		} else
			throw new RuntimeException(instance.getMessages().getString("bagofgoldcore.config.fail"));
		
		//Copy settings from BagOfGOld or MobHunting
		if (mConfig.configVersion==1) {
			mConfig.getConfigDataFromBagOfGoldPlugin();
			if (mConfig.configVersion==1)
				mConfig.getConfigDataFromMobHuntingPlugin();
		} 
		

		mWorldGroupManager = new WorldGroupManager(this);
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

		mSpigetUpdater = new SpigetUpdater(this);
		mSpigetUpdater.setCurrentJarFile(this.getFile().getName());

		mStoreManager = new DataStoreManager(this, mStore);

		mPlayerSettingsManager = new PlayerSettingsManager(this);

		//mRewardManager = new RewardManager(this);

		// Register commands
		mCommandDispatcher = new CommandDispatcher(this, "bagcore",
				instance.getMessages().getString("bagofgoldcore.command.base.description")
						+ getDescription().getVersion());
		getCommand("bagc").setExecutor(mCommandDispatcher);
		getCommand("bagc").setTabCompleter(mCommandDispatcher);
		mCommandDispatcher.registerCommand(new ReloadCommand(this));
		mCommandDispatcher.registerCommand(new UpdateCommand(this));
		mCommandDispatcher.registerCommand(new VersionCommand(this));
		mCommandDispatcher.registerCommand(new DebugCommand(this));
		mCommandDispatcher.registerCommand(new MuteCommand(this));

		// Check for new MobHuntig updates using Spiget.org
		mSpigetUpdater.hourlyUpdateCheck(getServer().getConsoleSender(), mConfig.updateCheck, false);

		// Handle online players when server admin do a /reload or /mh reload
		if (Tools.getOnlinePlayersAmount() > 0) {
			getMessages().debug("Reloading %s player settings from the database", Tools.getOnlinePlayersAmount());
			for (Player player : Tools.getOnlinePlayers()) {
				mPlayerSettingsManager.load(player);
			}
		}

		mInitialized = true;

	}

	@Override
	public void onDisable() {
		getMessages().debug("Disabling BagOfGoldCore initiated");
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

		mWorldGroupManager.save();

		instance.getMessages().debug("Core disabled.");
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
	 * Get the MessagesManager public BagOfGoldItems getBagOfGoldItems() { return
	 * mBagOfGoldItems; }
	 * 
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
	public WorldGroupManager getWorldGroupManager() {
		return mWorldGroupManager;
	}

	public SpigetUpdater getSpigetUpdater() {
		return mSpigetUpdater;
	}

}
