/*    */ package me.fatpigsarefat.skills.utils;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.util.Scanner;
/*    */ import java.util.function.Consumer;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ 
/*    */ public class UpdateChecker {
/*    */   private JavaPlugin plugin;
/*    */   private int resourceId;
/*    */   
/*    */   public UpdateChecker(JavaPlugin plugin, int resourceId) {
/* 17 */     this.plugin = plugin;
/* 18 */     this.resourceId = resourceId;
/*    */   }
/*    */   
/*    */   public void getVersion(Consumer<String> consumer) {
/* 22 */     Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
/*    */           
/*    */           try(InputStream inputStream = (new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId)).openStream(); Scanner scanner = new Scanner(inputStream)) {
/*    */             if (scanner.hasNext()) {
/*    */               consumer.accept(scanner.next());
/*    */             }
/* 28 */           } catch (IOException exception) {
/*    */             this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
/*    */           } 
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skill\\utils\UpdateChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */