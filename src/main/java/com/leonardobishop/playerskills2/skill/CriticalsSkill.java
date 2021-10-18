package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.config.CreatorConfigValue;
import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class CriticalsSkill extends Skill {

    public CriticalsSkill(PlayerSkills plugin) {
        super(plugin, "Criticals", "criticals");

        super.getCreatorConfigValues().add(new CreatorConfigValue("max-level", 3, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("gui-slot", 14, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("percent-increase", 4, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("critical-multiplier", 1.5, true));
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

        int criticalLevel = sPlayer.getLevel(this.getConfigName());

        double chance = criticalLevel * super.getDecimalNumber("percent-increase");

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            if (!Config.get(super.getPlugin(), "messages.critical").getColoredString().equals("")) {
                player.sendMessage(Config.get(super.getPlugin(), "messages.critical").getColoredString());
            }
            event.setDamage(event.getDamage() * (double) super.getConfig().get("critical-multiplier"));
        }
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int criticalLevel = player.getLevel(this.getConfigName());
        double damage = criticalLevel * super.getDecimalNumber("percent-increase");
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int criticalLevel = player.getLevel(this.getConfigName()) + 1;
        double damage = criticalLevel * super.getDecimalNumber("percent-increase");
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
