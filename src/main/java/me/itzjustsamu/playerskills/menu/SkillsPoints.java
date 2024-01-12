package me.itzjustsamu.playerskills.menu;

import com.cryptomorin.xseries.XSound;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.fundingsource.FundingSource;
import me.itzjustsamu.playerskills.fundingsource.VaultFundingSource;
import me.itzjustsamu.playerskills.fundingsource.XPFundingSource;
import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class SkillsPoints implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final Skill skill;
    private final SPlayer sPlayer;
    private final BukkitConfig bukkitConfig;

    private final Skill clickedSkill;


    public SkillsPoints(PlayerSkills plugin, Player player, Skill skill, SPlayer sPlayer, Skill clickedSkill) {
        this.plugin = plugin;
        this.player = player;
        this.skill = skill;
        this.sPlayer = sPlayer;
        this.bukkitConfig = new BukkitConfig(new File(plugin.getDataFolder(), "config.yml"));
        bukkitConfig.setup();
        this.clickedSkill = clickedSkill;

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

        if (skill != null) {
            inventory.setItem(MainConfig.POINTS_SLOT.getValue(), MainConfig.POINTS_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.POINTS_RESET_SLOT.getValue(), MainConfig.POINTS_RESET_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.POINTS_REFUND_SLOT.getValue(), MainConfig.POINTS_REFUND_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.POINTS_INCREMENT_SLOT.getValue(), MainConfig.POINTS_INCREMENT_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.POINTS_FUNDING_SLOT.getValue(), MainConfig.POINTS_FUNDING_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.GUI_BACK_SLOT.getValue(), MainConfig.GUI_BACK_DISPLAY.getValue().build(this.player));
        }

        return inventory;
    }

    public void onClick(int slot, ClickType clickType) {
        if (slot == MainConfig.POINTS_SLOT.getValue()) {
            if (clickType == ClickType.LEFT) {
                decreasePoints();
                XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.8F, 1.0F);
            } else if (clickType == ClickType.RIGHT) {
                increasePoints();
                XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.8F, 1.0F);
            }
        } else if (slot == MainConfig.POINTS_RESET_SLOT.getValue()) {
            if (clickType == ClickType.LEFT) {
                decreaseResetPoints();
                XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.8F, 1.0F);
            } else if (clickType == ClickType.RIGHT) {
                increaseResetPoints();
                XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.8F, 1.0F);
            }
        } else if (slot == MainConfig.POINTS_REFUND_SLOT.getValue()) {
            toggleRefundPoints();
            XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.8F, 1.0F);
        } else if (slot == MainConfig.POINTS_FUNDING_SLOT.getValue()) {
            toggleFundingSource();
            XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.8F, 1.0F);
        } else if (slot == MainConfig.POINTS_INCREMENT_SLOT.getValue()) {
            if (clickType == ClickType.LEFT) {
                decreaseIncrementedPoints();
                XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.8F, 1.0F);
            } else if (clickType == ClickType.RIGHT) {
                increaseIncrementedPoints();
                XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.8F, 1.0F);
            }
        } else if (slot == MainConfig.GUI_BACK_SLOT.getValue()) {
            SkillsSettings skillsSettings = new SkillsSettings(plugin, player, skill, sPlayer, clickedSkill);
            skillsSettings.open(this.player);
            XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 0.8F, 1.0F);
        }
    }

    private void increasePoints() {
        int currentPoints = MainConfig.POINTS_PRICE.getValue();
        int newPoints = currentPoints + 1;
        MainConfig.POINTS_PRICE.setValue(newPoints);
        player.openInventory(getInventory());
        bukkitConfig.getOriginal().set(MainConfig.POINTS_PRICE.getPath(), newPoints);
        bukkitConfig.save();
    }

    private void decreasePoints() {
        int currentPoints = MainConfig.POINTS_PRICE.getValue();
        int newPoints = Math.max(0, currentPoints - 1);
        MainConfig.POINTS_PRICE.setValue(newPoints);
        player.openInventory(getInventory());
        bukkitConfig.getOriginal().set(MainConfig.POINTS_PRICE.getPath(), newPoints);
        bukkitConfig.save();
    }

    private void increaseResetPoints() {
        int currentResetPoints = MainConfig.POINTS_RESET_PRICE.getValue();
        int newResetPoints = currentResetPoints + 1;
        MainConfig.POINTS_RESET_PRICE.setValue(newResetPoints);
        player.openInventory(getInventory());
        bukkitConfig.getOriginal().set(MainConfig.POINTS_RESET_PRICE.getPath(), newResetPoints);
        bukkitConfig.save();
    }

    private void decreaseResetPoints() {
        int currentResetPoints = MainConfig.POINTS_RESET_PRICE.getValue();
        int newResetPoints = Math.max(0, currentResetPoints - 1);
        MainConfig.POINTS_RESET_PRICE.setValue(newResetPoints);
        player.openInventory(getInventory());
        bukkitConfig.getOriginal().set(MainConfig.POINTS_RESET_PRICE.getPath(), newResetPoints);
        bukkitConfig.save();
    }

    private void toggleRefundPoints() {
        boolean currentRefundStatus = MainConfig.POINTS_REFUND_POINTS.getValue();
        boolean newRefundStatus = !currentRefundStatus;
        MainConfig.POINTS_REFUND_POINTS.setValue(newRefundStatus);
        player.openInventory(getInventory());
        bukkitConfig.getOriginal().set(MainConfig.POINTS_REFUND_POINTS.getPath(), newRefundStatus);
        bukkitConfig.save();
    }

    private void toggleFundingSource() {
        // Get the current funding source
        FundingSource currentFundingSource = MainConfig.POINTS_FUNDING_SOURCE.getValue();

        // Toggle between VAULT and XP
        FundingSource newFundingSource = (currentFundingSource instanceof VaultFundingSource)
                ? new XPFundingSource()
                : new VaultFundingSource();

        // Get the new funding source identifier
        String newFundingSourceIdentifier = (newFundingSource instanceof VaultFundingSource) ? "VAULT" : "XP";

        MainConfig.POINTS_FUNDING_SOURCE.setValue(newFundingSource);
        player.openInventory(getInventory());

        // Save the new funding source identifier to the configuration
        bukkitConfig.getOriginal().set(MainConfig.POINTS_FUNDING_SOURCE.getPath(), newFundingSourceIdentifier);
        bukkitConfig.save();
    }


    private void increaseIncrementedPoints() {
        int currentPoints = MainConfig.POINTS_INCREMENT_PRICE.getValue();
        int newPoints = currentPoints + 1;
        MainConfig.POINTS_INCREMENT_PRICE.setValue(newPoints);
        player.openInventory(getInventory());
        bukkitConfig.getOriginal().set(MainConfig.POINTS_INCREMENT_PRICE.getPath(), newPoints);
        bukkitConfig.save();
    }

    private void decreaseIncrementedPoints() {
        int currentPoints = MainConfig.POINTS_INCREMENT_PRICE.getValue();
        int newPoints = Math.max(0, currentPoints - 1);
        MainConfig.POINTS_INCREMENT_PRICE.setValue(newPoints);
        player.openInventory(getInventory());
        bukkitConfig.getOriginal().set(MainConfig.POINTS_INCREMENT_PRICE.getPath(), newPoints);
        bukkitConfig.save();
    }
}
