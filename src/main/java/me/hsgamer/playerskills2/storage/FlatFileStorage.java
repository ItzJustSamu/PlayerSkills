package me.hsgamer.playerskills2.storage;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.playerskills2.config.MainConfig;
import me.hsgamer.playerskills2.player.SPlayer;
import me.hsgamer.playerskills2.util.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

public class FlatFileStorage implements PlayerStorage {
    private Config setupFile(UUID uuid) {
        File dataFolder = JavaPlugin.getProvidingPlugin(getClass()).getDataFolder();
        File userFile = new File(dataFolder, "users" + File.separator + uuid.toString() + ".yml");
        BukkitConfig config = new BukkitConfig(userFile);
        config.setup();
        return config;
    }

    @Override
    public SPlayer load(UUID uuid) {
        Config config = setupFile(uuid);
        SPlayer sPlayer = new SPlayer(uuid);
        config.getNormalizedValues("skills", false).forEach((k, v) -> {
            try {
                sPlayer.getSkills().put(k, Integer.parseInt(String.valueOf(v)));
            } catch (Exception exception) {
                if (MainConfig.OPTIONS_VERBOSE.getValue()) {
                    Utils.logError("Error while loading player " + uuid, exception);
                }
            }
        });
        int points = config.getInstance("points", 0, Number.class).intValue();
        sPlayer.setPoints(points);
        return sPlayer;
    }

    @Override
    public void save(SPlayer player) {
        Config config = setupFile(player.getPlayer());
        config.remove("skills");
        config.set("skills", player.getSkills());
        config.set("points", player.getPoints());
        config.save();
    }

    @Override
    public String getName() {
        return "FLAT_FILE";
    }
}
