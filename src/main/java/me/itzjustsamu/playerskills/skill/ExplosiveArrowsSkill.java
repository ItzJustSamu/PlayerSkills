package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class ExplosiveArrowsSkill extends Skill {
    private final ConfigPath<Double> EXPLOSION_RADIUS = Paths.doublePath(new PathString("explosion-radius"), 3D);
    private final ConfigPath<Double> EXPLOSION_DAMAGE = Paths.doublePath(new PathString("explosion-damage"), 5D);

    public ExplosiveArrowsSkill(PlayerSkills plugin) {
        super(plugin, "ExplosiveArrows", "explosivearrows", 20, 4);
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        } else {
            event.getEntity();
        }

        Arrow arrow = (Arrow) event.getDamager();
        event.getEntity();

        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) arrow.getShooter();

        if (Worlds_Restriction(shooter)) {
            return;
        }

        SPlayer sPlayer = SPlayer.get(shooter.getUniqueId());

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + shooter.getUniqueId() + " is null.");
            }
            return;
        }

        if (getLevel(sPlayer) > 0) {
            // Apply explosive effect on the arrow
            applyExplosiveEffect(arrow);
        }
    }

    private void applyExplosiveEffect(Arrow arrow) {
        arrow.setMetadata("ExplosiveArrow", new FixedMetadataValue(getPlugin(), true));
    }

    @EventHandler
    public void onArrowHitGround(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) event.getEntity();

        if (arrow.hasMetadata("ExplosiveArrow")) {
            // Detonate the explosive arrow
            detonateExplosiveArrow(arrow);
        }
    }

    private void detonateExplosiveArrow(Arrow arrow) {
        double explosionRadius = EXPLOSION_RADIUS.getValue();
        double explosionDamage = EXPLOSION_DAMAGE.getValue();

        for (Entity entity : arrow.getNearbyEntities(explosionRadius, explosionRadius, explosionRadius)) {
            if (entity instanceof Player) {
                Player target = (Player) entity;

                // Deal explosive damage to nearby enemies
                target.damage(explosionDamage, (Entity) arrow.getShooter());
            }
        }

        // Remove the arrow
        arrow.remove();
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return List.of(EXPLOSION_RADIUS, EXPLOSION_DAMAGE);
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cExplosive Arrows"))
                .addItemModifier(new XMaterialModifier(XMaterial.SPECTRAL_ARROW))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skill-points-price} &7point(s).",
                        "&7This skill allows you to shoot explosive arrows.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cExplosion Radius: ",
                        "   &7{prev} blocks &7 >>> &7{next} blocks",
                        " ",
                        "&cExplosion Damage: ",
                        "   &7{prev} hearts &7 >>> &7{next} hearts"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double radius = EXPLOSION_RADIUS.getValue();
        double damage = EXPLOSION_DAMAGE.getValue();
        return String.format("%s blocks, %s hearts", radius, damage);
    }

    @Override
    public String getNextString(SPlayer player) {
        int playerLevel = getLevel(player) + 1;
        double radius = playerLevel * EXPLOSION_RADIUS.getValue();
        double damage = playerLevel * EXPLOSION_DAMAGE.getValue();
        return String.format("%s blocks, %s hearts", radius, damage);
    }
}
