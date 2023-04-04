/*    */ package me.fatpigsarefat.skills.helper;
/*    */ 
/*    */ import java.util.List;
/*    */ import me.fatpigsarefat.skills.PlayerSkills;
/*    */ import me.fatpigsarefat.skills.managers.FileManager;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MessageHelper
/*    */ {
/* 12 */   private FileManager.Config config = PlayerSkills.getFileManager().getConfig("messages");
/*    */ 
/*    */   
/*    */   private String getPrefix() {
/* 16 */     return this.config.get().getString("prefix") + " ";
/*    */   }
/*    */   
/*    */   public String getMessage(String key, String[] args) {
/* 20 */     String message = getPrefix() + this.config.get().getString(key);
/* 21 */     message = message.replace("&", "§");
/* 22 */     if (args == null) {
/* 23 */       return message;
/*    */     }
/* 25 */     for (int i = 0; i < args.length; i++) {
/* 26 */       message = message.replace("{" + i + "}", args[i]);
/*    */     }
/* 28 */     return message;
/*    */   }
/*    */   
/*    */   public List<String> getMessageList(String key) {
/* 32 */     return this.config.get().getStringList(key);
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\helper\MessageHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */