package tudbut.plugin.fullreachfix;

public class PlayerRecord {
    
    public float playerReach = 1.5f;
    
    public boolean isReachNormal() {
        return playerReach < Main.Config.maxReach;
    }
    
    public float offenses = 0;
    
    public void recordReach(float reach) {
        if(reach > 1.5) {
            playerReach += reach; // Add reach
            playerReach /= 2; // Average between this reach and the reach recorded before
            if(!isReachNormal()) {
                offenses++; // Add offense if reach is too much
                return;
            }
        }
        offenses -= 0.2;
        // If reach is normal and achievable,
        // remove one fifth of a offense, so after 5 normal hits,
        // a strange hit is normalized again.
        if(offenses < 0)
            offenses = 0;
    }
}
