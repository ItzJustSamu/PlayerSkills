package com.leonardobishop.playerskills2.config;

import com.cryptomorin.xseries.XMaterial;
import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.fundingsource.FundingSource;
import com.leonardobishop.playerskills2.fundingsource.XPFundingSource;
import com.leonardobishop.playerskills2.storage.FlatFileStorage;
import com.leonardobishop.playerskills2.storage.PlayerStorage;
import com.leonardobishop.playerskills2.util.CommonStringReplacer;
import com.leonardobishop.playerskills2.util.modifier.XMaterialModifier;
import com.leonardobishop.playerskills2.util.path.ItemBuilderConfigPath;
import com.leonardobishop.playerskills2.util.path.StringListConfigPath;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.*;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.IntegerConfigPath;
import me.hsgamer.hscore.config.path.StringConfigPath;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class MainConfig extends PathableConfig {
    public static final StringListConfigPath OPTIONS_DISABLED_SKILLS = new StringListConfigPath("options.disabled-skills", Collections.emptyList());
    public static final BooleanConfigPath OPTIONS_VERBOSE = new BooleanConfigPath("options.verbose", false);
    public static final StringListConfigPath OPTIONS_MENU_WORLD_RESTRICTION = new StringListConfigPath("options.menu-world-restriction", Collections.emptyList());
    public static final ConfigPath<PlayerStorage> OPTIONS_PLAYER_STORAGE = new StickyConfigPath<>(
            new AdvancedConfigPath<String, PlayerStorage>("options.player-storage", new FlatFileStorage()) {
                @Override
                public @Nullable String getFromConfig(@NotNull Config config) {
                    return config.getInstance(getPath(), String.class);
                }

                @Override
                public @Nullable PlayerStorage convert(@NotNull String rawValue) {
                    return PlayerSkills.PLAYER_STORAGE_MAP.getOrDefault(rawValue, FlatFileStorage::new).get();
                }

                @Override
                public @Nullable String convertToRaw(@NotNull PlayerStorage value) {
                    return value.getName();
                }
            }
    );
    public static final StringConfigPath GUI_TITLE = new StringConfigPath("gui.title", "&6Skills");
    public static final IntegerConfigPath GUI_SIZE = new IntegerConfigPath("gui.size", 27);
    public static final BooleanConfigPath GUI_BACKGROUND_ENABLED = new BooleanConfigPath("gui.background.enabled", true);
    public static final ConfigPath<ItemBuilder> GUI_BACKGROUND_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath("gui.background.display",
            addDefaultReplacer(new ItemBuilder()
                    .addItemModifier(new NameModifier().setName("&r"))
                    .addItemModifier(new XMaterialModifier(XMaterial.GRAY_STAINED_GLASS_PANE))
                    .addStringReplacer("colorize", CommonStringReplacer.COLORIZE)
                    .addStringReplacer("player-properties", CommonStringReplacer.PLAYER_PROPERTIES))
    ));
    public static final StringConfigPath GUI_PLACEHOLDERS_SKILL_PRICE_MAX = new StringConfigPath("gui.placeholders.skill-price-max", "--");
    public static final StringConfigPath GUI_PLACEHOLDERS_NEXT_MAX = new StringConfigPath("gui.placeholders.next-max", "--");
    public static final IntegerConfigPath GUI_INFO_SLOT = new IntegerConfigPath("gui.info.slot", 3);
    public static final ConfigPath<ItemBuilder> GUI_INFO_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath("gui.info.display",
            addDefaultReplacer(new ItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cInformation"))
                    .addItemModifier(new XMaterialModifier(XMaterial.PAPER))
                    .addItemModifier(new LoreModifier().setLore(
                            "&cSkill points &7can be bought using XP or money.",
                            "&7These points can be used to upgrade &cskills.",
                            "&7Each skill has its own individual perk."
                    )))
    ));
    public static final IntegerConfigPath GUI_POINTS_SLOT = new IntegerConfigPath("gui.points.slot", 4);
    public static final ConfigPath<ItemBuilder> GUI_POINTS_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath("gui.points.display",
            addDefaultReplacer(new ItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cSkill Points"))
                    .addItemModifier(new XMaterialModifier(XMaterial.BOOK))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7You have &e{points} &7points.",
                            "&eLeft-Click &7to purchase a skill point for &e{price} {symbol}&7."
                    )))
    ));
    public static final IntegerConfigPath GUI_RESET_SLOT = new IntegerConfigPath("gui.reset.slot", 5);
    public static final ConfigPath<ItemBuilder> GUI_RESET_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath("gui.reset.display",
            addDefaultReplacer(new ItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cReset"))
                    .addItemModifier(new XMaterialModifier(XMaterial.TNT))
                    .addItemModifier(new LoreModifier().setLore(
                            "&eLeft-Click &7to completely reset your skills.",
                            "&7This costs &e1 &7skill point.",
                            "&7Other skill points invested in existing skills will be refunded.",
                            "",
                            "&cThis action is irreversible."
                    )))
    ));

    public static final BooleanConfigPath GUI_CONFIRMATION_ENABLED_PURCHASE_SKILLS = new BooleanConfigPath("gui-confirmation.enabled.purchase-skills", false);
    public static final BooleanConfigPath GUI_CONFIRMATION_ENABLED_PURCHASE_SKILL_POINTS = new BooleanConfigPath("gui-confirmation.enabled.purchase-skill-points", false);
    public static final BooleanConfigPath GUI_CONFIRMATION_ENABLED_RESET_SKILLS = new BooleanConfigPath("gui-confirmation.enabled.reset-skills", true);
    public static final StringConfigPath GUI_CONFIRMATION_TITLE = new StringConfigPath("gui-confirmation.title", "Are you sure?");
    public static final BooleanConfigPath GUI_CONFIRMATION_BACKGROUND_ENABLED = new BooleanConfigPath("gui-confirmation.background.enabled", true);
    public static final ConfigPath<ItemBuilder> GUI_CONFIRMATION_BACKGROUND_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath("gui-confirmation.background.display",
            addDefaultReplacer(new ItemBuilder()
                    .addItemModifier(new NameModifier().setName("&r"))
                    .addItemModifier(new XMaterialModifier(XMaterial.GRAY_STAINED_GLASS_PANE)))
    ));
    public static final ConfigPath<ItemBuilder> GUI_CONFIRMATION_ACCEPT = new StickyConfigPath<>(new ItemBuilderConfigPath("gui-confirmation.accept",
            addDefaultReplacer(new ItemBuilder()
                    .addItemModifier(new NameModifier().setName("&a&lConfirm"))
                    .addItemModifier(new XMaterialModifier(XMaterial.LIME_STAINED_GLASS_PANE))
                    .addItemModifier(new LoreModifier().setLore("&7Confirm action.")))
    ));
    public static final ConfigPath<ItemBuilder> GUI_CONFIRMATION_DENY = new StickyConfigPath<>(new ItemBuilderConfigPath("gui-confirmation.deny",
            addDefaultReplacer(new ItemBuilder()
                    .addItemModifier(new NameModifier().setName("&c&lDecline"))
                    .addItemModifier(new XMaterialModifier(XMaterial.RED_STAINED_GLASS_PANE))
                    .addItemModifier(new LoreModifier().setLore("&7Decline and return to the previous menu.")))
    ));

    public static final IntegerConfigPath POINTS_PRICE = new IntegerConfigPath("points.price", 1);
    public static final IntegerConfigPath POINTS_RESET_PRICE = new IntegerConfigPath("points.reset-price", 1);
    public static final BooleanConfigPath POINTS_REFUND_SKILL_POINTS = new BooleanConfigPath("points.refund-skill-points", true);
    public static final ConfigPath<FundingSource> POINTS_FUNDING_SOURCE = new StickyConfigPath<>(
            new AdvancedConfigPath<String, FundingSource>("points.funding-source", new XPFundingSource()) {
                @Override
                public @Nullable String getFromConfig(@NotNull Config config) {
                    return config.getInstance(getPath(), String.class);
                }

                @Override
                public @Nullable FundingSource convert(@NotNull String rawValue) {
                    return PlayerSkills.FUNDING_SOURCE_MAP.getOrDefault(rawValue, XPFundingSource::new).get();
                }

                @Override
                public @Nullable String convertToRaw(@NotNull FundingSource value) {
                    return value.getName();
                }
            }
    );
    public static final BooleanConfigPath POINTS_DYNAMIC_PRICE_ENABLED = new BooleanConfigPath("points.dynamic-price.enabled", false);
    public static final IntegerConfigPath POINTS_DYNAMIC_PRICE_PRICE_INCREASE_PER_POINT = new IntegerConfigPath("points.dynamic-price.price-increase-per-point", 1);

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }

    public static boolean isVerboseLogging() {
        return OPTIONS_VERBOSE.getValue();
    }

    private static ItemBuilder addDefaultReplacer(ItemBuilder builder) {
        return builder
                .addStringReplacer("colorize", CommonStringReplacer.COLORIZE)
                .addStringReplacer("player-properties", CommonStringReplacer.PLAYER_PROPERTIES);
    }
}
