package net.synchthia.misq.gate;

import com.google.gson.annotations.Expose;
import lombok.Data;
import net.synchthia.misq.location.Range;
import net.synchthia.misq.location.StaticBlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.UUID;

@Data
public class Gate {
    private final String name;
    private final String destination;
    private final UUID worldUID;
    private final Range gateArea;
    private final StaticBlockLocation signLocation;

    @Expose
    private Range portalArea;

    public Gate(String name, String destination, UUID worldUID, Range gateArea, StaticBlockLocation signLocation) {
        this.name = name;
        this.destination = destination;
        this.worldUID = worldUID;
        this.gateArea = gateArea;
        this.signLocation = signLocation;
        this.portalArea = calcPortalArea(this);
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldUID);
    }

    private Range calcPortalArea(Gate gate) {
        Location from = null;
        Location to = null;

        for (int x = gate.getGateArea().getFrom().getX(); x <= gate.getGateArea().getTo().getX(); x++) {
            for (int y = gate.getGateArea().getFrom().getY(); y <= gate.getGateArea().getTo().getY(); y++) {
                for (int z = gate.getGateArea().getFrom().getZ(); z <= gate.getGateArea().getTo().getZ(); z++) {
                    Location location = new Location(gate.getWorld(), x, y, z);
                    if (location.getBlock().getType().equals(Material.WATER) || location.getBlock().getType().equals(Material.AIR)) {
                        from = location;
                        break;
                    }
                }
            }
        }

        for (int x = gate.getGateArea().getTo().getX(); x >= gate.getGateArea().getFrom().getX(); x--) {
            for (int y = gate.getGateArea().getTo().getY(); y >= gate.getGateArea().getFrom().getY(); y--) {
                for (int z = gate.getGateArea().getTo().getZ(); z >= gate.getGateArea().getFrom().getZ(); z--) {
                    Location location = new Location(gate.getWorld(), x, y, z);
                    if (location.getBlock().getType().equals(Material.WATER) || location.getBlock().getType().equals(Material.AIR)) {
                        to = location;
                        break;
                    }
                }
            }
        }

        return new Range(StaticBlockLocation.fromLocation(from), StaticBlockLocation.fromLocation(to));
    }
}
