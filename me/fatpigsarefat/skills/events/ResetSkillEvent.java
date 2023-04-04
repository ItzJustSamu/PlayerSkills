/*    */ package me.fatpigsarefat.skills.events;
/*    */ 
/*    */ import me.fatpigsarefat.skills.managers.SkillManager;
/*    */ import me.fatpigsarefat.skills.utils.Skill;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.Event;
/*    */ import org.bukkit.event.HandlerList;
/*    */ 
/*    */ public class ResetSkillEvent
/*    */   extends Event {
/*    */   private Player player;
/*    */   private SkillManager skillManager;
/*    */   private Skill skill;
/*    */   
/*    */   public ResetSkillEvent(Player player, SkillManager skillManager, Skill skill) {
/* 16 */     this.player = player;
/* 17 */     this.skillManager = skillManager;
/* 18 */     this.skill = skill;
/*    */   }
/*    */   
/*    */   public Player getPlayer() {
/* 22 */     return this.player;
/*    */   }
/*    */   
/*    */   public SkillManager getSkillManager() {
/* 26 */     return this.skillManager;
/*    */   }
/*    */   
/*    */   public Skill getSkill() {
/* 30 */     return this.skill;
/*    */   }
/*    */   
/*    */   public HandlerList getHandlers() {
/* 34 */     return handlers;
/*    */   }
/*    */   
/*    */   public static HandlerList getHandlerList() {
/* 38 */     return handlers;
/*    */   }
/*    */ 
/*    */   
/* 42 */   private static HandlerList handlers = new HandlerList();
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\events\ResetSkillEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */