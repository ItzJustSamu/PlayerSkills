package me.fatpigsarefat.skills.managers;

import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.utils.LocationUtil;
import me.fatpigsarefat.skills.utils.Skill;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HologramManager {
    private HashMap<UUID, Hologram> holograms;

    private FileManager.Config config;

    private Location holoLocation;

    public HologramManager() {
        this.holograms = new HashMap<>();
        this.config = PlayerSkills.getFileManager().getConfig("config");
        updateLocation();
    }

    public void updateLocation() {
        try {
            this.holoLocation = LocationUtil.toLocation(Objects.<String>requireNonNull(this.config.get().getString("holograms.location")));
        } catch (Exception e) {
            Bukkit.getLogger().info("[PlayerSkills] Hologram locations is not set");
        }
    }

    public void show(Player player) {
        if (this.holoLocation != null) {
            Hologram hologram = HolographicDisplaysAPI.get(PlayerSkills.getInstance()).createHologram(holoLocation);
            hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
            hologram.getVisibilitySettings().isVisibleTo(player);
            this.holograms.put(player.getUniqueId(), hologram);
            update(player);
        }
    }

    public void remove(Player player) {
        if (this.holograms.containsKey(player.getUniqueId())) {
            ((Hologram) this.holograms.get(player.getUniqueId())).getVisibilitySettings().clearIndividualVisibilities();
            this.holograms.remove(player.getUniqueId());
        }
    }

    public void clearUpdate(Player player) {
        remove(player);
        updateLocation();
        show(player);
    }

    public void update(Player player) {
        if (this.holograms.containsKey(player.getUniqueId())) {
            Hologram hologram = this.holograms.get(player.getUniqueId());
            hologram.getLines().clear();
            for (String line : this.config.get().getStringList("holograms.lines")) {
                String linePlaceholder = line
                        .replace("%player_name%", player.getName())
                        .replace("&", "§")
                        .replace("%strength_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.STRENGTH) + "")
                        .replace("%criticals_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.CRITICALS) + "")
                        .replace("%archery_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.ARCHERY) + "")
                        .replace("%health_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.HEALTH) + "")
                        .replace("%resistance_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.RESISTANCE) + "");
                hologram.getLines().appendText(ChatColor.translateAlternateColorCodes('&', linePlaceholder));
            }
        }
    }
}