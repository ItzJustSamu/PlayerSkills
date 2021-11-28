package com.leonardobishop.playerskills2.config;

import com.cryptomorin.xseries.XMaterial;
import com.leonardobishop.playerskills2.util.modifier.XMaterialModifier;
import com.leonardobishop.playerskills2.util.path.ItemBuilderConfigPath;
import com.leonardobishop.playerskills2.util.path.StringListConfigPath;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.IntegerConfigPath;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public class MainConfig extends PathableConfig {
    public static final StringListConfigPath DISABLED_SKILLS = new StringListConfigPath("disabled-skills", Collections.emptyList());

    public static final StringConfigPath GUI_TITLE = new StringConfigPath("gui.title", "&6Skills");
    public static final IntegerConfigPath GUI_SIZE = new IntegerConfigPath("gui.size", 27);
    public static final BooleanConfigPath GUI_BACKGROUND_ENABLED = new BooleanConfigPath("gui.background.enabled", true);
    public static final ItemBuilderConfigPath GUI_BACKGROUND_DISPLAY = new ItemBuilderConfigPath("gui.background.display",
            new ItemBuilder()
                    .addItemModifier(new NameModifier().setName("&r"))
                    .addItemModifier(new XMaterialModifier(XMaterial.GRAY_STAINED_GLASS_PANE))
    );
    public static final StringConfigPath GUI_PLACEHOLDERS_SKILL_PRICE_MAX = new StringConfigPath("gui.placeholders.skill-price-max", "--");
    public static final StringConfigPath GUI_PLACEHOLDERS_NEXT_MAX = new StringConfigPath("gui.placeholders.next-max", "--");

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
