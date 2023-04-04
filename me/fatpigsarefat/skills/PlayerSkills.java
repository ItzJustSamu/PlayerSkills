/*     */ package me.fatpigsarefat.skills;
/*     */ import java.util.HashMap;
/*     */ import me.fatpigsarefat.skills.commands.SkillsAdminCommand;
/*     */ import me.fatpigsarefat.skills.commands.SkillsCommand;
import me.fatpigsarefat.skills.listeners.*;
/*     */
/*     */
/*     */
/*     */ import me.fatpigsarefat.skills.managers.FileManager;
/*     */ import me.fatpigsarefat.skills.managers.HologramManager;
/*     */ import me.fatpigsarefat.skills.managers.SkillManager;
/*     */ import me.fatpigsarefat.skills.trait.SkillsTrait;
/*     */ import me.fatpigsarefat.skills.utils.Skill;
/*     */ import me.fatpigsarefat.skills.utils.UpdateChecker;
/*     */ import net.citizensnpcs.api.CitizensAPI;
/*     */ import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.potion.PotionEffect;
/*     */ import org.bukkit.potion.PotionEffectType;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class PlayerSkills extends JavaPlugin {
/*     */   private boolean availableUpdate = false;
/*     */   public static boolean useHolograms = false;
/*     */   public static PlayerSkills instance;
/*     */   
/*     */   public static PlayerSkills getInstance() {
/*  32 */     return instance;
/*     */   } public static FileManager fileManager; public static SkillManager skillManager; public static HologramManager hologramManager; public static FileManager getFileManager() {
/*  34 */     return fileManager;
/*     */   } public static SkillManager getSkillManager() {
/*  36 */     return skillManager;
/*     */   } public static HologramManager getHologramManager() {
/*  38 */     return hologramManager;
/*     */   } public static HashMap<Skill, Integer> getSkillMultipliers() {
/*  40 */     return skillMultipliers;
/*  41 */   } public static HashMap<Skill, Integer> skillMultipliers = new HashMap<>(); public static HashMap<Player, PotionEffect> getPotionEffect() {
/*  42 */     return potionEffect;
/*  43 */   } public static HashMap<Player, PotionEffect> potionEffect = new HashMap<>();
/*     */   
/*     */   public static boolean allowReset = true;
/*     */   
/*     */   public void onEnable() {
/*  48 */     instance = this;
/*  49 */     fileManager = new FileManager(this);
/*  50 */     fileManager.AddConfig("config");
/*  51 */     fileManager.AddConfig("messages");
/*  52 */     fileManager.AddConfig("gui");
/*  53 */     fileManager.AddConfig("data");
/*  54 */     skillManager = new SkillManager();
/*  55 */     if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
/*     */       try {
/*  57 */         CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(SkillsTrait.class).withName("playerskills"));
/*  58 */         getLogger().info("Successfully hooked into Citizens.");
/*     */       }
/*  60 */       catch (NullPointerException|NoClassDefFoundError ex) {
/*  61 */         getLogger().info("An error occured when trying to register a trait. Your Citizens version might not be supported.");
/*     */       } 
/*     */     } else {
/*     */       
/*  65 */       getLogger().info("Citizens not found. NPCs will not be available.");
/*     */     } 
/*     */     
/*  68 */     if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
/*     */       try {
/*  70 */         hologramManager = new HologramManager();
/*  71 */         useHolograms = fileManager.getConfig("config").get().getBoolean("holograms.use");
/*  72 */         getLogger().info("Successfully hooked into HolographicDisplays.");
/*     */       }
/*  74 */       catch (NullPointerException|NoClassDefFoundError ex) {
/*  75 */         getLogger().info("An error occured when trying to register. Your HolographicDisplays version might not be supported.");
/*     */       } 
/*     */     } else {
/*     */       
/*  79 */       getLogger().info("HolographicDisplays not found. Holograms will not be available.");
/*     */     } 
/*     */     
/*  82 */     Bukkit.getPluginManager().registerEvents((Listener)new InventoryClick(), (Plugin)this);
/*  83 */     Bukkit.getPluginManager().registerEvents((Listener)new EntityDamageByEntity(), (Plugin)this);
/*  84 */     Bukkit.getPluginManager().registerEvents((Listener)new EntityDamage(), (Plugin)this);
/*  85 */     Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
/*  86 */     Bukkit.getPluginManager().registerEvents((Listener)new UpgradeSkill(), (Plugin)this);
/*  87 */     Bukkit.getPluginManager().registerEvents((Listener)new ResetSkill(), (Plugin)this);
/*  88 */     Bukkit.getPluginCommand("skills").setExecutor((CommandExecutor)new SkillsCommand());
/*  89 */     Bukkit.getPluginCommand("skillsadmin").setExecutor((CommandExecutor)new SkillsAdminCommand());
/*  90 */     checkIfHealth();
/*  91 */     for (String s : fileManager.getConfig("config").get().getConfigurationSection("skills").getKeys(false)) {
/*  92 */       skillMultipliers.put(Skill.getSkillByName(s), Integer.valueOf(getConfig().getInt("skills." + s + ".increment")));
/*     */     }
/*  94 */     allowReset = fileManager.getConfig("gui").get().getBoolean("gui.reset-enabled");
/*  95 */     checkUpdates((Player)null);
/*     */   }
/*     */   
/*     */   public void checkIfHealth() {
/*  99 */     BukkitScheduler healthCheck = getServer().getScheduler();
/* 100 */     healthCheck.scheduleSyncRepeatingTask((Plugin)this, new Runnable() {
/*     */           public void run() {
/* 102 */             for (Player player : Bukkit.getOnlinePlayers()) {
/* 103 */               int level = PlayerSkills.getSkillManager().getSkillLevel(player, Skill.HEALTH) - 1;
/* 104 */               int additionalEffects = 0;
/* 105 */               PotionEffect hb = null;
/* 106 */               for (PotionEffect effect : player.getActivePotionEffects()) {
/* 107 */                 if (effect.getType().toString().equals("PotionEffectType[21, HEALTH_BOOST]")) {
/* 108 */                   hb = effect;
/* 109 */                   additionalEffects += 4 * (effect.getAmplifier() + 1);
/*     */                 }
/*     */               }
/* 112 */               if (PlayerSkills.fileManager.getConfig("config").get().getBoolean("worlds.restricted") &&
/* 113 */                 !PlayerSkills.fileManager.getConfig("config").get().getStringList("worlds.allowed-worlds").contains(player.getLocation().getWorld().getName())) {
/* 114 */                 if (player.getMaxHealth() != (20 + additionalEffects)) {
/* 115 */                   player.setMaxHealth(20.0D);
/* 116 */                   player.sendMessage("max health set");
/*     */                 }
/*     */                 continue;
/*     */               }
/* 120 */               if (player.getMaxHealth() != (20 + level + additionalEffects)) {
/* 121 */                 player.setMaxHealth((20 + level + additionalEffects));
/* 122 */                 if (player.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
/* 123 */                   PlayerSkills.potionEffect.put(player, hb);
/* 124 */                   player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
/*     */                   continue;
/*     */                 }
/* 127 */                 if (PlayerSkills.potionEffect.containsKey(player)) {
/* 128 */                   player.addPotionEffect(PlayerSkills.potionEffect.get(player));
/* 129 */                   PlayerSkills.potionEffect.remove(player);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }10L, 10L);
/*     */   }
/*     */   
/*     */   public void checkUpdates(Player player) {
/* 138 */     if (fileManager.getConfig("config").get().getBoolean("check-update"))
/* 139 */       (new UpdateChecker(this, 59383)).getVersion(version -> {
/*     */             this.availableUpdate = !getDescription().getVersion().equalsIgnoreCase(version);
/*     */             String string = "[PlayerSkillsReborn] " + (this.availableUpdate ? ("Found a new available version! " + ChatColor.DARK_GREEN + "Download at bit.ly/3oIG0RR") : "Looks like you have the latest version installed!");
/*     */             if (player != null) {
/*     */               if (player.hasPermission("admin"))
/*     */                 player.sendMessage(string); 
/*     */             } else {
/*     */               Bukkit.getConsoleSender().sendMessage(string);
/*     */             } 
/*     */           }); 
/*     */   }
/*     */ }
