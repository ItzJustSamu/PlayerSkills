package me.itzjustsamu.playerskills.menu;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.skill.Skill;
import me.itzjustsamu.playerskills.util.CommonStringReplacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static me.itzjustsamu.playerskills.Permissions.ADMIN;
import static me.itzjustsamu.playerskills.menu.Sounds.*;

public class SettingsMenu implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final Skill skill;
    private final SPlayer sPlayer;
    private final BukkitConfig bukkitConfig;
    private final Skill clickedSkill;

    public SettingsMenu(PlayerSkills plugin, Player player, Skill skill, SPlayer sPlayer, BukkitConfig bukkitConfig,Skill clickedSkill) {
        this.plugin = plugin;
        this.player = player;
        this.skill = skill;
        this.sPlayer = sPlayer;
        this.bukkitConfig = bukkitConfig = new BukkitConfig(new File(plugin.getDataFolder(), "skills" + File.separator + skill.getSkillsConfigName() + ".yml"));
        this.clickedSkill = clickedSkill;
        bukkitConfig.setup();
    }

    @Override
    public @NotNull Inventory getInventory() {
        String title = ColorUtils.colorize(MainConfig.SETTINGS_MENU_TITLE.getValue());
        int size = MainConfig.SETTINGS_MENU_SIZE.getValue();
        Inventory inventory = Bukkit.createInventory(this, size, title);

        if (MainConfig.SETTINGS_BACKGROUND_ENABLED.getValue()) {
            ItemStack background = MainConfig.SETTINGS_BACKGROUND_DISPLAY.getValue().build(player.getUniqueId());
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        if (clickedSkill != null) {
            inventory.setItem(MainConfig.PURCHASE_POINT_SLOT.getValue(), MainConfig.PURCHASE_POINT_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.RESET_SKILLS_SLOT.getValue(), MainConfig.RESET_SKILLS_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.BACK_SLOT.getValue(), MainConfig.BACK_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.ADMIN_SLOT.getValue(), MainConfig.ADMIN_DISPLAY.getValue().build(player.getUniqueId()));

            inventory.setItem(3, clickedSkill.getDisplayItem(player));
        }

        return inventory;
    }

    @Override
    public void onClick(int slot, ClickType clickType) {
        if (slot == 3) {
            handleSkillClick(clickType);
        }
        else if (slot == MainConfig.PURCHASE_POINT_SLOT.getValue()) {
            Runnable callback = getRunnable();
            if (slot == MainConfig.PURCHASE_POINT_SLOT.getValue() && MainConfig.CONFIRMATION_PURCHASE_POINTS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                confirmationMenu.open(player);
            } else {
                callback.run();
            }
        } else if (slot == MainConfig.RESET_SKILLS_SLOT.getValue()) {
            Runnable callback = getRunnable(clickType);
            if (slot == MainConfig.RESET_SKILLS_SLOT.getValue() && MainConfig.CONFIRMATION_RESET_SKILLS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                confirmationMenu.open(player);
            } else {
                callback.run();
            }
        } else if (slot == MainConfig.ADMIN_SLOT.getValue() && player.hasPermission(ADMIN)) {
            AdminMenu AdminMenu = new AdminMenu(plugin, player, skill, sPlayer, bukkitConfig, clickedSkill);
            AdminMenu.open(player);
        } else if (slot == MainConfig.BACK_SLOT.getValue()) {
            SkillsMenu skillsMenu = new SkillsMenu(plugin, player, sPlayer);
            skillsMenu.open(player);
            playUIButtonClickSound(player);
            CommonStringReplacer.resetSkill();
        }
    }

    private void handleSkillClick(ClickType event) {
        if ((event == ClickType.LEFT || event == ClickType.RIGHT) && clickedSkill.getLevel(sPlayer) < clickedSkill.getLimit()) {
            int price = clickedSkill.getPrice().getValue();
            if (sPlayer.getPoints() >= price) {
                Runnable callback = () -> {
                    sPlayer.setLevel(clickedSkill.getSkillsConfigName(), clickedSkill.getLevel(sPlayer) + 1);
                    sPlayer.setPoints(sPlayer.getPoints() - price);
                    playExperienceOrbPickupSound(player);
                    open(player);
                };

                if (MainConfig.CONFIRMATION_PURCHASE_SKILLS.getValue()) {
                    ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, clickedSkill.getDisplayItem(player), callback, this);
                    confirmationMenu.open(player);
                } else {
                    callback.run();
                }
            } else {
                playItemBreakSound(player);
                open(player);
            }
        }
    }

    @NotNull
    private Runnable getRunnable(ClickType event) {
        if ((event == ClickType.LEFT || event == ClickType.RIGHT) && MainConfig.CONFIRMATION_RESET_SKILLS.getValue()) {
            return getResetSkillsCallback();
        } else {
            return getCallbackWithoutConfirmation();
        }
    }

    @NotNull
    private Runnable getResetSkillsCallback() {
        return () -> {
            int resetSkillsPrice = 0;
            if (MainConfig.RESET_PRICE.getValue() > 0) {
                resetSkillsPrice = MainConfig.RESET_PRICE.getValue();
            } else if (MainConfig.RESET_INCREMENT_PRICE.getValue() > 0) {
                resetSkillsPrice = MainConfig.RESET_PRICE.getValue() + (MainConfig.RESET_INCREMENT_PRICE.getValue() * sPlayer.getResetCount());
            }

            if (sPlayer.getPoints() >= resetSkillsPrice) {
                sPlayer.setPoints(sPlayer.getPoints() - resetSkillsPrice);
                if (MainConfig.REFUND_POINTS.getValue()) {
                    refundPointsForReset();
                }
                sPlayer.getSkills().clear();
                playGenericExplodeSound(player);
                sPlayer.incrementResetCount();
                open(player);
            } else {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, MainConfig.RESET_SKILLS_DISPLAY.getValue().build(player.getUniqueId()), null, this);
                confirmationMenu.open(player);
            }
        };
    }

    private void refundPointsForReset() {
        for (String s : sPlayer.getSkills().keySet()) {
            for (int i = 1; i <= sPlayer.Level(s); ++i) {
                sPlayer.setPoints(sPlayer.getPoints() + plugin.getSkills().get(s).getPrice().getValue());
            }
        }
    }

    @NotNull
    private Runnable getCallbackWithoutConfirmation() {
        return () -> {
            int resetPoint;
            if (MainConfig.RESET_INCREMENT_PRICE.getValue() > 0) {
                resetPoint = sPlayer.getResetCount() * (MainConfig.RESET_PRICE.getValue() * MainConfig.RESET_INCREMENT_PRICE.getValue());
            } else {
                resetPoint = MainConfig.RESET_PRICE.getValue() * (sPlayer.getResetCount() + 1);
            }
            if (sPlayer.getPoints() >= resetPoint) {
                sPlayer.setPoints(sPlayer.getPoints() - resetPoint);
                if (MainConfig.REFUND_POINTS.getValue()) {
                    refundPointsForReset();
                }
                sPlayer.getSkills().clear();
                sPlayer.incrementResetCount();
                playGenericExplodeSound(player);
                open(player);
            } else {
                playItemBreakSound(player);
                open(player);
            }
        };
    }

    @NotNull
    private Runnable getRunnable() {
        int price = sPlayer.getPointPrice();
        return () -> {
            if (MainConfig.POINTS_FUNDING_SOURCE.getValue().doTransaction(sPlayer, price, player)) {
                sPlayer.setPoints(sPlayer.getPoints() + 1);
                playUIButtonClickSound(player);
                open(player);
            } else {
                playItemBreakSound(player);
            }
        };
    }
}
