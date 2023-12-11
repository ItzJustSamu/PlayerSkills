package me.itzjustsamu.playerskills.player;

import com.cryptomorin.xseries.messages.ActionBar;
import me.hsgamer.hscore.config.path.impl.BooleanConfigPath;
import me.hsgamer.hscore.config.path.impl.StringConfigPath;
import org.bukkit.entity.Player;


public class playerActionBar {
    public static final BooleanConfigPath ACTION_BAR_ENABLED = new BooleanConfigPath("actionbar.enabled", true);
    public static final StringConfigPath ACTION_BAR_DURABILITY_MESSAGE = new StringConfigPath("actionbar.durability", "&fItem Durability: &b{current}&6/&b{maximum} &6{percentage}%");

    private final Player player;

    public playerActionBar(Player player) {
        this.player = player;
    }

    public void sendActionBar(int durability, int maxDurability) {
        if (ACTION_BAR_ENABLED.getValue()) {
            String text = ACTION_BAR_DURABILITY_MESSAGE.getValue();
            int percentage = 100 * durability / maxDurability;
            text = text.replace("{current}", durability + "")
                    .replace("{maximum}", maxDurability + "")
                    .replace("{percentage}", percentage + "");
            ActionBar.sendActionBar(player, text);
        }
    }
}
