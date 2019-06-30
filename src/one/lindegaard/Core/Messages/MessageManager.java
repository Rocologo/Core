package one.lindegaard.Core.Messages;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class MessageManager {

	private HashMap<Player, Long> lastMessage = new HashMap<Player, Long>();

	public MessageManager() {

	}

	public long getLastMessageTime(Player player) {
		if (lastMessage.containsKey(player))
			return lastMessage.get(player);
		else
			return 0;
	}

}
