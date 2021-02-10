package tudbut.plugin.fullreachfix;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class EventListener implements Listener {
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        // If attacker is a player
        if(event.getDamager() instanceof Player) {
            // Fire local event, this returns the best-case reach of the player
            float f = onPlayerHitPlayer((Player) event.getDamager(), event.getEntity());
            
            // Get the player's record
            PlayerRecord record = Main.main.records.get(event.getDamager().getUniqueId().toString());
            
            if(Main.Config.showRecordOnHotbar)
                Utils.displayActionTitle(((Player) event.getDamager()), "Your reach record: " + f + " / " + record.playerReach + " / " + record.offenses);
            
            // Punish if required
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
        
        // Record
        PlayerRecord record;
        
        // Get the record
        if(!Main.main.records.keys().contains(source.getUniqueId().toString())) {
            record = new PlayerRecord();
            Main.main.records.set(source.getUniqueId().toString(), record);
        }
        else {
            record = Main.main.records.get(source.getUniqueId().toString());
        }
        
        // Calculate reach and add it to the record
        float f;
        record.recordReach(f = calculateReach(source, victim));
        
        return f;
    }
    
    public float calculateReach(Player source, Entity victim) {
        float d = Float.MAX_VALUE;
    
        EntityLiving entityVictim = (EntityLiving) Utils.getActualEntity(victim);
        Vector eyes = source.getEyeLocation().toVector();
        AxisAlignedBB hitBox = entityVictim.getBoundingBox();
        
        // All corners of the hitbox
        Vector posLLL = new Vector(hitBox.a, hitBox.b, hitBox.c); // x0 y0 z0
        Vector posLLH = new Vector(hitBox.a, hitBox.b, hitBox.f); // x0 y0 z1
        Vector posLHL = new Vector(hitBox.a, hitBox.e, hitBox.c); // x0 y1 z0
        Vector posLHH = new Vector(hitBox.a, hitBox.e, hitBox.f); // x0 y1 z1
        Vector posHLL = new Vector(hitBox.d, hitBox.b, hitBox.c); // x1 y0 z0
        Vector posHLH = new Vector(hitBox.d, hitBox.b, hitBox.f); // x1 y0 z1
        Vector posHHL = new Vector(hitBox.d, hitBox.e, hitBox.c); // x1 y1 z0
        Vector posHHH = new Vector(hitBox.d, hitBox.e, hitBox.f); // x1 y1 z1
        
        // Get distance to each corner, the smallest distance is then stored
        d = getDistance(d, eyes, posLLL);
        d = getDistance(d, eyes, posLLH);
        d = getDistance(d, eyes, posLHL);
        d = getDistance(d, eyes, posLHH);
        d = getDistance(d, eyes, posHLL);
        d = getDistance(d, eyes, posHLH);
        d = getDistance(d, eyes, posHHL);
        d = getDistance(d, eyes, posHHH);
        
        // Return smallest distance
        return d;
    }
    
    public float getDistance(float d, Vector location, Vector vector) {
        // Get smallest value, this distance, or d (prev distance)
        return (float) Math.min(d, location.distance(vector));
    }
}
