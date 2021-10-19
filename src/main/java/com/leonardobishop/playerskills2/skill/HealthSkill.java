package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.CreatorConfigValue;
import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class HealthSkill extends Skill {

    private final Map<UUID, Integer> knownMaxHealth = new IdentityHashMap<>();

    public HealthSkill(PlayerSkills plugin) {
        super(plugin, "Health", "health");

        super.getCreatorConfigValues().add(new CreatorConfigValue("max-level", 5, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("gui-slot", 22, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("extra-health-per-level", 1, true));
        super.getCreatorConfigValues().add(new CreatorConfigValue("compatibility-mode", false, false));
        super.getCreatorConfigValues().add(new CreatorConfigValue("only-in-worlds", Arrays.asList("world", "world_nether", "world_the_end")));
    }

    @Override
    public void enable(PlayerSkills plugin) {
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
                    int hpNeeded = (sPlayer.getLevel(HealthSkill.super.getConfigName()) * (((int) HealthSkill.super.getConfig().get("extra-health-per-level")) * 2));
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
        long tick = (getConfig().containsKey("compatibility-mode") && (boolean) getConfig().get("compatibility-mode") ? 1L : 20L);
        runnable.runTaskTimer(plugin, tick, tick);
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
        int healthLevel = player.getLevel(this.getConfigName());
        int hp = healthLevel * ((int) getConfig().get("extra-health-per-level"));
        return hp + (hp == 1 ? " heart" : " hearts");
    }

    @Override
    public String getNextString(SPlayer player) {
        int healthLevel = player.getLevel(this.getConfigName()) + 1;
        int hp = healthLevel * ((int) getConfig().get("extra-health-per-level"));
        return hp + (hp == 1 ? " heart" : " hearts");
    }
}
