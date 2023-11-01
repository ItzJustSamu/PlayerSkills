package me.hsgamer.playerskills2.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.playerskills2.PlayerSkills;
import me.hsgamer.playerskills2.config.MainConfig;
import me.hsgamer.playerskills2.player.SPlayer;
import me.hsgamer.playerskills2.util.Utils;
import me.hsgamer.playerskills2.util.modifier.XMaterialModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static me.hsgamer.playerskills2.util.Utils.getPercentageFormat;

public class LeapSkill extends Skill {

    private final ConfigPath<Integer> leapIncrement = Paths.integerPath("leap-increment", 1);

    public LeapSkill(PlayerSkills plugin) {
        super(plugin, "Leap", "leap", 1, 18);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (isWorldNotAllowed(player)) {
            return;
        }

        UUID uniqueId = player.getUniqueId();
        SPlayer sPlayer = SPlayer.get(uniqueId);

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + uniqueId + " is null.");
            }
            return;
        }

        int leapLevel = getLevel(sPlayer);
        int leapAmplifier = this.leapIncrement.getValue();

        if (leapLevel == 0) {
            // If the player has no leap level, remove any existing leap effect
            player.removePotionEffect(PotionEffectType.JUMP);
        } else {
            // Apply the leap effect based on the leap level and amplifier
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, leapAmplifier), true);
        }
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(this.leapIncrement);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cLeap Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.DIAMOND_BOOTS))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill gives you a leap effect for enhanced jumping.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cLeap Amplifier: &e{leap-amplifier}"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int leapLevel = getLevel(player);
        double leap = 1.0 + (leapLevel * leapIncrement.getValue());
        return getPercentageFormat().format(leap);
    }

    @Override
    public String getNextString(SPlayer player) {
        int swiftLevel=getLevel(player)+1;
        double leap = 1.0 + (swiftLevel * leapIncrement.getValue());
        return getPercentageFormat().format(leap);
    }
}
