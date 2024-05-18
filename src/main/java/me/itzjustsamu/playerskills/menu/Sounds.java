package me.itzjustsamu.playerskills.menu;

import me.itzjustsamu.playerskills.util.VersionControl;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Sounds {
    private static final boolean IS_NEW_VERSION = VersionControl.isVersionAbove113();

    static void playExperienceOrbPickupSound(Player player) {
        if (IS_NEW_VERSION) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8F, 1.0F);
        } else {
            player.playSound(player.getLocation(), Sound.valueOf("ORB_PICKUP"), 0.8F, 1.0F);
        }
    }

    static void playUIButtonClickSound(Player player) {
        if (IS_NEW_VERSION) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8F, 1.0F);
        } else {
            player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 0.8F, 1.0F);
        }
    }

    static void playGenericExplodeSound(Player player) {
        if (IS_NEW_VERSION) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8F, 1.0F);
        } else {
            player.playSound(player.getLocation(), Sound.valueOf("EXPLODE"), 0.8F, 1.0F);
        }
    }

    static void playItemBreakSound(Player player) {
        if (IS_NEW_VERSION) {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 0.6F);
        } else {
            player.playSound(player.getLocation(), Sound.valueOf("ITEM_BREAK"), 0.5F, 0.6F);
        }
    }
}
