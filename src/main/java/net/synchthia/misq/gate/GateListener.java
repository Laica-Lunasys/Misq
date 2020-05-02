package net.synchthia.misq.gate;

import net.synchthia.misq.location.Range;
import net.synchthia.misq.location.StaticBlockLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class GateListener implements Listener {
    private final GatePlugin plugin;

    public GateListener(GatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreateSign(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("misq.gate.create")) {
            return;
        }

        String name = event.getLine(0);
        String destination = event.getLine(1);
        World world = event.getBlock().getWorld();

        if (!(event.getBlock().getBlockData() instanceof WallSign)) {
            return;
        }

        // Get Sign
        Block attended = event.getBlock().getRelative(((WallSign) event.getBlock().getBlockData()).getFacing().getOppositeFace());
        // Check Layout
        // (G=Glow Stone, X=IRON Block, o=AIR, s=WallSign, p=portal(AIR))
        // [A3] o
        // [A2] p
        // [A1] G
        // [A0] Xs [X=attended]

        // A3
        Location a3 = new Location(world, attended.getX(), attended.getY() + 3, attended.getZ());
        if (!world.getBlockAt(a3).getType().equals(Material.AIR) &&
                !world.getBlockAt(a3).getType().equals(Material.WATER)) return;

        // A2
        Location a2 = new Location(world, attended.getX(), attended.getY() + 2, attended.getZ());
        if (!world.getBlockAt(a2).getType().equals(Material.AIR) &&
                !world.getBlockAt(a2).getType().equals(Material.WATER)) return;
        // A1
        Location a1 = new Location(world, attended.getX(), attended.getY() + 1, attended.getZ());
        if (!world.getBlockAt(a1).getType().equals(Material.GLOWSTONE)) return;

        // A0
        Location a0 = attended.getLocation();
        if (!world.getBlockAt(a0).getType().equals(Material.IRON_BLOCK)) return;

        // Check Sign
        if (name.isEmpty() || destination.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Name/Destination is Empty!");
            return;
        }

        // Range
        // Gate [A0] ~ [A3]
        Range gateRange = new Range(StaticBlockLocation.fromLocation(a0), StaticBlockLocation.fromLocation(a3));

        Gate gate = new Gate(name, destination, world.getUID(), gateRange, StaticBlockLocation.fromLocation(event.getBlock().getLocation()));
        boolean result = plugin.getGateStore().add(gate);
        if (result) {
            fillGate(gate, Material.WATER);
            player.sendMessage(String.format(ChatColor.GREEN + "Gate Created! %s -> %s", gate.getName(), gate.getDestination()));
        } else {
            player.sendMessage(ChatColor.RED + "Already Exist");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();

        if (!player.hasPermission("misq.gate.create")) {
            return;
        }

        // within portal
        if (plugin.getGateStore().get().values().stream().anyMatch(g -> g.getPortalArea().withInRange(loc))) {
            event.setCancelled(true);
            return;
        }

        Gate gate;

        // When break sign?
        if (event.getBlock().getBlockData() instanceof WallSign) {
            Optional<Gate> optGate = plugin.getGateStore().get().values().stream().filter(g -> g.getSignLocation().toLocation(player.getWorld()).distance(block.getLocation()) == 0).findAny();
            if (!optGate.isPresent()) {
                return;
            }
            gate = optGate.get();
        } else {
            Optional<Gate> optGate = plugin.getGateStore().get().values().stream().filter(g -> g.getGateArea().withInRange(loc)).findAny();
            if (!optGate.isPresent()) {
                return;
            }
            gate = optGate.get();
        }

        if (plugin.getGateStore().remove(gate.getName())) {
            fillGate(gate, Material.AIR);
            player.sendMessage(ChatColor.GREEN + "Gate Removed: " + gate.getName());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getTo();

        if (loc == null) {
            return;
        }

        Optional<Gate> optPortal = plugin.getGateStore().get().values().stream().filter(g ->
                g.getWorldUID().equals(player.getWorld().getUID()) && g.getPortalArea().withInBlockRange(loc)
        ).findFirst();

        if (optPortal.isPresent()) {
            Gate gate = optPortal.get();
            Gate destination = plugin.getGateStore().get().get(gate.getDestination());
            if (destination == null) {
                return;
            }

            Location to = calcTeleportPoint(loc, gate, destination);
            player.teleport(to);
        }
    }

    private Location calcTeleportPoint(Location location, Gate gate, Gate destination) {
        // calc face from sign
        Location destSignLoc = destination.getSignLocation().toLocation(destination.getWorld());

        if (!(destSignLoc.getBlock().getBlockData() instanceof WallSign)) {
            return null;
        }

        WallSign destSign = (WallSign) destSignLoc.getBlock().getBlockData();

        Location destLoc = destination.getPortalArea().getFrom().toLocation(destination.getWorld());

        Range portalArea = gate.getPortalArea();

        // Generate Teleport Point
        switch (destSign.getFacing()) {
            case SOUTH:
                destLoc = destLoc.add(0.5, compare(portalArea.getTo().getY(), (location.getBlockY() + 1)), 1.5);
                break;
            case WEST:
                destLoc = destLoc.add(-0.5, compare(portalArea.getTo().getY(), (location.getBlockY() + 1)), 0.5);
                break;
            case NORTH:
                destLoc = destLoc.add(0.5, compare(portalArea.getTo().getY(), (location.getBlockY() + 1)), -0.5);
                break;
            case EAST:
                destLoc = destLoc.add(1.5, compare(portalArea.getTo().getY(), (location.getBlockY() + 1)), 0.5);
                break;
        }

        destLoc.setYaw(convertBlockFace(destSign.getFacing()));
        destLoc.setPitch(location.getPitch());

        return destLoc;
    }

    private double compare(double a, double b) {
        if (a > b) {
            return a - b;
        } else {
            return b - a;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (plugin.getGateStore().get().values().stream().anyMatch(g -> g.getPortalArea().withInRange(event.getClickedBlock().getLocation()))) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getGateStore().get().values().stream().anyMatch(g -> g.getPortalArea().withInRange(getClickedLocation(event.getClickedBlock(), event.getBlockFace())))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPhysicWater(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        boolean isAround = plugin.getGateStore().get().values().stream().anyMatch(v -> {
            if (loc.getWorld() != null && !loc.getWorld().equals(v.getWorld())) {
                return false;
            }

            if (v.getGateArea().withInBlockRange(loc, 1, 0, 0)) {
                return true;
            } else if (v.getGateArea().withInBlockRange(loc, 0, 1, 0)) {
                return true;
            } else return v.getGateArea().withInBlockRange(loc, 0, 0, 1);
        });

        if (isAround) {
            event.setCancelled(true);
        }
    }

    private void fillGate(Gate gate, Material material) {
        Range portalArea = gate.getPortalArea();
        StaticBlockLocation from = portalArea.getFrom();
        StaticBlockLocation to = portalArea.getTo();
        for (int i = from.getX(); i <= to.getX(); i++) {
            for (int j = from.getY(); j <= to.getY(); j++) {
                for (int k = from.getZ(); k <= to.getZ(); k++) {
                    Location l = new Location(gate.getWorld(), i, j, k);
                    l.getBlock().setType(material, false);
                }
            }
        }
    }

    private float convertBlockFace(BlockFace blockFace) {
        switch (blockFace) {
            case WEST:
                return 90;
            case NORTH:
                return 180;
            case EAST:
                return -90;
        }
        return 0;
    }

    private Location getClickedLocation(Block block, BlockFace blockFace) {
        Location loc = block.getLocation();

        switch (blockFace) {
            case UP:
                loc.add(0, 1, 0);
                break;
            case DOWN:
                loc.add(0, -1, 0);
                break;
            case SOUTH:
                loc.add(0, 0, 1);
                break;
            case WEST:
                loc.add(-1, 0, 0);
                break;
            case NORTH:
                loc.add(0, 0, -1);
                break;
            case EAST:
                loc.add(1, 0, 0);
                break;
        }
        return loc;
    }
}
