package one.lindegaard.BagOfGoldCore.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import one.lindegaard.BagOfGoldCore.BagOfGoldCore;

public class ConfigManager extends AutoConfig {

	BagOfGoldCore plugin;

	public ConfigManager(BagOfGoldCore plugin, File file) {

		super(plugin, file);

		this.plugin = plugin;

		setCategoryComment("general", "########################################################################"
				+ "\nGeneral Settings" + "\n########################################################################");

		setCategoryComment("bagofgold",
				"########################################################################"
						+ "\nBagofGoldCore Item Settings"
						+ "\n########################################################################");

		setCategoryComment("database",
				"########################################################################" + "\nDatabase Settings."
						+ "\n########################################################################");

		setCategoryComment("updates", "########################################################################"
				+ "\nUpdate settings" + "\n########################################################################");

	}

	// #####################################################################################
	// Generel settings
	// #####################################################################################

	@ConfigField(name = "language", category = "general", comment = "The language (file) to use. You can put the name of the language file as the language code "
			+ "\n(eg. en_US, de_DE, fr_FR, ect.) or you can specify the name of a custom file without the .lang\nPlease check the lang/ folder for a list of all available translations.")
	public String language = "en_US";

	@ConfigField(name = "save-period", category = "general", comment = "Time between saves in ticks (20 ticks ~ 1 sec) This number must be higher that 1200 ticks = 2 minutes,"
			+ "\nbut I recommend to save every 5th minute = 6000 ticks")
	public int savePeriod = 6000;

	@ConfigField(name = "debug", category = "general", comment = "If kills are not being registered in mob hunting. Enable this to see why they arent")
	public boolean debug = false;

	@ConfigField(name = "newplayer_learning_mode", category = "general", comment = "When a new playerjoins the server he will by default start"
			+ "\nin 'LEARNING MODE' and get extra information about when he get rewards and not,"
			+ "\nwhen killing Mobs. The player can disable this InGame by using the command '/mobhunt learn'")
	public boolean learningMode = false;

	@ConfigField(name = "backup", category = "general", comment = "Backup config on each server start / reload")
	public boolean backup = true;

	@ConfigField(name = "config_version", category = "general", comment = "Do not chance this value unless you know what you are doing. It's meant for internal use.")
	public int configVersion = 1;

	// #####################################################################################
	// BagOfGold Settings
	// #####################################################################################

