package net.synchthia.misq;

import lombok.Getter;
import net.synchthia.misq.chairs.ChairsPlugin;
import net.synchthia.misq.gate.GatePlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class MisqPlugin extends JavaPlugin {
    @Getter
    private static MisqPlugin plugin;

    // MiniPlugins
    private ChairsPlugin chairs;
    private GatePlugin gate;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Chairs
        this.chairs = new ChairsPlugin(this);
        this.chairs.onEnable();

        // Gate
        this.gate = new GatePlugin(this);
        this.gate.onEnable();

        plugin = this;
        this.getLogger().log(Level.INFO, "Enabled: " + this.getName());
    }

    @Override
    public void onDisable() {
        this.chairs.onDisable();
        this.gate.onDisable();

        this.getLogger().log(Level.INFO, "Disabled: " + this.getName());
    }
}
