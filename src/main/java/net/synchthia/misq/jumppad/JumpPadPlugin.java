package net.synchthia.misq.jumppad;

import net.synchthia.misq.BasePlugin;
import net.synchthia.misq.MisqPlugin;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Level;

public class JumpPadPlugin extends BasePlugin {
    public JumpPadPlugin(MisqPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new JumpPadListener(this), plugin);

        plugin.getLogger().log(Level.INFO, "Enabled JumpPad!");
    }

    @Override
    public void onDisable() {
        plugin.getLogger().log(Level.INFO, "Disabled JumpPad!");
    }
}
