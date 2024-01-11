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
import java.util.Objects;

public abstract class Skill implements Listener {

    private final SkillConfig CONFIG;
    private final PlayerSkills PLUGIN;
    private final String NAME;
    private final String SKILL;

    private ItemBuilderConfigPath ITEM_CONFIG;

    private ConfigPath<List<String>> Worlds_Restrictions;
    private ConfigPath<Map<Integer, Integer>> POINT_PRICE;

    private ConfigPath<Integer> GET_INCREMENT;

    private ItemBuilder DISPLAY_ITEM;

    private ConfigPath<Integer> GET_MAX_LEVEL;
    private ConfigPath<Integer> GET_GUI_SLOT;

    private final int MAX_LEVEL;

    private final int INCREMENT;

    private final int GUI_SLOT;

    public Skill(PlayerSkills PlayerSkills, String SKILL_CONFIG_NAME, String SKILL, int SET_MAX_LEVEL, int SET_GUI_SLOT, int SET_INCREMENT) {
        this.PLUGIN = PlayerSkills;
        this.NAME = SKILL_CONFIG_NAME;
        this.SKILL = SKILL;
        this.MAX_LEVEL = SET_MAX_LEVEL;
        this.INCREMENT = SET_INCREMENT;
        this.GUI_SLOT = SET_GUI_SLOT;
        this.CONFIG = new SkillConfig(this);
    }

    public final void setup() {
        CONFIG.setup();
        this.GET_MAX_LEVEL = Paths.integerPath("max-level", MAX_LEVEL);
        GET_MAX_LEVEL.setConfig(CONFIG);
        this.GET_INCREMENT = new StickyConfigPath<>(new IntegerMapConfigPath("increment", Collections.emptyMap()));
        GET_INCREMENT.setConfig(CONFIG);
        this.GET_GUI_SLOT = Paths.integerPath("gui-slot", GUI_SLOT);
        GET_GUI_SLOT.setConfig(CONFIG);
        this.Worlds_Restrictions = new StickyConfigPath<>(new StringListConfigPath("only-in-worlds", Collections.emptyList()));
        Worlds_Restrictions.setConfig(CONFIG);
        this.POINT_PRICE = new StickyConfigPath<>(new IntegerMapConfigPath("price-override", Collections.emptyMap()));
        POINT_PRICE.setConfig(CONFIG);
        getAdditionalConfigPaths().forEach(configPath -> configPath.setConfig(CONFIG));
        ItemBuilderConfigPath itemBuilderConfigPath = new ItemBuilderConfigPath("display", getDefaultItem());
        itemBuilderConfigPath.setConfig(CONFIG);
        CONFIG.save();

        this.DISPLAY_ITEM = itemBuilderConfigPath.getValue();
        DISPLAY_ITEM.addStringReplacer("skill-properties", (original, uuid) -> {
            SPlayer sPlayer = SPlayer.get(uuid);
            int level = getLevel(sPlayer);
            int increment = getSkillIncrement();
            int maxLevel = getMaxLevel();
            if (level >= maxLevel) {
                original = original.replace("{next}", MainConfig.GUI_PLACEHOLDERS_NEXT_MAX.getValue())
                        .replace("{skillprice}", MainConfig.GUI_PLACEHOLDERS_SKILL_PRICE_MAX.getValue());
            } else {
                original = original.replace("{next}", getNextString(sPlayer))
                        .replace("{skillprice}", Integer.toString(getPrice(level + 1)));
            }
            original = original
                    .replace("{prev}", getPreviousString(sPlayer))
                    .replace("{level}", Integer.toString(level))
                    .replace("{max}", Integer.toString(maxLevel))
                    .replace("{increment}", Integer.toString(increment));
            return original;
        });

        List<ConfigPath<?>> messageConfigPaths = getMessageConfigPaths();
        if (!messageConfigPaths.isEmpty()) {
            MessageConfig messageConfig = PLUGIN.getMessageConfig();
            messageConfigPaths.forEach(configPath -> configPath.setConfig(messageConfig));
            messageConfig.save();
        }
        Bukkit.getPluginManager().registerEvents(this, PLUGIN);
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
        if (ITEM_CONFIG == null) {
            ITEM_CONFIG = new ItemBuilderConfigPath("display", null);
            ITEM_CONFIG.setConfig(CONFIG);
        }
        return ITEM_CONFIG.getValue();
    }

    public final String getSkillsConfigName() {
        return NAME;
    }

    public final String getSkillsName() {
        return SKILL;
    }

    public final PlayerSkills getPlugin() {
        return PLUGIN;
    }

    public final SkillConfig getConfig() {
        return CONFIG;
    }

    public ItemStack getDisplayItem(Player player) {
        return DISPLAY_ITEM.build(player);
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
        return player.Level(getSkillsConfigName());
    }

    public Integer getIncrement() {
        return GET_INCREMENT.getValue();
    }

    public void getIncrementPath() {
        GET_INCREMENT.getPath();
    }

    public void setIncrement(int increment) {
        GET_INCREMENT.setValue(increment, Objects.requireNonNull(GET_INCREMENT.getConfig()));
    }

    public int getSkillIncrement() {
        GET_INCREMENT.getValue(GET_INCREMENT.getConfig(), 0);
        return 0;
    }

    public int getPrice(int level) {
        return POINT_PRICE.getValue().getOrDefault(level, 1);
    }

    public Map<Integer, Integer> getPointPrice() {
        return POINT_PRICE.getValue();
    }

    public boolean Worlds_Restriction(Player player) {
        List<String> list = Worlds_Restrictions.getValue();
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