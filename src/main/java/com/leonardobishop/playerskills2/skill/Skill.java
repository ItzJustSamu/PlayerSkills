package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.skill.config.SkillNumberConfigValue;
import com.leonardobishop.playerskills2.skill.config.SkillRawConfigValue;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class Skill implements Listener {

    private final SkillNumberConfigValue maxLevelConfig;
    private final SkillNumberConfigValue guiSlotConfig;
    private final SkillRawConfigValue<List<String>> onlyInWorldsConfig;
    private final PlayerSkills plugin;
    private final String name;
    private final String configName;
    private final HashMap<String, Object> config = new HashMap<>();
    private final HashMap<Integer, Integer> pointPriceOverrides = new HashMap<>();
    private String itemLocation;

    public Skill(PlayerSkills plugin, String name, String configName, int defaultMaxLevel, int defaultGuiSlot) {
        this.plugin = plugin;
        this.name = name;
        this.configName = configName;
        this.maxLevelConfig = new SkillNumberConfigValue(this, "max-level", defaultMaxLevel);
        this.guiSlotConfig = new SkillNumberConfigValue(this, "gui-slot", defaultGuiSlot);
        this.onlyInWorldsConfig = new SkillRawConfigValue<>(this, "only-in-worlds", Collections.emptyList());
    }

    public final String getName() {
        return name;
    }

    public final String getConfigName() {
        return configName;
    }

    public final PlayerSkills getPlugin() {
        return plugin;
    }

    public HashMap<String, Object> getConfig() {
        return config;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public abstract String getPreviousString(SPlayer player);

    public abstract String getNextString(SPlayer player);

    public void enable(PlayerSkills plugin) {

    }

    public void disable(PlayerSkills plugin) {
        HandlerList.unregisterAll(this);
    }

    public int getLevel(SPlayer player) {
        return player.getLevel(getConfigName());
    }

    public int getPriceOverride(int level) {
        return pointPriceOverrides.getOrDefault(level, 1);
    }

    public HashMap<Integer, Integer> getPointPriceOverrides() {
        return pointPriceOverrides;
    }

    public <T> T getConfigValue(String location, Class<T> type, T def) {
        Object value = config.getOrDefault(location, def);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return def;
    }

    public boolean isWorldNotAllowed(Player player) {
        List<String> list = onlyInWorldsConfig.get();
        if (list.isEmpty()) {
            return false;
        }
        World world = player.getLocation().getWorld();
        if (world == null) {
            return true;
        }
        return !list.contains(world.getName());
    }

    public int getMaxLevel() {
        return maxLevelConfig.getInt();
    }

    public int getGuiSlot() {
        return guiSlotConfig.getInt();
    }
}
