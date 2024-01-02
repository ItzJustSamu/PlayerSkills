package me.itzjustsamu.playerskills.menu;

import com.cryptomorin.xseries.XSound;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.itzjustsamu.playerskills.Permissions;
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

public class SkillsSettings implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final Skill skill;
    private final Menu superMenu;
    private final SPlayer sPlayer;

    public SkillsSettings(PlayerSkills plugin, Player player, Skill skill, Menu superMenu, SPlayer sPlayer) {
        this.plugin = plugin;
        this.player = player;
        this.skill = skill;
        this.superMenu = superMenu;
        this.sPlayer = sPlayer;
    }

    @Override
    public @NotNull Inventory getInventory() {
        String title = ColorUtils.colorize(MainConfig.GUI_TITLE.getValue());
        int size = MainConfig.GUI_SIZE.getValue();
        Inventory inventory = Bukkit.createInventory(this, size, title);

        if (MainConfig.GUI_BACKGROUND_ENABLED.getValue()) {
            ItemStack background = MainConfig.GUI_BACKGROUND_DISPLAY.getValue().build(player);
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        // Check if skill is not null before using it
        if (skill != null) {
            inventory.setItem(MainConfig.GUI_POINTS_SLOT.getValue(), MainConfig.GUI_POINTS_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.GUI_RESET_SLOT.getValue(), MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.GUI_BACK_SLOT.getValue(), MainConfig.GUI_BACK_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.GUI_ADMIN_SLOT.getValue(), MainConfig.GUI_ADMIN_DISPLAY.getValue().build(this.player));
            inventory.setItem(3, skill.getDisplayItem(this.player));
        }

        return inventory;
    }

    @Override
    public void onClick(int slot, ClickType event) {
        if (slot == MainConfig.GUI_POINTS_SLOT.getValue()) {
            Runnable callback = getRunnable();
            if (event == ClickType.RIGHT) {
                SkillsSettings skillsSettings = new SkillsSettings(this.plugin, this.player, null, this, this.sPlayer);
                skillsSettings.open(this.player);
            } else if (event == ClickType.LEFT && MainConfig.GUI_CONFIRMATION_ENABLED_PURCHASE_SKILL_POINTS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, this.player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                confirmationMenu.open(this.player);
            } else {
                callback.run();
            }
        } else if (slot == MainConfig.GUI_RESET_SLOT.getValue()) {
            Runnable callback = null;
            if (event == ClickType.RIGHT) {
                SkillsSettings skillsSettings = new SkillsSettings(this.plugin, this.player, null, this, this.sPlayer);
                skillsSettings.open(this.player);
            } else if (event == ClickType.LEFT && MainConfig.GUI_CONFIRMATION_ENABLED_RESET_SKILLS.getValue()) {
                callback = () -> {
                    int resetPoint = MainConfig.POINTS_RESET_PRICE.getValue();
                    if (this.sPlayer.getPoints() >= resetPoint) {
                        this.sPlayer.setPoints(this.sPlayer.getPoints() - resetPoint);
                        if (MainConfig.POINTS_REFUND_SKILL_POINTS.getValue()) {
                            for (String s : this.sPlayer.getSkills().keySet()) {
                                for (int i = 1; i <= this.sPlayer.Level(s); ++i) {
                                    this.sPlayer.setPoints(this.sPlayer.getPoints() + plugin.getSkills().get(s).getPriceOverride(i));
                                }
                            }
                        }
                        this.sPlayer.getSkills().clear();
                        XSound.ENTITY_GENERIC_EXPLODE.play(this.player, 1.0F, 1.0F);
                        this.open(this.player);
                    } else {
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
                                    this.sPlayer.setPoints(this.sPlayer.getPoints() + plugin.getSkills().get(s).getPriceOverride(i));
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
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player), callback, this);
                confirmationMenu.open(this.player);
            }
        } else if (slot == MainConfig.GUI_BACK_SLOT.getValue()) {
                SkillsMenu skillsMenu = new SkillsMenu(this.plugin, this.player, this.sPlayer);
                skillsMenu.open(this.player);
        } else if (slot == MainConfig.GUI_ADMIN_SLOT.getValue()) {
            String ADMIN = "playerskills.admin";
            if (event == ClickType.LEFT && player.hasPermission(ADMIN)) {
                // Open SkillsAdmin when clicking the GUI_ADMIN_SLOT
                SkillsAdmin skillsAdmin = new SkillsAdmin(this.plugin, this.player, this.skill, this.sPlayer);
                skillsAdmin.open(this.player);
            }
        } else if (slot == 3) {
            // Handle click event for the skill in slot 3
            if ((event == ClickType.LEFT || event == ClickType.RIGHT) && skill.getLevel(this.sPlayer) < skill.getMaxLevel()) {
                int price = skill.getPriceOverride(skill.getLevel(this.sPlayer) + 1);
                if (this.sPlayer.getPoints() >= price) {
                    Runnable callback = () -> {
                        this.sPlayer.setLevel(skill.getSkillsConfigName(), skill.getLevel(this.sPlayer) + 1);
                        this.sPlayer.setPoints(this.sPlayer.getPoints() - price);
                        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(this.player, 2.0F, 2.0F);
                        this.open(this.player);
                    };

                    if (MainConfig.GUI_CONFIRMATION_ENABLED_PURCHASE_SKILLS.getValue()) {
                        ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, skill.getDisplayItem(this.player), callback, this);
                        confirmationMenu.open(this.player);
                    } else {
                        callback.run();
                    }
                } else {
                    XSound.ENTITY_ITEM_BREAK.play(this.player, 1.0F, 0.6F);
                }
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
