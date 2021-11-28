package com.leonardobishop.playerskills2.skill;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.MainConfig;
import com.leonardobishop.playerskills2.config.MessageConfig;
import com.leonardobishop.playerskills2.config.SkillConfig;
import com.leonardobishop.playerskills2.player.SPlayer;
import com.leonardobishop.playerskills2.util.path.IntegerMapConfigPath;
import com.leonardobishop.playerskills2.util.path.ItemBuilderConfigPath;
import com.leonardobishop.playerskills2.util.path.StringListConfigPath;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.StickyConfigPath;
import me.hsgamer.hscore.config.path.Paths;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Skill implements Listener {

    private final SkillConfig config;
    private final PlayerSkills plugin;
    private final String name;
    private final String configName;
    private final int defaultMaxLevel;
    private final int defaultGuiSlot;
    private ConfigPath<Integer> maxLevelConfig;
    private ConfigPath<Integer> guiSlotConfig;
    private ConfigPath<List<String>> onlyInWorldsConfig;
    private ConfigPath<Map<Integer, Integer>> pointPriceOverridesConfig;
    private ItemBuilder displayItem;

    public Skill(PlayerSkills plugin, String name, String configName, int defaultMaxLevel, int defaultGuiSlot) {
        this.plugin = plugin;
        this.name = name;
        this.configName = configName;
        this.defaultMaxLevel = defaultMaxLevel;
        this.defaultGuiSlot = defaultGuiSlot;
        this.config = new SkillConfig(this);
    }

    public void setup() {
        config.setup();
        this.maxLevelConfig = Paths.integerPath("max-level", defaultMaxLevel);
        maxLevelConfig.setConfig(config);
        this.guiSlotConfig = Paths.integerPath("gui-slot", defaultGuiSlot);
        guiSlotConfig.setConfig(config);
        this.onlyInWorldsConfig = new StickyConfigPath<>(new StringListConfigPath("only-in-worlds", Collections.emptyList()));
        onlyInWorldsConfig.setConfig(config);
        this.pointPriceOverridesConfig = new StickyConfigPath<>(new IntegerMapConfigPath("price-override", Collections.emptyMap()));
        pointPriceOverridesConfig.setConfig(config);
        getAdditionalConfigPaths().forEach(configPath -> configPath.setConfig(config));
        ItemBuilderConfigPath itemBuilderConfigPath = new ItemBuilderConfigPath("display", getDefaultItem());
        itemBuilderConfigPath.setConfig(config);
        config.save();

        this.displayItem = itemBuilderConfigPath.getValue();
        displayItem.addStringReplacer("skill-properties", (original, uuid) -> {
            SPlayer sPlayer = SPlayer.get(uuid);
            int level = getLevel(sPlayer);
            int maxLevel = getMaxLevel();
            if (level >= maxLevel) {
                original = original.replace("{next}", MainConfig.GUI_PLACEHOLDERS_NEXT_MAX.getValue())
                        .replace("{skillprice}", MainConfig.GUI_PLACEHOLDERS_SKILL_PRICE_MAX.getValue());
            } else {
                original = original.replace("{next}", getNextString(sPlayer))
                        .replace("{skillprice}", Integer.toString(getPriceOverride(level + 1)));
            }
            original = original
                    .replace("{prev}", getPreviousString(sPlayer))
                    .replace("{level}", Integer.toString(level))
                    .replace("{max}", Integer.toString(maxLevel));
            return original;
        });

        List<ConfigPath<?>> messageConfigPaths = getMessageConfigPaths();
        if (!messageConfigPaths.isEmpty()) {
            MessageConfig messageConfig = plugin.getMessageConfig();
            messageConfigPaths.forEach(configPath -> configPath.setConfig(messageConfig));
            messageConfig.save();
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public abstract List<ConfigPath<?>> getAdditionalConfigPaths();

    public List<ConfigPath<?>> getMessageConfigPaths() {
        return Collections.emptyList();
    }

    public abstract ItemBuilder getDefaultItem();

    public final String getName() {
        return name;
    }

    public final String getConfigName() {
        return configName;
    }

    public final PlayerSkills getPlugin() {
        return plugin;
    }

    public final SkillConfig getConfig() {
        return config;
    }

    public ItemStack getDisplayItem(Player player) {
        return displayItem.build(player);
    }

    public abstract String getPreviousString(SPlayer player);

    public abstract String getNextString(SPlayer player);

    public void enable() {
        // EMPTY
    }

    public void disable() {
        // EMPTY
    }

    public int getLevel(SPlayer player) {
        return player.getLevel(getConfigName());
    }

    public int getPriceOverride(int level) {
        return pointPriceOverridesConfig.getValue().getOrDefault(level, 1);
    }

    public Map<Integer, Integer> getPointPriceOverrides() {
        return pointPriceOverridesConfig.getValue();
    }

    public boolean isWorldNotAllowed(Player player) {
        List<String> list = onlyInWorldsConfig.getValue();
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
        return maxLevelConfig.getValue();
    }

    public int getGuiSlot() {
        return guiSlotConfig.getValue();
    }
}
