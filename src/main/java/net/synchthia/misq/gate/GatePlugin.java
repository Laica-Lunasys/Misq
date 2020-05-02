package net.synchthia.misq.gate;

import lombok.Getter;
import net.synchthia.misq.BasePlugin;
import net.synchthia.misq.MisqPlugin;
import org.bukkit.plugin.PluginManager;

import java.io.IOException;
import java.util.logging.Level;

public class GatePlugin extends BasePlugin {
    @Getter
    private GateStore gateStore;

    public GatePlugin(MisqPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.gateStore = new GateStore(this);

        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new GateListener(this), plugin);

        plugin.getLogger().log(Level.INFO, "Enabled Gate!");
    }

    @Override
    public void onDisable() {
        try {
            gateStore.save();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed save gate: " + e);
        }
        plugin.getLogger().log(Level.INFO, "Disabled Gate!");
    }
}
