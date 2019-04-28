package one.lindegaard.Core.rewards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.Core.Core;
import one.lindegaard.Core.Tools;

public class BagOfGoldItems implements Listener {

	Core plugin;

	public BagOfGoldItems() {
		this.plugin = Core.getInstance();

	}

	public String format(double money) {
		return Tools.format(money);
	}

	public boolean isBagOfGoldStyle() {
		return plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL")
				|| plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
				|| plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLED")
				|| plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLER");
	}

	/**
	 * setDisplayNameAndHiddenLores: add the Display name and the (hidden) Lores.
	 * The lores identifies the reward and contain secret information.
	 * 
	 * @param skull  - The base itemStack without the information.
	 * @param reward - The reward information is added to the ItemStack
	 * @return the updated ItemStack.
	 */
	public ItemStack setDisplayNameAndHiddenLores(ItemStack skull, Reward reward) {
		ItemMeta skullMeta = skull.getItemMeta();
		if (reward.getRewardType().equals(UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID)))
			skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + reward.getDisplayname(),
					"Hidden:" + reward.getMoney(), "Hidden:" + reward.getRewardType(),
					reward.getMoney() == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(),
					"Hidden:" + reward.getSkinUUID())));
		else
			skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + reward.getDisplayname(),
					"Hidden:" + reward.getMoney(), "Hidden:" + reward.getRewardType(),
					reward.getMoney() == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(),
					"Hidden:" + reward.getSkinUUID(), plugin.getMessages().getString("bagofgoldcore.reward.name"))));

		if (reward.getMoney() == 0)
			skullMeta.setDisplayName(
					ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + reward.getDisplayname());
		else
			skullMeta.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
					+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
							? format(reward.getMoney())
							: reward.getDisplayname() + " (" + format(reward.getMoney()) + ")"));
		skull.setItemMeta(skullMeta);
		return skull;
	}

	public double addBagOfGoldMoneyToPlayer(Player player, double amount) {
		boolean found = false;
		double moneyLeftToGive = amount;
		double addedMoney = 0;

		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				if ((rewardInSlot.isBagOfGoldReward() || rewardInSlot.isItemReward())) {
					if (rewardInSlot.getMoney() < plugin.getConfigManager().limitPerBag) {
						double space = plugin.getConfigManager().limitPerBag - rewardInSlot.getMoney();
						if (space > moneyLeftToGive) {
							addedMoney = addedMoney + moneyLeftToGive;
							rewardInSlot.setMoney(rewardInSlot.getMoney() + moneyLeftToGive);
							moneyLeftToGive = 0;
						} else {
							addedMoney = addedMoney + space;
							rewardInSlot.setMoney(plugin.getConfigManager().limitPerBag);
							moneyLeftToGive = moneyLeftToGive - space;
						}
						if (rewardInSlot.getMoney() == 0)
							player.getInventory().clear(slot);
						else
							is = setDisplayNameAndHiddenLores(is, rewardInSlot);
						plugin.getMessages().debug(
								"Added %s to %s's item in slot %s, new value is %s (addBagOfGoldPlayer_EconomyManager)",
								format(amount), player.getName(), slot, format(rewardInSlot.getMoney()));
						if (moneyLeftToGive <= 0) {
							found = true;
							break;
						}
					}
				}
			}
		}
		if (!found) {

			while (Tools.round(moneyLeftToGive) > 0 && canPickupMoney(player)) {
				double nextBag = 0;
				if (moneyLeftToGive > plugin.getConfigManager().limitPerBag) {
					nextBag = plugin.getConfigManager().limitPerBag;
					moneyLeftToGive = moneyLeftToGive - nextBag;
				} else {
					nextBag = moneyLeftToGive;
					moneyLeftToGive = 0;
				}
				if (player.getInventory().firstEmpty() == -1)
					dropBagOfGoldMoneyOnGround(player, null, player.getLocation(), Tools.round(nextBag));
				else {
					addedMoney = addedMoney + nextBag;
					ItemStack is;
					if (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL"))
						is = new CustomItems().getCustomtexture(UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID),
								plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
								plugin.getConfigManager().dropMoneyOnGroundSkullTextureValue,
								plugin.getConfigManager().dropMoneyOnGroundSkullTextureSignature, Tools.round(nextBag),
								UUID.randomUUID(), UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID));
					else {
						is = new ItemStack(Material.valueOf(plugin.getConfigManager().dropMoneyOnGroundItem), 1);
						setDisplayNameAndHiddenLores(is,
								new Reward(plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
										Tools.round(nextBag), UUID.fromString(Reward.MH_REWARD_ITEM_UUID),
										UUID.randomUUID(), null));
					}
					player.getInventory().addItem(is);
				}
			}
		}
		if (moneyLeftToGive > 0)
			dropBagOfGoldMoneyOnGround(player, null, player.getLocation(), moneyLeftToGive);
		return addedMoney;
	}

	public double removeBagOfGoldFromPlayer(Player player, double amount) {
		double taken = 0;
		double toBeTaken = Tools.round(amount);
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward reward = Reward.getReward(is);
				if (reward.isBagOfGoldReward() || reward.isItemReward()) {
					double saldo = Tools.round(reward.getMoney());
					if (saldo > toBeTaken) {
						reward.setMoney(Tools.round(saldo - toBeTaken));
						is = setDisplayNameAndHiddenLores(is, reward);
						player.getInventory().setItem(slot, is);
						taken = taken + toBeTaken;
						toBeTaken = 0;
						return Tools.round(taken);
					} else {
						player.getInventory().clear(slot);
						taken = taken + saldo;
						toBeTaken = toBeTaken - saldo;
					}
					if (reward.getMoney() == 0)
						player.getInventory().clear(slot);
				}
			}

		}
		return taken;
	}

	public void dropBagOfGoldMoneyOnGround(Player player, Entity killedEntity, Location location, double money) {
		Item item = null;
		double moneyLeftToDrop = Tools.ceil(money);
		ItemStack is;
		UUID uuid = null, skinuuid = null;
		double nextBag = 0;
		while (moneyLeftToDrop > 0) {
			if (moneyLeftToDrop > plugin.getConfigManager().limitPerBag) {
				nextBag = plugin.getConfigManager().limitPerBag;
				moneyLeftToDrop = Tools.round(moneyLeftToDrop - nextBag);
			} else {
				nextBag = Tools.round(moneyLeftToDrop);
				moneyLeftToDrop = 0;
			}

			if (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL")) {
				uuid = UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID);
				skinuuid = uuid;
				is = new CustomItems().getCustomtexture(uuid,
						plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
						plugin.getConfigManager().dropMoneyOnGroundSkullTextureValue,
						plugin.getConfigManager().dropMoneyOnGroundSkullTextureSignature, nextBag, UUID.randomUUID(),
						skinuuid);
			} else { // ITEM
				uuid = UUID.fromString(Reward.MH_REWARD_ITEM_UUID);
				skinuuid = null;
				is = new ItemStack(Material.valueOf(plugin.getConfigManager().dropMoneyOnGroundItem), 1);
			}

			item = location.getWorld().dropItem(location, is);
			if (item != null) {
				Core.getInstance().getRewardManager().getDroppedMoney().put(item.getEntityId(), nextBag);
				item.setMetadata(Reward.MH_REWARD_DATA,
						new FixedMetadataValue(plugin, new Reward(
								plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM") ? ""
										: Reward.getReward(is).getDisplayname(),
								nextBag, uuid, UUID.randomUUID(), skinuuid)));
				item.setCustomName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
						+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
								? format(nextBag)
								: Reward.getReward(is).getDisplayname() + " (" + format(nextBag) + ")"));
				item.setCustomNameVisible(true);
				plugin.getMessages().debug("%s dropped %s on the ground as item %s (# of rewards=%s)(3)",
						player.getName(), format(nextBag), plugin.getConfigManager().dropMoneyOnGroundItemtype,
						Core.getInstance().getRewardManager().getDroppedMoney().size());
			}
		}
	}

	public double getAmountOfBagOfGoldMoneyInInventory(Player player) {
		double amountInInventory = 0;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward reward = Reward.getReward(is);
				if (reward.isBagOfGoldReward() || reward.isItemReward())
					amountInInventory = amountInInventory + reward.getMoney();
			}
		}
		return amountInInventory;
	}

	public boolean canPickupMoney(Player player) {
		if (player.getInventory().firstEmpty() != -1)
			return true;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				if ((rewardInSlot.isBagOfGoldReward() || rewardInSlot.isItemReward())) {
					if (rewardInSlot.getMoney() < plugin.getConfigManager().limitPerBag)
						return true;
				}
			}
		}
		return false;
	}

	public double getSpaceForBagOfGoldMoney(Player player) {
		double space = 0;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				if ((rewardInSlot.isBagOfGoldReward() || rewardInSlot.isItemReward())) {
					space = space + plugin.getConfigManager().limitPerBag - rewardInSlot.getMoney();
				}
			} else if (is == null || is.getType() == Material.AIR) {
				space = space + plugin.getConfigManager().limitPerBag;
			}
		}
		plugin.getMessages().debug("%s has room for %s BagOfGold in the inventory", player.getName(), space);
		return space;
	}

	private boolean isFakeReward(Item item) {
		ItemStack itemStack = item.getItemStack();
		return isFakeReward(itemStack);
	}

	private boolean isFakeReward(ItemStack itemStack) {
		if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack
				.getItemMeta().getDisplayName().contains(plugin.getConfigManager().dropMoneyOnGroundSkullRewardName)) {
			if (!itemStack.getItemMeta().hasLore()) {
				return true;
			}
		}
		return false;
	}

}
