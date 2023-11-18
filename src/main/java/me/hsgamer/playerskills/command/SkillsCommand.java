package me.hsgamer.playerskills.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.playerskills.Permissions;
import me.hsgamer.playerskills.PlayerSkills;
import me.hsgamer.playerskills.config.MainConfig;
import me.hsgamer.playerskills.config.MessageConfig;
import me.hsgamer.playerskills.menu.SkillsMenu;
import me.hsgamer.playerskills.player.SPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SkillsCommand extends Command implements CommandExecutor {

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("playerskills.reload")) {
                plugin.reloadConfig(); // Reload the plugin's configuration
                sender.sendMessage("PlayerSkills configuration reloaded.");
                return true;
            } else {
                sender.sendMessage("You don't have permission to use this command.");
            }
        }
        return false;
    }
}
