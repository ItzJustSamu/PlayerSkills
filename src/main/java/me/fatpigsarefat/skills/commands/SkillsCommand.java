package me.fatpigsarefat.skills.commands;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.listeners.InventoryClick;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillsCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MessageHelper messageHelper = new MessageHelper();
        if (!cmd.getName().equalsIgnoreCase("skills") || !(sender instanceof Player player))
            return false;
        if (PlayerSkills.instance.getConfig().getBoolean("worlds.restricted") && !PlayerSkills.instance.getConfig().getStringList("worlds.allowed-worlds").contains(player.getLocation().getWorld().getName())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageHelper.getMessage("deny_message", new String[0])));
            return true;
        }
        InventoryClick.reconstructInventory(player, true);
        return true;
    }
}
