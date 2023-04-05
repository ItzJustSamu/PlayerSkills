package me.fatpigsarefat.skills.managers;


import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.utils.LocationUtil;
import me.fatpigsarefat.skills.utils.Skill;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
/*    */
/*    */ public class HologramManager
        /*    */ {
    /*    */   private HashMap<UUID, Hologram> holograms;
    /*    */   private FileManager.Config config;
    /*    */   private Location holoLocation;
    /*    */
    /*    */   public HologramManager() {
        /* 23 */     this.holograms = new HashMap<>();
        /* 24 */     this.config = PlayerSkills.getFileManager().getConfig("config");
        /* 25 */     updateLocation();
        /*    */   }
    /*    */
    /*    */   public void updateLocation() {
        /*    */     try {
            /* 30 */       this.holoLocation = LocationUtil.toLocation(Objects.<String>requireNonNull(this.config.get().getString("holograms.location")));
            /* 31 */     } catch (Exception e) {
            /* 32 */       Bukkit.getLogger().info("[PlayerSkillsReborn] Hologram locations is not set");
            /*    */     }
        /*    */   }
    /*    */
    /*    */   public void show(Player player) {
        /* 37 */     if (this.holoLocation != null) {
            /* 38 */       Hologram hologram = (Hologram) HolographicDisplaysAPI.get((Plugin)PlayerSkills.getInstance());
            /* 39 */       hologram.getVisibilitySettings().isVisibleTo(player);
            /* 40 */       hologram.getVisibilitySettings().isVisibleTo(player);
            /*    */
            /* 42 */       this.holograms.put(player.getUniqueId(), hologram);
            /* 43 */       update(player);
            /*    */     }
        /*    */   }
    /*    */
    /*    */   public void remove(Player player) {
        /* 48 */     if (this.holograms.containsKey(player.getUniqueId())) {
            /* 49 */       ((Hologram)this.holograms.get(player.getUniqueId())).getVisibilitySettings().isVisibleTo(player);
            /* 50 */       this.holograms.remove(player.getUniqueId());
            /*    */     }
        /*    */   }
    /*    */   public void clearUpdate(Player player) {
        /* 54 */     remove(player);
        /* 55 */     updateLocation();
        /* 56 */     show(player);
        /*    */   }
    /*    */
    /*    */   public void update(Player player) {
        /* 60 */     if (this.holograms.containsKey(player.getUniqueId())) {
            /* 61 */       Hologram hologram = this.holograms.get(player.getUniqueId());
            /* 62 */       hologram.getLines();
            /* 63 */       for (String line : this.config.get().getStringList("holograms.lines")) {
                /*    */
                /*    */
                /*    */
                /*    */
                /*    */
                /*    */
                /*    */
                /* 71 */         String linePlaceholder = line.replace("%player_name%", player.getName()).replace("&", "§").replace("%strength_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.STRENGTH) + "").replace("%criticals_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.CRITICALS) + "").replace("%archery_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.ARCHERY) + "").replace("%health_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.HEALTH) + "").replace("%resistance_lvl%", PlayerSkills.getSkillManager().getSkillLevel(player, Skill.RESISTANCE) + "");
                /* 72 */         hologram.getLines();
                /*    */       }
            /*    */     }
        /*    */   }
    /*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\managers\HologramManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */