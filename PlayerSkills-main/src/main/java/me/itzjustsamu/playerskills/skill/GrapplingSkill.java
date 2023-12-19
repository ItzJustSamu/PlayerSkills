package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

public class GrapplingSkill extends Skill {

    private final ConfigPath<Double> grapplingStrength = Paths.doublePath("grappling-strength", 1.0);
    private final double scalingFactor = 0.3; // Adjust this factor to control the strength

    public GrapplingSkill(PlayerSkills plugin) {
        super(plugin, "Grappling", "grappling", 3, 30);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getState() != State.IN_GROUND || !isFishingRod(player) || isWorldNotAllowed(player)) {
            return;
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        // Get the player's grappling strength based on the skill level
        double originalStrength = grapplingStrength.getValue() * getLevel(sPlayer);
        double scaledStrength = originalStrength * scalingFactor;

        // Get the player's direction vector
        Vector direction = player.getLocation().getDirection();

        // Apply the grappling hook effect by setting the player's velocity
        player.setVelocity(direction.multiply(scaledStrength));
    }

    private boolean isFishingRod(Player player) {
        // Check if the player is holding the correct item (e.g., grappling hook)
        return player.getInventory().getItemInMainHand().getType() == XMaterial.FISHING_ROD.parseMaterial();
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(grapplingStrength);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cGrappling Hook"))
                .addItemModifier(new XMaterialModifier(XMaterial.FISHING_ROD))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill allows you to use a grappling hook for fast movement.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cGrappling Strength: ",
                        "   &e{prev}x &7 >>> &e{next}x"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double originalStrength = grapplingStrength.getValue() * getLevel(player);
        double scaledStrength = originalStrength * scalingFactor;
        return Utils.getPercentageFormat().format(scaledStrength);
    }

    @Override
    public String getNextString(SPlayer player) {
        double originalStrength = grapplingStrength.getValue() * (getLevel(player) + 1);
        double scaledStrength = originalStrength * scalingFactor;
        return Utils.getPercentageFormat().format(scaledStrength);
    }
}
