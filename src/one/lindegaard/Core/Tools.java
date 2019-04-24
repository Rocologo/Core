package one.lindegaard.Core;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit; 
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class Tools {

	public static String format(double money) {
		Locale locale = new Locale("en", "UK");
		String pattern = "0.#####";
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);
		return decimalFormat.format(money);
	}

	public static boolean isUUID(String string) {
	    try {
	        UUID.fromString(string);
	        return true;
	    } catch (Exception ex) {
	        return false;
	    }
	}

	public static final Block getTargetBlock(Player player, int range) {
		BlockIterator iter = new BlockIterator(player, range);
		Block lastBlock = iter.next();
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (lastBlock.getType() == Material.AIR) {
				continue;
			}
			break;
		}
		return lastBlock;
	}

	/**
	 * Gets the online player (backwards compatibility)
	 *
	 * @return number of players online
	 */
	public static int getOnlinePlayersAmount() {
		try {
			Method method = Server.class.getMethod("getOnlinePlayers");
			if (method.getReturnType().equals(Collection.class)) {
				return ((Collection<?>) method.invoke(Bukkit.getServer())).size();
			} else {
				return ((Player[]) method.invoke(Bukkit.getServer())).length;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	/**
	 * Gets the online player (for backwards compatibility)
	 *
	 * @return all online players as a Java Collection, if return type of
	 *         Bukkit.getOnlinePlayers() is Player[] it will be converted to a
	 *         Collection.
	 */
	@SuppressWarnings({ "unchecked" })
	public static Collection<Player> getOnlinePlayers() {
		Method method;
		try {
			method = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
			Object players = method.invoke(null);
			Collection<Player> newPlayers;
			if (players instanceof Player[])
				newPlayers = Arrays.asList((Player[]) players);
			else
				newPlayers = (Collection<Player>) players;
			return newPlayers;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return Collections.emptyList();
	}
	
}
