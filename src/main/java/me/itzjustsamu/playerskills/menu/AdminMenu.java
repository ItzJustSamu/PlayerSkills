package me.itzjustsamu.playerskills.menu;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.config.path.impl.IntegerConfigPath;
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

import java.io.File;

import static me.itzjustsamu.playerskills.Permissions.ADMIN;
import static me.itzjustsamu.playerskills.menu.Sounds.*;

public class AdminMenu implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final Skill skill;
    private final SPlayer sPlayer;
    private final BukkitConfig bukkitConfig;
    private final Skill clickedSkill;

    public AdminMenu(PlayerSkills plugin, Player player, Skill skill, SPlayer sPlayer, BukkitConfig bukkitConfig, Skill clickedSkill) {
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
        String title = ColorUtils.colorize(MainConfig.ADMIN_MENU_TITLE.getValue());
        int size = MainConfig.ADMIN_MENU_SIZE.getValue();
        Inventory inventory = Bukkit.createInventory(this, size, title);

        if (MainConfig.ADMIN_BACKGROUND_ENABLED.getValue()) {
            ItemStack background = MainConfig.ADMIN_BACKGROUND_DISPLAY.getValue().build(player.getUniqueId());
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        if (clickedSkill != null) {
            inventory.setItem(MainConfig.ADMIN_PURCHASE_POINTS_SLOT.getValue(), MainConfig.ADMIN_PURCHASE_POINTS_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.ADMIN_RESET_SKILLS_SLOT.getValue(), MainConfig.ADMIN_RESET_SKILLS_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.ADMIN_SKILLS_UPGRADE_SLOT.getValue(), MainConfig.ADMIN_SKILLS_UPGRADE_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.ADMIN_SKILLS_POINT_PRICE_SLOT.getValue(), MainConfig.ADMIN_SKILLS_POINT_PRICE_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.ADMIN_SKILLS_INCREMENT_POINT_PRICE_SLOT.getValue(), MainConfig.ADMIN_SKILLS_INCREMENT_POINT_PRICE_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.ADMIN_CONFIRMATION_TOGGLE_SLOT.getValue(), MainConfig.ADMIN_CONFIRMATION_TOGGLE_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.ADMIN_SKILLS_MAX_LEVEL_SLOT.getValue(), MainConfig.ADMIN_SKILLS_MAX_LEVEL_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(MainConfig.ADMIN_BACK_SLOT.getValue(), MainConfig.ADMIN_BACK_DISPLAY.getValue().build(player.getUniqueId()));
            inventory.setItem(3, clickedSkill.getDisplayItem(player));
        }

        return inventory;
    }

    @Override
    public void onClick(int slot, ClickType clickType) {
        if (slot == MainConfig.ADMIN_PURCHASE_POINTS_SLOT.getValue()) {
            Runnable callback = getRunnable();
            if (clickType == ClickType.LEFT && MainConfig.CONFIRMATION_PURCHASE_POINTS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                confirmationMenu.open(player);
            } else {
                callback.run();
            }
            if (clickType == ClickType.RIGHT && player.hasPermission(ADMIN)) {
                PointsMenu pointsMenu = new PointsMenu(plugin, player, skill, sPlayer, clickedSkill);
                pointsMenu.open(player);
            }
        } else if (slot == MainConfig.ADMIN_RESET_SKILLS_SLOT.getValue()) {
            Runnable callback = getRunnable(clickType);
            if (clickType == ClickType.LEFT && MainConfig.CONFIRMATION_RESET_SKILLS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player, player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                confirmationMenu.open(player);
            } else {
                callback.run();
            }
            if (clickType == ClickType.RIGHT) {
                ResetMenu resetMenu = new ResetMenu(plugin, player, skill, sPlayer, clickedSkill);
                resetMenu.open(player);
            }
        } else if (slot == 3) {
            handleSkillClick(clickType);
        } else if (slot == MainConfig.ADMIN_SKILLS_UPGRADE_SLOT.getValue()) {
            handleUpgradeClick(clickType);
        } else if (slot == MainConfig.ADMIN_SKILLS_POINT_PRICE_SLOT.getValue()) {
            handlePriceClick(clickType);
        } else if (slot == MainConfig.ADMIN_SKILLS_INCREMENT_POINT_PRICE_SLOT.getValue()) {
            handleIncrementClick(clickType);
        } else if (slot == MainConfig.ADMIN_SKILLS_MAX_LEVEL_SLOT.getValue()) {
            handleSkillsMaxLevelClick(clickType);
        } else if (slot == MainConfig.ADMIN_BACK_SLOT.getValue()) {
            SettingsMenu SettingsMenu = new SettingsMenu(plugin, player, skill, sPlayer, bukkitConfig, clickedSkill);
            SettingsMenu.open(player);
            playUIButtonClickSound(player);
        } else if (slot == MainConfig.ADMIN_CONFIRMATION_TOGGLE_SLOT.getValue()) {
            toggleConfirmation(clickType, slot);
            playUIButtonClickSound(player);
        }
    }

    private void handleSkillClick(ClickType event) {
        if ((event == ClickType.LEFT || event == ClickType.RIGHT) && clickedSkill.getLevel(sPlayer) < clickedSkill.getMaxLevel()) {
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


    private void handleUpgradeClick(ClickType event) {
        if (event == ClickType.RIGHT && player.hasPermission(ADMIN)) {
            increaseSkillsUpgrade();
            playUIButtonClickSound(player);
        } else if (event == ClickType.LEFT && player.hasPermission(ADMIN)) {
            decreaseSkillsUpgrade();
            playUIButtonClickSound(player);
        }
    }

    private void handleIncrementClick(ClickType event) {
        if (event == ClickType.RIGHT && player.hasPermission(ADMIN)) {
            increaseSkillsUpgradeIncrementPrice();
            playUIButtonClickSound(player);
        } else if (event == ClickType.LEFT && player.hasPermission(ADMIN)) {
            decreaseSkillsUpgradeIncrementPrice();
            playUIButtonClickSound(player);
        }
    }

    private void handlePriceClick(ClickType event) {
        if (event == ClickType.RIGHT && player.hasPermission(ADMIN)) {
            increaseSkillsPrice();
            playUIButtonClickSound(player);
        } else if (event == ClickType.LEFT && player.hasPermission(ADMIN)) {
            decreaseSkillsPrice();
            playUIButtonClickSound(player);
        }
    }

    private void handleSkillsMaxLevelClick(ClickType event) {
        if (event == ClickType.RIGHT && player.hasPermission(ADMIN)) {
            increaseSkillsMaxLevel();
            playUIButtonClickSound(player);
        } else if (event == ClickType.LEFT && player.hasPermission(ADMIN)) {
            decreaseSkillsMaxLevel();
            playUIButtonClickSound(player);
        }
    }

    @NotNull
    private Runnable getRunnable(ClickType event) {
        if ((event == ClickType.LEFT) && MainConfig.CONFIRMATION_RESET_SKILLS.getValue()) {
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
                this.open(player);
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
            if (MainConfig.POINTS_FUNDING_SOURCE.getValue().doTransaction(this.sPlayer, price, this.player)) {
                sPlayer.setPoints(sPlayer.getPoints() + 1);
                playUIButtonClickSound(player);
                open(player);
            } else {
                playItemBreakSound(player);
            }
        };
    }

    public void increaseSkillsUpgrade() {
        if (clickedSkill != null) {
            IntegerConfigPath incrementedSkill = clickedSkill.getUpgrade();
            int currentIncrement = incrementedSkill.getValue();
            int newIncrement = currentIncrement + 1;
            clickedSkill.setIncrement(newIncrement);
            clickedSkill.getConfig().save();
            player.openInventory(getInventory());
        }
    }

    public void decreaseSkillsUpgrade() {
        if (clickedSkill != null) {
            IntegerConfigPath incrementedSkill = clickedSkill.getUpgrade();
            int currentIncrement = incrementedSkill.getValue();
            int newIncrement = Math.max(0, currentIncrement - 1);
            clickedSkill.setIncrement(newIncrement);
            clickedSkill.getConfig().save();
            player.openInventory(getInventory());
        }
    }

    public void decreaseSkillsPrice() {
        if (clickedSkill != null) {
            int currentPrice = clickedSkill.getPrice().getValue();
            int newPrice = Math.max(0, currentPrice - 1);
            clickedSkill.setPrice(newPrice);
            clickedSkill.getConfig().save();
            player.openInventory(getInventory());
        }
    }

    public void increaseSkillsPrice() {
        if (clickedSkill != null) {
            int currentPrice = clickedSkill.getPrice().getValue();
            int newPrice = currentPrice + 1;
            clickedSkill.setPrice(newPrice);
            clickedSkill.getConfig().save();
            player.openInventory(getInventory());
        }
    }

    public void decreaseSkillsUpgradeIncrementPrice() {
        if (clickedSkill != null) {
            int currentPrice = clickedSkill.getPrice().getValue();
            int newPrice = Math.max(0, currentPrice - 1);
            clickedSkill.setPrice(newPrice);
            clickedSkill.getConfig().save();
            player.openInventory(getInventory());
        }
    }

    public void increaseSkillsUpgradeIncrementPrice() {
        if (clickedSkill != null) {
            int currentPrice = clickedSkill.getPrice().getValue();
            int newPrice = currentPrice + 1;
            clickedSkill.setPrice(newPrice);
            clickedSkill.getConfig().save();
            player.openInventory(getInventory());
        }
    }

    public void increaseSkillsMaxLevel() {
        if (clickedSkill != null) {
            int currentMaxLevel = clickedSkill.getMaxLevel();
            int newMaxLevel = currentMaxLevel + 1;
            clickedSkill.setMaxLevel(newMaxLevel);
            clickedSkill.getConfig().save();
            player.openInventory(getInventory());
        }

    }

    public void decreaseSkillsMaxLevel() {
        if (clickedSkill != null) {
            int currentMaxLevel = clickedSkill.getMaxLevel();
            int newMaxLevel = Math.max(0, currentMaxLevel - 1);
            clickedSkill.setMaxLevel(newMaxLevel);
            clickedSkill.getConfig().save();
            player.openInventory(getInventory());
        }
    }

    private void toggleConfirmation(ClickType event, int slot) {
        if ((event == ClickType.LEFT) && slot == MainConfig.ADMIN_CONFIRMATION_TOGGLE_SLOT.getValue()) {
            String nextConfirmationName = MainConfig.CONFIRMATION_PURCHASE_SKILLS_NAME.getValue();

            switch (nextConfirmationName) {
                case "Purchase Skills":
                    nextConfirmationName = MainConfig.CONFIRMATION_PURCHASE_POINTS_NAME.getValue();
                    bukkitConfig.set(MainConfig.CONFIRMATION_PURCHASE_SKILLS_NAME.getPath(), nextConfirmationName);
                    break;
                case "Purchase Points":
                    nextConfirmationName = MainConfig.CONFIRMATION_RESET_SKILLS_NAME.getValue();
                    bukkitConfig.set(MainConfig.CONFIRMATION_PURCHASE_POINTS_NAME.getPath(), nextConfirmationName);
                    break;
                case "Reset Skills":
                    nextConfirmationName = MainConfig.CONFIRMATION_PURCHASE_SKILLS_NAME.getValue();
                    bukkitConfig.set(MainConfig.CONFIRMATION_RESET_SKILLS_NAME.getPath(), nextConfirmationName);
                    break;
            }
            bukkitConfig.save();
        }
        if ((event == ClickType.RIGHT) && slot == MainConfig.ADMIN_CONFIRMATION_TOGGLE_SLOT.getValue()) {
            boolean currentConfirmationValue;
            String currentConfirmationName = MainConfig.CONFIRMATION_PURCHASE_SKILLS_NAME.getValue();

            switch (currentConfirmationName) {
                case "Purchase Skills":
                    currentConfirmationValue = MainConfig.CONFIRMATION_PURCHASE_SKILLS.getValue();
                    bukkitConfig.set(MainConfig.CONFIRMATION_PURCHASE_SKILLS.getPath(), !currentConfirmationValue);
                    break;
                case "Purchase Points":
                    currentConfirmationValue = MainConfig.CONFIRMATION_PURCHASE_POINTS.getValue();
                    bukkitConfig.set(MainConfig.CONFIRMATION_PURCHASE_POINTS.getPath(), !currentConfirmationValue);
                    break;
                case "Reset Skills":
                    currentConfirmationValue = MainConfig.CONFIRMATION_RESET_SKILLS.getValue();
                    bukkitConfig.set(MainConfig.CONFIRMATION_RESET_SKILLS.getPath(), !currentConfirmationValue);
                    break;
            }
            bukkitConfig.save();
        }
        player.openInventory(getInventory());
    }
}
