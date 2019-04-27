package one.lindegaard.Core.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import one.lindegaard.Core.Core;
import one.lindegaard.Core.update.UpdateStatus;

public class VersionCommand implements ICommand {

	private Core plugin;

	public VersionCommand(Core plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "version";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "ver", "-v" };
	}

	@Override
	public String getPermission() {
		return "bagofgoldcore.version";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " version" + ChatColor.WHITE
				+ " - to get the version number" };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgoldcore.commands.version.description");
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
		plugin.getMessages().senderSendMessage(sender,
				ChatColor.GREEN + plugin.getMessages().getString("bagofgoldcore.commands.version.currentversion",
						"currentversion", plugin.getDescription().getVersion()));
		if (plugin.getSpigetUpdater().getUpdateAvailable() == UpdateStatus.AVAILABLE)
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.GREEN + plugin.getMessages().getString("bagofgoldcore.commands.version.newversion",
							"newversion", plugin.getSpigetUpdater().getNewDownloadVersion()));
		else if (sender.hasPermission("bagofgoldcore.update"))
			plugin.getSpigetUpdater().checkForUpdate(sender, true);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
