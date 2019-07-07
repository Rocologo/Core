package one.lindegaard.Core.skins;

import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_13_R1.EntityPlayer;

public class Skins_1_13_R1 implements Skins {

	// How to get Playerskin
	// https://www.spigotmc.org/threads/how-to-get-a-players-texture.244966/

	@Override
	public String[] getSkin(Player player) {
		EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
		GameProfile profile = playerNMS.getProfile();
		String[] result = new String[2];
		if (profile.getProperties().containsKey("textures")) {
			Property property = profile.getProperties().get("textures").iterator().next();
			result[0] = property.getValue();
			result[1] = property.getValue();
		}
		return result;
	}

}
