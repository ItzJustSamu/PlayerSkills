package me.fatpigsarefat.skills.commands;

import java.util.Iterator;
import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.LocationUtil;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillsAdminCommand implements CommandExecutor {
    public SkillsAdminCommand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MessageHelper messages = new MessageHelper();
        if (!cmd.getName().equalsIgnoreCase("skillsadmin")) {
            return false;
        } else if (!sender.hasPermission("playerskills.admin")) {
            sender.sendMessage(messages.getMessage("no_permissions_message", new String[0]));
            return true;
        } else {
            SkillManager sm = PlayerSkills.getSkillManager();
            int length = args.length;
            Iterator var14;
            if (length == 0) {
                var14 = messages.getMessageList("skill_help").iterator();

                while(var14.hasNext()) {
                    String s = (String)var14.next();
                    sender.sendMessage(s);
                }

                return true;
            } else if (length == 2 && args[0].equalsIgnoreCase("reload")) {
                PlayerSkills.getFileManager().reloadConfig(args[1]);
                if (args[1].equals("config")) {
                    PlayerSkills.useHolograms = PlayerSkills.getFileManager().getConfig("config").get().getBoolean("holograms.use");
                    var14 = Bukkit.getOnlinePlayers().iterator();

                    while(var14.hasNext()) {
                        Player player = (Player)var14.next();
                        if (PlayerSkills.useHolograms) {
                            PlayerSkills.getHologramManager().clearUpdate(player);
                        } else {
                            PlayerSkills.getHologramManager().remove(player);
                        }
                    }
                }

                sender.sendMessage(messages.getMessage("config_reload", new String[]{args[1]}));
                return true;
            } else if (!args[0].equalsIgnoreCase("sethologram")) {
                OfflinePlayer ofp;
                if (length == 3 && args[0].equalsIgnoreCase("givepoints")) {
                    ofp = Bukkit.getOfflinePlayer(args[1]);
                    if (!ofp.hasPlayedBefore()) {
                        sender.sendMessage(messages.getMessage("player_no_found", new String[0]));
                    } else {
                        try {
                            Integer.parseInt(args[2]);
                        } catch (NumberFormatException var11) {
                            sender.sendMessage(messages.getMessage("is_no_number", new String[]{args[3]}));
                            return true;
                        }

                        sm.setSkillPoints(ofp, sm.getSkillPoints(ofp) + Integer.parseInt(args[2]));
                        sender.sendMessage(messages.getMessage("successful_add_points", new String[]{args[1], args[2]}));
                    }
                    return true;
                } else if (length != 4 && !args[0].equalsIgnoreCase("setlevel")) {
                    sender.sendMessage(messages.getMessage("invalied_args", new String[0]));
                    return true;
                } else {
                    ofp = Bukkit.getOfflinePlayer(args[1]);
                    if (!ofp.hasPlayedBefore()) {
                        sender.sendMessage(messages.getMessage("player_no_found", new String[0]));
                        return true;
                    } else {
                        Skill skill = Skill.getSkillByName(args[2]);
                        if (skill == null) {
                            sender.sendMessage(messages.getMessage("valid_skill", new String[0]));
                            return true;
                        } else {
                            try {
                                Integer.parseInt(args[3]);
                            } catch (NumberFormatException var12) {
                                sender.sendMessage(messages.getMessage("is_no_number", new String[]{args[3]}));
                                return true;
                            }

                            if (Integer.parseInt(args[3]) > sm.getMaximumLevel(skill)) {
                                sender.sendMessage(messages.getMessage("max_lvl_skill", new String[]{skill.toString().toLowerCase(), String.valueOf(sm.getMaximumLevel(skill))}));
                                return true;
                            } else if (Integer.parseInt(args[3]) < 1) {
                                sender.sendMessage(messages.getMessage("min_lvl_skill", new String[0]));
                                return true;
                            } else {
                                sm.setSkillLevel(ofp, skill, Integer.parseInt(args[3]));
                                sender.sendMessage(messages.getMessage("successful_set_skill_lvl", new String[]{args[1], String.valueOf(sm.getSkillLevel(ofp, skill)), skill.toString().toLowerCase()}));
                                return true;
                            }
                        }
                    }
                }
            } else {
                if (PlayerSkills.useHolograms) {
                    Location holoLoc = ((Player)sender).getLocation();
                    holoLoc.setY(holoLoc.getBlockY() + 2);
                    PlayerSkills.getFileManager().getConfig("config").set("holograms.location", LocationUtil.toString(holoLoc));
                    PlayerSkills.getFileManager().getConfig("config").save();

                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        PlayerSkills.getHologramManager().clearUpdate(pl);
                    }

                    sender.sendMessage(messages.getMessage("hologram_set", new String[]{LocationUtil.toString(holoLoc)}));
                } else {
                    sender.sendMessage(messages.getMessage("hologram_disabled", new String[0]));
                }

                return true;
            }
        }
    }
}
