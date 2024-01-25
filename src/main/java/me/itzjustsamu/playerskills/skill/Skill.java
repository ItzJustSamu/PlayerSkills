package me.itzjustsamu.playerskills.skill;

import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.StickyConfigPath;
import me.hsgamer.hscore.config.path.impl.BooleanConfigPath;
import me.hsgamer.hscore.config.path.impl.IntegerConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
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

public abstract class Skill implements Listener {

    private final SkillConfig CONFIG;
    private final PlayerSkills PLUGIN;
    private final String NAME;
    private final String SKILL;
    private final ConfigPath<List<String>> WORLDS_RESTRICTIONS = new StickyConfigPath<>(new StringListConfigPath(new PathString("only-in-worlds"), Collections.emptyList()));

    private final int MAX_LEVEL;
    private final int GUI_SLOT;
    private int INCREMENT;
    private int PRICE;
    private ItemBuilderConfigPath ITEM_CONFIG;
    private IntegerConfigPath GET_INCREMENT;
    private IntegerConfigPath GET_PRICE;
    private final BooleanConfigPath GET_DISABLED = new BooleanConfigPath(new PathString("disable"), false);
    private ItemBuilder<ItemStack> DISPLAY_ITEM;
    private IntegerConfigPath GET_MAX_LEVEL;
    private IntegerConfigPath GET_GUI_SLOT;

    public Skill(PlayerSkills PlayerSkills, String SKILL_CONFIG_NAME, String SKILL, int SET_MAX_LEVEL, int SET_GUI_SLOT) {
        this.PLUGIN = PlayerSkills;
        this.NAME = SKILL_CONFIG_NAME;
        this.SKILL = SKILL;
        this.MAX_LEVEL = SET_MAX_LEVEL;
        this.GUI_SLOT = SET_GUI_SLOT;
        this.CONFIG = new SkillConfig(this);
    }

    public final void setup() {
        CONFIG.setup();
        GET_MAX_LEVEL = Paths.integerPath(new PathString("max-level"), MAX_LEVEL);
        GET_GUI_SLOT = Paths.integerPath(new PathString("gui-slot"), GUI_SLOT);
        GET_MAX_LEVEL.setConfig(CONFIG);
        GET_INCREMENT = Paths.integerPath(new PathString("increment"), INCREMENT);
        GET_INCREMENT.setConfig(CONFIG);
        GET_PRICE = Paths.integerPath(new PathString("price"), PRICE);
        GET_PRICE.setConfig(CONFIG);
        GET_DISABLED.setConfig(CONFIG);
        GET_GUI_SLOT.setConfig(CONFIG);
        WORLDS_RESTRICTIONS.setConfig(CONFIG);
        getAdditionalConfigPaths().forEach(configPath -> configPath.setConfig(CONFIG));
        ItemBuilderConfigPath itemBuilderConfigPath = new ItemBuilderConfigPath(new PathString("display"), getDefaultItem());
        itemBuilderConfigPath.setConfig(CONFIG);
        CONFIG.save();

        DISPLAY_ITEM = itemBuilderConfigPath.getValue();
        DISPLAY_ITEM.addStringReplacer(StringReplacer.of((original, uuid) -> {
            SPlayer sPlayer = SPlayer.get(uuid);
            int level = getLevel(sPlayer);
            int getMaxLevel = GET_MAX_LEVEL.getValue();
            IntegerConfigPath increment = getIncrement();
            IntegerConfigPath price = getPrice();
            if (level >= getMaxLevel) {
                original = original.replace("{next}", MainConfig.GUI_PLACEHOLDERS_NEXT_MAX.getValue())
                        .replace("{price}", MainConfig.GUI_PLACEHOLDERS_SKILL_PRICE_MAX.getValue());
            } else {
                original = original.replace("{next}",  getNextString(sPlayer))
                        .replace("{price}", Integer.toString(price.getValue()));
            }
            original = original
                    .replace("{prev}", getPreviousString(sPlayer))
                    .replace("{level}", Integer.toString(level))
                    .replace("{max}", Integer.toString(getMaxLevel))
                    .replace("{increment}", Integer.toString(increment.getValue()));
            return original;
        }));

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

    public ItemBuilder<ItemStack> getDefaultItem() {
        if (ITEM_CONFIG == null) {
            ITEM_CONFIG = new ItemBuilderConfigPath(new PathString("display"), null);
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
        return DISPLAY_ITEM.build(player.getUniqueId());
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

    public IntegerConfigPath getPrice() {
        return GET_PRICE;
    }

    public IntegerConfigPath getIncrement() {
        return GET_INCREMENT;
    }

    public void setIncrement(int increment) {
        GET_INCREMENT.setValue(increment, getConfig());
    }

    public void setPrice(int price) {
        GET_PRICE.setValue(price, getConfig());
    }


    public boolean Worlds_Restriction(Player player) {
        List<String> list = WORLDS_RESTRICTIONS.getValue();
        if (list.isEmpty()) {
            return false;
        }
        World world = player.getLocation().getWorld();
        if (world == null) {
            return true;
        }
        return !list.contains(world.getName());
    }

    public boolean isSkillDisabled() {
        return GET_DISABLED.getValue();
    }
}
