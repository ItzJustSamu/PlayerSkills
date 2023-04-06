package me.fatpigsarefat.skills;

import me.fatpigsarefat.skills.commands.SkillsAdminCommand;
import me.fatpigsarefat.skills.commands.SkillsCommand;
import me.fatpigsarefat.skills.listeners.*;
import me.fatpigsarefat.skills.managers.FileManager;
import me.fatpigsarefat.skills.managers.HologramManager;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.trait.SkillsTrait;
import me.fatpigsarefat.skills.utils.Skill;
import me.fatpigsarefat.skills.utils.UpdateChecker;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

public class PlayerSkills extends JavaPlugin {
    private boolean availableUpdate = false;
    public static boolean useHolograms = false;
    public static PlayerSkills instance;
    public static FileManager fileManager;
    public static SkillManager skillManager;
    public static HologramManager hologramManager;
    public static HashMap<Skill, Integer> skillMultipliers = new HashMap<>();
    public static HashMap<Player, PotionEffect> potionEffect = new HashMap<>();
    public static boolean allowReset = true;

    public PlayerSkills() {
    }

    public void onEnable() {
        instance = this;
        fileManager = new FileManager(this);
        fileManager.AddConfig("config");
        fileManager.AddConfig("messages");
        fileManager.AddConfig("gui");
        fileManager.AddConfig("data");
        skillManager = new SkillManager();
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            try {
                CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(SkillsTrait.class).withName("playerskills"));
                this.getLogger().info("Successfully hooked into Citizens.");
            } catch (NoClassDefFoundError | NullPointerException var4) {
                this.getLogger().info("An error occured when trying to register a trait. Your Citizens version might not be supported.");
            }
        } else {
            this.getLogger().info("Citizens not found. NPCs will not be available.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            try {
                hologramManager = new HologramManager();
                useHolograms = fileManager.getConfig("config").get().getBoolean("holograms.use");
                this.getLogger().info("Successfully hooked into HolographicDisplays.");
            } catch (NoClassDefFoundError | NullPointerException var3) {
                this.getLogger().info("An error occured when trying to register. Your HolographicDisplays version might not be supported.");
            }
        } else {
            this.getLogger().info("HolographicDisplays not found. Holograms will not be available.");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntity(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeSkill(), this);
        Bukkit.getPluginManager().registerEvents(new ResetSkill(), this);
        Bukkit.getPluginCommand("skills").setExecutor(new SkillsCommand());
        Bukkit.getPluginCommand("skillsadmin").setExecutor(new SkillsAdminCommand());
        this.checkIfHealth();

        for (String s : fileManager.getConfig("config").get().getConfigurationSection("skills").getKeys(false)) {
            skillMultipliers.put(Skill.getSkillByName(s), this.getConfig().getInt("skills." + s + ".increment"));
        }

        allowReset = fileManager.getConfig("gui").get().getBoolean("gui.reset-enabled");
        this.checkUpdates(null);
    }

    public void checkIfHealth() {
        BukkitScheduler healthCheck = this.getServer().getScheduler();
        healthCheck.scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getHealth() > player.getMaxHealth()) {
                    player.setHealth(player.getMaxHealth());
                }
            }
        }, 10L, 10L);
    }

    public void checkUpdates(Player player) {
        if (fileManager.getConfig("config").get().getBoolean("check-update")) {
            (new UpdateChecker(this, 59383)).getVersion((version) -> {
                this.availableUpdate = !this.getDescription().getVersion().equalsIgnoreCase(version);
                String string = "[PlayerSkillsReborn] " + (this.availableUpdate ? "Found a new available version! " + ChatColor.RED + "Download at bit.ly/3oIG0RR" : "Looks like you have the latest version installed!");
                if (player != null) {
                    if (player.hasPermission("admin")) {
                        player.sendMessage(string);
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage(string);
                }

            });
        }

    }

    public static PlayerSkills getInstance() {
        return instance;
    }

    public static FileManager getFileManager() {
        return fileManager;
    }

    public static SkillManager getSkillManager() {
        return skillManager;
    }

    public static HologramManager getHologramManager() {
        return hologramManager;
    }

    public static HashMap<Skill, Integer> getSkillMultipliers() {
        return skillMultipliers;
    }

    public static HashMap<Player, PotionEffect> getPotionEffect() {
        return potionEffect;
    }
}
