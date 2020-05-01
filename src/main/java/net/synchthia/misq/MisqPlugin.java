package net.synchthia.misq;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class MisqPlugin extends JavaPlugin {
    @Getter
    private static MisqPlugin plugin;

    @Override
    public void onEnable() {
        // TODO: Implements something...

        plugin = this;
        this.getLogger().log(Level.INFO, "Enabled: " + this.getName());
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "Disabled: " + this.getName());
    }
}
