/*    */ package me.fatpigsarefat.skills.listeners;
/*    */ 
/*    */ import me.fatpigsarefat.skills.PlayerSkills;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.entity.PlayerDeathEvent;
/*    */ import org.bukkit.event.player.PlayerJoinEvent;
/*    */ import org.bukkit.event.player.PlayerQuitEvent;
/*    */ 
/*    */ public class PlayerListener
/*    */   implements Listener {
/*    */   @EventHandler(priority = EventPriority.MONITOR)
/*    */   public void onPlayerJoin(PlayerJoinEvent e) {
/* 16 */     PlayerSkills.getInstance().checkUpdates(e.getPlayer());
/*    */
/*    */   }
/*    */   
/*    */   @EventHandler(priority = EventPriority.MONITOR)
/*    */   public void onPlayerLeave(PlayerQuitEvent e) {
/* 24 */     if (PlayerSkills.potionEffect.containsKey(e.getPlayer())) {
/* 25 */       PlayerSkills.potionEffect.remove(e.getPlayer());
/*    */     }
/*    */
/*    */   }
/*    */   
/*    */   @EventHandler(priority = EventPriority.MONITOR)
/*    */   public void onPlayerLeave(PlayerDeathEvent e) {
/* 34 */     Player p = e.getEntity();
/* 35 */     if (PlayerSkills.potionEffect.containsKey(p))
/* 36 */       PlayerSkills.potionEffect.remove(p); 
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\listeners\PlayerListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */