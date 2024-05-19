package me.itzjustsamu.playerskills.util;

import org.bukkit.Bukkit;

public class VersionControl {
    private static final String version = Bukkit.getServer().getClass().getPackage().getName();

    public static boolean isAtLeastVersion(int major, int minor) {
        String[] versionParts = getServerVersion().replace("v", "").split("_");
        int serverMajor = Integer.parseInt(versionParts[0]);
        int serverMinor = Integer.parseInt(versionParts[1]);

        return (serverMajor > major) || (serverMajor == major && serverMinor >= minor);
    }

    private static String getServerVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static boolean isVersionAbove113() {
        return isAtLeastVersion(1, 13);
    }

    private static final boolean NEW_VERSION;

    static {
        boolean newVersion;
        try {
            Class.forName("org.bukkit.attribute.Attribute");
            newVersion = true;
        } catch (ClassNotFoundException e) {
            newVersion = false;
        }
        NEW_VERSION = newVersion;
    }

    public static boolean isNewVersion() {
        return NEW_VERSION;
    }

    public static boolean isOldVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return version.equals("v1_8_R3") || version.startsWith("v1_8");
    }
}
