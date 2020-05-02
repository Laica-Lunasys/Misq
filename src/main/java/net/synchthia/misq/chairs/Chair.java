package net.synchthia.misq.chairs;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

@Data
public class Chair {
    @NonNull
    private final ArmorStand armorStand;

    private final Location chairLocation;
    private final Location beforeLocation;
}
