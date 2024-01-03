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
    private final Skill skill;
    private final SPlayer sPlayer;

    public SkillsPoints(PlayerSkills plugin, Player player, Skill skill, SPlayer sPlayer) {
        this.plugin = plugin;
        this.player = player;
        this.skill = skill;
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

        // Check if skill is not null before using it
        if (skill != null) {
            inventory.setItem(MainConfig.POINTS_SLOT.getValue(), MainConfig.POINTS_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.GUI_BACK_SLOT.getValue(), MainConfig.GUI_BACK_DISPLAY.getValue().build(this.player));
        }

        return inventory;
    }

    public void onClick(int slot, ClickType clickType) {
        // Check if it's a left or right click and if the slot matches POINTS_SLOT
        if (slot == MainConfig.POINTS_SLOT.getValue()) {
            if (clickType == ClickType.LEFT) {
                // Decrease points logic
                decreasePoints();
            } else if (clickType == ClickType.RIGHT) {
                // Increase points logic
                increasePoints();
            }
        } else if (slot == MainConfig.GUI_BACK_SLOT.getValue() && clickType == ClickType.LEFT) {
            // Back to SkillsSettings logic
            SkillsSettings skillsSettings = new SkillsSettings(this.plugin, this.player, null, sPlayer);
            skillsSettings.open(this.player);
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
        player.openInventory(getInventory()); // Update inventory
    }

    private void decreasePoints() {
        // Decrease points logic
        int currentPoints = MainConfig.POINTS_PRICE.getValue();
        int newPoints = Math.max(1, currentPoints - 1); // Ensure points do not go below 1

        MainConfig.POINTS_PRICE.setValue(newPoints);
        XSound.ENTITY_ITEM_BREAK.play(player, 1.0F, 0.6F);
        player.openInventory(getInventory()); // Update inventory
    }
}
