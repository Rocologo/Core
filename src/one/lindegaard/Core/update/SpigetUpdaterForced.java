package one.lindegaard.Core.update;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import com.google.common.io.Files;

public class SpigetUpdaterForced {

	private static SpigetUpdate spigetUpdate = null;

	private static UpdateStatus updateAvailable = UpdateStatus.UNKNOWN;
	private static String currentJarFile = "";
	private static String currentPath = "";
	private static String newDownloadVersion = "";

	public static void ForceDownloadJar(Plugin plugin) {
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		if (plugin != null)
			sender.sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN + "Plugin=" + plugin.getName());
		else
			sender.sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN + "Plugin=null");

		spigetUpdate = new SpigetUpdate(plugin, 66905);
		spigetUpdate.setVersionComparator(VersionComparator.EQUAL);
		spigetUpdate.setUserAgent("BagOfGoldCore");

		sender.sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN + "Check for updates");
		spigetUpdate.checkForUpdate(new UpdateCallback() {

			@Override
			public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
				//// VersionComparator.EQUAL handles all updates as new, so I have to check the
				//// version number manually
				updateAvailable = UpdateStatus.FORCED_DOWNLOAD;
				newDownloadVersion = newVersion;
				sender.sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN
						+ "Downloading version: BagOfGoldCore-" + newVersion + ".jar");
				final String OS = System.getProperty("os.name");
				boolean succes = spigetUpdate.downloadUpdate();

				int count = 0;

				while (count++ < 20) {
					// Wait for succes to become true (= downloading finished.
					Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN
							+ "Waiting for file transfer to be completed. Done=" + succes);
					if (succes) {
						Bukkit.getConsoleSender().sendMessage(
								ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "Download completed");
						if (OS.indexOf("Win") >= 0) {
							File downloadedJar = new File("./plugins/update/" + currentJarFile);
							File newJar = new File("./plugins/update/BagOfGoldCore-" + newDownloadVersion + ".jar");
							Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN
									+ " downloadedJar=" + downloadedJar.toString() + " newJar=" + newJar.toString());

							try {
								Files.move(downloadedJar, newJar);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// downloadedJar.renameTo(newJar);

						} else {
							if (updateAvailable != UpdateStatus.RESTART_NEEDED) {
								File downloadedJar = new File(currentPath+"/update/" + currentJarFile);
								File newJar = new File(currentPath+"/BagOfGoldCore-" + newDownloadVersion + ".jar");
								Bukkit.getConsoleSender()
										.sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN
												+ "downloadedJar=" + downloadedJar.toString() + " newJar="
												+ newJar.toString() + " path=" + plugin.getDataFolder());
								/**try {
									Files.move(downloadedJar, newJar);
								} catch (IOException e) {
									e.printStackTrace();
								}**/
								if (downloadedJar.exists())
									downloadedJar.renameTo(newJar);
								else { 
									Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "Could not find downloadedJar="+downloadedJar.toString()+", currentJar="+currentJarFile);
									if (new File(currentJarFile).exists()) 
										Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "1111 path="+new File(currentJarFile).getPath());
									if (new File(currentPath).exists()) 
										Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + 
												"1122 path="+new File(currentPath).getPath());
									if (new File("plugins/update/"+currentJarFile).exists())
										Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "2222");
									if (new File("/plugins/update/"+currentJarFile).exists())
										Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "2233");
									if (new File("./plugins/update/"+currentJarFile).exists())
										Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "2255");
									if (new File("../plugins/update/"+currentJarFile).exists())
										Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "3333");
									if (new File("../../plugins/update/"+currentJarFile).exists())
										Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "4444");
									if (new File("/home/christian/Nextcloud/Testservers/TestServer 1.14/plugins/"+currentJarFile).exists())
										Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "5555");
									if (new File(currentPath+"/update/"+currentJarFile).exists())
										Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "6666");
									
								}
							
								Bukkit.getConsoleSender()
										.sendMessage(ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "Moved "
												+ downloadedJar.toString() + " to " + newJar.toString());
								updateAvailable = UpdateStatus.RESTART_NEEDED;
								Bukkit.getConsoleSender().sendMessage(
										ChatColor.GOLD + "[BagOfGoldCore]" + ChatColor.GREEN + "Download completed");
							}
						}
						break;
					} else {
						try {
							Thread.sleep(1000L);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				sender.sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN + "Update completed");
			}

			@Override
			public void upToDate() {
				sender.sendMessage(ChatColor.GOLD + "[BagOfGoldCore] " + ChatColor.GREEN + "No updates found");
			}
		});
	}

	public static void setCurrentJarFile(String currentJarFile) {
		SpigetUpdaterForced.currentJarFile = currentJarFile;
	}
	public static void setCurrentPath(String currentPath) {
		SpigetUpdaterForced.currentPath = currentPath;
	}

}
