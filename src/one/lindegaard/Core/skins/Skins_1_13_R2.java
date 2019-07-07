package one.lindegaard.Core.skins;

import java.util.Collection;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_13_R2.EntityPlayer;

public class Skins_1_13_R2 implements Skins {

	// How to get Playerskin
	// https://www.spigotmc.org/threads/how-to-get-a-players-texture.244966/

	@Override
	public String[] getSkin(Player player) {
		EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
		GameProfile profile = playerNMS.getProfile();
		Collection<Property> collection = profile.getProperties().get("textures");
		Property property;
		if (!collection.isEmpty())
			property = collection.iterator().next();
		else
			property = new Property("texture", "", "");
		String[] result = { property.getValue(), property.getValue() };
		return result;
	}

}