	@ConfigField(name = "bagofgold_itemtype", category = "bagofgold", comment = "Here you can set the type of the ITEM to be used as the BagOfGold item."
			+ "\nYou can choose between \"ITEM\",\"KILLED\",\"SKULL\",\"KILLER\",\"GRINGOTTS_STYLE\". The default is SKULL."
			+ "\nThe value will be showed above the item." + "\nITEM: The reward is dropped as a normal Minecraft item."
			+ "\nSKULL: The reward is dropped as a SKULL with a custom texture. You can generate custom texture value"
			+ "\nand custom texture signature at http://mineskin.org" + "\nExamples:"
			+ "\n\nBag of gold: (https://mineskin.org/6875)" + "\n\nbagofgold_texture_signature: 'Bag of gold'"
			+ "\nbagofgold_texture_value: 'eyJ0aW1lc3RhbXAiOjE0ODU5MTIwNjk3OTgsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM5NmNlMTNmZjYxNTVmZGYzMjM1ZDhkMjIxNzRjNWRlNGJmNTUxMmYxYWRlZGExYWZhM2ZjMjgxODBmM2Y3In19fQ=='"
			+ "\nbagofgold_texture_signature: 'm8u2ChI43ySVica7pcY0CsCuMCGgAdN7c9f/ZOxDZsPzJY8eiDrwxLIh6oPY1rvE1ja/rmftPSmdnbeHYrzLQ18QBzehFp8ZVegPsd9iNHc4FuD7nr1is2FD8M8AWAZOViiwlUKnfd8avb3SKfvFmhmVhQtE+atJYQrXhJwiqR4S+KTccA6pjIESM3AWlbCOmykg31ey7MQWB4YgtRp8NyFD3HNTLZ8alcEXBuG3t58wYBEME1UaOFah45tHuV1FW+iGBHHFWLu1UsAbg0Uw87Pp+KSTUGrhdwSc/55czILulI8IUnUfxmkaThRjd7g6VpH/w+9jLvm+7tOwfMQZlXp9104t9XMVnTAchzQr6mB3U6drCsGnuZycQzEgretQsUh3hweN7Jzz5knl6qc1n3Sn8t1yOvaIQLWG1f3l6irPdl28bwEd4Z7VDrGqYgXsd2GsOK/gCQ7rChNqbJ2p+jCja3F3ZohfmTYOU8W7DJ8Ne+xaofSuPnWODnZN9x+Y+3RE3nzH9tzP+NBMsV3YQXpvUD7Pepg7ScO+k9Fj3/F+KfBje0k6xfl+75s7kR3pNWQI5EVrO6iuky6dMuFPUBfNfq33fZV6Tqr/7o24aKpfA4WwJf91G9mC18z8NCgFR6iK4cPGmkTMvNtxUQ3MoB0LCOkRcbP0i7qxHupt8xE='"
			+ "\n\nBag of gold (alternative): (https://mineskin.org/3384)"
			+ "\n\nbagofgold_texture_signature: 'Bag of gold'"
			+ "\nbagofgold_texture_value: 'eyJ0aW1lc3RhbXAiOjE0NzQzMzI0MzY1MDYsInByb2ZpbGVJZCI6IjNlMjZiMDk3MWFjZDRjNmQ5MzVjNmFkYjE1YjYyMDNhIiwicHJvZmlsZU5hbWUiOiJOYWhlbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg2NzczZDc0Y2Y1MDhmZDc3Yzc4MmZmZDI5ZGYyZmU0N2ZiNzE0YjViMGQ3ZGU2N2Q1Mjg2OTMxZTJmMWRmMiJ9fX0='"
			+ "\nbagofgold_texture_signature: 'JdvJksowuxYQ0eqf56J+Dmczg7zvlw2DbIc58Q33kRt65uMUNn2iRCQsbNpztC1cAAgyYMOyFDiOUZQeIK03CSRoPLDtWp2u501YoGKqhjgrE0V0UDh3JetWKz4Ob0KmATtY+4R2vSoMjHFEFppM0Oq+8ZER12FAiVEMAzeseFN3Z9fWAMc/V10LoquGBpq6ExTfSCEEMDEGZopF1T8ZBKL0vf4DVendfz4v3yl7bRBzISZEAnF+ECTa9z36r8HRqS8+s0eO/AWYQcRaKIu9H+wSK5F/1v+rgifeSlMAnt1Na8m1b5tMfNuq6pXxWCq4nUGgYVTOLUinqs9ZcFz3Z6Mtx5YtymKk2M0mzxmTm9+AeOL4s3K/UrJYQlcmLBJSv4hd6EigJXoashzWNCHKmFDYCdEhh4FArq4G9vRZtoudcTeMsvi0VmXIgER8U5iSfoTtzXcGbf/GT0ECtgfeA40f5oCqyE4nXreudMmvlDCBr/KHbILQWeeH/jhtYqQ6OwJb3Ji2Bs9F5fQmICSqk7X4yKzexf8rdDhOG1z+/TCot7K8unPVuQx46sXPeP7t2hCiHOXMAnOMt8vuL3gQUURIEM6fMryjmlKsgvk8Jo0gawavRCIZQtA6vT0JRRnSAchzEOA7QP1iiVV3LnwX9Yqw7oMJ/+REV1hWesuzDOc='"
			+ "\n\nChest: (https://mineskin.org/3136)" + "\n\ndrop-money-on-ground-skull-reward-name: 'Treasure chest'"
			+ "\nbagofgold_texture_value: 'eyJ0aW1lc3RhbXAiOjE0NzI4Mzk3Nzk2ODMsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJJbnZlbnRpdmVHYW1lcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY5NDcxMjQ1YmNhN2M0ZmUwNjQ0MGQ5YjRiOWY3NDIxN2VkNzM0M2FhZDU5YTc5MThiMWExZDYxZDhiYTZkYSJ9fX0='"
			+ "\nbagofgold_texture_signature: 'lVA2QIbvybpzhcXof5yWz/7nkHdhG/3MGO+1DyD1txdRCALV6BRwsDUBwIUg06MkLUpBkjmiOvFcCRgal/jDE/xkkJPyk2tb/w4NtQ5PiPiAe0oInVnuiSIVFIE4tnsCdvX0joll3uKwVu6XY3t1KEsqJATcPhA5hslVn1iOp/IfMziIfuCzzob04rScpwcw0mLNtbtbMVAl6LYR9gXVuOkAfXujuYq4lbI/iW0yuLxSAzr8i9QWBP2ftup4qQHwocQRTdUE6/G5G9LwJWXhhnqKWjgjfvL0y2FRFJkgN1cvuq7DvUDBVsePnRIHwU5YvBPMjcZe/KE8VPTSodsN84/+++5p95Puxe1DXMX822xR71IQsxM7eax7Ffrr/Tzxw2rSDh9ivGGlRAB85OHwp/ouUgWNSrT8inNMYImque9EuZku9p3OFet8iZsFhkMXANeNtTVL7LKV7/L/0YWwoeyBnw5QQqvGyWKw3dac5eDkRNCyCtdDIntM5vsd8FxnIFj36zxLWgmrJmOM9hg5PBM4gcDxxryBcug8jSe+W9XDU39OOJotXajj8dgSL8yUn+d7l4Qvat/vJbAE8lonMl7P0P9QBPzmcIUvlRMuHSpRZQYkoCbwc2Filahd/5INtm7I4Y28XYzzupdwLk3cavKfOloL5YrWNqaZr/+9Tbk='"
			+ "\n\nBirthday present: (https://mineskin.org/4743)"
			+ "\n\nbagofgold_texture_signature: 'Birthday present'"
			+ "\nbagofgold_texture_value: 'eyJ0aW1lc3RhbXAiOjE0Nzk5MzEzNDMxMjgsInByb2ZpbGVJZCI6IjNlMjZiMDk3MWFjZDRjNmQ5MzVjNmFkYjE1YjYyMDNhIiwicHJvZmlsZU5hbWUiOiJOYWhlbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2NmNDRkZjIzMjBiNzYzMTI0N2FhZGY1OWMwZWNlOTdhNGJiNTdkZjI4YzFjZWU3OTM0ZjZhZTI4YWY4OTg5In19fQ=='"
			+ "\nbagofgold_texture_signature: 'k1xQ6E1NuxG1ZN7nlQqRJltYrJn44XHVhNA9pSEu2Pt2mkuixMxhIDj2Tg6o+JWlTyGfXtPVWLxygeGymmeSGaVcmDTaCALg7PL11ZfSzSWSxaIufNbj1EcSi264jg5FrAa/2/DnFsgu16wjlWiIGtjCzgx2QabY8YofoPKw6Y6Y5FHZJVXpT8Rsxs8ok6ZHtfm/ZyyTgvRSzh2mKmVyQIYJ1ZKxuqWhDQfbtBpu3dlEzMAEJo85Dvb7uIFYa7WFitjFJue/c9qpqAnazWFLrx33nYpjjeYhcfAvsaNQW3JVFEkyxzEgzOHbdsbiZcqTCwO+49whu175xOqT7XhouEubDT7A3H1jiSvQvkUZJv/GzUF4qFYHSfxhr6OWoBrRGwWmPdcrYx7fUWKo43CAqa5inaiTV4gU70BWrx5i3LhIJxpnspAyTXs8tZBxeoh8IizWD7uXkYYqh3j9cwuHoxfwZuMpOx9CPTC6R/YwJ1YK5OgJBY1+QhNw+NOilWT3jTok82elFvOLm3a5yLyVs+/UPmLD7rZsFm7/DD3VnRcpgjKRiyy2j9vYsYLyNE2BVLVJxBVk2yyy9u7L4VR6PO+8v2dh9DQl7vM2ORCxKPl2lt6woHWM2+eT1PXr16LtMtAOGYT8mlKFhp8Ou2+9fu4AqWkX7n3swU6XLiK5cJs='"
			+ "\n\nChoose between \"ITEM\",\"SKULL\"")
	public String dropMoneyOnGroundItemtype = "SKULL";

	@ConfigField(name = "bagofgold_item", category = "bagofgold", comment = "Here you can set which item should be used when you have chosen bagofgold_itemtype=ITEM "
			+ "\nUse Minecraft Item names like: " + "\nGOLD_NUGGET, DIAMOND, GOLD_INGOT, EMERALD, GOLDEN_APPLE"
			+ "\nChoose from this list: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html")
	public String dropMoneyOnGroundItem = "GOLD_INGOT";

	@ConfigField(name = "bagofgold_text_color", category = "bagofgold", comment = "Here you can set of the color of the number above the dropped item. \nUse color names like WHITE, RED, BLUE, GOLD")
	public String dropMoneyOnGroundTextColor = "GOLD";

	@ConfigField(name = "bagofgold_name", category = "bagofgold", comment = "This is the name of the BagOfGold item used in the player chat. Other examples could be Coins, Goldchest or what ever you like.")
	public String dropMoneyOnGroundSkullRewardName = "Bag of gold";

	@ConfigField(name = "bagofgold_name_plural", category = "bagofgold", comment = "This is the name of the reward in plural")
	public String dropMoneyOnGroundSkullRewardNamePlural = "Bag of gold";

	@ConfigField(name = "bagofgold_texture_value", category = "bagofgold", comment = "This is the Custom Texture Value generated at http://mineskin.org")
	public String dropMoneyOnGroundSkullTextureValue = "eyJ0aW1lc3RhbXAiOjE0ODU5MTIwNjk3OTgsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM5NmNlMTNmZjYxNTVmZGYzMjM1ZDhkMjIxNzRjNWRlNGJmNTUxMmYxYWRlZGExYWZhM2ZjMjgxODBmM2Y3In19fQ==";

	@ConfigField(name = "bagofgold_texture_signature", category = "bagofgold", comment = "This is the Custom Texture Signature generated at http://mineskin.org")
	public String dropMoneyOnGroundSkullTextureSignature = "m8u2ChI43ySVica7pcY0CsCuMCGgAdN7c9f/ZOxDZsPzJY8eiDrwxLIh6oPY1rvE1ja/rmftPSmdnbeHYrzLQ18QBzehFp8ZVegPsd9iNHc4FuD7nr1is2FD8M8AWAZOViiwlUKnfd8avb3SKfvFmhmVhQtE+atJYQrXhJwiqR4S+KTccA6pjIESM3AWlbCOmykg31ey7MQWB4YgtRp8NyFD3HNTLZ8alcEXBuG3t58wYBEME1UaOFah45tHuV1FW+iGBHHFWLu1UsAbg0Uw87Pp+KSTUGrhdwSc/55czILulI8IUnUfxmkaThRjd7g6VpH/w+9jLvm+7tOwfMQZlXp9104t9XMVnTAchzQr6mB3U6drCsGnuZycQzEgretQsUh3hweN7Jzz5knl6qc1n3Sn8t1yOvaIQLWG1f3l6irPdl28bwEd4Z7VDrGqYgXsd2GsOK/gCQ7rChNqbJ2p+jCja3F3ZohfmTYOU8W7DJ8Ne+xaofSuPnWODnZN9x+Y+3RE3nzH9tzP+NBMsV3YQXpvUD7Pepg7ScO+k9Fj3/F+KfBje0k6xfl+75s7kR3pNWQI5EVrO6iuky6dMuFPUBfNfq33fZV6Tqr/7o24aKpfA4WwJf91G9mC18z8NCgFR6iK4cPGmkTMvNtxUQ3MoB0LCOkRcbP0i7qxHupt8xE=";

	@ConfigField(name = "deny_hoppers_to_pickup_money_on_ground", category = "bagofgold", comment = "Dark room mobspawners usually collect items in a HOPPER. This is allowed by default."
			+ "\nIf you want to deny HOPPERS to collect MobHunting Money rewards "
			+ "\nset \"deny_hoppers_to_pickup_money_on_ground\"=false")
	public boolean denyHoppersToPickUpMoney = true;

