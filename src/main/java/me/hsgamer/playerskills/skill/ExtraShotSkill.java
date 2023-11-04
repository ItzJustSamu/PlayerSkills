package me.hsgamer.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.playerskills.PlayerSkills;
import me.hsgamer.playerskills.config.MainConfig;
import me.hsgamer.playerskills.player.SPlayer;
import me.hsgamer.playerskills.util.Utils;
import me.hsgamer.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static me.hsgamer.playerskills.util.Utils.getPercentageFormat;

public class ExtraShotSkill extends Skill {

    private final ConfigPath<Double> arrowIncrement = Paths.doublePath("arrow-increment", 1.0);

    public ExtraShotSkill(PlayerSkills plugin) {
        super(plugin, "ExtraShot", "extrashot", 10, 10);
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
            long delayBetweenShots = 4L; // Adjust this value to control the delay between shots (in ticks)

            for (int i = 0; i < numArrows; i++) {
                int finalI = i;
                scheduler.runTaskLater(getPlugin(), () -> {
                    AbstractArrow arrow = (AbstractArrow) player.launchProjectile(AbstractArrow.class, player.getLocation().getDirection());

                    // Adjust arrow speed and distance here
                    double speedMultiplier = 2.0; // Adjust the speed multiplier as needed
                    arrow.setVelocity(arrow.getVelocity().multiply(speedMultiplier));

                    // Make the arrow not pick-uppable (Paper-specific)
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

                    if (finalI == numArrows - 1) {
                        // This is the last arrow, perform any final actions here
                    }
                }, i * delayBetweenShots);
            }
        }
    }





    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(arrowIncrement);
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
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int arrowLevel = getLevel(player);
        double arrows = 1.0 + (arrowLevel * arrowIncrement.getValue());
        return getPercentageFormat().format(arrows);
    }

    @Override
    public String getNextString(SPlayer player) {
        int arrowLevel = getLevel(player) + 1;
        double arrows = 1.0 + (arrowLevel * arrowIncrement.getValue());
        return getPercentageFormat().format(arrows);
    }
}
