package net.synchthia.misq;

import co.aikar.commands.BukkitCommandManager;
import lombok.Getter;
import net.synchthia.misq.chairs.ChairsPlugin;
import net.synchthia.misq.commands.MisqCommand;
import net.synchthia.misq.gate.GatePlugin;
import net.synchthia.misq.jumppad.JumpPadPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class MisqPlugin extends JavaPlugin {
    @Getter
    private static MisqPlugin plugin;

    @Getter
    private BukkitCommandManager cmdManager;

    // MiniPlugins
    private ChairsPlugin chairs;
    private GatePlugin gate;
    private JumpPadPlugin jumpPad;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        this.cmdManager = new BukkitCommandManager(this);
        cmdManager.registerCommand(new MisqCommand(this));

        // Chairs
        this.chairs = new ChairsPlugin(this);
        this.chairs.onEnable();

        // Gate
        this.gate = new GatePlugin(this);
        this.gate.onEnable();

        // JumpPad
        this.jumpPad = new JumpPadPlugin(this);
        this.jumpPad.onEnable();

        plugin = this;
        this.getLogger().log(Level.INFO, "Enabled: " + this.getName());
    }

    @Override
    public void onDisable() {
        this.chairs.onDisable();
        this.gate.onDisable();
        this.jumpPad.onDisable();

        this.getLogger().log(Level.INFO, "Disabled: " + this.getName());
    }
}
