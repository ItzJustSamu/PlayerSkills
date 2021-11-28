package com.leonardobishop.playerskills2.command;

import com.leonardobishop.playerskills2.Permissions;
import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.MainConfig;
import com.leonardobishop.playerskills2.config.MessageConfig;
import com.leonardobishop.playerskills2.menu.SkillsMenu;
import com.leonardobishop.playerskills2.player.SPlayer;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
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

        List<String> listOfWorlds = MainConfig.OPTIONS_MENU_WORLD_RESTRICTION.getValue();
        if (!listOfWorlds.isEmpty() && !listOfWorlds.contains(player.getWorld().getName())) {
            MessageUtils.sendMessage(player, MessageConfig.MENU_WORLD_RESTRICTION.getValue());
            return true;
        }

        new SkillsMenu(plugin, player, sPlayer).open(player);
        return true;
    }

}
