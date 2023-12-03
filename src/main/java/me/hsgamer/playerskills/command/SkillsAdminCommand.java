package me.hsgamer.playerskills.command;

import me.hsgamer.playerskills.Permissions;
import me.hsgamer.playerskills.PlayerSkills;
import me.hsgamer.playerskills.menu.SkillsSettings;
import me.hsgamer.playerskills.player.SPlayer;
import me.hsgamer.playerskills.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

public class SkillsAdminCommand extends Command {

    private final PlayerSkills plugin;

    public SkillsAdminCommand(PlayerSkills plugin) {
        super("skillsadmin", "Change players skill points", "/skillsadmin", Arrays.asList("sa", "skillsadmin", "skilladmin"));
        setPermission(Permissions.ADMIN.getName());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length >= 4) {
            if (args[0].equalsIgnoreCase("setskill")) {
                setSkill(sender, args);
                return true;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("givepoints")) {
                givePoints(sender, args);
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("view")) {
                viewSkills(sender, args);
                return true;
            } else if (args[0].equalsIgnoreCase("fullreset")) {
                fullReset(sender, args);
                return true;
            }
        } else if (args.length == 1)  {
            if (args[0].equalsIgnoreCase("settings")) {
                openSettingsMenu(sender);
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "/skillsadmin view <player>");
        sender.sendMessage(ChatColor.RED + "/skillsadmin givepoints <player> [-]<points>");
        sender.sendMessage(ChatColor.RED + "/skillsadmin setskill <player> <skill name> <level>");
        sender.sendMessage(ChatColor.RED + "/skillsadmin fullreset <player>");
        sender.sendMessage(ChatColor.RED + "/skillsadmin settings");
        return true;
    }

    private void setSkill(CommandSender sender, String[] args) {
        SPlayer sPlayer;
        if ((sPlayer = getPlayer(sender, args[1])) == null) return;
        String skillName = args[2];

        int level;
        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Not a number.");
            return;
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
            return;
        }

        sPlayer.setLevel(s.getConfigName(), level);
        sender.sendMessage(ChatColor.GREEN + "Skill level for " + s.getName() + " updated to " + s.getLevel(sPlayer) + ".");
    }

    private void givePoints(CommandSender sender, String[] args) {
        SPlayer sPlayer;
        if ((sPlayer = getPlayer(sender, args[1])) == null) return;

        int points;
        try {
            points = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Not a number.");
            return;
        }

        sPlayer.setPoints(sPlayer.getPoints() + points);

        sender.sendMessage(ChatColor.GREEN + "Points updated to " + sPlayer.getPoints() + ".");
    }

    private void viewSkills(CommandSender sender, String[] args) {
        SPlayer sPlayer;
        if ((sPlayer = getPlayer(sender, args[1])) == null) return;

        sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Skills");
        for (Skill skill : plugin.getSkillRegistrar().values()) {
            sender.sendMessage(ChatColor.RED + skill.getName() + ": " + ChatColor.GRAY + skill.getLevel(sPlayer));
        }
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Points: " + ChatColor.GRAY + sPlayer.getPoints());
    }

    private void fullReset(CommandSender sender, String[] args) {
        SPlayer sPlayer;
        if ((sPlayer = getPlayer(sender, args[1])) == null) return;

        sPlayer.setPoints(0);
        Set<String> skills = sPlayer.getSkills().keySet();
        for (String skill : skills) {
            sPlayer.setLevel(skill, 0);
        }
        sender.sendMessage(ChatColor.GREEN + "Skill data for (" + ChatColor.GRAY + sPlayer.getPlayer() + ChatColor.GREEN + ") " +
                "has been reset.");
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

    private void openSettingsMenu(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            SPlayer sPlayer = SPlayer.get(player.getUniqueId());
            SkillsSettings menu = new SkillsSettings(plugin, player, sPlayer);
            menu.open(player);
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player");
        }
    }
}