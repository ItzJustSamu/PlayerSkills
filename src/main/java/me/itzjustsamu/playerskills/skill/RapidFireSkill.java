package me.itzjustsamu.playerskills.skill;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RapidFireSkill extends Skill {

    public RapidFireSkill(PlayerSkills plugin) {
        super(plugin, "Rapidfire", "rapidfire", 10, 15);
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

        createArrows(player, sPlayer);
    }

    private void createArrows(Player player, SPlayer sPlayer) {
        int numArrows = 1 + getLevel(sPlayer); // Increase the number of arrows based on arrowLevel
        long delayBetweenShots = 1L; // Default delay between shots

        BukkitScheduler scheduler = Bukkit.getScheduler();
        for (int i = 0; i < numArrows; i++) {
            scheduler.runTaskLater(getPlugin(), () -> {
                Arrow arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection());

                // Adjust arrow speed and distance here
                double speedMultiplier = 1.5; // Adjust the speed multiplier as needed
                arrow.setVelocity(arrow.getVelocity().multiply(speedMultiplier));

                // Make the arrow disappear after being shot
                scheduler.runTaskLater(getPlugin(), arrow::remove, 1L);

                // This is the last arrow, perform any final actions here
            }, i * delayBetweenShots);
        }

        // Drop a single arrow after all arrows have been shot
        scheduler.runTaskLater(getPlugin(), () -> player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Objects.requireNonNull(XMaterial.ARROW.parseItem()))), numArrows * delayBetweenShots);
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(getUpgrade());
    }

    @Override
    public ItemBuilder<ItemStack> getDefaultItem() {
        return new BukkitItemBuilder()
                .addItemModifier(new NameModifier().setName("&cRapidFire Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.ARROW))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{price} &7point(s).",
                        "&7This skill increases the amount of arrows shot.",
                        "&7Level: &e{level}&7/&e{limit}&7",
                        " ",
                        "&cRapidFire amount: ",
                        "   &e{prev}x&7 >>> &e{next}x"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        double arrows = getLevel(player) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(arrows);
    }

    @Override
    public String getNextString(SPlayer player) {
        double arrows = (getLevel(player) + 1) * getUpgrade().getValue();
        return Utils.getPercentageFormat().format(arrows);
    }
}
