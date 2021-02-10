package tudbut.plugin.fullreachfix;

import de.tudbut.tools.FileRW;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tudbut.obj.Save;
import tudbut.obj.TLMap;
import tudbut.parsing.TCN;
import tudbut.tools.ConfigSaverTCN;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
            new File("plugins/FullReachFix").mkdirs();
            ConfigSaverTCN.loadConfig(this, TCN.read(new FileRW("plugins/FullReachFix/main.cfg").getContent().join("\n")));
        }
        catch (TCN.TCNException | IOException e) {
            e.printStackTrace();
        }
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("Enabled FullReachFix.MAIN");
    }
    
    @Override
    public void onDisable() {
        try {
            new FileRW("plugins/FullReachFix/main.cfg").setContent(ConfigSaverTCN.saveConfig(this).toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
