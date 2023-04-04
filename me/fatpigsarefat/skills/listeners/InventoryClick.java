/*     */ package me.fatpigsarefat.skills.listeners;
/*     */ import me.fatpigsarefat.skills.PlayerSkills;
/*     */ import me.fatpigsarefat.skills.events.ResetSkillEvent;
/*     */ import me.fatpigsarefat.skills.events.UpgradeSkillEvent;
import me.fatpigsarefat.skills.helper.MessageHelper;
/*     */ import me.fatpigsarefat.skills.managers.ConfigurationManager;
/*     */ import me.fatpigsarefat.skills.managers.FileManager;
/*     */ import me.fatpigsarefat.skills.managers.SkillManager;
/*     */ import me.fatpigsarefat.skills.utils.Skill;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Sound;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.Event;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/*     */
/*     */ public class InventoryClick implements Listener {
/*  21 */   private MessageHelper messageHelper = new MessageHelper();
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onInventoryClick(InventoryClickEvent e) {
/*  26 */     FileManager.Config gui = PlayerSkills.getFileManager().getConfig("gui");
/*  27 */     FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");
/*     */     
/*  29 */     if (e.getClickedInventory() != null && e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Objects.<String>requireNonNull(gui.get().getString("gui.title"))))) {
/*  30 */       InventoryAction a; e.setCancelled(true);
/*  31 */       Player player = (Player)e.getWhoClicked();
/*  32 */       SkillManager sm = PlayerSkills.getSkillManager();
/*     */       
/*  34 */       if (gui.get().getBoolean("gui.display.points-purchase.right-click")) {
/*  35 */         a = InventoryAction.PICKUP_HALF;
/*     */       } else {
/*     */         
/*  38 */         a = InventoryAction.PICKUP_ALL;
/*     */       } 
/*  40 */       if (e.getSlot() == gui.get().getInt("gui.display.points-purchase.slot") && e.getAction().equals(a)) {
/*  41 */         if (player.getLevel() >= sm.getPointPrice(player)) {
/*  42 */           if (config.get().getBoolean("points.restriction")) {
/*  43 */             if (sm.getTotalPointsSpent(player) != config.get().getInt("points.restriction-per")) {
/*  44 */               sm.buySkillPoint(player);
/*  45 */               reconstructInventory(player, false);
/*     */             } else {
/*     */               
/*  48 */               player.sendMessage(this.messageHelper.getMessage("points_limit", new String[] { config.get().getString("points.restriction-per") }));
/*     */             } 
/*     */           } else {
/*     */             
/*  52 */             sm.buySkillPoint(player);
/*  53 */             reconstructInventory(player, false);
/*     */           }
/*     */         
/*     */         }
/*  57 */       } else if (e.getSlot() == gui.get().getInt("gui.display.reset-all.slot")) {
/*  58 */         if (!PlayerSkills.allowReset) {
/*     */           return;
/*     */         }
/*  61 */         sm.resetAll(player);
/*  62 */         reconstructInventory(player, false);
/*  63 */         player.playSound(player.getLocation(), Sound.EXPLODE, 100.0F, 100.0F);
/*  64 */         player.sendMessage(this.messageHelper.getMessage("skill_full_reset", new String[0]));
/*     */       }
/*  66 */       else if (e.getSlot() == gui.get().getInt("gui.display.strength-normal.slot")) {
/*  67 */         updateSkill(sm, player, Skill.STRENGTH);
/*     */       }
/*  69 */       else if (e.getSlot() == gui.get().getInt("gui.display.criticals-normal.slot")) {
/*  70 */         updateSkill(sm, player, Skill.CRITICALS);
/*     */       }
/*  72 */       else if (e.getSlot() == gui.get().getInt("gui.display.resistance-normal.slot")) {
/*  73 */         updateSkill(sm, player, Skill.RESISTANCE);
/*     */       }
/*  75 */       else if (e.getSlot() == gui.get().getInt("gui.display.archery-normal.slot")) {
/*  76 */         updateSkill(sm, player, Skill.ARCHERY);
/*     */       }
/*  78 */       else if (e.getSlot() == gui.get().getInt("gui.display.health-normal.slot")) {
/*  79 */         updateSkill(sm, player, Skill.HEALTH);
/*     */       }
/*  81 */       else if (e.getSlot() == gui.get().getInt("gui.display.reset-strength.slot")) {
/*  82 */         resetSkill(sm, player, Skill.STRENGTH);
/*     */       }
/*  84 */       else if (e.getSlot() == gui.get().getInt("gui.display.reset-criticals.slot")) {
/*  85 */         resetSkill(sm, player, Skill.CRITICALS);
/*     */       }
/*  87 */       else if (e.getSlot() == gui.get().getInt("gui.display.reset-resistance.slot")) {
/*  88 */         resetSkill(sm, player, Skill.RESISTANCE);
/*     */       }
/*  90 */       else if (e.getSlot() == gui.get().getInt("gui.display.reset-archery.slot")) {
/*  91 */         resetSkill(sm, player, Skill.ARCHERY);
/*     */       }
/*  93 */       else if (e.getSlot() == gui.get().getInt("gui.display.reset-health.slot")) {
/*  94 */         resetSkill(sm, player, Skill.HEALTH);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void reconstructInventory(Player player, boolean completeUpdate) {
/* 100 */     FileManager.Config gui = PlayerSkills.getFileManager().getConfig("gui");
/* 101 */     SkillManager sm = PlayerSkills.getSkillManager();
/* 102 */     ConfigurationManager cm = new ConfigurationManager();
/* 103 */     Inventory inv = Bukkit.createInventory(null, gui.get().getInt("gui.size") * 9, ChatColor.translateAlternateColorCodes('&', gui.get().getString("gui.title")));
/* 104 */     int strength = 1;
/* 105 */     int criticals = 1;
/* 106 */     int resistance = 1;
/* 107 */     int archery = 1;
/* 108 */     int health = 1;
/* 109 */     strength = sm.getSkillLevel(player, Skill.STRENGTH);
/* 110 */     criticals = sm.getSkillLevel(player, Skill.CRITICALS);
/* 111 */     resistance = sm.getSkillLevel(player, Skill.RESISTANCE);
/* 112 */     archery = sm.getSkillLevel(player, Skill.ARCHERY);
/* 113 */     health = sm.getSkillLevel(player, Skill.HEALTH);
/* 114 */     ItemStack skillpointsIs = new ItemStack(cm.getItemStack("points-purchase", player));
/* 115 */     if (sm.getSkillPoints(player) > 0) {
/* 116 */       skillpointsIs.setAmount(sm.getSkillPoints(player));
/*     */     } else {
/*     */       
/* 119 */       skillpointsIs.setAmount(1);
/*     */     } 
/* 121 */     ItemStack playerstatsIs = cm.getItemStack("stats", player);
/* 122 */     playerstatsIs.setAmount(1);
/* 123 */     ItemStack strengthIs = cm.getItemStack("strength-normal", player);
/* 124 */     strengthIs.setAmount(strength);
/* 125 */     ItemStack criticalsIs = cm.getItemStack("criticals-normal", player);
/* 126 */     criticalsIs.setAmount(criticals);
/* 127 */     ItemStack resistanceIs = cm.getItemStack("resistance-normal", player);
/* 128 */     resistanceIs.setAmount(resistance);
/* 129 */     ItemStack archeryIs = cm.getItemStack("archery-normal", player);
/* 130 */     archeryIs.setAmount(archery);
/* 131 */     ItemStack healthIs = cm.getItemStack("health-normal", player);
/* 132 */     healthIs.setAmount(health);
/* 133 */     ItemStack rs = cm.getItemStack("reset-strength", player);
/* 134 */     rs.setAmount(1);
/* 135 */     ItemStack rc = cm.getItemStack("reset-criticals", player);
/* 136 */     rc.setAmount(1);
/* 137 */     ItemStack rr = cm.getItemStack("reset-resistance", player);
/* 138 */     rr.setAmount(1);
/* 139 */     ItemStack ra = cm.getItemStack("reset-archery", player);
/* 140 */     ra.setAmount(1);
/* 141 */     ItemStack rh = cm.getItemStack("reset-health", player);
/* 142 */     rh.setAmount(1);
/* 143 */     ItemStack barrier2Is = cm.getItemStack("reset-all", player);
/* 144 */     barrier2Is.setAmount(1);
/* 145 */     inv.setItem(gui.get().getInt("gui.display.points-purchase.slot"), skillpointsIs);
/* 146 */     inv.setItem(gui.get().getInt("gui.display.stats.slot"), playerstatsIs);
/* 147 */     inv.setItem(gui.get().getInt("gui.display.strength-normal.slot"), strengthIs);
/* 148 */     inv.setItem(gui.get().getInt("gui.display.criticals-normal.slot"), criticalsIs);
/* 149 */     inv.setItem(gui.get().getInt("gui.display.resistance-normal.slot"), resistanceIs);
/* 150 */     inv.setItem(gui.get().getInt("gui.display.archery-normal.slot"), archeryIs);
/* 151 */     inv.setItem(gui.get().getInt("gui.display.health-normal.slot"), healthIs);
/* 152 */     if (PlayerSkills.allowReset) {
/* 153 */       inv.setItem(gui.get().getInt("gui.display.reset-strength.slot"), rs);
/* 154 */       inv.setItem(gui.get().getInt("gui.display.reset-criticals.slot"), rc);
/* 155 */       inv.setItem(gui.get().getInt("gui.display.reset-resistance.slot"), rr);
/* 156 */       inv.setItem(gui.get().getInt("gui.display.reset-archery.slot"), ra);
/* 157 */       inv.setItem(gui.get().getInt("gui.display.reset-health.slot"), rh);
/* 158 */       inv.setItem(gui.get().getInt("gui.display.reset-all.slot", 5), barrier2Is);
/*     */     } 
/* 160 */     if (!completeUpdate) {
/* 161 */       player.getOpenInventory().getTopInventory().setContents(inv.getContents());
/*     */     } else {
/*     */       
/* 164 */       player.closeInventory();
/* 165 */       player.openInventory(inv);
/*     */     } 
/* 167 */     player.updateInventory();
/*     */   }
/*     */   
/*     */   public void updateSkill(SkillManager sm, Player player, Skill skill) {
/* 171 */     Bukkit.getPluginManager().callEvent((Event)new UpgradeSkillEvent(player, sm, skill));
/*     */   }
/*     */   
/*     */   public void resetSkill(SkillManager sm, Player player, Skill skill) {
/* 175 */     Bukkit.getPluginManager().callEvent((Event)new ResetSkillEvent(player, sm, skill));
/*     */   }
/*     */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\listeners\InventoryClick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */