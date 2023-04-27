package me.fatpigsarefat.skills;

import me.fatpigsarefat.skills.commands.SkillsAdminCommand;
import me.fatpigsarefat.skills.commands.SkillsCommand;
import me.fatpigsarefat.skills.expansion.PlayerSkillsExpansion;
import me.fatpigsarefat.skills.listeners.*;
import me.fatpigsarefat.skills.managers.FileManager;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
import me.fatpigsarefat.skills.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class PlayerSkills extends JavaPlugin {
    private boolean availableUpdate = false;
    public static PlayerSkills instance;
    public static FileManager fileManager;
    public static SkillManager skillManager;
    public static HashMap<Skill, Integer> skillMultipliers = new HashMap<>();
    public static HashMap<Player, PotionEffect> potionEffect = new HashMap<>();
    public static boolean allowReset = true;

    public PlayerSkills() {
    }

    public void onEnable() {
        // Load configs
        instance = this;
        fileManager = new FileManager(this);
        fileManager.AddConfig("config");
        fileManager.AddConfig("messages");
        fileManager.AddConfig("gui");
        fileManager.AddConfig("data");

        // Connect to MySQL
        if (fileManager.getConfig("config").get().getBoolean("mysql-enabled")) {
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://" + fileManager.getConfig("config").get().getString("mysql-host") + ":" + fileManager.getConfig("config").get().getInt("mysql-port") + "/" + fileManager.getConfig("config").get().getString("mysql-database"),
                        fileManager.getConfig("config").get().getString("mysql-username"),
                        fileManager.getConfig("config").get().getString("mysql-password")
                );
                getLogger().info("Mysql doesn't work yet!");
                // TODO: Implement database operations
            } catch (SQLException e) {
                getLogger().severe("Failed to connect to MySQL database: " + e.getMessage());
                getLogger().severe("Please set 'mysql-enabled' to 'false' or change MySQL details in the config.yml file.");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        new PlayerSkillsExpansion().register();

        // Set up managers
        skillManager = new SkillManager();
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            try {
                getLogger().info("Successfully hooked into HolographicDisplays.");
            } catch (NoClassDefFoundError | NullPointerException var3) {
                getLogger().info("An error occurred when trying to register. Your HolographicDisplays version might not be supported.");
            }
        } else {
            getLogger().info("HolographicDisplays not found. Holograms will not be available.");
        }

        // Register listeners and commands
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntity(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeSkill(), this);
        Bukkit.getPluginManager().registerEvents(new ResetSkill(), this);
        Bukkit.getPluginCommand("skills").setExecutor(new SkillsCommand());
        Bukkit.getPluginCommand("skillsadmin").setExecutor(new SkillsAdminCommand());

        // Load skill multipliers
        for (String skillName : fileManager.getConfig("config").get().getConfigurationSection("skills").getKeys(false)) {
            skillMultipliers.put(Skill.getSkillByName(skillName), fileManager.getConfig("config").get().getInt("skills." + skillName + ".increment"));
        }

        // Set reset state
        allowReset = fileManager.getConfig("gui").get().getBoolean("gui.reset-enabled");

        // Start health check
        checkIfHealth();

        // Check for updates
        checkUpdates(null);
    }

    public void checkIfHealth() {
        BukkitScheduler healthCheck = this.getServer().getScheduler();
        healthCheck.scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getHealth() > player.getHealth()) {
                    player.setHealth(player.getHealth());
                }
            }
        }, 10L, 10L);
    }

    public void checkUpdates(Player player) {
        if (fileManager.getConfig("config").get().getBoolean("check-update")) {
            (new UpdateChecker(this, 109080)).getVersion((version) -> {
                this.availableUpdate = !this.getDescription().getVersion().equalsIgnoreCase(version);
                String string = "[PlayerSkills] " + (this.availableUpdate ? "Found a new available version! " + ChatColor.RED + "Download at https://www.spigotmc.org/resources/▶-playerskills-◀-upgrade-skills-citizens-support-holograms-suport-orbs-sourcecode.109080/" : "Looks like you have the latest version installed!");
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


    public static HashMap<Skill, Integer> getSkillMultipliers() {
        return skillMultipliers;
    }

    public static HashMap<Player, PotionEffect> getPotionEffect() {
        return potionEffect;
    }
}
