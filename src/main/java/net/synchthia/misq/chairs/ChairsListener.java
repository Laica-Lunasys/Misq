package net.synchthia.misq.chairs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class ChairsListener implements Listener {
    private final ChairsPlugin plugin;

    public ChairsListener(ChairsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location playerLoc = player.getLocation();
        Block block = event.getClickedBlock();

        // Ignore when isn't right-click
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        // Ignore sneaking
        if (player.isSneaking()) {
            return;
        }

        if (block == null) {
            return;
        }

        if (block.getState().getBlockData() instanceof Stairs) {
            Location loc = block.getLocation();
            loc.add(0.5, 0.3, 0.5);

            Stairs stairs = (Stairs) block.getState().getBlockData();

            // Is top
            if (stairs.getHalf().equals(Bisected.Half.TOP)) {
                return;
            } else {
                event.setCancelled(true);
            }

            // Occupied?
            if (plugin.getStore().containsKey(player)) {
                return;
            }

            // or Location?
            if (plugin.getStore().values().stream().anyMatch(v -> v.getChairLocation().equals(loc))) {
                return;
            }

            ArmorStand armorStand = block.getWorld().spawn(loc, ArmorStand.class, a -> {
                float turnOffset = 0;

                switch (stairs.getFacing()) {
                    case EAST:
                        turnOffset = 90;
                        break;
                    case SOUTH:
                        turnOffset = 180;
                        break;
                    case WEST:
                        turnOffset = -90;
                        break;
                }

                a.setRotation(turnOffset, 0);
                a.setVisible(false);
                a.setSmall(true);
                a.setGravity(false);
                a.setCollidable(false);
                a.setInvulnerable(true);
                a.setBasePlate(false);
                a.setMarker(true);
            });

            armorStand.addPassenger(player);

            plugin.getStore().put(player, new Chair(armorStand, block.getLocation(), playerLoc));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getStore().containsKey(player)) {
            Chair chair = plugin.getStore().get(player);
            chair.getArmorStand().removePassenger(player);
            chair.getArmorStand().remove();
            player.teleport(chair.getBeforeLocation());
            plugin.getStore().remove(player);
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            Player player = Bukkit.getPlayer(event.getEntity().getUniqueId());
            if (plugin.getStore().containsKey(player) && player != null) {
                Chair chair = plugin.getStore().get(player);
                chair.getArmorStand().removePassenger(player);
                chair.getArmorStand().remove();
                player.getServer().getScheduler().runTaskLater(plugin.getPlugin(), () -> player.teleport(chair.getBeforeLocation()), 1L);

                plugin.getStore().remove(player);
            }
        }
    }
}
