package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

public class KnockBackSkill extends Skill {

    private final ConfigPath<Double> knockbackIncrement = Paths.doublePath("knockback-increment", 0.2);

    public KnockBackSkill(PlayerSkills plugin) {
        super(plugin, "Knockback", "knockback", 10, 13,0);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();

        if (Worlds_Restriction(player)) {
            return;
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int knockbackLevel = getLevel(sPlayer);
        double knockbackIncrementValue = knockbackIncrement.getValue();

        // Check if the values are finite
        if (!isFinite(knockbackLevel) || !isFinite(knockbackIncrementValue)) {
            // Handle the case where the values are not finite
            return;
        }

        // Get the entity being damaged
        Entity damager = event.getDamager();
        if (!(damager instanceof LivingEntity)) {
            // Check if the damager is a LivingEntity
            return;
        }

        LivingEntity entity = (LivingEntity) damager;

        // Calculate the knockback strength based on the knockback level and increment
        double knockbackStrength = knockbackLevel * knockbackIncrementValue;

        // Check if knockbackStrength is finite
        if (!isFinite(knockbackStrength)) {
            // Handle the case where knockbackStrength is not finite
            return;
        }

        // Get the player's direction vector
        Vector direction = player.getLocation().getDirection();

        // Set the entity's velocity using the direction vector and knockback strength
        entity.setVelocity(direction.multiply(knockbackStrength));
    }

    private boolean isFinite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(knockbackIncrement);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cKnockback Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.STICK))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases knockback strength.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cKnockback Increase: ",
                        "   &e{prev}x &7 >>> &e{next}x"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int knockbackLevel = getLevel(player);
        double knockback = knockbackLevel * knockbackIncrement.getValue();
        return Utils.getPercentageFormat().format(knockback);
    }

    @Override
    public String getNextString(SPlayer player) {
        int knockbackLevel = getLevel(player) + 1;
        double knockback = knockbackLevel * knockbackIncrement.getValue();
        return Utils.getPercentageFormat().format(knockback);
    }
}
