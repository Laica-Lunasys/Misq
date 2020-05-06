package net.synchthia.misq.jumppad;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class JumpPadListener implements Listener {
    private final JumpPadPlugin plugin;

    public JumpPadListener(JumpPadPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block baseBlock = event.getTo().getBlock().getRelative(BlockFace.DOWN);
        if (!baseBlock.getType().equals(Material.SLIME_BLOCK) && !baseBlock.getType().equals(Material.REDSTONE_LAMP)) {
            return;
        }

        Block plate = event.getTo().getBlock();

        if (plate.getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)) {
            shoot(player, 3, 1);
        } else if (plate.getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
            shoot(player, 1.5, 1);
        } else if (plate.getType().equals(Material.STONE_PRESSURE_PLATE)) {
            shoot(player, 1, 0.5);
        } else if (plate.getType().equals(Material.OAK_PRESSURE_PLATE)) {
            shoot(player, 0.5, 0.3);
        }
    }

    private void shoot(Player player, double length, double height) {
        // Play Sound
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 1.0F, 1);

        // Calc vector
        Vector vector = player.getLocation().getDirection();

        // Shoot player
        player.setVelocity(vector.normalize().multiply(length).setY(height));
        for (int i = 1; i <= 4; i++) {
            long j = i;
            plugin.getPlugin().getServer().getScheduler().runTaskLater(plugin.getPlugin(), () -> {
                double l = length * j < 10 ? length * j : 10;
                player.setVelocity(vector.normalize().multiply(l).setY(height));
            }, j * 2L);
        }
    }
}
