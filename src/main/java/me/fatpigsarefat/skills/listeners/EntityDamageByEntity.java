/*    */ package me.fatpigsarefat.skills.listeners;
/*    */ import me.fatpigsarefat.skills.PlayerSkills;
/*    */ import me.fatpigsarefat.skills.managers.SkillManager;
/*    */ import me.fatpigsarefat.skills.utils.Skill;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.entity.Arrow;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*    */ import org.bukkit.potion.PotionEffectType;
/*    */ 
/*    */ public class EntityDamageByEntity implements Listener {
/*    */   @EventHandler(priority = EventPriority.MONITOR)
/*    */   public void onEntityByEntityDamage(EntityDamageByEntityEvent e) {
/* 16 */     if (e.getDamager() instanceof Player) {
/* 17 */       Player player = (Player)e.getDamager();
/* 18 */       if (PlayerSkills.instance.getConfig().getBoolean("worlds.restricted") && !PlayerSkills.instance.getConfig().getStringList("worlds.allowed-worlds").contains(player.getLocation().getWorld().getName())) {
/*    */         return;
/*    */       }
/* 21 */       SkillManager sm = PlayerSkills.getSkillManager();
/* 22 */       int skill = sm.getSkillLevel(player, Skill.STRENGTH) - 1;
/* 23 */       double d = e.getDamage() / 100.0D;
/* 24 */       d *= ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH)).intValue();
/* 25 */       double finalDamage = skill * d;
/* 26 */       e.setDamage(e.getDamage() + finalDamage);
/* 27 */       boolean result = (player.getFallDistance() > 0.0F && !player.isOnGround() && !player.hasPotionEffect(PotionEffectType.BLINDNESS) && player.getVehicle() == null && !player.isSprinting() && !player.getLocation().getBlock().isLiquid() && !player.getLocation().add(0.0D, 0.0D, 1.0D).getBlock().getType().equals(Material.LADDER));
/* 28 */       double dmg = e.getDamage();
/* 29 */       if (result && dmg > 0.0D) {
/* 30 */         int sk = sm.getSkillLevel(player, Skill.CRITICALS) - 1;
/* 31 */         double damage = e.getDamage() / 150.0D;
/* 32 */         damage *= ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS)).intValue();
/* 33 */         double fdamage = sk * damage;
/* 34 */         e.setDamage(e.getDamage() + fdamage);
/*    */       }
/*    */     
/* 37 */     } else if (e.getDamager() instanceof Arrow) {
/* 38 */       Arrow arrow = (Arrow)e.getDamager();
/* 39 */       if (arrow.getShooter() instanceof Player) {
/* 40 */         Player player2 = (Player)arrow.getShooter();
/* 41 */         if (PlayerSkills.instance.getConfig().getBoolean("worlds.restricted") && !PlayerSkills.instance.getConfig().getStringList("worlds.allowed-worlds").contains(player2.getLocation().getWorld().getName())) {
/*    */           return;
/*    */         }
/* 44 */         SkillManager sm2 = PlayerSkills.getSkillManager();
/* 45 */         int skill2 = sm2.getSkillLevel(player2, Skill.ARCHERY) - 1;
/* 46 */         double d2 = e.getDamage() / 100.0D;
/* 47 */         d2 *= ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY)).intValue();
/* 48 */         double finalDamage2 = skill2 * d2;
/* 49 */         e.setDamage(e.getDamage() + finalDamage2);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\listeners\EntityDamageByEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */