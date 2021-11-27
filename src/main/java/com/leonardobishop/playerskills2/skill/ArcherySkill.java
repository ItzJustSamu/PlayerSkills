package com.leonardobishop.playerskills2.skill;

import com.cryptomorin.xseries.XMaterial;
import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.util.modifier.XMaterialModifier;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.path.Paths;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collections;
import java.util.List;

import static com.leonardobishop.playerskills2.util.Utils.getPercentageFormat;

public class ArcherySkill extends Skill {
    private final ConfigPath<Double> damageIncrement = Paths.doublePath("damage-increment", 6D);

    public ArcherySkill(PlayerSkills plugin) {
        super(plugin, "Archery", "archery", 10, 15);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player) {
                player = (Player) arrow.getShooter();
            }
        }

        if (player == null) {
            return;
        }

        if (isWorldNotAllowed(player)) {
            return;
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (super.getPlugin().isVerboseLogging()) {
                super.getPlugin().logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int archeryLevel = getLevel(sPlayer);

        double percentile = event.getDamage() / 100;
        percentile = percentile * damageIncrement.getValue();
        double weightedDamage = archeryLevel * percentile;
        event.setDamage(event.getDamage() + weightedDamage);
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.singletonList(damageIncrement);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cArchery Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.BOW))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill increases damage dealt using bows.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cBow damage dealt: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int archeryLevel = getLevel(player);
        double damage = 100 + (archeryLevel * damageIncrement.getValue());
        return getPercentageFormat().format(damage);
    }

    @Override
    public String getNextString(SPlayer player) {
        int archeryLevel = getLevel(player) + 1;
        double damage = 100 + (archeryLevel * damageIncrement.getValue());
        return getPercentageFormat().format(damage);
    }
}
