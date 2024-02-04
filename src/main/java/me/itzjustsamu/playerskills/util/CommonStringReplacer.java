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
        int skillsUpgrade = (skill != null) ? skill.getUpgrade().getValue() : 0;
        int skillPrice = (skill != null) ? skill.getPrice().getValue() : 0;
        int price = sPlayer.getPointPrice();
        int skillUpgradeIncrement = (skill != null) ? skill.getIncrementedUpgrade().getValue() : 0;
        int skillMaxLevel = (skill != null) ? skill.getMaxLevel() : 0;
        int resetPointPrice;
        if (MainConfig.RESET_PRICE.getValue() > 0) {
            int base = MainConfig.RESET_PRICE.getValue();
            int playerPoints = sPlayer.getResetCount();
            resetPointPrice = base + (playerPoints * MainConfig.INCREMENT_RESET_PRICE.getValue());
        } else {
            resetPointPrice = sPlayer.getResetCount() * MainConfig.INCREMENT_RESET_PRICE.getValue();
        }

        String nextConfirmationName = MainConfig.CONFIRMATION_PURCHASE_SKILLS_NAME.getValue();
        switch (nextConfirmationName) {
            case "Purchase Skills":
                nextConfirmationName = MainConfig.CONFIRMATION_PURCHASE_POINTS_NAME.getValue();
                break;
            case "Purchase Points":
                nextConfirmationName = MainConfig.CONFIRMATION_RESET_SKILLS_NAME.getValue();
                break;
            case "Reset Skills":
            default:
                nextConfirmationName = MainConfig.CONFIRMATION_PURCHASE_SKILLS_NAME.getValue();
                break;
        }


        String currentConfirmationName = MainConfig.CONFIRMATION_PURCHASE_SKILLS_NAME.getValue();
        boolean currentConfirmationValue;

        switch (currentConfirmationName) {
            case "Purchase Skills":
                currentConfirmationValue = MainConfig.CONFIRMATION_PURCHASE_SKILLS.getValue();
                break;
            case "Purchase Points":
                currentConfirmationValue = MainConfig.CONFIRMATION_PURCHASE_POINTS.getValue();
                break;
            case "Reset Skills":
            default:
                currentConfirmationValue = MainConfig.CONFIRMATION_RESET_SKILLS.getValue();
                break;
        }


        return original.replace("{player-price}", Integer.toString(price))
                .replace("{symbol}", MainConfig.POINTS_FUNDING_SOURCE.getValue().getSymbol(price))
                .replace("{player-points}", Integer.toString(sPlayer.getPoints()))
                .replace("{reset-price}", Integer.toString(MainConfig.RESET_PRICE.getValue()))
                .replace("{reset-increment-price}", Integer.toString(MainConfig.INCREMENT_RESET_PRICE.getValue()))
                .replace("{reset-points-price}", Integer.toString(resetPointPrice))
                .replace("{refund-status}", Boolean.toString(MainConfig.REFUND_POINTS.getValue()))
                .replace("{points-price}", Integer.toString(MainConfig.POINTS_PRICE.getValue()))
                .replace("{skills-upgrade-price}", Integer.toString(skillsUpgrade))
                .replace("{skills-increment-point-price}", Integer.toString(skillUpgradeIncrement))
                .replace("{points-increment-price}", Integer.toString(MainConfig.POINTS_INCREMENT_PRICE.getValue()))
                .replace("{skill}", (skill != null) ? skill.getSkillsConfigName() : "")
                .replace("{skill-point-price}", Integer.toString(skillPrice))
                .replace("{next-confirmation-name}", nextConfirmationName)
                .replace("{next-confirmation-value}", Boolean.toString(currentConfirmationValue))
                .replace("{skills-max-level}", Integer.toString(skillMaxLevel));
    });

    private CommonStringReplacer() {
    }

    public static void setSkill(Skill skill) {
        CommonStringReplacer.skill = skill;
    }

    public static void resetSkill() {
    }
}
