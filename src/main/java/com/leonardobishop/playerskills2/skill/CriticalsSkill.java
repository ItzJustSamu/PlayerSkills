package com.leonardobishop.playerskills2.skill;

import com.cryptomorin.xseries.XMaterial;
import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.util.modifier.XMaterialModifier;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.path.DoubleConfigPath;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.leonardobishop.playerskills2.util.Utils.getPercentageFormat;

public class CriticalsSkill extends Skill {
    private final DoubleConfigPath percentIncrease = new DoubleConfigPath("percent-increase", 4D);
    private final DoubleConfigPath criticalMultiplier = new DoubleConfigPath("critical-multiplier", 1.5D);

    public CriticalsSkill(PlayerSkills plugin) {
        super(plugin, "Criticals", "criticals", 3, 14);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
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

        int criticalLevel = getLevel(sPlayer);

        double chance = criticalLevel * percentIncrease.getValue();

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            if (!Config.get(super.getPlugin(), "messages.critical").getColoredString().equals("")) {
                player.sendMessage(Config.get(super.getPlugin(), "messages.critical").getColoredString());
            }
            event.setDamage(event.getDamage() * criticalMultiplier.getValue());
        }
    }

    @Override
    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Arrays.asList(percentIncrease, criticalMultiplier);
    }

    @Override
    public ItemBuilder getDefaultItem() {
        return new ItemBuilder()
                .addItemModifier(new NameModifier().setName("&cCriticals Overview"))
                .addItemModifier(new XMaterialModifier(XMaterial.BLAZE_POWDER))
                .addItemModifier(new LoreModifier().setLore(
                        "&eLeft-Click &7to upgrade this skill using &e{skillprice} &7point(s).",
                        "&7This skill gives a chance to deal a critical (150%) shot.",
                        "&7Level: &e{level}&7/&e{max}&7",
                        " ",
                        "&cCritical chance: ",
                        "   &e{prev}% &7 >>> &e{next}%"
                ));
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int criticalLevel = getLevel(player);
        double damage = criticalLevel * percentIncrease.getValue();
        return getPercentageFormat().format(damage);
    }

    @Override
    public String getNextString(SPlayer player) {
        int criticalLevel = getLevel(player) + 1;
        double damage = criticalLevel * percentIncrease.getValue();
        return getPercentageFormat().format(damage);
    }
}
