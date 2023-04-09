package me.fatpigsarefat.skills.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
    public static Location toLocation(String str) {
        String[] str2loc = str.split(":");
        if (str2loc.length == 4) {
            Location loc = new Location(Bukkit.getServer().getWorld(str2loc[0]), 0.0D, 0.0D, 0.0D);
            loc.setX(Double.parseDouble(str2loc[1]));
            loc.setY(Double.parseDouble(str2loc[2]));
            loc.setZ(Double.parseDouble(str2loc[3]));
            return loc;
        }
        return null;
    }

    public static String toString(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ();
    }
}
