package me.hsgamer.playerskills.command;

import me.hsgamer.playerskills.Permissions;
import me.hsgamer.playerskills.PlayerSkills;
import me.hsgamer.playerskills.player.SPlayer;
import me.hsgamer.playerskills.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SkillsAdminCommand extends Command {

    private final PlayerSkills plugin;

    public SkillsAdminCommand(PlayerSkills plugin) {
        super("skillsadmin", "Change players skill points", "/skillsadmin", Arrays.asList("sa", "skillsadmin", "skilladmin"));
        setPermission(Permissions.ADMIN.getName());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadAllConfigs(sender);
            return true;
        }
         else if (args.length >= 4) {
            if (args[0].equalsIgnoreCase("setskill")) {
                SPlayer sPlayer;
                if ((sPlayer = getPlayer(sender, args[1])) == null) return true;
                String skillName = args[2];

                int level;
                try {
                    level = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Not a number.");
                    return true;
                }

                Skill s = null;
                for (Skill skill : plugin.getSkillRegistrar().values()) {
                    if (skill.getConfigName().equalsIgnoreCase(skillName)) {
                        s = skill;
                        break;
                    }
                }

                if (s == null) {
                    sender.sendMessage(ChatColor.RED + "Skill could not be found. Skill names used in configurations and commands should contain no spaces and" +
                            " should be all lower case.");
                    return true;
                }

                sPlayer.setLevel(s.getConfigName(), level);
                sender.sendMessage(ChatColor.GREEN + "Skill level for " + s.getName() + " updated to " + s.getLevel(sPlayer) + ".");
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /skillsadmin <setskill> <player> <skillname> <level> ");
            }
        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("givepoints")) {
                SPlayer sPlayer;
                if ((sPlayer = getPlayer(sender, args[1])) == null) return true;

                int points;
                try {
                    points = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Not a number.");
                    return true;
                }

                sPlayer.setPoints(sPlayer.getPoints() + points);

                sender.sendMessage(ChatColor.GREEN + "Points updated to " + sPlayer.getPoints() + ".");
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /skillsadmin <givepoints> <player> <level>");
            }
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("view")) {
                SPlayer sPlayer;
                if ((sPlayer = getPlayer(sender, args[1])) == null) return true;

                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Skills");
                for (Skill skill : plugin.getSkillRegistrar().values()) {
                    sender.sendMessage(ChatColor.RED + skill.getName() + ": " + ChatColor.GRAY + skill.getLevel(sPlayer));
                }
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Points: " + ChatColor.GRAY + sPlayer.getPoints());
            } else if (args[0].equalsIgnoreCase("fullreset")) {
                SPlayer sPlayer;
                if ((sPlayer = getPlayer(sender, args[1])) == null) return true;

                sPlayer.setPoints(0);
                Set<String> skills = sPlayer.getSkills().keySet();
                for (String skill : skills) {
                    sPlayer.setLevel(skill, 0);
                }
                sender.sendMessage(ChatColor.GREEN + "Skill data for (" + ChatColor.GRAY + sPlayer.getPlayer() + ChatColor.GREEN + ") " +
                        "has been reset.");
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /skillsadmin <fullreset> <player>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "========================");
            sender.sendMessage(ChatColor.RED + "Usage:");
            sender.sendMessage(ChatColor.RED + "/skillsadmin <setskill> <player> <skillname> <level>");
            sender.sendMessage(ChatColor.RED + "/skillsadmin <givepoints> <player> <level>");
            sender.sendMessage(ChatColor.RED + "/skillsadmin <fullreset> <player>");
            sender.sendMessage(ChatColor.RED + "/skillsadmin <reload>");
            sender.sendMessage(ChatColor.RED + "=========================");




        }

        return true;
    }

    private void reloadAllConfigs(CommandSender sender) {
        reloadConfig(sender, "config.yml");
        reloadConfig(sender, "messages.yml");
        for (Skill skill : plugin.getSkillRegistrar().values()) {
            skill.reload();
        }
    }

    private void reloadConfig(CommandSender sender, String fileName) {
        File configFile = new File(this.plugin.getDataFolder(), fileName);
        try {
            plugin.getConfig().load(configFile);
            sender.sendMessage(ChatColor.GREEN + "Configuration file '" + fileName + "' reloaded!");
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Failed to reload configuration file '" + fileName + "'. Check console for errors.");
        }
    }

    @Override
    public @NotNull List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subcommands = Arrays.asList("reload", "setskill", "givepoints", "view", "fullreset");
            return filterStartingWith(args[0], subcommands);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setskill")) {
            return filterStartingWith(args[1], getPlayerNames());
        }
        return super.tabComplete(sender, alias, args);
    }

    private List<String> getPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }

    private List<String> filterStartingWith(String filter, List<String> options) {
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(filter.toLowerCase())) {
                matches.add(option);
            }
        }
        return matches;
    }

    private SPlayer getPlayer(CommandSender sender, String arg) {
        Player player = Bukkit.getPlayer(arg);
        SPlayer sPlayer = null;
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
        } else {
            sPlayer = SPlayer.get(player.getUniqueId());
            if (sPlayer == null) {
                sender.sendMessage(ChatColor.RED + "ERROR: SPlayer could not be found.");
            }
        }
        return sPlayer;
    }
}
