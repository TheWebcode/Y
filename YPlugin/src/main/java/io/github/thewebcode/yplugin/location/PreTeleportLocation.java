package io.github.thewebcode.yplugin.location;

import org.bukkit.Location;

public class PreTeleportLocation extends BaseLocation {

    private PreTeleportType type;

    public PreTeleportLocation(Location loc, PreTeleportType type) {
        super(loc);
        this.type = type;
    }

    public PreTeleportType getType() {
        return type;
    }
}
