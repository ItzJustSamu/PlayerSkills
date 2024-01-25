package me.itzjustsamu.playerskills.menu;

import com.sun.tools.javac.Main;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.config.path.impl.IntegerConfigPath;
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

public class SkillsSettings implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final Skill skill;
    private final SPlayer sPlayer;
    private final BukkitConfig bukkitConfig;
    private final Skill clickedSkill;

    public SkillsSettings(PlayerSkills plugin, Player player, Skill skill, SPlayer sPlayer, Skill clickedSkill) {
        this.plugin = plugin;
        this.player = player;
        this.skill = skill;
        this.sPlayer = sPlayer;
        this.bukkitConfig = new BukkitConfig(new File(plugin.getDataFolder(), "skills" + File.separator + skill.getSkillsConfigName() + ".yml"));
        this.clickedSkill = clickedSkill;
        bukkitConfig.setup();
    }

    @Override
    public @NotNull Inventory getInventory() {
        String title = ColorUtils.colorize(MainConfig.GUI_TITLE.getValue());
        int size = MainConfig.GUI_SIZE.getValue();
        Inventory inventory = Bukkit.createInventory(this, size, title);

        if (MainConfig.GUI_BACKGROUND_ENABLED.getValue()) {
            ItemStack background = MainConfig.GUI_BACKGROUND_DISPLAY.getValue().build(player.getUniqueId());
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        if (clickedSkill != null) {
            inventory.setItem(MainConfig.PURCHASE_POINTS_SLOT.getValue(), MainConfig.PURCHASE_POINTS_DISPLAY.getValue().build(this.player.getUniqueId()));
            inventory.setItem(MainConfig.GUI_RESET_SLOT.getValue(), MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player.getUniqueId()));
            inventory.setItem(MainConfig.GUI_BACK_SLOT.getValue(), MainConfig.GUI_BACK_DISPLAY.getValue().build(this.player.getUniqueId()));
            inventory.setItem(MainConfig.GUI_ADMIN_SLOT.getValue(), MainConfig.GUI_ADMIN_DISPLAY.getValue().build(this.player.getUniqueId()));
        }

        return inventory;
    }

    @Override
    public void onClick(int slot, ClickType event) {
        if (slot == MainConfig.PURCHASE_POINTS_SLOT.getValue()) {
            Runnable callback = getRunnable();
            if (event == ClickType.RIGHT && player.hasPermission(ADMIN)) {
                SkillsPoints skillsPoints = new SkillsPoints(this.plugin, this.player, this.skill, this.sPlayer, this.clickedSkill);
                skillsPoints.open(this.player);
            } else if (event == ClickType.LEFT && MainConfig.GUI_CONFIRMATION_ENABLED_PURCHASE_SKILL_POINTS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, this.player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                confirmationMenu.open(this.player);
            } else {
                callback.run();
            }
        } else if (slot == MainConfig.GUI_RESET_SLOT.getValue()) {
            Runnable callback = getRunnable(event);
            ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player.getUniqueId()), callback, this);
            confirmationMenu.open(this.player);
        } else if (slot == MainConfig.GUI_BACK_SLOT.getValue()) {
            SkillsList skillsList = new SkillsList(this.plugin, this.player, this.sPlayer);
            skillsList.open(this.player);
            playUIButtonClickSound(player);
            CommonStringReplacer.resetSkill();
        } else if (slot == 3) {
            handleSkillClick(event);
        } else if (slot == MainConfig.GUI_ADMIN_SLOT.getValue()) {
            SkillsAdmin SkillsAdmin = new SkillsAdmin(this.plugin, this.player, this.skill, this.sPlayer, this.clickedSkill);
            SkillsAdmin.open(this.player);
        }
    }

    private void handleSkillClick(ClickType event) {
        if ((event == ClickType.LEFT || event == ClickType.RIGHT) && clickedSkill.getLevel(this.sPlayer) < clickedSkill.getMaxLevel()) {
            int price = clickedSkill.getPrice().getValue();
            if (this.sPlayer.getPoints() >= price) {
                Runnable callback = () -> {
                    this.sPlayer.setLevel(clickedSkill.getSkillsConfigName(), clickedSkill.getLevel(this.sPlayer) + 1);
                    this.sPlayer.setPoints(this.sPlayer.getPoints() - price);
                    playExperienceOrbPickupSound(player);
                    this.open(this.player);
                };

                if (MainConfig.GUI_CONFIRMATION_ENABLED_PURCHASE_SKILLS.getValue()) {
                    ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, clickedSkill.getDisplayItem(this.player), callback, this);
                    confirmationMenu.open(this.player);
                } else {
                    callback.run();
                }
            } else {
                playItemBreakSound(player);
                this.open(this.player);
            }
        }
    }

    @NotNull
    private Runnable getRunnable(ClickType event) {
        if (event == ClickType.LEFT || event == ClickType.RIGHT && MainConfig.GUI_CONFIRMATION_ENABLED_RESET_SKILLS.getValue()) {
            return getResetSkillsCallback();
        } else {
            return getCallbackWithoutConfirmation();
        }
    }

    @NotNull
    private Runnable getResetSkillsCallback() {
        return () -> {
            int resetPoint = MainConfig.POINTS_RESET_PRICE.getValue();
            if (sPlayer.getPoints() >= resetPoint) {
                sPlayer.setPoints(this.sPlayer.getPoints() - resetPoint);
                if (MainConfig.POINTS_REFUND_POINTS.getValue()) {
                    refundPointsForReset();
                }
                this.sPlayer.getSkills().clear();
                playGenericExplodeSound(player);
                this.open(this.player);
            } else {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player.getUniqueId()), null, this);
                confirmationMenu.open(this.player);
            }
        };
    }

    private void refundPointsForReset() {
        for (String s : this.sPlayer.getSkills().keySet()) {
            for (int i = 1; i <= this.sPlayer.Level(s); ++i) {
                sPlayer.setPoints(sPlayer.getPoints() + plugin.getSkills().get(s).getPrice().getValue());
            }
        }
    }

    @NotNull
    private Runnable getCallbackWithoutConfirmation() {
        return () -> {
            int resetPoint = MainConfig.POINTS_RESET_PRICE.getValue();
            if (this.sPlayer.getPoints() >= resetPoint) {
                this.sPlayer.setPoints(this.sPlayer.getPoints() - resetPoint);
                if (MainConfig.POINTS_REFUND_POINTS.getValue()) {
                    refundPointsForReset();
                }
                this.sPlayer.getSkills().clear();
                playGenericExplodeSound(player);
                this.open(this.player);
            } else {
                playItemBreakSound(player);
                this.open(this.player);
            }
        };
    }

    @NotNull
    private Runnable getRunnable() {
        int price = this.sPlayer.getPointPrice();
        return () -> {
            if (MainConfig.POINTS_FUNDING_SOURCE.getValue().doTransaction(this.sPlayer, price, this.player)) {
                this.sPlayer.setPoints(this.sPlayer.getPoints() + 1);
                playUIButtonClickSound(player);
                this.open(this.player);
            } else {
                playItemBreakSound(player);
            }
        };
    }
}
