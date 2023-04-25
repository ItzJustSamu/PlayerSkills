package me.fatpigsarefat.skills.commands;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SkillsAdminCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MessageHelper messages = new MessageHelper();
        if (!cmd.getName().equalsIgnoreCase("skillsadmin"))
            return false;
        if (!sender.hasPermission("playerskills.admin")) {
            sender.sendMessage(messages.getMessage("no_permissions_message", new String[0]));
            return true;
        }
        SkillManager sm = PlayerSkills.getSkillManager();
        int length = args.length;
        if (length == 0) {
            for (String s : messages.getMessageList("skill_help"))
                sender.sendMessage(s);
            return true;
        }

        if (length == 3 && args[0].equalsIgnoreCase("givepoints")) {
            OfflinePlayer ofp = Bukkit.getPlayer(args[1]);
            if (!ofp.hasPlayedBefore()) {
                sender.sendMessage(messages.getMessage("player_no_found", new String[0]));
                return true;
            }
            try {
                Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(messages.getMessage("is_no_number", new String[] { args[3] }));
                return true;
            }
            sm.setSkillPoints(ofp, sm.getSkillPoints(ofp) + Integer.parseInt(args[2]));
            sender.sendMessage(messages.getMessage("successful_add_points", new String[] { args[1], args[2] }));
            return true;
        }
        if (length == 4 || args[0].equalsIgnoreCase("setlevel")) {
            OfflinePlayer ofp = Bukkit.getPlayer(args[1]);
            if (!ofp.hasPlayedBefore()) {
                sender.sendMessage(messages.getMessage("player_no_found", new String[0]));
                return true;
            }
            Skill skill = Skill.getSkillByName(args[2]);
            if (skill == null) {
                sender.sendMessage(messages.getMessage("valid_skill", new String[0]));
                return true;
            }
            try {
                Integer.parseInt(args[3]);
            } catch (NumberFormatException ex2) {
                sender.sendMessage(messages.getMessage("is_no_number", new String[] { args[3] }));
                return true;
            }
            if (Integer.parseInt(args[3]) > sm.getMaximumLevel(skill)) {
                sender.sendMessage(messages.getMessage("max_lvl_skill", new String[] { skill.toString().toLowerCase(), String.valueOf(sm.getMaximumLevel(skill))}));
                return true;
            }
            if (Integer.parseInt(args[3]) < 1) {
                sender.sendMessage(messages.getMessage("min_lvl_skill", new String[0]));
                return true;
            }
            sm.setSkillLevel(ofp, skill, Integer.parseInt(args[3]));
            sender.sendMessage(messages.getMessage("successful_set_skill_lvl", new String[] { args[1], String.valueOf(sm.getSkillLevel(ofp, skill)), skill.toString().toLowerCase() }));
            return true;
        }
        sender.sendMessage(messages.getMessage("invalid_args", new String[0]));
        return true;
    }
}
