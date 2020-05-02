package net.synchthia.misq.location;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;

@Data
public class StaticBlockLocation {
    private final int x;
    private final int y;
    private final int z;

    public static StaticBlockLocation fromLocation(Location location) {
        return new StaticBlockLocation(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }
}
