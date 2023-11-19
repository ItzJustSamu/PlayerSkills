package me.hsgamer.playerskills.menu;

import com.cryptomorin.xseries.XSound;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.playerskills.config.MainConfig;
import me.hsgamer.playerskills.menu.ConfirmationMenu;
import me.hsgamer.playerskills.PlayerSkills;
import me.hsgamer.playerskills.config.MainConfig;
import me.hsgamer.playerskills.player.SPlayer;
import me.hsgamer.playerskills.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

    @Override
    public @NotNull Inventory getInventory() {
        String title = ColorUtils.colorize(MainConfig.GUI_TITLE.getValue());
        int size = MainConfig.GUI_SIZE.getValue();

        Inventory inventory = Bukkit.createInventory(this, size, title);

        for (Skill skill : plugin.getSkillRegistrar().values()) {
            inventory.setItem(skill.getGuiSlot(), skill.getDisplayItem(player));
        }

        inventory.setItem(MainConfig.GUI_INFO_SLOT.getValue(), MainConfig.GUI_INFO_DISPLAY.getValue().build(player));
        inventory.setItem(MainConfig.GUI_POINTS_SLOT.getValue(), MainConfig.GUI_POINTS_DISPLAY.getValue().build(player));
        inventory.setItem(MainConfig.GUI_RESET_SLOT.getValue(), MainConfig.GUI_RESET_DISPLAY.getValue().build(player));

        if (MainConfig.GUI_BACKGROUND_ENABLED.getValue()) {
            ItemStack background = MainConfig.GUI_BACKGROUND_DISPLAY.getValue().build(player);
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, background);
                }
            }
        }

        return inventory;
    }


    @Override
    public void onClick(int slot) {
        for (Skill skill : plugin.getSkillRegistrar().values()) {
            if (slot == skill.getGuiSlot() && (skill.getLevel(sPlayer) < skill.getMaxLevel())) {
                int price = skill.getPriceOverride(skill.getLevel(sPlayer) + 1);
                if (sPlayer.getPoints() >= price) {
                    Runnable callback = () -> {
                        sPlayer.setLevel(skill.getConfigName(), skill.getLevel(sPlayer) + 1);
                        sPlayer.setPoints(sPlayer.getPoints() - price);
                        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 2, 2);
                        this.open(player);
                    };
                    if (MainConfig.GUI_CONFIRMATION_ENABLED_PURCHASE_SKILLS.getValue()) {
                        ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player,
                                player.getOpenInventory().getTopInventory().getItem(slot), callback, SkillsMenu.this);
                        confirmationMenu.open(player);
                    } else {
                        callback.run();
                    }
                    return;
                } else {
                    XSound.ENTITY_ITEM_BREAK.play(player, 1, 0.6f);
                }
            }
        }


        if (slot == MainConfig.GUI_POINTS_SLOT.getValue()) {
            int price = sPlayer.getNextPointPrice();
            Runnable callback = () -> {
                if (MainConfig.POINTS_FUNDING_SOURCE.getValue().doTransaction(sPlayer, price, player)) {
                    sPlayer.setPoints(sPlayer.getPoints() + 1);
                    XSound.UI_BUTTON_CLICK.play(player, 1, 1);
                    this.open(player);
                } else {
                    XSound.ENTITY_ITEM_BREAK.play(player, 1, 0.6f);
                }
            };
            if (MainConfig.GUI_CONFIRMATION_ENABLED_PURCHASE_SKILL_POINTS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player,
                        player.getOpenInventory().getTopInventory().getItem(slot), callback, SkillsMenu.this);
                confirmationMenu.open(player);
            } else {
                callback.run();
            }
        } else if (slot == MainConfig.GUI_RESET_SLOT.getValue()) {
            Runnable callback = () -> {
                int resetPoint = MainConfig.POINTS_RESET_PRICE.getValue();
                if (sPlayer.getPoints() >= resetPoint) {
                    sPlayer.setPoints(sPlayer.getPoints() - resetPoint);
                    if (MainConfig.POINTS_REFUND_SKILL_POINTS.getValue()) {
                        for (String s : sPlayer.getSkills().keySet()) {
                            for (int i = 1; i <= sPlayer.getLevel(s); i++) {
                                sPlayer.setPoints(sPlayer.getPoints() + plugin.getSkillRegistrar().get(s).getPriceOverride(i));
                            }
                        }
                    }
                    sPlayer.getSkills().clear();
                    XSound.ENTITY_GENERIC_EXPLODE.play(player, 1, 1);
                    this.open(player);
                } else {
                    XSound.ENTITY_ITEM_BREAK.play(player, 1, 0.6f);
                }
            };
            if (MainConfig.GUI_CONFIRMATION_ENABLED_RESET_SKILLS.getValue()) {
                ConfirmationMenu confirmationMenu = new ConfirmationMenu(plugin, player,
                        player.getOpenInventory().getTopInventory().getItem(slot), callback, SkillsMenu.this);
                confirmationMenu.open(player);
            } else {
                callback.run();
            }
        }
    }
}