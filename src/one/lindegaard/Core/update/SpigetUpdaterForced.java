package one.lindegaard.Core.update;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import one.lindegaard.Core.Core;
import one.lindegaard.Core.update.UpdateStatus;

public class SpigetUpdaterForced {

	private static Plugin plugin;

	public SpigetUpdaterForced(Plugin plugin) {
		SpigetUpdaterForced.plugin = plugin;
	}

	private static SpigetUpdate spigetUpdate = null;
	private static UpdateStatus updateAvailable = UpdateStatus.UNKNOWN;
	private static String currentJarFile = "";
	private static String newDownloadVersion = "";

	public SpigetUpdate getSpigetUpdate() {
		return spigetUpdate;
	}

	public UpdateStatus getUpdateAvailable() {
		return updateAvailable;
	}

	public void setUpdateAvailable(UpdateStatus b) {
		updateAvailable = b;
	}

	public String getCurrentJarFile() {
		return currentJarFile;
	}

	public void setCurrentJarFile(String name) {
		currentJarFile = name;
	}

	public String getNewDownloadVersion() {
		return newDownloadVersion;
	}

	public void setNewDownloadVersion(String newDownloadVersion) {
		this.newDownloadVersion = newDownloadVersion;
	}

	public void hourlyUpdateCheck(final CommandSender sender, boolean updateCheck, final boolean silent) {
		long seconds = Core.getInstance().getConfigManager().checkEvery;
		if (seconds < 900) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGOldCore]" + ChatColor.RED
					+ "[Warning] check_every in your config.yml is too low. A low number can cause server crashes. The number is raised to 900 seconds = 15 minutes.");
			seconds = 900;
		}
		if (updateCheck) {
			new BukkitRunnable() {
				@Override
				public void run() {
					checkForUpdate(sender, false);
				}
			}.runTaskTimer(Core.getInstance(), 0L, seconds * 20L);
		}
	}

	/**
	 * Download a new version, add version number to the downloaded filename
	 * (filename-n.n.n) , and the rename the old version to ???.jar.oldnnn
	 * 
	 * @param sender
	 * @return
	 */
	public static boolean downloadAndUpdateJar(CommandSender sender) {
		final String OS = System.getProperty("os.name");
		boolean succes = spigetUpdate.downloadUpdate();

		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if (count++ > 20) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.RED
							+ " No updates found. (No response from server after 20s)");
					//plugin.getMessages().senderSendMessage(sender, ChatColor.GREEN
					//		+ plugin.getMessages().getString("bagofgoldcore.commands.update.could-not-update"));
					//plugin.getMessages().debug("Update error: %s", spigetUpdate.getFailReason().toString());
					this.cancel();
				} else {
					// Wait for the response
					if (succes) {
						if (OS.indexOf("Win") >= 0) {
							File downloadedJar = new File("plugins/update/" + currentJarFile);
							File newJar = new File("plugins/update/BagOfGoldCore-" + newDownloadVersion + ".jar");
							if (newJar.exists())
								newJar.delete();
							downloadedJar.renameTo(newJar);
							Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN
									+ "Download completed");
						} else {
							if (updateAvailable != UpdateStatus.RESTART_NEEDED) {
								File currentJar = new File("plugins/" + currentJarFile);
								File disabledJar = new File("plugins/" + currentJarFile + ".old");
								int count = 0;
								while (disabledJar.exists() && count++ < 100) {
									disabledJar = new File("plugins/" + currentJarFile + ".old" + count);
								}
								if (!disabledJar.exists()) {
									currentJar.renameTo(disabledJar);
									File downloadedJar = new File("plugins/update/" + currentJarFile);
									File newJar = new File("plugins/BagOfGoldCore-" + newDownloadVersion + ".jar");
									downloadedJar.renameTo(newJar);
									Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN
											+ "Moved plugins/update/" + currentJarFile
											+ " to plugins/BagOfGoldCore-" + newDownloadVersion + ".jar");
									updateAvailable = UpdateStatus.RESTART_NEEDED;
									Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN
											+ "<download completed");
								}
							}
						}
						this.cancel();
					}
				}
			}
		}.runTaskTimer(plugin, 20L, 20L);
		return true;
	}

	/**
	 * Check is a new version is available
	 * 
	 * @param sender
	 * @param updateCheck
	 * @param silent      - if true the player will not get the status in Game
	 */
	public void checkForUpdate(final CommandSender sender, final boolean silent) {
		if (!silent)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.RESET
					+ "Updated check");
		if (updateAvailable != UpdateStatus.RESTART_NEEDED) {
			spigetUpdate = new SpigetUpdate(plugin, 66905);
			spigetUpdate.setVersionComparator(VersionComparator.EQUAL);
			spigetUpdate.setUserAgent("BagOfGoldCore-" + plugin.getDescription().getVersion());

			spigetUpdate.checkForUpdate(new UpdateCallback() {

				@Override
				public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
					//// VersionComparator.EQUAL handles all updates as new, so I have to check the
					//// version number manually
					updateAvailable = UpdateStatus.FORCED_DOWNLOAD;
					if (updateAvailable == UpdateStatus.FORCED_DOWNLOAD) {
						newDownloadVersion = newVersion;
						sender.sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN + "Downloading version:"+ newVersion);
							downloadAndUpdateJar(sender);
							sender.sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN+"Update completed");
					}
				}

				@Override
				public void upToDate() {
					//// Plugin is up-to-date
				}
			});
		}
	}

	public static void ForceDownloadJar(Plugin plugin) {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		spigetUpdate = new SpigetUpdate(plugin, 66905);
		spigetUpdate.setVersionComparator(VersionComparator.EQUAL);
		spigetUpdate.setUserAgent("BagOfGold");

		spigetUpdate.checkForUpdate(new UpdateCallback() {

			@Override
			public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
				//// VersionComparator.EQUAL handles all updates as new, so I have to check the
				//// version number manually
					newDownloadVersion = newVersion;
					sender.sendMessage(
							ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN + "Downloaded BagOfGoldCore - please restart your server.");
					downloadAndUpdateJar(sender);
			}

			@Override
			public void upToDate() {
				//// Plugin is up-to-date
			}
		});
	}

}
