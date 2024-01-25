package me.itzjustsamu.playerskills.util;

import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.skill.Skill;

public final class CommonStringReplacer {
    public static final StringReplacer COLORIZE = ColorUtils::colorize;
    private static Skill skill;
    public static final StringReplacer PLAYER_PROPERTIES = StringReplacer.of((original, uuid) -> {
        SPlayer sPlayer = SPlayer.get(uuid);
        int skillsIncrement = (skill != null) ? skill.getIncrement().getValue() : 0;
        int skillPrice = (skill != null) ? skill.getPrice().getValue() : 0;
        int price = sPlayer.getPointPrice();
        return original.replace("{player-price}", Integer.toString(price))
                .replace("{symbol}", MainConfig.POINTS_FUNDING_SOURCE.getValue().getSymbol(price))
                .replace("{player-points}", Integer.toString(sPlayer.getPoints()))
                .replace("{reset-points}", Integer.toString(MainConfig.POINTS_RESET_PRICE.getValue()))
                .replace("{refund-status}", Boolean.toString(MainConfig.POINTS_REFUND_POINTS.getValue()))
                .replace("{points-price}", Integer.toString(MainConfig.POINTS_PRICE.getValue()))
                .replace("{skills-upgrade-price}", Integer.toString(skillsIncrement))
                .replace("{points-increment-price}", Integer.toString(MainConfig.POINTS_INCREMENT_PRICE.getValue()))
                .replace("{skill}", (skill != null) ? skill.getSkillsConfigName() : "")
                .replace("{skill-point-price}", Integer.toString(skillPrice));
    });

    private CommonStringReplacer() {
        // EMPTY
    }

    public static void setSkill(Skill skill) {
        CommonStringReplacer.skill = skill;
    }

    public static void resetSkill() {
        // Restore previous value before skill is set
    }

}
