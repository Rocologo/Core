package one.lindegaard.BagOfGoldCore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.BagOfGoldCore.BagOfGoldCore;
import one.lindegaard.BagOfGoldCore.PlayerSettings;
import one.lindegaard.BagOfGoldCore.storage.DataStoreManager;

public class MuteCommand implements ICommand {

	private BagOfGoldCore plugin;

	public MuteCommand(BagOfGoldCore plugin) {
		this.plugin = plugin;
	}

	// Used case
	// /mh mute - No args, args.length = 0 || arg[0]=""

	@Override
	public String getName() {
		return "mute";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "silent", "notify" };
	}

	@Override
	public String getPermission() {
		return "bagofgoldcore.mute";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.WHITE + " - to mute/unmute.",
				ChatColor.GOLD + label + ChatColor.GREEN + " <playername>" + ChatColor.WHITE
						+ " - to mute/unmute a the notifications for a specific player." };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgoldcore.commands.mute.description");
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {

		if (args.length == 0) {
			togglePlayerMuteMode((Player) sender);
			return true;
		} else if (args.length == 1) {
			DataStoreManager ds = plugin.getDataStoreManager();
			Player player = (Player) ds.getPlayerByName(args[0]);
			if (player != null) {
				if (sender.hasPermission("bagofgoldcore.mute.other") || sender instanceof ConsoleCommandSender) {
					togglePlayerMuteMode(player);
				} else {
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.RED + "You dont have permission " + ChatColor.AQUA + "'bagofgoldcore.mute.other'");
				}
				return true;
			} else {
				plugin.getMessages().senderSendMessage(sender, ChatColor.RED + "Player " + args[0] + " is not online.");
				return false;
			}
		}
		return false;
	}

	private void togglePlayerMuteMode(Player player) {
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(player);
		ps.setMuteMode(!ps.isMuted());
		plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);
		if (ps.isMuted())
			player.sendMessage(
					plugin.getMessages().getString("bagofgoldcore.commands.mute.muted", "player", player.getName()));
		else
			player.sendMessage(
					plugin.getMessages().getString("bagofgoldcore.commands.mute.unmuted", "player", player.getName()));
	}
}
