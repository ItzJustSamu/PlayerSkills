package me.itzjustsamu.playerskills.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.impl.StringConfigPath;
import org.bukkit.plugin.Plugin;

public class MessageConfig extends PathableConfig {
    public static final StringConfigPath PREFIX = new StringConfigPath("prefix", "&7[&cPlayerSkills&7]&r ");
    public static final StringConfigPath MENU_Worlds_Restrictions = new StringConfigPath("menu-world-restriction", "&cYou cannot modify your skills in your current location.");
    public static final StringConfigPath XP_SYMBOL = new StringConfigPath("xp-symbol", "XP");

    public MessageConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "messages.yml"));
    }
}
