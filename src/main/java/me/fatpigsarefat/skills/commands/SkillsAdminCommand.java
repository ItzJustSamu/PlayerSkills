/*     */ package me.fatpigsarefat.skills.commands;
/*     */

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/*     */
/*     */ public class SkillsAdminCommand implements CommandExecutor {
/*     */   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
/*  15 */     MessageHelper messages = new MessageHelper();
/*  16 */     if (!cmd.getName().equalsIgnoreCase("skillsadmin")) {
/*  17 */       return false;
/*     */     }
/*  19 */     if (!sender.hasPermission("playerskills.admin")) {
/*  20 */       sender.sendMessage(messages.getMessage("no_permissions_message", new String[0]));
/*  21 */       return true;
/*     */     } 
/*  23 */     SkillManager sm = PlayerSkills.getSkillManager();
/*  24 */     int length = args.length;
/*  25 */     if (length == 0) {
/*  26 */       for (String s : messages.getMessageList("skill_help")) {
/*  27 */         sender.sendMessage(s);
/*     */       }
/*  29 */       return true;
/*     */     } 
/*  31 */     if (length == 2 && args[0].equalsIgnoreCase("reload")) {
/*  32 */       PlayerSkills.getFileManager().reloadConfig(args[1]);
/*     */
/*     */       
/*  45 */       sender.sendMessage(messages.getMessage("config_reload", new String[] { args[1] }));
/*  46 */       return true;
/*     */     } 
/*     */
/*  67 */     if (length == 3 && args[0].equalsIgnoreCase("givepoints")) {
/*  68 */       OfflinePlayer ofp = Bukkit.getOfflinePlayer(args[1]);
/*  69 */       if (!ofp.hasPlayedBefore()) {
/*  70 */         sender.sendMessage(messages.getMessage("player_no_found", new String[0]));
/*  71 */         return true;
/*     */       } 
/*     */       try {
/*  74 */         Integer.parseInt(args[2]);
/*     */       }
/*  76 */       catch (NumberFormatException ex) {
/*  77 */         sender.sendMessage(messages.getMessage("is_no_number", new String[] { args[3] }));
/*  78 */         return true;
/*     */       } 
/*  80 */       sm.setSkillPoints(ofp, sm.getSkillPoints(ofp) + Integer.parseInt(args[2]));
/*  81 */       sender.sendMessage(messages.getMessage("successful_add_points", new String[] { args[1], args[2] }));
/*  82 */       return true;
/*     */     } 
/*     */     
/*  85 */     if (length == 4 || args[0].equalsIgnoreCase("setlevel")) {
/*  86 */       OfflinePlayer ofp = Bukkit.getOfflinePlayer(args[1]);
/*  87 */       if (!ofp.hasPlayedBefore()) {
/*  88 */         sender.sendMessage(messages.getMessage("player_no_found", new String[0]));
/*  89 */         return true;
/*     */       } 
/*  91 */       Skill skill = Skill.getSkillByName(args[2]);
/*  92 */       if (skill == null) {
/*  93 */         sender.sendMessage(messages.getMessage("valid_skill", new String[0]));
/*  94 */         return true;
/*     */       } 
/*     */       try {
/*  97 */         Integer.parseInt(args[3]);
/*     */       }
/*  99 */       catch (NumberFormatException ex2) {
/* 100 */         sender.sendMessage(messages.getMessage("is_no_number", new String[] { args[3] }));
/* 101 */         return true;
/*     */       } 
/* 103 */       if (Integer.parseInt(args[3]) > sm.getMaximumLevel(skill)) {
/* 104 */         sender.sendMessage(messages.getMessage("max_lvl_skill", new String[] { skill.toString().toLowerCase(), sm.getMaximumLevel(skill) + "" }));
/* 105 */         return true;
/*     */       } 
/* 107 */       if (Integer.parseInt(args[3]) < 1) {
/* 108 */         sender.sendMessage(messages.getMessage("min_lvl_skill", new String[0]));
/* 109 */         return true;
/*     */       } 
/* 111 */       sm.setSkillLevel(ofp, skill, Integer.parseInt(args[3]));
/* 112 */       sender.sendMessage(messages.getMessage("successful_set_skill_lvl", new String[] { args[1], sm.getSkillLevel(ofp, skill) + "", skill.toString().toLowerCase() }));
/* 113 */       return true;
/*     */     } 
/*     */     
/* 116 */     sender.sendMessage(messages.getMessage("invalied_args", new String[0]));
/* 117 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\commands\SkillsAdminCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */