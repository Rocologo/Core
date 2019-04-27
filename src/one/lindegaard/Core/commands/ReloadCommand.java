package one.lindegaard.Core.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.Core.Core;
import one.lindegaard.Core.Messages;
import one.lindegaard.Core.Tools;

public class ReloadCommand implements ICommand {

	private Core plugin;

	public ReloadCommand(Core plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "bagofgoldcore.reload";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.WHITE + " - to reload BagOfGold configuration." };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgoldcore.commands.reload.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {

		long starttime = System.currentTimeMillis();
		int i = 1;
		while (plugin.getDataStoreManager().isRunning() && (starttime + 10000 > System.currentTimeMillis())) {
			if (((int) (System.currentTimeMillis() - starttime)) / 1000 == i) {
				plugin.getMessages().debug("saving data (%s)");
				i++;
			}
		}

		plugin.setMessages(new Messages(plugin));

		if (plugin.getConfigManager().loadConfig()) {
			plugin.getWorldGroupManager().load();
			
			int n = Tools.getOnlinePlayersAmount();
			if (n > 0) {
				plugin.getMessages().debug("Reloading %s PlayerSettings & PlayerBalancees from the database", n);
				for (Player player : Tools.getOnlinePlayers()){
					plugin.getPlayerSettingsManager().load(player);
				}
			}

			plugin.getMessages().senderSendMessage(sender,
					ChatColor.GREEN + plugin.getMessages().getString("bagofgoldcore.commands.reload.reload-complete"));

		} else
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.RED + plugin.getMessages().getString("bagofgoldcore.commands.reload.reload-error"));

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
