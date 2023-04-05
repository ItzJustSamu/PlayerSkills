package me.fatpigsarefat.skills.managers;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.utils.LocationUtil;
import me.fatpigsarefat.skills.utils.Skill;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class HologramManager {
    private HashMap<UUID, Hologram> holograms = new HashMap();
    private FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");
    private Location holoLocation;

    public HologramManager() {
        this.updateLocation();
    }

    public void updateLocation() {
        try {
            this.holoLocation = LocationUtil.toLocation((String)Objects.requireNonNull(this.config.get().getString("holograms.location")));
        } catch (Exception var2) {
            Bukkit.getLogger().info("[PlayerSkillsReborn] Hologram locations is not set");
        }

    }

    public void show(Player player) {
        if (this.holoLocation != null) {
            Hologram hologram = (Hologram) HolographicDisplaysAPI.get(PlayerSkills.getInstance());
            hologram.getVisibilitySettings().isVisibleTo(player);
            this.holograms.put(player.getUniqueId(), hologram);
            this.update(player);
        }

    }

    public void remove(Player player) {
        if (this.holograms.containsKey(player.getUniqueId())) {
            ((Hologram)this.holograms.get(player.getUniqueId())).getVisibilitySettings().isVisibleTo(player);
            this.holograms.remove(player.getUniqueId());
        }

    }

    public void clearUpdate(Player player) {
        this.remove(player);
        this.updateLocation();
        this.show(player);
    }

    public void update(Player player) {
        if (this.holograms.containsKey(player.getUniqueId())) {
            Hologram hologram = (Hologram)this.holograms.get(player.getUniqueId());
            hologram.getLines();
            Iterator var3 = this.config.get().getStringList("holograms.lines").iterator();

            while(var3.hasNext()) {
                String line = (String)var3.next();
                String linePlaceholder = line.replace("%player_name%", player.getName()).replace("&", "§").replace("%strength_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.STRENGTH) + "").replace("%criticals_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.CRITICALS) + "").replace("%archery_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.ARCHERY) + "").replace("%health_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.HEALTH) + "").replace("%resistance_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.RESISTANCE) + "");
                hologram.getLines();
            }
        }

    }
}
