/*    */ package me.fatpigsarefat.skills.listeners;
/*    */ import me.fatpigsarefat.skills.PlayerSkills;
/*    */ import me.fatpigsarefat.skills.events.UpgradeSkillEvent;
/*    */ import me.fatpigsarefat.skills.helper.MessageHelper;
/*    */ import me.fatpigsarefat.skills.managers.FileManager;
/*    */ import me.fatpigsarefat.skills.managers.SkillManager;
/*    */ import me.fatpigsarefat.skills.utils.Skill;
/*    */ import org.bukkit.Sound;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ 
/*    */ public class UpgradeSkill implements Listener {
/*    */   @EventHandler
/*    */   public void onUpgradeSkill(UpgradeSkillEvent e) {
/* 16 */     FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");
/* 17 */     Player player = e.getPlayer();
/* 18 */     SkillManager sm = e.getSkillManager();
/* 19 */     Skill skill = e.getSkill();
/* 20 */     MessageHelper messageHelper = new MessageHelper();
/* 21 */     if (config.get().getBoolean("permissions.use")) {
/* 22 */       if (player.hasPermission("playerskills." + skill.name().toLowerCase())) {
/* 23 */         if (config.get().getBoolean("permissions.level-perms") && 
/* 24 */           !player.hasPermission("playerskills." + skill.name() + "." + sm.getSkillLevel(player, skill))) {
/* 25 */           player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
/* 26 */           player.sendMessage(messageHelper.getMessage("skill_upgrade_false_perms", new String[0]));
/*    */           
/*    */           return;
/*    */         } 
/* 30 */         if (sm.getSkillPoints(player) <= 0) {
/* 31 */           player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
/* 32 */           player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
/*    */           return;
/*    */         } 
/* 35 */         if (sm.getSkillLevel(player, skill) >= sm.getMaximumLevel(skill)) {
/* 36 */           player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
/* 37 */           player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
/*    */           return;
/*    */         } 
/* 40 */         sm.setSkillPoints(player, sm.getSkillPoints(player) - 1);
/* 41 */         sm.setSkillLevel(player, skill, sm.getSkillLevel(player, skill) + 1);
/* 42 */         InventoryClick.reconstructInventory(player, false);
/* 43 */         player.playSound(player.getLocation(), Sound.LEVEL_UP, 100.0F, 100.0F);
/* 44 */         player.sendMessage(messageHelper.getMessage("skill_upgrade", new String[] { skill.name().toLowerCase() }));
/*    */       } else {
/*    */         
/* 47 */         player.sendMessage(messageHelper.getMessage("no_permissions_message", new String[0]));
/*    */       } 
/*    */     } else {
/*    */       
/* 51 */       if (sm.getSkillPoints(player) <= 0) {
/* 52 */         player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
/* 53 */         player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
/*    */         return;
/*    */       } 
/* 56 */       if (sm.getSkillLevel(player, skill) >= sm.getMaximumLevel(skill)) {
/*    */         
/* 58 */         player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
/* 59 */         player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
/*    */         return;
/*    */       } 
/* 62 */       sm.setSkillPoints(player, sm.getSkillPoints(player) - 1);
/* 63 */       sm.setSkillLevel(player, skill, sm.getSkillLevel(player, skill) + 1);
/* 64 */       InventoryClick.reconstructInventory(player, false);
/*    */       
/* 66 */       player.playSound(player.getLocation(), Sound.LEVEL_UP, 100.0F, 100.0F);
/* 67 */       player.sendMessage(messageHelper.getMessage("skill_upgrade", new String[] { skill.name().toLowerCase() }));
/*    */     } 
/* 69 */     if (PlayerSkills.useHolograms)
/* 70 */       PlayerSkills.getHologramManager().update(player); 
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\listeners\UpgradeSkill.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */