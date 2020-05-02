package net.synchthia.misq.chairs;

import lombok.Getter;
import net.synchthia.misq.BasePlugin;
import net.synchthia.misq.MisqPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ChairsPlugin extends BasePlugin {
    @Getter
    private final Map<Player, Chair> store = new HashMap<>();

    public ChairsPlugin(MisqPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new ChairsListener(this), plugin);

        plugin.getLogger().log(Level.INFO, "Enabled Chairs!");
    }

    @Override
    public void onDisable() {
        this.getStore().forEach((p, c) -> {
            c.getArmorStand().removePassenger(p);
            p.teleport(c.getBeforeLocation());
        });
        this.getStore().clear();

        plugin.getLogger().log(Level.INFO, "Disabled Chairs!");
    }
}
