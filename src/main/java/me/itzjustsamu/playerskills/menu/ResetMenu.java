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

import static me.itzjustsamu.playerskills.menu.Sounds.*;

public class ResetMenu implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final Skill skill;
    private final SPlayer sPlayer;
    private final Skill clickedSkill;

    private final BukkitConfig bukkitConfig;

    public ResetMenu(PlayerSkills plugin, Player player, Skill skill, SPlayer sPlayer, Skill clickedSkill) {
        this.plugin = plugin;
        this.player = player;
        this.skill = skill;
        this.sPlayer = sPlayer;
        this.clickedSkill = clickedSkill;
        this.bukkitConfig = new BukkitConfig(new File(plugin.getDataFolder(), "config.yml"));
        bukkitConfig.setup();
    }

    @Override
    public @NotNull Inventory getInventory() {
        String title = ColorUtils.colorize(MainConfig.RESET_MENU_TITLE.getValue());
        int size = MainConfig.RESET_MENU_SIZE.getValue();
        Inventory inventory = Bukkit.createInventory(this, size, title);

        if (MainConfig.RESET_BACKGROUND_ENABLED.getValue()) {
            ItemStack background = MainConfig.RESET_BACKGROUND_DISPLAY.getValue().build(player.getUniqueId());
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        if (clickedSkill != null) {
            inventory.setItem(MainConfig.RESET_SKILLS_SLOT.getValue(), MainConfig.RESET_SKILLS_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.REFUND_SLOT.getValue(), MainConfig.REFUND_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.RESET_SLOT.getValue(), MainConfig.RESET_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.INCREMENT_RESET_SLOT.getValue(), MainConfig.INCREMENT_RESET_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.RESET_BACK_SLOT.getValue(), MainConfig.RESET_BACK_DISPLAY.getValue().build(player.getUniqueId()));
        }

        return inventory;
    }

    @Override
    public void onClick(int slot, ClickType clickType) {
        if (slot == MainConfig.RESET_SKILLS_SLOT.getValue()) {
            Runnable callback = getRunnable(clickType);
            if (slot == MainConfig.RESET_SKILLS_SLOT.getValue() && MainConfig.CONFIRMATION_RESET_SKILLS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                confirmationMenu.open(player);
            } else {
                callback.run();
            }
        } else if (slot == MainConfig.RESET_BACK_SLOT.getValue()) {
            AdminMenu AdminMenu = new AdminMenu(plugin, player, skill, sPlayer, bukkitConfig, clickedSkill);
            AdminMenu.open(player);
            playUIButtonClickSound(player);
            CommonStringReplacer.resetSkill();
        } else if (slot == MainConfig.RESET_SLOT.getValue()) {
            if (clickType == ClickType.LEFT) {
                decreaseResetPrice();
                playUIButtonClickSound(player);
            } else if (clickType == ClickType.RIGHT) {
                increaseResetPrice();
                playUIButtonClickSound(player);
            }
        } else if (slot == MainConfig.INCREMENT_RESET_SLOT.getValue()) {
            if (clickType == ClickType.LEFT) {
                decreaseIncrementResetPrice();
                playUIButtonClickSound(player);
            } else if (clickType == ClickType.RIGHT) {
                increaseIncrementResetPrice();
                playUIButtonClickSound(player);
            }
        } else if (slot == MainConfig.REFUND_SLOT.getValue()) {
            toggleRefundPoints();
            playUIButtonClickSound(player);
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
            int resetPoint;
            if (MainConfig.INCREMENT_RESET_PRICE.getValue() > 0) {
                resetPoint = MainConfig.RESET_PRICE.getValue() * MainConfig.INCREMENT_RESET_PRICE.getValue() * (sPlayer.getResetCount() + 1);
            } else {
                resetPoint = MainConfig.RESET_PRICE.getValue() * (sPlayer.getResetCount() + 1);
            }

            if (sPlayer.getPoints() >= resetPoint) {
                sPlayer.setPoints(sPlayer.getPoints() - resetPoint);
                if (MainConfig.REFUND_POINTS.getValue()) {
                    refundPointsForReset();
                }
                sPlayer.incrementResetCount();
                sPlayer.getSkills().clear();
                playGenericExplodeSound(player);
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
            sPlayer.incrementResetCount();

            int resetPrice = MainConfig.RESET_PRICE.getValue();
            int incrementResetPrice = MainConfig.INCREMENT_RESET_PRICE.getValue();

            int resetPoint = (resetPrice > 0) ? resetPrice * incrementResetPrice * (sPlayer.getResetCount() + 1) : incrementResetPrice * (sPlayer.getResetCount() + 1);

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

    private void increaseResetPrice() {
        int newResetPoints = MainConfig.RESET_PRICE.getValue() + 1;
        MainConfig.RESET_PRICE.setValue(newResetPoints);
        player.openInventory(getInventory());
        bukkitConfig.set(MainConfig.RESET_PRICE.getPath(), newResetPoints);
        bukkitConfig.save();
    }

    private void decreaseResetPrice() {
        int newResetPoints = Math.max(0, MainConfig.RESET_PRICE.getValue() - 1);
        MainConfig.RESET_PRICE.setValue(newResetPoints);
        player.openInventory(getInventory());
        bukkitConfig.set(MainConfig.RESET_PRICE.getPath(), newResetPoints);
        bukkitConfig.save();
    }

    private void increaseIncrementResetPrice() {
        int newIncrementResetPoints = MainConfig.INCREMENT_RESET_PRICE.getValue() + 1;
        MainConfig.INCREMENT_RESET_PRICE.setValue(newIncrementResetPoints);
        player.openInventory(getInventory());
        bukkitConfig.set(MainConfig.INCREMENT_RESET_PRICE.getPath(), newIncrementResetPoints);
        bukkitConfig.save();
    }

    private void decreaseIncrementResetPrice() {
        int newIncrementResetPoints = Math.max(0, MainConfig.INCREMENT_RESET_PRICE.getValue() - 1);
        MainConfig.INCREMENT_RESET_PRICE.setValue(newIncrementResetPoints);
        player.openInventory(getInventory());
        bukkitConfig.set(MainConfig.INCREMENT_RESET_PRICE.getPath(), newIncrementResetPoints);
        bukkitConfig.save();
    }

    private void toggleRefundPoints() {
        boolean newRefundStatus = !MainConfig.REFUND_POINTS.getValue();
        MainConfig.REFUND_POINTS.setValue(newRefundStatus);
        player.openInventory(getInventory());
        bukkitConfig.set(MainConfig.REFUND_POINTS.getPath(), newRefundStatus);
        bukkitConfig.save();
    }
}
