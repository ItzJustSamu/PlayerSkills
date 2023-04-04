/*     */ package me.fatpigsarefat.skills.managers;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Objects;
/*     */ import me.fatpigsarefat.skills.PlayerSkills;
/*     */ import me.fatpigsarefat.skills.utils.Skill;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.meta.ItemMeta;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigurationManager
/*     */ {
/*  17 */   private FileManager.Config gui = PlayerSkills.getFileManager().getConfig("gui");
/*  18 */   private FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");
/*     */ 
/*     */   
/*     */   public ItemStack getItemStack(String s, Player player) {
/*  22 */     String pathRoot = "gui.display." + s + ".";
/*  23 */     String name = ChatColor.translateAlternateColorCodes('&', Objects.<String>requireNonNull(this.gui.get().getString(pathRoot + "name")));
/*  24 */     name = placeholder(name, player);
/*  25 */     ArrayList<String> lore = new ArrayList<>();
/*  26 */     if (this.gui.get().contains(pathRoot + "lore")) {
/*  27 */       for (String key : this.gui.get().getStringList(pathRoot + "lore")) {
/*  28 */         key = ChatColor.translateAlternateColorCodes('&', key);
/*  29 */         key = placeholder(key, player);
/*  30 */         lore.add(key);
/*     */       } 
/*     */     }
/*  33 */     ItemStack itemToGive = new ItemStack(Objects.<Material>requireNonNull(Material.getMaterial(Objects.<String>requireNonNull(this.gui.get().getString(pathRoot + "material")))));
/*  34 */     ItemMeta itemToGiveMeta = itemToGive.getItemMeta();
/*  35 */     itemToGiveMeta.setDisplayName(name);
/*  36 */     itemToGiveMeta.setLore(lore);
/*  37 */     itemToGive.setItemMeta(itemToGiveMeta);
/*  38 */     return itemToGive;
/*     */   }
/*     */   
/*     */   private String placeholder(String s, Player player) {
/*  42 */     SkillManager sm = PlayerSkills.getSkillManager();
/*  43 */     int strength = 1;
/*  44 */     int criticals = 1;
/*  45 */     int resistance = 1;
/*  46 */     int archery = 1;
/*  47 */     int health = 1;
/*  48 */     strength = sm.getSkillLevel(player, Skill.STRENGTH);
/*  49 */     criticals = sm.getSkillLevel(player, Skill.CRITICALS);
/*  50 */     resistance = sm.getSkillLevel(player, Skill.RESISTANCE);
/*  51 */     archery = sm.getSkillLevel(player, Skill.ARCHERY);
/*  52 */     health = sm.getSkillLevel(player, Skill.HEALTH);
/*  53 */     if (s.contains("strength")) {
/*  54 */       s = s.replace("%strength%", "[" + strength + "/" + sm.getMaximumLevel(Skill.STRENGTH) + "]");
/*  55 */       s = s.replace("%strengthincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH)).toString());
/*  56 */       int strengthSkillBefore = (sm.getSkillLevel(player, Skill.STRENGTH) - 1) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH)).intValue() + 100;
/*  57 */       int strengthSkillAfter = sm.getSkillLevel(player, Skill.STRENGTH) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH)).intValue() + 100;
/*  58 */       if (sm.getSkillLevel(player, Skill.STRENGTH) >= sm.getMaximumLevel(Skill.STRENGTH)) {
/*  59 */         strengthSkillAfter = strengthSkillBefore;
/*     */       }
/*  61 */       s = s.replace("%strengthprogress%", ChatColor.GREEN.toString() + strengthSkillBefore + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + strengthSkillAfter + "%");
/*     */     } 
/*  63 */     if (s.contains("criticals")) {
/*  64 */       s = s.replace("%criticals%", "[" + criticals + "/" + sm.getMaximumLevel(Skill.CRITICALS) + "]");
/*  65 */       s = s.replace("%criticalsincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS)).toString());
/*  66 */       int criticalsSkillBefore = (sm.getSkillLevel(player, Skill.CRITICALS) - 1) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS)).intValue() + 150;
/*  67 */       int criticalsSkillAfter = sm.getSkillLevel(player, Skill.CRITICALS) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS)).intValue() + 150;
/*  68 */       if (sm.getSkillLevel(player, Skill.CRITICALS) >= sm.getMaximumLevel(Skill.CRITICALS)) {
/*  69 */         criticalsSkillAfter = criticalsSkillBefore;
/*     */       }
/*  71 */       s = s.replace("%criticalsprogress%", ChatColor.GREEN.toString() + criticalsSkillBefore + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + criticalsSkillAfter + "%");
/*     */     } 
/*  73 */     if (s.contains("resistance")) {
/*  74 */       s = s.replace("%resistance%", "[" + resistance + "/" + sm.getMaximumLevel(Skill.RESISTANCE) + "]");
/*  75 */       s = s.replace("%resistanceincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE)).toString());
/*  76 */       int resistanceSkillBefore = (sm.getSkillLevel(player, Skill.RESISTANCE) - 1) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE)).intValue();
/*  77 */       int resistanceSkillAfter = sm.getSkillLevel(player, Skill.RESISTANCE) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE)).intValue();
/*  78 */       if (sm.getSkillLevel(player, Skill.RESISTANCE) >= sm.getMaximumLevel(Skill.RESISTANCE)) {
/*  79 */         resistanceSkillAfter = resistanceSkillBefore;
/*     */       }
/*  81 */       s = s.replace("%resistanceprogress%", ChatColor.GREEN.toString() + resistanceSkillBefore + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + resistanceSkillAfter + "%");
/*     */     } 
/*  83 */     if (s.contains("archery")) {
/*  84 */       s = s.replace("%archery%", "[" + archery + "/" + sm.getMaximumLevel(Skill.ARCHERY) + "]");
/*  85 */       s = s.replace("%archeryincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY)).toString());
/*  86 */       int archerySkillBefore = (sm.getSkillLevel(player, Skill.ARCHERY) - 1) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY)).intValue() + 100;
/*  87 */       int archerySkillAfter = sm.getSkillLevel(player, Skill.ARCHERY) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY)).intValue() + 100;
/*  88 */       if (sm.getSkillLevel(player, Skill.ARCHERY) >= sm.getMaximumLevel(Skill.ARCHERY)) {
/*  89 */         archerySkillAfter = archerySkillBefore;
/*     */       }
/*  91 */       s = s.replace("%archeryprogress%", ChatColor.GREEN.toString() + archerySkillBefore + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + archerySkillAfter + "%");
/*     */     } 
/*  93 */     if (s.contains("health")) {
/*  94 */       s = s.replace("%health%", "[" + health + "/" + sm.getMaximumLevel(Skill.HEALTH) + "]");
/*  95 */       s = s.replace("%healthincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.HEALTH)).toString());
/*  96 */       int healthSkillBefore = (sm.getSkillLevel(player, Skill.HEALTH) - 1) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.HEALTH)).intValue() + 20;
/*  97 */       int healthSkillAfter = sm.getSkillLevel(player, Skill.HEALTH) * ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.HEALTH)).intValue() + 20;
/*  98 */       if (sm.getSkillLevel(player, Skill.HEALTH) >= sm.getMaximumLevel(Skill.HEALTH)) {
/*  99 */         healthSkillAfter = healthSkillBefore;
/*     */       }
/* 101 */       s = s.replace("%healthprogress%", ChatColor.GREEN.toString() + healthSkillBefore + "HP " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "HP");
/*     */     } 
/* 103 */     int xpPrice = 1;
/* 104 */     xpPrice = this.config.get().getInt("xp.price");
/* 105 */     if (this.config.get().getBoolean("xp.add-total-to-price")) {
/* 106 */       xpPrice += sm.getTotalPointsSpent(player) * this.config.get().getInt("xp.add-total-to-price-multiplier");
/*     */     }
/* 108 */     s = s.replace("%pointsprice%", xpPrice + "XP");
/* 109 */     s = s.replace("%points%", sm.getSkillPoints(player) + "");
/* 110 */     s = s.replace("%username%", player.getName());
/* 111 */     s = s.replace("%expierencelevel%", player.getLevel() + "");
/* 112 */     if (s.contains("expierence")) {
/* 113 */       String expierenceBar = ""; double f;
/* 114 */       for (f = 0.0D; f <= player.getExp(); f += 0.03D) {
/* 115 */         expierenceBar = expierenceBar + ChatColor.GREEN + "|";
/*     */       }
/* 117 */       for (int toAdd = 30 - ChatColor.stripColor(expierenceBar).length(), i = 0; i <= toAdd; i++) {
/* 118 */         expierenceBar = expierenceBar + ChatColor.GRAY + "|";
/*     */       }
/* 120 */       s = s.replace("%expierencebar%", expierenceBar);
/*     */     } 
/* 122 */     s = s.replace("%totalspent%", sm.getTotalPointsSpent(player) + "");
/* 123 */     s = s.replace("%strength-points-spend%", (strength - 1) + "");
/* 124 */     s = s.replace("%criticals-points-spend%", (criticals - 1) + "");
/* 125 */     s = s.replace("%resistance-points-spend%", (resistance - 1) + "");
/* 126 */     s = s.replace("%archery-points-spend%", (archery - 1) + "");
/* 127 */     s = s.replace("%health-points-spend%", (health - 1) + "");
/* 128 */     return s;
/*     */   }
/*     */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\managers\ConfigurationManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */