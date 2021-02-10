package tudbut.plugin.fullreachfix;

import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.util.Vector;
import tudbut.obj.TLMap;

import java.lang.reflect.Field;
import java.util.UUID;

public class EventListener implements Listener {
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            float f = onPlayerHitPlayer((Player) event.getDamager(), event.getEntity());
            PlayerRecord record = Main.main.records.get(event.getDamager().getUniqueId().toString());
            Utils.displayActionTitle(((Player) event.getDamager()), "Your reach record: " + f + " / " + record.playerReach + " / " + record.offenses);
            if(record.offenses >= Main.Config.stopAt) {
                event.setCancelled(true);
            }
            if(record.offenses >= Main.Config.kickAt) {
                ((Player) event.getDamager()).kickPlayer("ReachHacks detected!");
                record.offenses = (float) (Main.Config.stopAt - 0.2);
            }
        }
    }
    
    public float onPlayerHitPlayer(Player source, Entity victim) {
        PlayerRecord record;
        if(!Main.main.records.keys().contains(source.getUniqueId().toString())) {
            record = new PlayerRecord();
            Main.main.records.set(source.getUniqueId().toString(), record);
        }
        else {
            record = Main.main.records.get(source.getUniqueId().toString());
        }
        
        float f;
        record.recordReach(f = calculateReach(source, victim));
        
        
        return f;
    }
    
    public float calculateReach(Player source, Entity victim) {
        float d = Float.MAX_VALUE;
    
        EntityLiving entityVictim = (EntityLiving) Utils.getActualEntity(victim);
        Vector eyes = source.getEyeLocation().toVector();
        AxisAlignedBB hitBox = entityVictim.getBoundingBox();
        
        // All corners
        Vector posLLL = new Vector(hitBox.a, hitBox.b, hitBox.c); // x0 y0 z0
        Vector posLLH = new Vector(hitBox.a, hitBox.b, hitBox.f); // x0 y0 z1
        Vector posLHL = new Vector(hitBox.a, hitBox.e, hitBox.c); // x0 y1 z0
        Vector posLHH = new Vector(hitBox.a, hitBox.e, hitBox.f); // x0 y1 z1
        Vector posHLL = new Vector(hitBox.d, hitBox.b, hitBox.c); // x1 y0 z0
        Vector posHLH = new Vector(hitBox.d, hitBox.b, hitBox.f); // x1 y0 z1
        Vector posHHL = new Vector(hitBox.d, hitBox.e, hitBox.c); // x1 y1 z0
        Vector posHHH = new Vector(hitBox.d, hitBox.e, hitBox.f); // x1 y1 z1
        
        d = getDistance(d, eyes, posLLL);
        d = getDistance(d, eyes, posLLH);
        d = getDistance(d, eyes, posLHL);
        d = getDistance(d, eyes, posLHH);
        d = getDistance(d, eyes, posHLL);
        d = getDistance(d, eyes, posHLH);
        d = getDistance(d, eyes, posHHL);
        d = getDistance(d, eyes, posHHH);
        
        return d;
    }
    
    public float getDistance(float d, Vector location, Vector vector) {
        return (float) Math.min(d, location.distance(vector));
    }
}
