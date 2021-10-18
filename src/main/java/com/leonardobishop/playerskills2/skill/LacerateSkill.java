package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.config.CreatorConfigValue;
import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LacerateSkill extends Skill {

    private final HashMap<LivingEntity, BukkitTask> cutEntities = new HashMap<>();

    public LacerateSkill(PlayerSkills plugin) {
        super(plugin, "Lacerate", "lacerate");

        super.getCreatorConfigValues().add(new CreatorConfigValue("max-level", 4, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("gui-slot", 23, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("percent-increase", 4, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("bleed-cycles", 8, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("bleed-interval", 50, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("bleed-damage", 2, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("apply-to-non-players", false, false));
        super.getCreatorConfigValues().add(new CreatorConfigValue("only-in-worlds", Arrays.asList("world", "world_nether", "world_the_end")));
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || (!(event.getEntity() instanceof Player)
                && (!(super.getConfig().containsKey("apply-to-non-players")) || !((boolean) super.getConfig().get("apply-to-non-players"))))) {
            return;
        }

        Player player = (Player) event.getDamager();
        if (this.getConfig().containsKey("only-in-worlds")) {
            List<String> listOfWorlds = (List<String>) this.getConfig().get("only-in-worlds");
            if (!listOfWorlds.contains(player.getLocation().getWorld().getName())) {
                return;
            }
        }

        SPlayer sPlayer = SPlayer.get(player.getUniqueId());

        if (sPlayer == null) {
            if (super.getPlugin().isVerboseLogging()) {
                super.getPlugin().logError("Failed event. SPlayer for " + player.getUniqueId() + " is null.");
            }
            return;
        }

        int lacerateLevel = sPlayer.getLevel(this.getConfigName());

        double chance = lacerateLevel * super.getDecimalNumber("percent-increase");

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            LivingEntity victim = (LivingEntity) event.getEntity();

            bleed(victim);

            if (!Config.get(super.getPlugin(), "messages.bleeding-enemy").getColoredString().equals("")) {
                player.sendMessage(Config.get(super.getPlugin(), "messages.bleeding-enemy").getColoredString());
            }
            if (!Config.get(super.getPlugin(), "messages.bleeding-self").getColoredString().equals("")) {
                victim.sendMessage(Config.get(super.getPlugin(), "messages.bleeding-self").getColoredString());
            }
        }
    }

    private void bleed(LivingEntity player) {
        if (cutEntities.containsKey(player)) {
            return;
        }
        BukkitTask bt = new BukkitRunnable() {
            int times = 0;

            @Override
            public void run() {
                player.damage((int) LacerateSkill.super.getConfig().get("bleed-damage"), null);
                times++;
                if (times >= (int) LacerateSkill.super.getConfig().get("bleed-cycles")) {
                    LacerateSkill.this.cutEntities.remove(player);
                    try {
                        this.cancel();
                    } catch (Throwable ignored) {
                        // cancelled check throws error in 1.8
                    }
                }
            }
        }.runTaskTimer(super.getPlugin(), (int) super.getConfig().get("bleed-interval"), (int) super.getConfig().get("bleed-interval"));
        cutEntities.put(player, bt);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (cutEntities.containsKey(event.getEntity())) {
            BukkitTask bt = cutEntities.get(event.getEntity());
            try {
                bt.cancel();
            } catch (Throwable ignored) {
                // cancelled check throws error in 1.8
            }
            cutEntities.remove(event.getEntity());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (cutEntities.containsKey(event.getPlayer())) {
            BukkitTask bt = cutEntities.get(event.getPlayer());
            try {
                bt.cancel();
            } catch (Throwable ignored) {
                // cancelled check throws error in 1.8
            }
            cutEntities.remove(event.getPlayer());
        }
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int lacerateLevel = player.getLevel(this.getConfigName());
        double damage = lacerateLevel * super.getDecimalNumber("percent-increase");
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int lacerateLevel = player.getLevel(this.getConfigName()) + 1;
        double damage = lacerateLevel * super.getDecimalNumber("percent-increase");
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
