package me.itzjustsamu.playerskills.menu;

import com.cryptomorin.xseries.XSound;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SkillsMenu implements Menu {
    private final PlayerSkills plugin;
    private final Player player;
    private final SPlayer sPlayer;

    public SkillsMenu(PlayerSkills plugin, Player player, SPlayer sPlayer) {
        this.plugin = plugin;
        this.player = player;
        this.sPlayer = sPlayer;
    }

    public @NotNull Inventory getInventory() {
        String title = ColorUtils.colorize(MainConfig.GUI_TITLE.getValue());
        int size = MainConfig.GUI_SIZE.getValue();
        Inventory inventory = Bukkit.createInventory(this, size, title);
        if (MainConfig.GUI_BACKGROUND_ENABLED.getValue()) {
            ItemStack background = MainConfig.GUI_BACKGROUND_DISPLAY.getValue().build(this.player);

            for (int i = 0; i < inventory.getSize(); ++i) {
                inventory.setItem(i, background);
            }
        }

        for (Skill skill : this.plugin.getSkills().values()) {
            if (!MainConfig.OPTIONS_DISABLED_SKILLS.getValue().contains(skill.getSkillsConfigName())) {
                skill.setup();
                inventory.setItem(skill.getGuiSlot(), skill.getDisplayItem(this.player));
            }
        }

        inventory.setItem(MainConfig.GUI_INFO_SLOT.getValue(), MainConfig.GUI_INFO_DISPLAY.getValue().build(this.player));
        inventory.setItem(MainConfig.GUI_POINTS_SLOT.getValue(), MainConfig.GUI_POINTS_DISPLAY.getValue().build(this.player));
        inventory.setItem(MainConfig.GUI_RESET_SLOT.getValue(), MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player));
        return inventory;
    }

    public void onClick(int slot, ClickType clickType) {
        for (Skill skill : this.plugin.getSkills().values()) {
            if (clickType == ClickType.RIGHT && slot == skill.getGuiSlot()) {
                // Handle right-click on skill slot
                SkillsSettings skillsSettings = new SkillsSettings(plugin, player, skill, this, sPlayer);
                skillsSettings.open(this.player);
                return; // Stop processing further actions
            }

            if (clickType == ClickType.LEFT && slot == skill.getGuiSlot() && skill.getLevel(this.sPlayer) < skill.getMaxLevel()) {
                int price = skill.getPriceOverride(skill.getLevel(this.sPlayer) + 1);
                if (this.sPlayer.getPoints() >= price) {
                    Runnable callback = () -> {
                        this.sPlayer.setLevel(skill.getSkillsConfigName(), skill.getLevel(this.sPlayer) + 1);
                        this.sPlayer.setPoints(this.sPlayer.getPoints() - price);
                        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(this.player, 2.0F, 2.0F);
                        this.open(this.player);
                    };

                    if (MainConfig.GUI_CONFIRMATION_ENABLED_PURCHASE_SKILLS.getValue()) {
                        ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, this.player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                        confirmationMenu.open(this.player);
                    } else {
                        callback.run();
                    }
                    return;
                }
                XSound.ENTITY_ITEM_BREAK.play(this.player, 1.0F, 0.6F);
            }
        }

        if (slot == MainConfig.GUI_POINTS_SLOT.getValue()) {
            Runnable callback = getRunnable();
            if (clickType == ClickType.RIGHT) {
                SkillsSettings skillsSettings = new SkillsSettings(plugin, player, null, this, sPlayer);
                skillsSettings.open(this.player);
            } else if (clickType == ClickType.LEFT && MainConfig.GUI_CONFIRMATION_ENABLED_PURCHASE_SKILL_POINTS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, this.player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                confirmationMenu.open(this.player);
            } else {
                callback.run();
            }
        } else if (slot == MainConfig.GUI_RESET_SLOT.getValue()) {
            Runnable callback = null;  // Declare callback as final
            if (clickType == ClickType.RIGHT) {
                SkillsSettings skillsSettings = new SkillsSettings(plugin, player, null, this, sPlayer);
                skillsSettings.open(this.player);
            } else if (clickType == ClickType.LEFT && MainConfig.GUI_CONFIRMATION_ENABLED_RESET_SKILLS.getValue()) {
                callback = () -> {
                    int resetPoint = MainConfig.POINTS_RESET_PRICE.getValue();
                    if (this.sPlayer.getPoints() >= resetPoint) {
                        this.sPlayer.setPoints(this.sPlayer.getPoints() - resetPoint);
                        if (MainConfig.POINTS_REFUND_SKILL_POINTS.getValue()) {
                            for (String s : this.sPlayer.getSkills().keySet()) {
                                for (int i = 1; i <= this.sPlayer.Level(s); ++i) {
                                    this.sPlayer.setPoints(this.sPlayer.getPoints() + this.plugin.getSkills().get(s).getPriceOverride(i));
                                }
                            }
                        }
                        this.sPlayer.getSkills().clear();
                        XSound.ENTITY_GENERIC_EXPLODE.play(this.player, 1.0F, 1.0F);
                        this.open(this.player);
                    } else {
                        // Open ConfirmationMenu for reset action
                        ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player), null, this);
                        confirmationMenu.open(this.player);
                    }
                };
            } else {
                callback = () -> {
                    int resetPoint = MainConfig.POINTS_RESET_PRICE.getValue();
                    if (this.sPlayer.getPoints() >= resetPoint) {
                        this.sPlayer.setPoints(this.sPlayer.getPoints() - resetPoint);
                        if (MainConfig.POINTS_REFUND_SKILL_POINTS.getValue()) {
                            for (String s : this.sPlayer.getSkills().keySet()) {
                                for (int i = 1; i <= this.sPlayer.Level(s); ++i) {
                                    this.sPlayer.setPoints(this.sPlayer.getPoints() + this.plugin.getSkills().get(s).getPriceOverride(i));
                                }
                            }
                        }
                        this.sPlayer.getSkills().clear();
                        XSound.ENTITY_GENERIC_EXPLODE.play(this.player, 1.0F, 1.0F);
                        this.open(this.player);
                    } else {
                        XSound.ENTITY_ITEM_BREAK.play(this.player, 1.0F, 0.6F);
                    }
                };
            }

            if (callback != null) {
                // Open ConfirmationMenu for reset action
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player), callback, this);
                confirmationMenu.open(this.player);
            }
        }
    }

    @NotNull
    private Runnable getRunnable() {
        int price = this.sPlayer.getNextPointPrice();
        return () -> {
            if (MainConfig.POINTS_FUNDING_SOURCE.getValue().doTransaction(this.sPlayer, price, this.player)) {
                this.sPlayer.setPoints(this.sPlayer.getPoints() + 1);
                XSound.UI_BUTTON_CLICK.play(this.player, 1.0F, 1.0F);
                this.open(this.player);
            } else {
                XSound.ENTITY_ITEM_BREAK.play(this.player, 1.0F, 0.6F);
            }
        };
    }
}
