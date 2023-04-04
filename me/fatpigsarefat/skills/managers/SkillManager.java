/*     */ package me.fatpigsarefat.skills.managers;
/*     */ 
/*     */ import me.fatpigsarefat.skills.PlayerSkills;
/*     */ import me.fatpigsarefat.skills.utils.Skill;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SkillManager
/*     */ {
/*  15 */   private FileManager.Config data = PlayerSkills.getFileManager().getConfig("data");
/*  16 */   private FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");
/*     */ 
/*     */   
/*     */   public int getSkillLevel(Player player, Skill skill) {
/*  20 */     if (this.data.get().contains(player.getUniqueId() + "." + skill.toString())) {
/*  21 */       return this.data.get().getInt(player.getUniqueId() + "." + skill.toString());
/*     */     }
/*  23 */     return 1;
/*     */   }
/*     */   
/*     */   public int getSkillPoints(Player player) {
/*  27 */     if (this.data.get().contains(player.getUniqueId() + ".points")) {
/*  28 */       return this.data.get().getInt(player.getUniqueId() + ".points");
/*     */     }
/*  30 */     return 0;
/*     */   }
/*     */   
/*     */   public int getTotalPointsSpent(Player player) {
/*  34 */     int points = getSkillPoints(player);
/*  35 */     int damage = getSkillLevel(player, Skill.STRENGTH) - 1;
/*  36 */     int criticals = getSkillLevel(player, Skill.CRITICALS) - 1;
/*  37 */     int resistance = getSkillLevel(player, Skill.RESISTANCE) - 1;
/*  38 */     int archery = getSkillLevel(player, Skill.ARCHERY) - 1;
/*  39 */     int health = getSkillLevel(player, Skill.HEALTH) - 1;
/*  40 */     int total = points + damage + criticals + resistance + archery + health;
/*  41 */     return total;
/*     */   }
/*     */   
/*     */   public void setSkillPoints(Player player, int points) {
/*  45 */     this.data.set(player.getUniqueId() + ".points", Integer.valueOf(points));
/*  46 */     this.data.save();
/*     */   }
/*     */   
/*     */   public void setSkillLevel(Player player, Skill skill, int level) {
/*  50 */     this.data.set(player.getUniqueId() + "." + skill.toString(), Integer.valueOf(level));
/*  51 */     this.data.save();
/*     */   }
/*     */   
/*     */   public int getSkillLevel(OfflinePlayer player, Skill skill) {
/*  55 */     if (!player.hasPlayedBefore()) {
/*  56 */       return 1;
/*     */     }
/*  58 */     if (this.data.get().contains(player.getUniqueId() + "." + skill.toString())) {
/*  59 */       return this.data.get().getInt(player.getUniqueId() + "." + skill.toString());
/*     */     }
/*  61 */     return 1;
/*     */   }
/*     */   
/*     */   public int getSkillPoints(OfflinePlayer player) {
/*  65 */     if (!player.hasPlayedBefore()) {
/*  66 */       return 0;
/*     */     }
/*  68 */     if (this.data.get().contains(player.getUniqueId() + ".points")) {
/*  69 */       return this.data.get().getInt(player.getUniqueId() + ".points");
/*     */     }
/*  71 */     return 0;
/*     */   }
/*     */   
/*     */   public int getPointPrice(Player player) {
/*  75 */     int xpPrice = 1;
/*  76 */     xpPrice = this.config.get().getInt("xp.price");
/*  77 */     if (this.config.get().getBoolean("xp.add-total-to-price")) {
/*  78 */       xpPrice += getTotalPointsSpent(player) * this.config.get().getInt("xp.add-total-to-price-multiplier");
/*     */     }
/*  80 */     return xpPrice;
/*     */   }
/*     */   
/*     */   public void resetAll(Player player) {
/*  84 */     setSkillPoints(player, 0);
/*  85 */     setSkillLevel(player, Skill.HEALTH, 1);
/*  86 */     setSkillLevel(player, Skill.ARCHERY, 1);
/*  87 */     setSkillLevel(player, Skill.RESISTANCE, 1);
/*  88 */     setSkillLevel(player, Skill.CRITICALS, 1);
/*  89 */     setSkillLevel(player, Skill.STRENGTH, 1);
/*     */   }
/*     */   
/*     */   public void buySkillPoint(Player player) {
/*  93 */     player.setLevel(player.getLevel() - getPointPrice(player));
/*  94 */     setSkillPoints(player, getSkillPoints(player) + 1);
/*  95 */     player.playSound(player.getLocation(), Sound.LEVEL_UP, 100.0F, 100.0F);
/*     */   }
/*     */   
/*     */   public int getTotalPointsSpent(OfflinePlayer player) {
/*  99 */     if (!player.hasPlayedBefore()) {
/* 100 */       return 0;
/*     */     }
/* 102 */     int points = getSkillPoints(player);
/* 103 */     int damage = getSkillLevel(player, Skill.STRENGTH) - 1;
/* 104 */     int criticals = getSkillLevel(player, Skill.CRITICALS) - 1;
/* 105 */     int resistance = getSkillLevel(player, Skill.RESISTANCE) - 1;
/* 106 */     int archery = getSkillLevel(player, Skill.ARCHERY) - 1;
/* 107 */     int health = getSkillLevel(player, Skill.HEALTH) - 1;
/* 108 */     int total = points + damage + criticals + resistance + archery + health;
/* 109 */     return total;
/*     */   }
/*     */   
/*     */   public void setSkillPoints(OfflinePlayer player, int points) {
/* 113 */     this.data.set(player.getUniqueId() + ".points", Integer.valueOf(points));
/* 114 */     this.data.save();
/*     */   }
/*     */   
/*     */   public void setSkillLevel(OfflinePlayer player, Skill skill, int level) {
/* 118 */     this.data.set(player.getUniqueId() + "." + skill.toString(), Integer.valueOf(level));
/* 119 */     this.data.save();
/*     */   }
/*     */   
/*     */   public int getMaximumLevel(Skill skill) {
/* 123 */     return this.config.get().getInt("skills." + skill.toString().toLowerCase() + ".max-level");
/*     */   }
/*     */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\managers\SkillManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */