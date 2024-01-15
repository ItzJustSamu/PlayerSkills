package me.itzjustsamu.playerskills.menu;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Sounds {

    static void playExperienceOrbPickupSound(Player player) {
        Sound experienceOrbPickupSound = XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound();
        if (experienceOrbPickupSound != null) {
            player.playSound(player.getLocation(), experienceOrbPickupSound, 0.8F, 1.0F);
        }
    }

    static void playUIButtonClickSound(Player player) {
        Sound uiButtonClickSound = XSound.UI_BUTTON_CLICK.parseSound();
        if (uiButtonClickSound != null) {
            player.playSound(player.getLocation(), uiButtonClickSound, 0.8F, 1.0F);
        }
    }

    static void playGenericExplodeSound(Player player) {
        Sound genericExplodeSound = XSound.ENTITY_GENERIC_EXPLODE.parseSound();
        if (genericExplodeSound != null) {
            player.playSound(player.getLocation(), genericExplodeSound, 0.8F, 1.0F);
        }
    }

    static void playItemBreakSound(Player player) {
        Sound itemBreakSound = XSound.ENTITY_ITEM_BREAK.parseSound();
        if (itemBreakSound != null) {
            player.playSound(player.getLocation(), itemBreakSound, 0.5F, 0.6F);
        }
    }
}
