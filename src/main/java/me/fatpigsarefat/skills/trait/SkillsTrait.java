/*    */ package me.fatpigsarefat.skills.trait;
/*    */ 
/*    */ import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.listeners.InventoryClick;
/*    */
/*    */
import net.citizensnpcs.api.event.NPCRightClickEvent;
/*    */ import net.citizensnpcs.api.trait.Trait;
/*    */ import net.citizensnpcs.api.util.DataKey;
/*    */ import org.bukkit.event.EventHandler;

/*    */
/*    */ public class SkillsTrait
/*    */   extends Trait
/*    */ {
/*    */   public SkillsTrait() {
/* 14 */     super("playerskills");
/*    */   }
/*    */   
/*    */   @EventHandler
/*    */   public void click(NPCRightClickEvent event) {
/* 19 */     if (event.getNPC().hasTrait(SkillsTrait.class)) {
/* 20 */       MessageHelper messageHelper = new MessageHelper();
/* 21 */       if (event.getClicker().hasPermission("playerskills.npc-use")) {
/* 22 */         InventoryClick.reconstructInventory(event.getClicker(), true);
/*    */       } else {
/*    */         
/* 25 */         event.getClicker().sendMessage(messageHelper.getMessage("no_permissions_message", new String[0]));
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void load(DataKey key) {}
/*    */   
/*    */   public void save(DataKey key) {}
/*    */   
/*    */   public void onAttach() {}
/*    */   
/*    */   public void onDespawn() {}
/*    */   
/*    */   public void onSpawn() {}
/*    */   
/*    */   public void onRemove() {}
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\trait\SkillsTrait.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */