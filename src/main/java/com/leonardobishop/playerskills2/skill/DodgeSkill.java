package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.config.SkillNumberConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.concurrent.ThreadLocalRandom;

public class DodgeSkill extends Skill {
    private final SkillNumberConfigValue percentIncrease = new SkillNumberConfigValue(this, "percent-increase", 2);

    public DodgeSkill(PlayerSkills plugin) {
        super(plugin, "Dodge", "dodge", 6, 13);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
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

        int dodgeLevel = getLevel(sPlayer);

        double chance = dodgeLevel * percentIncrease.getDouble();

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            if (!Config.get(super.getPlugin(), "messages.dodge").getColoredString().equals("")) {
                player.sendMessage(Config.get(super.getPlugin(), "messages.dodge").getColoredString());
            }
            event.setCancelled(true);
        }
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int dodgeLevel = getLevel(player);
        double damage = dodgeLevel * percentIncrease.getDouble();
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int dodgeLevel = getLevel(player) + 1;
        double damage = dodgeLevel * percentIncrease.getDouble();
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
