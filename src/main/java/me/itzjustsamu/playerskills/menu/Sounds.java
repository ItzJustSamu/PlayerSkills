package me.itzjustsamu.playerskills.menu;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;

public class Sounds {

    static void playExperienceOrbPickupSound(Player player) {
        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.record()
                .withVolume(0.8F)
                .withPitch(1.0F)
                .soundPlayer()
                .atLocation(player.getLocation())
                .play();
    }

    static void playUIButtonClickSound(Player player) {
        XSound.UI_BUTTON_CLICK.record()
                .withVolume(0.8F)
                .withPitch(1.0F)
                .soundPlayer()
                .atLocation(player.getLocation())
                .play();
    }

    static void playGenericExplodeSound(Player player) {
        XSound.ENTITY_GENERIC_EXPLODE.record()
                .withVolume(0.8F)
                .withPitch(1.0F)
                .soundPlayer()
                .atLocation(player.getLocation())
                .play();
    }

    static void playItemBreakSound(Player player) {
        XSound.ENTITY_ITEM_BREAK.record()
                .withVolume(0.5F)
                .withPitch(0.6F)
                .soundPlayer()
                .atLocation(player.getLocation())
                .play();
    }
}
