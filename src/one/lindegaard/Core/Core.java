package one.lindegaard.Core;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import one.lindegaard.Core.commands.CommandDispatcher;
import one.lindegaard.Core.commands.DebugCommand;
import one.lindegaard.Core.commands.MuteCommand;
import one.lindegaard.Core.commands.ReloadCommand;
import one.lindegaard.Core.commands.UpdateCommand;
import one.lindegaard.Core.commands.VersionCommand;
import one.lindegaard.Core.config.ConfigManager;
import one.lindegaard.Core.rewards.BagOfGoldItems;
import one.lindegaard.Core.rewards.RewardManager;
import one.lindegaard.Core.storage.DataStoreException;
import one.lindegaard.Core.storage.DataStoreManager;
import one.lindegaard.Core.storage.IDataStore;
import one.lindegaard.Core.storage.MySQLDataStore;
import one.lindegaard.Core.storage.SQLiteDataStore;
import one.lindegaard.Core.update.SpigetUpdater;

public class Core extends JavaPlugin {

	private static Core instance;
	private File mFile = new File(getDataFolder(), "config.yml");

	private ConfigManager mConfig;
	private Messages mMessages;
	private PlayerSettingsManager mPlayerSettingsManager;
	private CommandDispatcher mCommandDispatcher;
	private IDataStore mStore;
	private DataStoreManager mStoreManager;
	private WorldGroup mWorldGroupManager;
	private BagOfGoldItems mBagOfGoldItems;
	private SpigetUpdater mSpigetUpdater;
	private RewardManager mRewardManager;

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

		mSpigetUpdater = new SpigetUpdater(this);
		mSpigetUpdater.setCurrentJarFile(this.getFile().getName());

		mStoreManager = new DataStoreManager(this, mStore);

		mPlayerSettingsManager = new PlayerSettingsManager(this);
		
		mRewardManager = new RewardManager(this);

		// Register commands
		mCommandDispatcher = new CommandDispatcher(
				this, 
				"bagcore",
				instance.
				getMessages().
				getString("bagofgoldcore.command.base.description")
						+ getDescription().getVersion());
		getCommand("bagc").
			setExecutor(mCommandDispatcher);
		getCommand("bagc").
			setTabCompleter(mCommandDispatcher);
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
	public static Core getInstance() {
		return instance;
	}

	public static Core getAPI() {
		return instance;
	}

	@Deprecated
	public static Core getApi() {
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

	public SpigetUpdater getSpigetUpdater() {
		return mSpigetUpdater;
	}
	public RewardManager getRewardManager() {
		return mRewardManager;
	}

}
