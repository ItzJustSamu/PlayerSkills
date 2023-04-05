/*    */ package me.fatpigsarefat.skills.listeners;
/*    */ 
/*    */ import me.fatpigsarefat.skills.PlayerSkills;
/*    */ import me.fatpigsarefat.skills.events.ResetSkillEvent;
/*    */ import me.fatpigsarefat.skills.helper.MessageHelper;
/*    */ import me.fatpigsarefat.skills.managers.SkillManager;
/*    */ import me.fatpigsarefat.skills.utils.Skill;
/*    */ import org.bukkit.Sound;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ 
/*    */ public class ResetSkill implements Listener {
/*    */   @EventHandler
/*    */   public void onSkillReset(ResetSkillEvent e) {
/* 16 */     MessageHelper messageHelper = new MessageHelper();
/* 17 */     Player player = e.getPlayer();
/* 18 */     SkillManager sm = e.getSkillManager();
/* 19 */     Skill skill = e.getSkill();
/* 20 */     if (!PlayerSkills.allowReset) {
/*    */       return;
/*    */     }
/* 23 */     sm.setSkillPoints(player, sm.getSkillPoints(player) + sm.getSkillLevel(player, skill) - 1);
/* 24 */     sm.setSkillLevel(player, skill, 1);
/* 25 */     InventoryClick.reconstructInventory(player, false);
/* 26 */     player.sendMessage(messageHelper.getMessage("skill_reset", new String[] { skill.name().toLowerCase() }));
/* 27 */     player.playSound(player.getLocation(), Sound.ORB_PICKUP, 100.0F, 100.0F);
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\listeners\ResetSkill.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */