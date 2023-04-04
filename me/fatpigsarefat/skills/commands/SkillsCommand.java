/*    */ package me.fatpigsarefat.skills.commands;
/*    */ import me.fatpigsarefat.skills.PlayerSkills;
/*    */ import me.fatpigsarefat.skills.helper.MessageHelper;
/*    */ import me.fatpigsarefat.skills.listeners.InventoryClick;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class SkillsCommand implements CommandExecutor {
/*    */   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
/* 13 */     MessageHelper messageHelper = new MessageHelper();
/* 14 */     if (!cmd.getName().equalsIgnoreCase("skills") || !(sender instanceof Player)) {
/* 15 */       return false;
/*    */     }
/* 17 */     Player player = (Player)sender;
/* 18 */     if (PlayerSkills.instance.getConfig().getBoolean("worlds.restricted") && !PlayerSkills.instance.getConfig().getStringList("worlds.allowed-worlds").contains(player.getLocation().getWorld().getName())) {
/* 19 */       player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageHelper.getMessage("deny_message", new String[0])));
/* 20 */       return true;
/*    */     } 
/* 22 */     InventoryClick.reconstructInventory(player, true);
/* 23 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\jessl\Downloads\PlayerSkills.jar!\me\fatpigsarefat\skills\commands\SkillsCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */