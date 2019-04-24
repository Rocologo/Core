package one.lindegaard.Core.Server;

import org.bukkit.Bukkit; 

public class Servers {

	// *******************************************************************
	// Version detection
	// *******************************************************************
	public static boolean isMC114() {
		return Bukkit.getBukkitVersion().contains("1.14");
	}

	public static boolean isMC113() {
		return Bukkit.getBukkitVersion().contains("1.13");
	}

	public static boolean isMC112() {
		return Bukkit.getBukkitVersion().contains("1.12");
	}

	public static boolean isMC111() {
		return Bukkit.getBukkitVersion().contains("1.11");
	}

	public static boolean isMC110() {
		return Bukkit.getBukkitVersion().contains("1.10");
	}

	public static boolean isMC19() {
		return Bukkit.getBukkitVersion().contains("1.9");
	}

	public static boolean isMC18() {
		return Bukkit.getBukkitVersion().contains("1.8");
	}

	public static boolean isMC114OrNewer() {
		if (isMC114())
			return true;
		else if (isMC113() || isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC113OrNewer() {
		if (isMC113())
			return true;
		else if (isMC112() || isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC112OrNewer() {
		if (isMC112())
			return true;
		else if (isMC111() || isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC111OrNewer() {
		if (isMC111())
			return true;
		else if (isMC110() || isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC110OrNewer() {
		if (isMC110())
			return true;
		else if (isMC19() || isMC18())
			return false;
		return true;
	}

	public static boolean isMC19OrNewer() {
		if (isMC19())
			return true;
		else if (isMC18())
			return false;
		return true;
	}

	// *******************************************************************
	// Version detection
	// *******************************************************************
	public static boolean isGlowstoneServer() {
		return Bukkit.getServer().getName().equalsIgnoreCase("Glowstone");
	}

    public static boolean isPaperServer() {
        return Bukkit.getServer().getName().equalsIgnoreCase("Paper")
                && Bukkit.getServer().getVersion().toLowerCase().contains("paper");
    }

	public static boolean isSpigotServer() {
		return Bukkit.getServer().getName().equalsIgnoreCase("CraftBukkit")
				&& Bukkit.getServer().getVersion().toLowerCase().contains("spigot");
	}

	public static boolean isCraftBukkitServer() {
		return Bukkit.getServer().getName().equalsIgnoreCase("CraftBukkit")
				&& Bukkit.getServer().getVersion().toLowerCase().contains("bukkit");
	}

}
