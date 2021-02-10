package tudbut.plugin.fullreachfix;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class Utils {
    
    public static Entity getActualEntity(org.bukkit.entity.Entity entity) {
        try {
            Field f = CraftEntity.class.getDeclaredField("entity");
            f.setAccessible(true);
            return (Entity) f.get(entity);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static void displayActionTitle(Player player, String s) {
        ((EntityPlayer) getActualEntity(player)).playerConnection.sendPacket(new PacketPlayOutChat(new ChatComponentText(s), (byte) 2));
    }
}
