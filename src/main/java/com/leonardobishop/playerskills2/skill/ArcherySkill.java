package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.config.SkillNumberConfigValue;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ArcherySkill extends Skill {
    private final SkillNumberConfigValue damageIncrement = new SkillNumberConfigValue(this, "damage-increment", 6);

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
        percentile = percentile * damageIncrement.getDouble();
        double weightedDamage = archeryLevel * percentile;
        event.setDamage(event.getDamage() + weightedDamage);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int archeryLevel = getLevel(player);
        double damage = 100 + (archeryLevel * damageIncrement.getDouble());
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int archeryLevel = getLevel(player) + 1;
        double damage = 100 + (archeryLevel * damageIncrement.getDouble());
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
