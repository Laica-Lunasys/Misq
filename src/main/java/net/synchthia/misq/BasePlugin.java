package net.synchthia.misq;

import lombok.Getter;

public abstract class BasePlugin {
    @Getter
    protected final MisqPlugin plugin;

    protected BasePlugin(MisqPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
