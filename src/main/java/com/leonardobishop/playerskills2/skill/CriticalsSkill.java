package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.config.SkillNumberConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CriticalsSkill extends Skill {
    private final SkillNumberConfigValue percentIncrease = new SkillNumberConfigValue(this, "percent-increase", 4);
    private final SkillNumberConfigValue criticalMultiplier = new SkillNumberConfigValue(this, "critical-multiplier", 1.5);

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

        double chance = criticalLevel * percentIncrease.getDouble();

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            if (!Config.get(super.getPlugin(), "messages.critical").getColoredString().equals("")) {
                player.sendMessage(Config.get(super.getPlugin(), "messages.critical").getColoredString());
            }
            event.setDamage(event.getDamage() * criticalMultiplier.getDouble());
        }
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int criticalLevel = getLevel(player);
        double damage = criticalLevel * percentIncrease.getDouble();
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int criticalLevel = getLevel(player) + 1;
        double damage = criticalLevel * percentIncrease.getDouble();
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
