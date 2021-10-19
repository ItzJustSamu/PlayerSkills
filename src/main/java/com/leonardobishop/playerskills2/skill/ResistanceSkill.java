package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.config.SkillNumberConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class ResistanceSkill extends Skill {
    private final SkillNumberConfigValue damageDrop = new SkillNumberConfigValue(this, "damage-drop", 3);

    public ResistanceSkill(PlayerSkills plugin) {
        super(plugin, "Resistance", "resistance", 10, 12);
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
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

        int resistanceLevel = getLevel(sPlayer);

        double percentile = event.getDamage() / 100;
        percentile = percentile * damageDrop.getDouble();
        double weightedDamage = resistanceLevel * percentile;
        event.setDamage(event.getDamage() - weightedDamage);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int resistanceLevel = getLevel(player);
        double damage = 100 - (resistanceLevel * damageDrop.getDouble());
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int resistanceLevel = getLevel(player) + 1;
        double damage = 100 - (resistanceLevel * damageDrop.getDouble());
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
