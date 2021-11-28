package com.leonardobishop.playerskills2.util;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommonStringReplacer {
    public static final StringReplacer COLORIZE = (original, uuid) -> MessageUtils.colorize(original);
    public static final StringReplacer PLAYER_PROPERTIES = (original, uuid) -> {
        PlayerSkills plugin = JavaPlugin.getPlugin(PlayerSkills.class);
        SPlayer sPlayer = SPlayer.get(uuid);
        int price = sPlayer.getNextPointPrice();
        return original.replace("{price}", Integer.toString(price))
                .replace("{symbol}", plugin.getFundingSource().getSymbol(price))
                .replace("{points}", Integer.toString(sPlayer.getPoints()));
    };

    private CommonStringReplacer() {
        // EMPTY
    }
}
