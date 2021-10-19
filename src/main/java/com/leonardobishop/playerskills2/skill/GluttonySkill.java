package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.config.SkillNumberConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class GluttonySkill extends Skill {
    private final SkillNumberConfigValue percentIncrease = new SkillNumberConfigValue(this, "percent-increase", 50);

    public GluttonySkill(PlayerSkills plugin) {
        super(plugin, "Gluttony", "gluttony", 4, 21);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (isWorldNotAllowed(player)) {
            return;
        }

        if (player.getFoodLevel() >= event.getFoodLevel()) {
            return;
        }

        event.setCancelled(true);
        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (super.getPlugin().isVerboseLogging()) {
                super.getPlugin().logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int diff = event.getFoodLevel() - player.getFoodLevel();
        int gluttonyLevel = getLevel(sPlayer);
        double multiplier = 1D + (gluttonyLevel * (percentIncrease.getDouble() / 100D));

        double newLevel = diff * multiplier;
        player.setFoodLevel(player.getFoodLevel() + (int) newLevel);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int gluttonyLevel = getLevel(player);
        double heal = 100 + (gluttonyLevel * percentIncrease.getDouble());
        return getPlugin().getPercentageFormat().format(heal) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int gluttonyLevel = getLevel(player) + 1;
        double heal = 100 + (gluttonyLevel * percentIncrease.getDouble());
        return getPlugin().getPercentageFormat().format(heal) + "%";
    }
}
