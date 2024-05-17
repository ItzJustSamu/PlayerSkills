package me.itzjustsamu.playerskills.menu;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
public class Sounds {

    private static final boolean IS_NEW_VERSION = isVersionAbove113();

    private static boolean isVersionAbove113() {
        String version = Bukkit.getServer().getVersion();
        return version.contains("1.13") || version.contains("1.14") || version.contains("1.15") || version.contains("1.16") ||
                version.contains("1.17") || version.contains("1.18") || version.contains("1.19") || version.contains("1.20");
    }

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