	@ConfigField(name = "number_format", category = "bagofgold", comment = "Here you can change the way the BagOfGold value is formatted.")
	public String numberFormat = "#.#####";

	@ConfigField(name = "reward_rounding", category = "bagofgold", comment = "This is how the value of a BagOfGold is rounded, is they are merged, split and more."
			+ "\nAll numbers except 0 can be used. "
			+ "\nSet rounding_reward=1 if you want integers. IE. 10,11,12,13,14..."
			+ "\nSet rounding_reward=0.01 if you want 2 decimals 10.00, 10.01, 10.02... integers."
			+ "\nSet rounding_reward=5 if you want multipla of 5 IE. 10,15,20,25..."
			+ "\nSet rounding_reward=2 if you want multipla of 2 IE. 10,12,14,16...")
	public double rewardRounding = 1;

	@ConfigField(name = "minimum_reward", category = "bagofgold", comment = "This is the smallest value a BagOfGold item can have. 0.01 will be fine"
			+ "\nin most installation, but Gringott users may wont to have very low rewards (like 0.001  for killing a mob in MobHunting)"
			+ "\nRemember that some multipliers are less than 1, which might make the multipla even smaller."
			+ "\nThe minimum_reward should therefor be less than 10%"
			+ "\n of smallest reward. In the Gringotts example minimum_reward should be 0.0001 or 0.00005.")
	public double minimumReward = 0.01;

	@ConfigField(name = "limit_value_per_bag", category = "bagofgold", comment = "If you only want the bags to be able to contain a "
			+ "\ncertain amount of gold you can set the limit here. Ex this could be 10000 coins per bag.")
	public double limitPerBag = 10000;

	// #####################################################################################
	// Database
	// #####################################################################################
	@ConfigField(name = "type", category = "database", comment = "Type of database to use. Valid values are: sqlite, mysql")
	public String databaseType = "sqlite";

	@ConfigField(name = "username", category = "database")
	public String databaseUsername = "user";

	@ConfigField(name = "password", category = "database")
	public String databasePassword = "password";

	@ConfigField(name = "host", category = "database")
	public String databaseHost = "localhost:3306";

	@ConfigField(name = "database", category = "database")
	public String databaseName = "bagofgoldcore";

	@ConfigField(name = "database_version", category = "database", comment = "This is the database layout version. Mostly for internal use and you should not need"
			+ "\nto change this value. In case you decide to delete your database and let it recreate"
			+ "\nor if you change database type sqlite/mysql you should set this value to 0 again.")
	public int databaseVersion = 0;

	// #####################################################################################
	// Update Settings
	// #####################################################################################
	@ConfigField(name = "update-check", category = "updates", comment = "Check if there is a new version of the plugin available.")
	public boolean updateCheck = true;

	@ConfigField(name = "check_every", category = "updates", comment = "Set the number of seconds between each check. Recommended setting is"
			+ "\ncheck_every: 7200 ~ to check every second hour.")
	public int checkEvery = 7200;

	@ConfigField(name = "autoupdate", category = "updates", comment = "Set 'autoupdate: true' if you want new updates downloaded and installed."
			+ "\nYou will still have to reboot the server manually.")
	public boolean autoupdate = false;

	@Override
	protected void onPostLoad() throws InvalidConfigurationException {
		plugin.getMessages().setLanguage(language + ".lang");
	}

