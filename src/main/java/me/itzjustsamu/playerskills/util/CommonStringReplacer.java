package me.itzjustsamu.playerskills.util;

import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.skill.Skill;

public final class CommonStringReplacer {
    private static Skill skill;


    public static final StringReplacer COLORIZE = (original, uuid) -> ColorUtils.colorize(original);
    public static final StringReplacer PLAYER_PROPERTIES = (original, uuid) -> {
        SPlayer sPlayer = SPlayer.get(uuid);
        int price = sPlayer.getNextPointPrice();
        return original.replace("{price}", Integer.toString(price))
                .replace("{symbol}", MainConfig.POINTS_FUNDING_SOURCE.getValue().getSymbol(price))
                .replace("{points}", Integer.toString(sPlayer.getPoints()))
                .replace("{reset-points}", Integer.toString(MainConfig.POINTS_RESET_PRICE.getValue()))
                .replace("{refund-status}", Boolean.toString(MainConfig.POINTS_REFUND_POINTS.getValue()))
                .replace("{incremented-price}", Integer.toString(MainConfig.POINTS_INCREMENT_PRICE.getValue()))
                .replace("{incremented-skill-price}", Integer.toString(skill.getIncrement()));
    };


    private CommonStringReplacer() {
        // EMPTY
    }
}
