package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.CreatorConfigValue;
import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class ResistanceSkill extends Skill {

    public ResistanceSkill(PlayerSkills plugin) {
        super(plugin, "Resistance", "resistance");

        super.getCreatorConfigValues().add(new CreatorConfigValue("max-level", 10, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("gui-slot", 12, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("damage-drop", 3, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("only-in-worlds", Arrays.asList("world", "world_nether", "world_the_end")));
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

        int resistanceLevel = sPlayer.getLevel(this.getConfigName());

        double percentile = event.getDamage() / 100;
        percentile = percentile * super.getDecimalNumber("damage-drop");
        double weightedDamage = resistanceLevel * percentile;
        event.setDamage(event.getDamage() - weightedDamage);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int resistanceLevel = player.getLevel(this.getConfigName());
        double damage = 100 - (resistanceLevel * super.getDecimalNumber("damage-drop"));
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int resistanceLevel = player.getLevel(this.getConfigName()) + 1;
        double damage = 100 - (resistanceLevel * super.getDecimalNumber("damage-drop"));
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