	public void backupConfig(File mFile) {
		File backupFile = new File(mFile.toString());
		int count = 0;
		while (backupFile.exists() && count++ < 1000) {
			backupFile = new File("plugins/BagOfGoldCore/backup/" + mFile.getName() + ".bak" + count);
		}
		if (mFile.exists())
			try {
				if (!backupFile.exists())
					backupFile.mkdirs();
				Files.copy(mFile.toPath(), backupFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
						StandardCopyOption.REPLACE_EXISTING);
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RESET
						+ " Config.yml was backed up to " + backupFile.getPath());
			} catch (IOException e1) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ "[ERROR] - Could not backup config.yml file to plugins/BagOfGoldCore/config.yml. Delete some old backups");
				e1.printStackTrace();
			}
	}

	public static int getConfigVersion(File file) {
		if (!file.exists())
			return -1;

		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		return config.getInt("general.config_version", config.contains("general.kill-debug") == true ? 0 : -1);
	}

	public void getConfigDataFromBagOfGoldPlugin() {
		File bagOfGoldConfigFile = new File("plugins/BagOfGold/config.yml");
		if (bagOfGoldConfigFile.exists()) {
			YamlConfiguration bagOfGoldConfig = YamlConfiguration.loadConfiguration(bagOfGoldConfigFile);
			dropMoneyOnGroundItem = bagOfGoldConfig.getString("dropmoneyonground.drop-money-on-ground-item");
			dropMoneyOnGroundItemtype = bagOfGoldConfig.getString("dropmoneyonground.drop-money-on-ground-itemtype");
			dropMoneyOnGroundSkullRewardName = bagOfGoldConfig
					.getString("dropmoneyonground.drop-money-on-ground-skull-reward-name");
			dropMoneyOnGroundSkullRewardNamePlural = bagOfGoldConfig
					.getString("dropmoneyonground.drop-money-on-ground-skull-reward-name-plural");
			dropMoneyOnGroundSkullTextureSignature = bagOfGoldConfig
					.getString("dropmoneyonground.drop-money-on-ground-skull-texture-value");
			dropMoneyOnGroundSkullTextureValue = bagOfGoldConfig
					.getString("dropmoneyonground.drop-money-on-ground-skull-texture-signature");
			dropMoneyOnGroundTextColor = bagOfGoldConfig.getString("dropmoneyonground.drop-money-on-ground-text-color");
			limitPerBag = bagOfGoldConfig.getDouble("economy.limit_per_bag");
			minimumReward = bagOfGoldConfig.getDouble("economy.minimum_reward");
			numberFormat = bagOfGoldConfig.getString("economy.number-format");
			rewardRounding = bagOfGoldConfig.getDouble("economy.reward_rounding");
			denyHoppersToPickUpMoney = bagOfGoldConfig
					.getBoolean("dropmoneyonground.deny_hoppers_to_pickup_money_on_ground");
			configVersion = 2;
		}
	}
	
	public void getConfigDataFromMobHuntingPlugin() {
		File mobhuntingConfigFile = new File("plugins/BagOfGold/config.yml");
		if (mobhuntingConfigFile.exists()) {
			YamlConfiguration bagOfGoldConfig = YamlConfiguration.loadConfiguration(mobhuntingConfigFile);
			dropMoneyOnGroundItem = bagOfGoldConfig.getString("dropmoneyonground.drop-money-on-ground-item");
			dropMoneyOnGroundItemtype = bagOfGoldConfig.getString("dropmoneyonground.drop-money-on-ground-itemtype");
			dropMoneyOnGroundSkullRewardName = bagOfGoldConfig
					.getString("dropmoneyonground.drop-money-on-ground-skull-reward-name");
			dropMoneyOnGroundSkullRewardNamePlural = bagOfGoldConfig
					.getString("dropmoneyonground.drop-money-on-ground-skull-reward-name-plural");
			dropMoneyOnGroundSkullTextureSignature = bagOfGoldConfig
					.getString("dropmoneyonground.drop-money-on-ground-skull-texture-value");
			dropMoneyOnGroundSkullTextureValue = bagOfGoldConfig
					.getString("dropmoneyonground.drop-money-on-ground-skull-texture-signature");
			dropMoneyOnGroundTextColor = bagOfGoldConfig.getString("dropmoneyonground.drop-money-on-ground-text-color");
			limitPerBag = bagOfGoldConfig.getDouble("economy.limit_per_bag");
			minimumReward = bagOfGoldConfig.getDouble("economy.minimum_reward");
			numberFormat = bagOfGoldConfig.getString("economy.number-format");
			rewardRounding = bagOfGoldConfig.getDouble("economy.reward_rounding");
			denyHoppersToPickUpMoney = bagOfGoldConfig
					.getBoolean("dropmoneyonground.deny_hoppers_to_pickup_money_on_ground");
			configVersion = 3;
		}
	}

}
