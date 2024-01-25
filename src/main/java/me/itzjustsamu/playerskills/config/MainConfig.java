package me.itzjustsamu.playerskills.config;

import com.cryptomorin.xseries.XMaterial;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.bukkit.item.modifier.LoreModifier;
import me.hsgamer.hscore.bukkit.item.modifier.NameModifier;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.AdvancedConfigPath;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.StickyConfigPath;
import me.hsgamer.hscore.config.path.impl.BooleanConfigPath;
import me.hsgamer.hscore.config.path.impl.IntegerConfigPath;
import me.hsgamer.hscore.config.path.impl.LongConfigPath;
import me.hsgamer.hscore.config.path.impl.StringConfigPath;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.fundingsource.FundingSource;
import me.itzjustsamu.playerskills.fundingsource.XPFundingSource;
import me.itzjustsamu.playerskills.storage.FlatFileStorage;
import me.itzjustsamu.playerskills.storage.PlayerStorage;
import me.itzjustsamu.playerskills.util.modifier.XMaterialModifier;
import me.itzjustsamu.playerskills.util.path.ItemBuilderConfigPath;
import me.itzjustsamu.playerskills.util.path.StringListConfigPath;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;


public class MainConfig extends PathableConfig {
    // Skill-related configurations

    public static final BooleanConfigPath OPTIONS_VERBOSE = new BooleanConfigPath(new PathString("options", "verbose"), false);
    public static final StringListConfigPath OPTIONS_MENU_Worlds_Restrictions = new StringListConfigPath(new PathString("options", "menu-world-restriction"), Collections.emptyList());

    public static final ConfigPath<PlayerStorage> OPTIONS_PLAYER_STORAGE = new StickyConfigPath<>(
            new AdvancedConfigPath<String, PlayerStorage>(new PathString("options", "player-storage"), new FlatFileStorage()) {
                @Override
                public @Nullable String getFromConfig(@NotNull me.hsgamer.hscore.config.Config config) {
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
    public static final LongConfigPath OPTIONS_AUTO_SAVE_TICKS = new LongConfigPath(new PathString("options", "auto-save", "ticks"), 1000L);
    public static final BooleanConfigPath OPTIONS_AUTO_SAVE_ASYNC = new BooleanConfigPath(new PathString("options", "auto-save", "async"), true);

    // GUI-related configurations
    public static final StringConfigPath GUI_TITLE = new StringConfigPath(new PathString("gui", "title"), "&6Skills");
    public static final IntegerConfigPath GUI_SIZE = new IntegerConfigPath(new PathString("gui", "size"), 54);

    // Background configurations
    public static final BooleanConfigPath GUI_BACKGROUND_ENABLED = new BooleanConfigPath(new PathString("gui", "background", "enabled"), true);
    public static final ConfigPath<ItemBuilder<ItemStack>> GUI_BACKGROUND_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("gui", "background", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&r"))
                    .addItemModifier(new XMaterialModifier(XMaterial.GRAY_STAINED_GLASS_PANE))
    ));
    public static final StringConfigPath GUI_PLACEHOLDERS_SKILL_PRICE_MAX = new StringConfigPath(new PathString("gui", "placeholders", "skill-price-max"), "--");
    public static final StringConfigPath GUI_PLACEHOLDERS_NEXT_MAX = new StringConfigPath(new PathString("gui", "placeholders", "next-max"), "--");
    public static final IntegerConfigPath POINTS_SLOT = new IntegerConfigPath(new PathString("points", "slot"), 2);

    public static final IntegerConfigPath POINTS_PRICE = new IntegerConfigPath(new PathString("points", "price"), 1);
    public static final ConfigPath<ItemBuilder<ItemStack>> POINTS_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("points", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cSet Points"))
                    .addItemModifier(new XMaterialModifier(XMaterial.PAPER))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7Skills Points Price: &e{price} {symbol}",
                            "&eLeft-Click &7to decrease price.",
                            "&eRight-Click &7 to increase price."
                    ))
    ));
    public static final IntegerConfigPath POINTS_RESET_SLOT = new IntegerConfigPath(new PathString("points", "reset", "slot"), 3);

    public static final IntegerConfigPath POINTS_RESET_PRICE = new IntegerConfigPath(new PathString("points", "reset-price"), 1);
    public static final ConfigPath<ItemBuilder<ItemStack>> POINTS_RESET_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("points", "reset", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cSet Reset Points"))
                    .addItemModifier(new XMaterialModifier(XMaterial.PAPER))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7Skills Reset Cost: &e{reset-points}",
                            "&eLeft-Click &7to decrease price.",
                            "&eRight-Click &7 to increase price."
                    ))
    ));
    public static final IntegerConfigPath POINTS_REFUND_SLOT = new IntegerConfigPath(new PathString("points", "refund", "slot"), 4);

    public static final BooleanConfigPath POINTS_REFUND_POINTS = new BooleanConfigPath(new PathString("points", "refund-points"), true);
    public static final ConfigPath<ItemBuilder<ItemStack>> POINTS_REFUND_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("points", "refund", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cRefund Skill Points"))
                    .addItemModifier(new XMaterialModifier(XMaterial.PAPER))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7Refund Reset Status &e{refund-status}."
                    ))
    ));
    public static final IntegerConfigPath POINTS_FUNDING_SLOT = new IntegerConfigPath(new PathString("points", "funding", "slot"), 5);

    public static final ConfigPath<FundingSource> POINTS_FUNDING_SOURCE = new StickyConfigPath<>(
            new AdvancedConfigPath<String, FundingSource>(new PathString("points", "funding-source"), new XPFundingSource()) {
                @Override
                public @Nullable String getFromConfig(@NotNull me.hsgamer.hscore.config.Config config) {
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
    public static final ConfigPath<ItemBuilder<ItemStack>> POINTS_FUNDING_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("points", "funding", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cPoints Funding"))
                    .addItemModifier(new XMaterialModifier(XMaterial.PAPER))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7Funding Source: &e{symbol}."
                    ))
    ));

    public static final IntegerConfigPath POINTS_INCREMENT_SLOT = new IntegerConfigPath(new PathString("points", "price", "slot"), 6);

    public static final IntegerConfigPath POINTS_INCREMENT_PRICE = new IntegerConfigPath(new PathString("points", "increment", "price"), 0);

    public static final ConfigPath<ItemBuilder<ItemStack>> POINTS_INCREMENT_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("points", "price", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cSet Increment Points"))
                    .addItemModifier(new XMaterialModifier(XMaterial.PAPER))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7Incrementing Price Points: &e{incremented-price}."
                    ))
    ));

    public static final IntegerConfigPath SKILLS_INCREMENT_SLOT = new IntegerConfigPath(new PathString("skills", "increment", "slot"), 9);

    public static final ConfigPath<ItemBuilder<ItemStack>> SKILLS_INCREMENT_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("skills", "price", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cSet {skill} upgrade increment"))
                    .addItemModifier(new XMaterialModifier(XMaterial.PAPER))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7Points: &e{incremented-skill-points}",
                            "&eLeft-Click &7to decrease points.",
                            "&eRight-Click &7to increase points."
                    ))
    ));

    public static final IntegerConfigPath SKILLS_PRICE_SLOT = new IntegerConfigPath(new PathString("skills", "price", "slot"), 10);
    public static final ConfigPath<ItemBuilder<ItemStack>> SKILLS_PRICE_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("skills", "price", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cSet {skill} point price"))
                    .addItemModifier(new XMaterialModifier(XMaterial.PAPER))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7Points: &e{skill-price}",
                            "&eLeft-Click &7to decrease points.",
                            "&eRight-Click &7to increase points."
                    ))
    ));
    public static final IntegerConfigPath SKILLS_INCREMENTED_PRICE_SLOT = new IntegerConfigPath(new PathString("skills", "price", "slot"), 11);
    public static final ConfigPath<ItemBuilder<ItemStack>> SKILLS_INCREMENTED_PRICE_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("skills", "price", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cSet {skill} point price increment"))
                    .addItemModifier(new XMaterialModifier(XMaterial.PAPER))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7Points: &e{skill-incremented-price}",
                            "&eLeft-Click &7to decrease points.",
                            "&eRight-Click &7to increase points."
                    ))
    ));
    // Confirmation Menu
    public static final StringConfigPath GUI_CONFIRMATION_TITLE = new StringConfigPath(new PathString("gui", "title"), "&cSkills");

    public static final IntegerConfigPath GUI_CONFIRMATION_SIZE = new IntegerConfigPath(new PathString("gui-confirmation", "size"), 27);
    public static final BooleanConfigPath GUI_CONFIRMATION_BACKGROUND_ENABLED = new BooleanConfigPath(new PathString("gui", "background", "enabled"), true);
    public static final ConfigPath<ItemBuilder<ItemStack>> GUI_CONFIRMATION_BACKGROUND_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("gui", "background", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&r"))
                    .addItemModifier(new XMaterialModifier(XMaterial.GRAY_STAINED_GLASS_PANE))
    ));

    public static final BooleanConfigPath GUI_CONFIRMATION_ENABLED_PURCHASE_SKILLS = new BooleanConfigPath(new PathString("gui-confirmation", "enabled", "purchase-skills"), false);
    public static final BooleanConfigPath GUI_CONFIRMATION_ENABLED_PURCHASE_SKILL_POINTS = new BooleanConfigPath(new PathString("gui-confirmation", "enabled", "purchase-skill-points"), false);
    public static final BooleanConfigPath GUI_CONFIRMATION_ENABLED_RESET_SKILLS = new BooleanConfigPath(new PathString("gui-confirmation", "enabled", "reset-skills"), true);

    public static final ConfigPath<ItemBuilder<ItemStack>> GUI_CONFIRMATION_ACCEPT = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("gui-confirmation", "accept"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&a&lConfirm"))
                    .addItemModifier(new XMaterialModifier(XMaterial.LIME_STAINED_GLASS_PANE))
                    .addItemModifier(new LoreModifier().setLore("&7Confirm action."))
    ));
    public static final ConfigPath<ItemBuilder<ItemStack>> GUI_CONFIRMATION_DENY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("gui-confirmation", "deny"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&c&lDecline"))
                    .addItemModifier(new XMaterialModifier(XMaterial.RED_STAINED_GLASS_PANE))
                    .addItemModifier(new LoreModifier().setLore("&7Decline and return to the previous menu."))
    ));

    // Back Arrow
    public static final IntegerConfigPath GUI_BACK_SLOT = new IntegerConfigPath(new PathString("gui", "back", "slot"), 45);
    public static final ConfigPath<ItemBuilder<ItemStack>> GUI_BACK_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("gui", "back", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&aBACK"))
                    .addItemModifier(new XMaterialModifier(XMaterial.ARROW))
    ));
    public static final IntegerConfigPath GUI_NEXT_SLOT = new IntegerConfigPath(new PathString("gui", "next", "slot"), 53);
    public static final ConfigPath<ItemBuilder<ItemStack>> GUI_NEXT_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("gui", "next", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&aNEXT"))
                    .addItemModifier(new XMaterialModifier(XMaterial.ARROW))
    ));

    public static final IntegerConfigPath GUI_POINTS_SLOT = new IntegerConfigPath(new PathString("gui", "points", "slot"), 4);
    public static final ConfigPath<ItemBuilder<ItemStack>> GUI_POINTS_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("gui", "points", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cSkill Points"))
                    .addItemModifier(new XMaterialModifier(XMaterial.BOOK))
                    .addItemModifier(new LoreModifier().setLore(
                            "&7You have &e{points} &7points.",
                            "&eLeft-Click &7to purchase a skill point for &e{price} {symbol}&7."
                    ))
    ));

    public static final IntegerConfigPath GUI_ADMIN_SLOT = new IntegerConfigPath(new PathString("gui", "admin", "slot"), 8);
    public static final ConfigPath<ItemBuilder<ItemStack>> GUI_ADMIN_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("gui", "admin", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cEdit Skills"))
                    .addItemModifier(new XMaterialModifier(XMaterial.NETHER_STAR))
                    .addItemModifier(new LoreModifier().setLore(
                            "&eClick to edit skill!"
                    ))
    ));

    public static final IntegerConfigPath GUI_RESET_SLOT = new IntegerConfigPath(new PathString("gui", "reset", "slot"), 5);
    public static final ConfigPath<ItemBuilder<ItemStack>> GUI_RESET_DISPLAY = new StickyConfigPath<>(new ItemBuilderConfigPath(new PathString("gui", "reset", "display"),
            new BukkitItemBuilder()
                    .addItemModifier(new NameModifier().setName("&cReset"))
                    .addItemModifier(new XMaterialModifier(XMaterial.TNT))
                    .addItemModifier(new LoreModifier().setLore(
                            "&eLeft-Click &7to completely reset your skills.",
                            "&7This costs &e{reset-points} &7skill point.",
                            "&7Other skill points invested in existing skills will be refunded.",
                            "",
                            "&cThis action is irreversible."
                    ))
    ));

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }

    public static boolean isVerboseLogging() {
        return OPTIONS_VERBOSE.getValue();
    }
}