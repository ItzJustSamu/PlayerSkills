package com.leonardobishop.playerskills2.commands;

import com.leonardobishop.playerskills2.Permissions;
import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.menu.SkillsMenu;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.utils.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SkillsCommand extends Command {

    private final PlayerSkills plugin;

    public SkillsCommand(PlayerSkills plugin) {
        super("skills", "Open skills menu", "/skills", Arrays.asList("s", "skills", "skill"));
        setPermission(Permissions.COMMAND.getName());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Please use /skillsadmin instead.");
            return false;
        }
        Player player = (Player) sender;
        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (!Config.get(plugin, "options.menu-world-restriction").isNull()) {
            List<String> listOfWorlds = Config.get(plugin, "options.menu-world-restriction").getStringList();
            if (!listOfWorlds.contains(player.getLocation().getWorld().getName())) {
                player.sendMessage(Config.get(plugin, "messages.menu-world-restriction").getColoredString());
                return true;
            }
        }

        new SkillsMenu(plugin, player, sPlayer).open(player);
        return true;
    }

}
