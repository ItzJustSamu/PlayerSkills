package me.itzjustsamu.playerskills.menu;

import com.cryptomorin.xseries.XSound;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
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

import java.io.File;

import static me.itzjustsamu.playerskills.Permissions.ADMIN;

public class SkillsSettings implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final Skill skill;
    private final SPlayer sPlayer;
    private final BukkitConfig bukkitConfig;

    public SkillsSettings(PlayerSkills plugin, Player player, Skill skill, SPlayer sPlayer) {
        this.plugin = plugin;
        this.player = player;
        this.skill = skill;
        this.sPlayer = sPlayer;
        this.bukkitConfig = new BukkitConfig(new File(plugin.getDataFolder(), "skills" + File.separator + skill.getSkillsConfigName() + ".yml"));
        bukkitConfig.setup();
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

        if (skill != null) {
            inventory.setItem(MainConfig.GUI_POINTS_SLOT.getValue(), MainConfig.GUI_POINTS_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.GUI_RESET_SLOT.getValue(), MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.GUI_BACK_SLOT.getValue(), MainConfig.GUI_BACK_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.SKILLS_INCREMENT_SLOT.getValue(), MainConfig.SKILLS_INCREMENT_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.GUI_NEXT_SLOT.getValue(), MainConfig.GUI_NEXT_DISPLAY.getValue().build(this.player));
            inventory.setItem(3, skill.getDisplayItem(this.player));
        }

        return inventory;
    }

    @Override
    public void onClick(int slot, ClickType event) {
        if (slot == MainConfig.GUI_POINTS_SLOT.getValue()) {
            Runnable callback = getRunnable();
            if (event == ClickType.RIGHT && player.hasPermission(ADMIN)) {
                SkillsPoints skillsPoints = new SkillsPoints(this.plugin, this.player, this.skill, this.sPlayer);
                skillsPoints.open(this.player);
            } else if (event == ClickType.LEFT && MainConfig.GUI_CONFIRMATION_ENABLED_PURCHASE_SKILL_POINTS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, this.player.getOpenInventory().getTopInventory().getItem(slot), callback, this);
                confirmationMenu.open(this.player);
            } else {
                callback.run();
            }
        } else if (slot == MainConfig.GUI_RESET_SLOT.getValue()) {
            Runnable callback = getRunnable(event);
            ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player), callback, this);
            confirmationMenu.open(this.player);
        } else if (slot == MainConfig.GUI_BACK_SLOT.getValue()) {
            SkillsList skillsList = new SkillsList(this.plugin, this.player, this.sPlayer);
            skillsList.open(this.player);
        } else if (slot == 3) {
            handleSkillClick(event);
        } else if (slot == MainConfig.SKILLS_INCREMENT_SLOT.getValue()) {
            handleIncrementClick(event);
        }
    }

    private void handleSkillClick(ClickType event) {
        if ((event == ClickType.LEFT || event == ClickType.RIGHT) && skill.getLevel(this.sPlayer) < skill.getMaxLevel()) {
            int price = skill.getPrice(skill.getLevel(this.sPlayer) + 1);
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

    private void handleIncrementClick(ClickType event) {
        if (event == ClickType.RIGHT && player.hasPermission(ADMIN)) {
            increaseSkillsIncrement();
            XSound.UI_BUTTON_CLICK.play(this.player, 1.0F, 1.0F);
        } else if (event == ClickType.LEFT && player.hasPermission(ADMIN)) {
            decreaseSkillsIncrement();
            XSound.UI_BUTTON_CLICK.play(this.player, 1.0F, 1.0F);
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
                XSound.ENTITY_GENERIC_EXPLODE.play(this.player, 1.0F, 1.0F);
                this.open(this.player);
            } else {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(this.plugin, this.player, MainConfig.GUI_RESET_DISPLAY.getValue().build(this.player), null, this);
                confirmationMenu.open(this.player);
            }
        };
    }

    private void refundPointsForReset() {
        for (String s : this.sPlayer.getSkills().keySet()) {
            for (int i = 1; i <= this.sPlayer.Level(s); ++i) {
                this.sPlayer.setPoints(this.sPlayer.getPoints() + plugin.getSkills().get(s).getPrice(i));
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
                XSound.ENTITY_GENERIC_EXPLODE.play(this.player, 1.0F, 1.0F);
                this.open(this.player);
            } else {
                XSound.ENTITY_ITEM_BREAK.play(this.player, 1.0F, 0.6F);
            }
        };
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

    public void increaseSkillsIncrement() {
        if (skill != null) {
            int currentIncrement = skill.getIncrement();
            int newIncrement = currentIncrement + 1;
            skill.setIncrement(newIncrement);
            player.openInventory(getInventory());
        }
    }

    public void decreaseSkillsIncrement() {
        if (skill != null) {
            int currentIncrement = skill.getIncrement();
            int newIncrement = Math.max(0, currentIncrement - 1);
            skill.setIncrement(newIncrement);
            player.openInventory(getInventory());
        }
    }
}
