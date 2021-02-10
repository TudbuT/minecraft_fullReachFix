package tudbut.plugin.fullreachfix;

import de.tudbut.tools.FileRW;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tudbut.obj.Save;
import tudbut.obj.TLMap;
import tudbut.parsing.TCN;
import tudbut.tools.ConfigSaverTCN;
import tudbut.tools.ObjectSerializerTCN;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
    
    public static Main main;
    
    {
        main = this;
    }
    
    @Save
    TLMap<String, PlayerRecord> records = new TLMap<>();
    
    @Override
    public void onEnable() {
        try {
            TCN tcn;
            if(new File("plugins/FullReachFix").mkdirs()) {
                //noinspection InstantiationOfUtilityClass
                tcn = new ObjectSerializerTCN(new Config()).convertAll().done();
                assert tcn != null;
                tcn.set("$type", null);
                tcn.set("$isArray", null);
                new FileRW("plugins/FullReachFix/config.tcn").setContent(tcn.toString());
            }
            tcn = TCN.read(new FileRW("plugins/FullReachFix/config.tcn").getContent().join("\n"));
            tcn.set("$isArray", false);
            tcn.set("$type", Config.class.getName());
            new ObjectSerializerTCN(tcn);
            
            ConfigSaverTCN.loadConfig(this, TCN.read(new FileRW("plugins/FullReachFix/record.tcn").getContent().join("\n")));
        }
        catch (Exception ignored) { }
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("Enabled FullReachFix.MAIN");
    }
    
    @Override
    public void onDisable() {
        try {
            new FileRW("plugins/FullReachFix/record.tcn").setContent(ConfigSaverTCN.saveConfig(this).toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static class Config {
        public static int kickAt = 6;
        public static int stopAt = 3;
        public static float maxReach = 3;
        public static boolean showRecordOnHotbar = true;
    }
}
