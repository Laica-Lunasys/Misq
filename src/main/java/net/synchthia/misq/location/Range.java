package net.synchthia.misq.location;

import lombok.Data;
import org.bukkit.Location;

@Data
public class Range {
    private final StaticBlockLocation from;
    private final StaticBlockLocation to;

    public StaticBlockLocation getFrom() {
        return this.getFrom(true);
    }

    public StaticBlockLocation getTo() {
        return this.getTo(true);
    }

    public StaticBlockLocation getFrom(boolean min) {
        if (min) {
            return new StaticBlockLocation(
                    Integer.min(from.getX(), to.getX()),
                    Integer.min(from.getY(), to.getY()),
                    Integer.min(from.getZ(), to.getZ())
            );
        }
        return from;
    }

    public StaticBlockLocation getTo(boolean max) {
        if (max) {
            return new StaticBlockLocation(
                    Integer.max(from.getX(), to.getX()),
                    Integer.max(from.getY(), to.getY()),
                    Integer.max(from.getZ(), to.getZ())
            );
        }
        return to;
    }

    public boolean withInRange(Location location) {
        return location.getX() >= getFrom().getX() && location.getX() <= getTo().getX() &&
                location.getY() >= getFrom().getY() && location.getY() <= getTo().getY() &&
                location.getZ() >= getFrom().getZ() && location.getZ() <= getTo().getZ();
    }

    public boolean withInBlockRange(Location location) {
        return withInBlockRange(location, 0, 0, 0);
    }

    public boolean withInBlockRange(Location location, int offsetX, int offsetY, int offsetZ) {
        return location.getBlockX() >= getFrom().getX() - offsetX && location.getBlockX() <= getTo().getX() + offsetX &&
                location.getBlockY() >= getFrom().getY() - offsetY && location.getBlockY() <= getTo().getY() + offsetY &&
                location.getBlockZ() >= getFrom().getZ() - offsetZ && location.getBlockZ() <= getTo().getZ() + offsetZ;
    }
}
