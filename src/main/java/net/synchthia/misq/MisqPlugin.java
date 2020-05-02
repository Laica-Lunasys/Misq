package net.synchthia.misq;

import lombok.Getter;
import net.synchthia.misq.chairs.ChairsPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class MisqPlugin extends JavaPlugin {
    @Getter
    private static MisqPlugin plugin;

    // MiniPlugins
    private ChairsPlugin chairs;

    @Override
    public void onEnable() {
        // Chairs
        this.chairs = new ChairsPlugin(this);
        this.chairs.onEnable();

        plugin = this;
        this.getLogger().log(Level.INFO, "Enabled: " + this.getName());
    }

    @Override
    public void onDisable() {
        this.chairs.onDisable();

        this.getLogger().log(Level.INFO, "Disabled: " + this.getName());
    }
}
