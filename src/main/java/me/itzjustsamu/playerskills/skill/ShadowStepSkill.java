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
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.Random;

import static me.itzjustsamu.playerskills.skill.SkillEffect.playSound;
import static me.itzjustsamu.playerskills.util.Utils.getPercentageFormat;

public class ShadowStepSkill extends Skill {
    private final ConfigPath<Double> TELEPORT_BASE_CHANCE = Paths.doublePath("Teleport-base-chance", 20D);
    private final ConfigPath<Double> TELEPORT_CHANCE_INCREMENT = Paths.doublePath("Teleport-chance-increment", 5D);

    private final Random random = new Random();

    public ShadowStepSkill(PlayerSkills plugin) {
        super(plugin, "ShadowStep", "shadowstep", 15, 29);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player target = (Player) event.getEntity();
        Player player = (Player) event.getDamager();

        if (isWorldNotAllowed(player)) {
            return;
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        if (getLevel(sPlayer) > 0) {
            playSound(player);

            // Check if teleportation should occur
            if (shouldTeleport(sPlayer)) {
                // Teleport behind the target
                teleportBehindTarget(player, target);
            }
        }
    }

    private boolean shouldTeleport(SPlayer sPlayer) {
        double baseChance = TELEPORT_BASE_CHANCE.getValue();
        double increment = TELEPORT_CHANCE_INCREMENT.getValue();
        double chance = baseChance + (getLevel(sPlayer) - 1) * increment;

        return random.nextDouble() * 100 < chance;
    }

    private void teleportBehindTarget(Player player, Player target) {
        // Implement teleportation logic here
        player.teleport(target.getLocation().add(target.getLocation().getDirection().multiply(-1)));
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return List.of(TELEPORT_BASE_CHANCE, TELEPORT_CHANCE_INCREMENT);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cShadow Step"))
                .addItemModifier(new XMaterialModifier(XMaterial.NETHER_STAR))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases the chance to teleport behind the target in combat.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cTeleport Chance: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double baseChance = TELEPORT_BASE_CHANCE.getValue();
        double increment = TELEPORT_CHANCE_INCREMENT.getValue();
        double chance = baseChance + (getLevel(player) - 1) * increment;
        return getPercentageFormat().format(chance);
    }

    @Override
    public String getNextString(SPlayer player) {
        int playerLevel = getLevel(player) + 1;
        double baseChance = TELEPORT_BASE_CHANCE.getValue();
        double increment = TELEPORT_CHANCE_INCREMENT.getValue();
        double chance = baseChance + (playerLevel - 1) * increment;
        return getPercentageFormat().format(chance);
    }
}
