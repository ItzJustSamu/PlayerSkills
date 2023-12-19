package me.itzjustsamu.playerskills.skill;

import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.StickyConfigPath;
import me.hsgamer.hscore.config.path.impl.*;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.config.MessageConfig;
import me.itzjustsamu.playerskills.config.SkillConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.path.IntegerMapConfigPath;
import me.itzjustsamu.playerskills.util.path.ItemBuilderConfigPath;
import me.itzjustsamu.playerskills.util.path.StringListConfigPath;
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
    private final String skills;

    private ItemBuilderConfigPath itemBuilderConfigPath;

    private ConfigPath<List<String>> onlyInWorldsConfig;
    private ConfigPath<Map<Integer, Integer>> pointPriceOverridesConfig;
    private ItemBuilder displayItem;

    private ConfigPath<Integer> GET_MAX_LEVEL;
    private ConfigPath<Integer> GET_GUI_SLOT;

    private final int MAX_LEVEL;

    private final int GUI_SLOT;

    public Skill(PlayerSkills plugin, String name, String skills, int SET_MAX_LEVEL, int SET_GUI_SLOT) {
        this.plugin = plugin;
        this.name = name;
        this.skills = skills;
        this.MAX_LEVEL = SET_MAX_LEVEL;
        this.GUI_SLOT = SET_GUI_SLOT;
        this.config = new SkillConfig(this);
    }

    public final void setup() {

        config.setup();
        this.GET_MAX_LEVEL = Paths.integerPath("max-level", MAX_LEVEL);
        GET_MAX_LEVEL.setConfig(config);
        this.GET_GUI_SLOT = Paths.integerPath("gui-slot", GUI_SLOT);
        GET_GUI_SLOT.setConfig(config);
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

    public int getMaxLevel() {
        return GET_MAX_LEVEL.getValue();
    }

    public int getGuiSlot() {
        return GET_GUI_SLOT.getValue();
    }

    public List<ConfigPath<?>> getAdditionalConfigPaths() {
        return Collections.emptyList();
    }

    public List<ConfigPath<?>> getMessageConfigPaths() {
        return Collections.emptyList();
    }

    public ItemBuilder getDefaultItem() {
        if (itemBuilderConfigPath == null) {
            itemBuilderConfigPath = new ItemBuilderConfigPath("display", null);
            itemBuilderConfigPath.setConfig(config);
        }
        return itemBuilderConfigPath.getValue();
    }

    public final String getName() {
        return name;
    }

    public final String getConfigName() {
        return skills;
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

}