package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.config.SkillNumberConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class StrengthSkill extends Skill {
    private final SkillNumberConfigValue damageIncrement = new SkillNumberConfigValue(this, "damage-increment", 6);

    public StrengthSkill(PlayerSkills plugin) {
        super(plugin, "Strength", "strength", 10, 11);
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

        int strengthLevel = getLevel(sPlayer);

        double percentile = event.getDamage() / 100;
        percentile = percentile * damageIncrement.getDouble();
        double weightedDamage = strengthLevel * percentile;
        event.setDamage(event.getDamage() + weightedDamage);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int strengthLevel = getLevel(player);
        double damage = 100 + (strengthLevel * damageIncrement.getDouble());
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int strengthLevel = getLevel(player) + 1;
        double damage = 100 + (strengthLevel * damageIncrement.getDouble());
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
