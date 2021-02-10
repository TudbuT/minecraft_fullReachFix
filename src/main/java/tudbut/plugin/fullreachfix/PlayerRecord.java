package tudbut.plugin.fullreachfix;

public class PlayerRecord {
    
    // Default reach for a player record
    public float playerReach = 1.5f;
    
    // Reach below configured max?
    public boolean isReachNormal() {
        return playerReach < Main.Config.maxReach;
    }
    
    // Offense counter for plugin
    public float offenses = 0;
    // All offenses that ever happened
    public int allOffenses = 0;
    
    // Called by event handler, records the reach of a hit
    public void recordReach(float reach) {
        // Not a small-distance hit
        if(reach > 1) {
            playerReach += reach; // Add reach
            playerReach /= 2; // Average between this reach and the reach recorded before
            if(!isReachNormal()) {
                offenses++; // Add offense if reach is too much
                allOffenses++;
                return;
            }
        }
        offenses -= 0.2;
        // If reach is normal and achievable,
        // remove one fifth of a offense, so after 5 normal hits,
        // a strange hit is normalized again.
        
        // No negative offenses
        if(offenses < 0)
            offenses = 0;
    }
}
