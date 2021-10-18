package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.CreatorConfigValue;
import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;

public class StrengthSkill extends Skill {

    public StrengthSkill(PlayerSkills plugin) {
        super(plugin, "Strength", "strength");

        super.getCreatorConfigValues().add(new CreatorConfigValue("max-level", 10, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("gui-slot", 11, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("damage-increment", 6, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("only-in-worlds", Arrays.asList("world", "world_nether", "world_the_end")));
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

        int strengthLevel = sPlayer.getLevel(this.getConfigName());

        double percentile = event.getDamage() / 100;
        percentile = percentile * super.getDecimalNumber("damage-increment");
        double weightedDamage = strengthLevel * percentile;
        event.setDamage(event.getDamage() + weightedDamage);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int strengthLevel = player.getLevel(this.getConfigName());
        double damage = 100 + (strengthLevel * super.getDecimalNumber("damage-increment"));
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int strengthLevel = player.getLevel(this.getConfigName()) + 1;
        double damage = 100 + (strengthLevel * super.getDecimalNumber("damage-increment"));
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
