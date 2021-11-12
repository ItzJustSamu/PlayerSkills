package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.config.SkillConfigValue;
import com.leonardobishop.playerskills2.skill.config.SkillNumberConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HealthSkill extends Skill {

    private final SkillNumberConfigValue extraHealthPerLevel = new SkillNumberConfigValue(this, "extra-health-per-level", 1);
    private final SkillConfigValue<Boolean> compatibilityMode = new SkillConfigValue<>(this, Boolean.class, "compatibility-mode", false);
    private final Map<UUID, Integer> knownMaxHealth = new IdentityHashMap<>();
    private BukkitTask task;

    public HealthSkill(PlayerSkills plugin) {
        super(plugin, "Health", "health", 5, 22);
    }

    @Override
    public void enable() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    SPlayer sPlayer = SPlayer.get(uuid);
                    if (sPlayer == null) {
                        if (HealthSkill.super.getPlugin().isVerboseLogging()) {
                            HealthSkill.super.getPlugin().logError("Failed event. SPlayer for " + uuid + " is null.");
                        }
                        continue;
                    }
                    if (isWorldNotAllowed(player)) {
                        clearPlayer(player);
                        return;
                    }
                    int hpNeeded = (getLevel(sPlayer) * (extraHealthPerLevel.getInt() * 2));
                    if (hpNeeded != knownMaxHealth.getOrDefault(uuid, 0)) {
                        clearPlayer(player);
                        if (hpNeeded > 0) {
                            knownMaxHealth.put(player.getUniqueId(), hpNeeded);
                            clearModifier(player);
                            addNewHealth(player, hpNeeded);
                        }
                    }
                }
            }
        };
        long tick = compatibilityMode.get() ? 1L : 20L;
        task = runnable.runTaskTimer(getPlugin(), tick, tick);
    }

    @Override
    public void disable() {
        super.disable();
        if (task != null) {
            try {
                task.cancel();
            } catch (Exception ignored) {
                // IGNORED
            }
        }
    }

    private void addNewHealth(Player player, int amount) {
        Optional.ofNullable(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).ifPresent(instance -> {
            AttributeModifier modifier = new AttributeModifier("PlayerSkillsHealth", amount, AttributeModifier.Operation.ADD_NUMBER);
            instance.addModifier(modifier);
        });
    }

    private void clearPlayer(Player player) {
        knownMaxHealth.remove(player.getUniqueId());
        clearModifier(player);
    }

    private void clearModifier(Player player) {
        Optional.ofNullable(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).ifPresent(instance -> {
            for (AttributeModifier modifier : instance.getModifiers()) {
                if (modifier.getName().equals("PlayerSkillsHealth")) {
                    instance.removeModifier(modifier);
                }
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clearPlayer(event.getPlayer());
    }

    @Override
    public String getPreviousString(SPlayer player) {
        int healthLevel = getLevel(player);
        int hp = healthLevel * extraHealthPerLevel.getInt();
        return hp + "❤";
    }

    @Override
    public String getNextString(SPlayer player) {
        int healthLevel = getLevel(player) + 1;
        int hp = healthLevel * extraHealthPerLevel.getInt();
        return hp + "❤";
    }
}
