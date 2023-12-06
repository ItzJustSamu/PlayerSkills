package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.player.SPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ExtraShotSkill extends Skill {

    private final ConfigPath<Double> ARROW_INCREMENT = Paths.doublePath("arrow-increment", 1.0);

    public ExtraShotSkill(PlayerSkills plugin) {
        super(plugin, "ExtraShot", "extrashot", 10, 14);
    }

    @EventHandler
    public void onBowFire(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        UUID uniqueId = player.getUniqueId();
        SPlayer sPlayer = SPlayer.get(uniqueId);

        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logError("Failed event. SPlayer for " + uniqueId + " is null.");
            }
            return;
        }

        int arrowLevel = getLevel(sPlayer);

        if (arrowLevel > 0) {
            int numArrows = 1 + arrowLevel; // Increase the number of arrows based on arrowLevel

            BukkitScheduler scheduler = Bukkit.getScheduler();
            long delayBetweenShots = 3L; // Adjust this value to control the delay between shots (in ticks)

            for (int i = 0; i < numArrows; i++) {
                int finalI = i;
                scheduler.runTaskLater(getPlugin(), () -> {
                    Arrow arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection());

                    // Adjust arrow speed and distance here
                    double speedMultiplier = 1.5; // Adjust the speed multiplier as needed
                    arrow.setVelocity(arrow.getVelocity().multiply(speedMultiplier));

                    // Deny arrow pickup (Paper-specific)
                    arrow.setPickupStatus(Arrow.PickupStatus.CREATIVE_ONLY);

                    if (finalI == numArrows - 1) {
                        // This is the last arrow, perform any final actions here
                    }
                }, i * delayBetweenShots);
            }
        }
    }





    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(ARROW_INCREMENT);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cArrows Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.ARROW))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases the amount of arrows shot.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cArrow amount: ",
                        "   &e{prev}x&7 >>> &e{next}x"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int arrowLevel = getLevel(player);
        double arrows = arrowLevel * ARROW_INCREMENT.getValue();
        return Utils.getPercentageFormat().format(arrows);
    }

    @Override
    public String getNextString(SPlayer player) {
        int arrowLevel = getLevel(player) + 1;
        double arrows = arrowLevel * ARROW_INCREMENT.getValue();
        return Utils.getPercentageFormat().format(arrows);
    }
}
