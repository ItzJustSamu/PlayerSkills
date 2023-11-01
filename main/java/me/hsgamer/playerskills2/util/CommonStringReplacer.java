package me.hsgamer.playerskills2.util;

import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import me.hsgamer.playerskills2.config.MainConfig;
import me.hsgamer.playerskills2.player.SPlayer;

public final class CommonStringReplacer {
    public static final StringReplacer COLORIZE = (original, uuid) -> ColorUtils.colorize(original);
    public static final StringReplacer PLAYER_PROPERTIES = (original, uuid) -> {
        SPlayer sPlayer = SPlayer.get(uuid);
        int price = sPlayer.getNextPointPrice();
        return original.replace("{price}", Integer.toString(price))
                .replace("{symbol}", MainConfig.POINTS_FUNDING_SOURCE.getValue().getSymbol(price))
                .replace("{points}", Integer.toString(sPlayer.getPoints()))
                .replace("{reset-points}", Integer.toString(MainConfig.POINTS_RESET_PRICE.getValue()));
    };

    private CommonStringReplacer() {
        // EMPTY
    }
}
