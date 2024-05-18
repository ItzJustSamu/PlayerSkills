package me.itzjustsamu.playerskills.util;

import org.bukkit.Bukkit;

public class VersionControl {
    private static final String version = Bukkit.getServer().getVersion();

    public static boolean isAtLeastVersion(int major, int minor) {
        String[] serverVersionComponents = getServerVersion().split("\\.");
        int serverMajor = Integer.parseInt(serverVersionComponents[0]);
        int serverMinor = Integer.parseInt(serverVersionComponents[1]);

        return (serverMajor > major) || (serverMajor == major && serverMinor >= minor);
    }

    private static String getServerVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static boolean isVersionAbove113() {
        return isAtLeastVersion(1, 13);
    }

    public static boolean isNewVersion() {
        // Determine if the server is running a version with the Attribute class (1.9+)
        try {
            Class.forName("org.bukkit.attribute.Attribute");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
