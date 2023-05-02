package me.fatpigsarefat.skills.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(Consumer<Optional<String>> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                 Scanner scanner = new Scanner(inputStream)) {

                if (scanner.hasNext()) {
                    consumer.accept(Optional.of(scanner.next()));
                } else {
                    consumer.accept(Optional.empty());
                }
            } catch (IOException e) {
                plugin.getLogger().info("Cannot look for updates: " + e.getMessage());
                consumer.accept(Optional.empty());
            }
        });
    }
}
