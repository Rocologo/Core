package one.lindegaard.Core.update;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

public class SpigetUpdaterForced {

	private static SpigetUpdate spigetUpdate = null;
	private static String currentJarFile = "";
	private static String newDownloadVersion = "";

	public static void ForceDownloadJar(Plugin plugin) {
		spigetUpdate = new SpigetUpdate(plugin, 66905);
		spigetUpdate.setVersionComparator(VersionComparator.EQUAL);
		spigetUpdate.setUserAgent("BagOfGoldCore");

		spigetUpdate.checkForUpdate(new UpdateCallback() {
			@Override
			public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
				// VersionComparator.EQUAL handles all updates as new, so I have to check the
				// version number manually
				newDownloadVersion = newVersion;
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN
						+ "Forced downloading of BagOfGoldCore-" + newVersion + ".jar");
				final String OS = System.getProperty("os.name");
				boolean succes = spigetUpdate.downloadUpdate();

				try {
					Thread.sleep(5L);
				} catch (InterruptedException e1) {
				}
				int count = 0;

				while (count++ < 20) {
					// Wait for succes to become true & downloading has finished.
					Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN
							+ "Waiting for file transfer to be completed.");
					File downloadedJar = new File("plugins/update/" + currentJarFile);
					if (succes && downloadedJar.exists()) {
						if (OS.indexOf("Win") >= 0) {
							File newJar = new File("plugins/update/BagOfGoldCore-" + newDownloadVersion + ".jar");
							downloadedJar.renameTo(newJar);
						} else {
							File newJar = new File("plugins/BagOfGoldCore-" + newDownloadVersion + ".jar");
							downloadedJar.renameTo(newJar);
							Bukkit.getConsoleSender().sendMessage(
									ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "Download completed. Please restart your server to make the plugin active.");
							try {
								Bukkit.getPluginManager().loadPlugin(newJar);
							} catch (UnknownDependencyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvalidPluginException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvalidDescriptionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						break;
					} else {
						try {
							Thread.sleep(1000L);
						} catch (InterruptedException e) {
						}
					}
				}
			}

			@Override
			public void upToDate() {
			}
		});
	}

	public static void setCurrentJarFile(String currentJarFile) {
		SpigetUpdaterForced.currentJarFile = currentJarFile;
	}

}
