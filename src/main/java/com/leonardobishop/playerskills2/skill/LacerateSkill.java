package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.config.SkillConfigValue;
import com.leonardobishop.playerskills2.skill.config.SkillNumberConfigValue;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class LacerateSkill extends Skill {
    private final SkillNumberConfigValue percentIncrease = new SkillNumberConfigValue(this, "percent-increase", 4);
    private final SkillNumberConfigValue bleedCycles = new SkillNumberConfigValue(this, "bleed-cycles", 8);
    private final SkillNumberConfigValue bleedInterval = new SkillNumberConfigValue(this, "bleed-interval", 50);
    private final SkillNumberConfigValue bleedDamage = new SkillNumberConfigValue(this, "bleed-damage", 2);
    private final SkillConfigValue<Boolean> applyToNonPlayers = new SkillConfigValue<>(this, Boolean.class, "apply-to-non-players", false);

    private final HashMap<LivingEntity, BukkitTask> cutEntities = new HashMap<>();

    public LacerateSkill(PlayerSkills plugin) {
        super(plugin, "Lacerate", "lacerate", 4, 23);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || (!(event.getEntity() instanceof Player) && (!applyToNonPlayers.get()))) {
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

        int lacerateLevel = getLevel(sPlayer);

        double chance = lacerateLevel * percentIncrease.getDouble();

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
                player.damage(bleedDamage.getInt(), null);
                times++;
                if (times >= bleedCycles.getInt()) {
                    cutEntities.remove(player);
                    try {
                        this.cancel();
                    } catch (Exception ignored) {
                        // cancelled check throws error in 1.8
                    }
                }
            }
        }.runTaskTimer(super.getPlugin(), bleedInterval.getLong(), bleedInterval.getLong());
        cutEntities.put(player, bt);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (cutEntities.containsKey(event.getEntity())) {
            BukkitTask bt = cutEntities.get(event.getEntity());
            try {
                bt.cancel();
            } catch (Exception ignored) {
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
            } catch (Exception ignored) {
                // cancelled check throws error in 1.8
            }
            cutEntities.remove(event.getPlayer());
        }
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int lacerateLevel = getLevel(player);
        double damage = lacerateLevel * percentIncrease.getDouble();
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }

    @Override
    public String getNextString(SPlayer player) {
        int lacerateLevel = getLevel(player) + 1;
        double damage = lacerateLevel * percentIncrease.getDouble();
        return getPlugin().getPercentageFormat().format(damage) + "%";
    }
}
