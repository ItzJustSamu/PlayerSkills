package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.CreatorConfigValue;
import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Arrays;
import java.util.List;

public class GluttonySkill extends Skill {

    public GluttonySkill(PlayerSkills plugin) {
        super(plugin, "Gluttony", "gluttony");

        super.getCreatorConfigValues().add(new CreatorConfigValue("max-level", 4, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("gui-slot", 21, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("percent-increase", 50, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("only-in-worlds", Arrays.asList("world", "world_nether", "world_the_end")));
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (this.getConfig().containsKey("only-in-worlds")) {
            List<String> listOfWorlds = (List<String>) this.getConfig().get("only-in-worlds");
            if (!listOfWorlds.contains(player.getLocation().getWorld().getName())) {
                return;
            }
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
        int gluttonyLevel = sPlayer.getLevel(this.getConfigName());
        // java.lang.Integer cannot be cast to java.lang.Double
        // my fucking ass
        double multiplier = 1D + ((gluttonyLevel) * (super.getDecimalNumber("percent-increase") / 100D));

        double newLevel = diff * multiplier;
        player.setFoodLevel(player.getFoodLevel() + (int) newLevel);
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int gluttonyLevel = player.getLevel(this.getConfigName());
        double heal = 100 + (gluttonyLevel * super.getDecimalNumber("percent-increase"));
        return getPlugin().getPercentageFormat().format(heal) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int gluttonyLevel = player.getLevel(this.getConfigName()) + 1;
        double heal = 100 + (gluttonyLevel * super.getDecimalNumber("percent-increase"));
        return getPlugin().getPercentageFormat().format(heal) + "%";
    }
}
