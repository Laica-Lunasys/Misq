package net.synchthia.misq.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import net.synchthia.misq.MisqPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("misq")
@Description("Misq Command")
@RequiredArgsConstructor
public class MisqCommand extends BaseCommand {
    private final MisqPlugin plugin;

    @Default
    @CatchUnknown
    public void onMisq(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5Misq&8] &7" + plugin.toString()));
    }
}
