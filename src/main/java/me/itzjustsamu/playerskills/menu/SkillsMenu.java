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

    }
}
