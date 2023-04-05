package me.fatpigsarefat.skills;

import me.fatpigsarefat.skills.commands.SkillsAdminCommand;
import me.fatpigsarefat.skills.commands.SkillsCommand;
import me.fatpigsarefat.skills.listeners.EntityDamage;
import me.fatpigsarefat.skills.listeners.EntityDamageByEntity;
import me.fatpigsarefat.skills.listeners.InventoryClick;
import me.fatpigsarefat.skills.listeners.PlayerListener;
import me.fatpigsarefat.skills.listeners.ResetSkill;
import me.fatpigsarefat.skills.listeners.UpgradeSkill;
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
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

    public static boolean allowReset = true;

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
                getLogger().info("Successfully hooked into Citizens.");
            } catch (NullPointerException | NoClassDefFoundError ex) {
                getLogger().info("An error occured when trying to register a trait. Your Citizens version might not be supported.");
            }
        } else {
            getLogger().info("Citizens not found. NPCs will not be available.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            try {
                hologramManager = new HologramManager();
                useHolograms = fileManager.getConfig("config").get().getBoolean("holograms.use");
                /*  72 */
                getLogger().info("Successfully hooked into HolographicDisplays.");
                /*     */
            }
            /*  74 */ catch (NullPointerException | NoClassDefFoundError ex) {
                /*  75 */
                getLogger().info("An error occured when trying to register. Your HolographicDisplays version might not be supported.");
                /*     */
            }
            /*     */
        } else {
            /*     */
            /*  79 */
            getLogger().info("HolographicDisplays not found. Holograms will not be available.");
            /*     */
        }
        /*     */
        /*  82 */
        Bukkit.getPluginManager().registerEvents((Listener) new InventoryClick(), (Plugin) this);
        /*  83 */
        Bukkit.getPluginManager().registerEvents((Listener) new EntityDamageByEntity(), (Plugin) this);
        /*  84 */
        Bukkit.getPluginManager().registerEvents((Listener) new EntityDamage(), (Plugin) this);
        /*  85 */
        Bukkit.getPluginManager().registerEvents((Listener) new PlayerListener(), (Plugin) this);
        /*  86 */
        Bukkit.getPluginManager().registerEvents((Listener) new UpgradeSkill(), (Plugin) this);
        /*  87 */
        Bukkit.getPluginManager().registerEvents((Listener) new ResetSkill(), (Plugin) this);
        /*  88 */
        Bukkit.getPluginCommand("me/fatpigsarefat/skills").setExecutor((CommandExecutor) new SkillsCommand());
        /*  89 */
        Bukkit.getPluginCommand("skillsadmin").setExecutor((CommandExecutor) new SkillsAdminCommand());
        /*  90 */
        checkIfHealth();
        /*  91 */
        for (String s : fileManager.getConfig("config").get().getConfigurationSection("me/fatpigsarefat/skills").getKeys(false)) {
            /*  92 */
            skillMultipliers.put(Skill.getSkillByName(s), Integer.valueOf(getConfig().getInt("skills." + s + ".increment")));
            /*     */
        }
        /*  94 */
        allowReset = fileManager.getConfig("gui").get().getBoolean("gui.reset-enabled");
        /*  95 */
        checkUpdates((Player) null);
        /*     */
    }

    /*     */
    /*     */
    public void checkIfHealth() {
        BukkitScheduler healthCheck = getServer().getScheduler();
        healthCheck.scheduleSyncRepeatingTask((Plugin) this, new Runnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int level = PlayerSkills.getSkillManager().getSkillLevel(player, Skill.HEALTH) - 1;
                    int additionalEffects = 0;
                    PotionEffect hb = null;
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        if (effect.getType().toString().equals("PotionEffectType[21, HEALTH_BOOST]")) {
                            hb = effect;
                            additionalEffects += 4 * (effect.getAmplifier() + 1);
                        }
                    }
                    if (PlayerSkills.fileManager.getConfig("config").get().getBoolean("worlds.restricted")
                            && !PlayerSkills.fileManager.getConfig("config").get().getStringList("worlds.allowed-worlds").contains(player.getLocation().getWorld().getName())) {
                        if (player.getMaxHealth() != (20 + additionalEffects)) {
                            player.setMaxHealth(20.0D);
                            player.sendMessage("max health set");
                        }
                        continue;
                    }
                    if (player.getMaxHealth() != (20 + level + additionalEffects)) {
                        player.setMaxHealth((20 + level + additionalEffects));
                        if (player.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
                            PlayerSkills.potionEffect.put(player, hb);
                            player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
                            continue;
                        }
                        if (PlayerSkills.potionEffect.containsKey(player)) {
                            player.addPotionEffect(PlayerSkills.potionEffect.get(player));
                            PlayerSkills.potionEffect.remove(player);
                        }
                    }
                }
            }
        }, 10L, 10L);
    }

    public void checkUpdates(Player player) {
        if (fileManager.getConfig("config").get().getBoolean("check-update")) {
            (new UpdateChecker(this, 59383)).getVersion(version -> {
                boolean availableUpdate = !getDescription().getVersion().equalsIgnoreCase(version);
                sendMessage(player, availableUpdate);
            });
        }
    }

    private void sendMessage(Player player, boolean availableUpdate) {
        String message = "[PlayerSkillsReborn] " + (availableUpdate
                ? ("Found a new available version! " + ChatColor.DARK_GREEN + "Download at bit.ly/3oIG0RR")
                : "Looks like you have the latest version installed!");
        if (player != null && player.hasPermission("admin")) {
            player.sendMessage(message);
        } else {
            Bukkit.getConsoleSender().sendMessage(message);
        }
    }
}