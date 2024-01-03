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

public class SkillsPoints implements Menu {
    private final PlayerSkills plugin;
    private final Player player;

    public SkillsPoints(PlayerSkills plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
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
                inventory.setItem(MainConfig.POINTS_PRICE.getValue(), MainConfig.POINTS_DISPLAY.getValue().build(this.player));
                inventory.setItem(MainConfig.GUI_BACK_SLOT.getValue(), MainConfig.GUI_BACK_DISPLAY.getValue().build(this.player));
            }
        }
        return inventory;
    }

    public void onClick(int slot, ClickType clickType) {
        // Check if it's a left or right click and if the slot matches POINTS_PRICE
        if ((clickType == ClickType.LEFT || clickType == ClickType.RIGHT) && slot == MainConfig.POINTS_PRICE.getValue()) {
            if (clickType == ClickType.LEFT) {
                // Decrease points logic
                decreasePoints();
            } else {
                // Increase points logic
                increasePoints();
            }
        }
    }

    private void increasePoints() {
        // Increase points logic
        int currentPoints = MainConfig.POINTS_PRICE.getValue();
        int newPoints = currentPoints + 1;

        // Set a limit if needed
        // int maxPoints = ...;
        // newPoints = Math.min(newPoints, maxPoints);

        MainConfig.POINTS_PRICE.setValue(newPoints);

        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 1.0F, 1.0F);
    }

    private void decreasePoints() {
        // Decrease points logic
        int currentPoints = MainConfig.POINTS_PRICE.getValue();
        int newPoints = Math.max(1, currentPoints - 1); // Ensure points do not go below 1

        MainConfig.POINTS_PRICE.setValue(newPoints);
        XSound.ENTITY_ITEM_BREAK.play(player, 1.0F, 0.6F);
    }
}
