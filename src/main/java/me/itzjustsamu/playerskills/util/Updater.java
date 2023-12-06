package me.itzjustsamu.playerskills.util;

import me.itzjustsamu.playerskills.PlayerSkills;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class Updater {
    private final PlayerSkills plugin;
    private final int resourceId;

    public Updater(PlayerSkills plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void checkForUpdates() {
        getLatestVersion(
                latestVersion -> {
                    String currentVersion = plugin.getDescription().getVersion();

                    if (compareVersions(currentVersion, latestVersion) < 0) {
                        plugin.getLogger().info("A new version of the PlayerSkills is available: " + latestVersion);
                    } else if (compareVersions(currentVersion, latestVersion) > 0) {
                        plugin.getLogger().info("Experimental build: " + currentVersion);
                    } else {
                        plugin.getLogger().info("Your plugin is up to date!");
                    }
                },
                error -> plugin.getLogger().warning("Failed to check for updates: " + error.getMessage())
        );
    }

    private void getLatestVersion(Consumer<String> consumer, Consumer<Throwable> errorHandler) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URI uri = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
                URL url = uri.toURL();

                try (InputStream inputStream = url.openStream(); Scanner scanner = new Scanner(inputStream)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                }
            } catch (IOException | RuntimeException | Error exception) {
                errorHandler.accept(exception);
            } catch (Exception exception) {
                errorHandler.accept(new IOException(exception));
            }
        });
    }

    private int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int part1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int part2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (part1 < part2) {
                return -1;
            } else if (part1 > part2) {
                return 1;
            }
        }

        return 0;
    }
}
