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

        if (skill != null) {
            inventory.setItem(MainConfig.POINTS_SLOT.getValue(), MainConfig.POINTS_DISPLAY.getValue().build(this.player));
            inventory.setItem(MainConfig.GUI_BACK_SLOT.getValue(), MainConfig.GUI_BACK_DISPLAY.getValue().build(this.player));
        }

        return inventory;
    }

    public void onClick(int slot, ClickType clickType) {
        if (slot == MainConfig.POINTS_SLOT.getValue()) {
            if (clickType == ClickType.LEFT) {
                decreasePoints();
            } else if (clickType == ClickType.RIGHT) {
                increasePoints();
            }
        } else if (slot == MainConfig.GUI_BACK_SLOT.getValue()) {
            SkillsMenu skillsMenu = new SkillsMenu(this.plugin, this.player, this.sPlayer);
            skillsMenu.open(this.player);
            XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 1.0F, 1.0F);
        }
    }

    private void increasePoints() {
        int currentPoints = MainConfig.POINTS_PRICE.getValue();

        int newPoints = currentPoints + 1;
        MainConfig.POINTS_PRICE.setValue(newPoints);

        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 1.0F, 1.0F);
        player.openInventory(getInventory());
    }

    private void decreasePoints() {
        int currentPoints = MainConfig.POINTS_PRICE.getValue();
        int newPoints = Math.max(0, currentPoints - 1);

        MainConfig.POINTS_PRICE.setValue(newPoints);
        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 1.0F, 1.0F);
        player.openInventory(getInventory());
    }
}
