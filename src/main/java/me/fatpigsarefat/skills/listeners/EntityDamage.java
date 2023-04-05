/*    */ package me.fatpigsarefat.skills.listeners;
/*    */ import me.fatpigsarefat.skills.PlayerSkills;
/*    */ import me.fatpigsarefat.skills.managers.SkillManager;
/*    */ import me.fatpigsarefat.skills.utils.Skill;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.entity.EntityDamageEvent;
/*    */ 
/*    */ public class EntityDamage implements Listener {
/*    */   @EventHandler(priority = EventPriority.MONITOR)
/*    */   public void onEntityDamage(EntityDamageEvent e) {
/* 14 */     if (e.getEntity() instanceof Player) {
/* 15 */       Player player = (Player)e.getEntity();
/* 16 */       if (PlayerSkills.instance.getConfig().getBoolean("worlds.restricted") && !PlayerSkills.instance.getConfig().getStringList("worlds.allowed-worlds").contains(player.getLocation().getWorld().getName())) {
/*    */         return;
/*    */       }
/* 19 */       SkillManager sm = PlayerSkills.getSkillManager();
/* 20 */       int skill = sm.getSkillLevel(player, Skill.RESISTANCE) - 1;
/* 21 */       double d = e.getDamage() / 100.0D;
/* 22 */       d *= ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE)).intValue();
/* 23 */       double finalDamage = skill * d;
/* 24 */       e.setDamage(e.getDamage() - finalDamage);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\listeners\EntityDamage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */