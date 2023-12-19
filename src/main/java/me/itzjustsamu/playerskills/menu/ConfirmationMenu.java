package me.itzjustsamu.playerskills.menu;

import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.itzjustsamu.playerskills.PlayerSkills;
import me.itzjustsamu.playerskills.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConfirmationMenu implements Menu {

    private final PlayerSkills plugin;
    private final Player player;
    private final ItemStack display;
    private final Runnable callback;
    private final Menu superMenu;

    public ConfirmationMenu(PlayerSkills plugin, Player player, ItemStack display, Runnable callback) {
        this(plugin, player, display, callback, null);
    }

    public ConfirmationMenu(PlayerSkills plugin, Player player, ItemStack display, Runnable callback, Menu superMenu) {
        this.plugin = plugin;
        this.player = player;
        this.display = display;
        this.callback = callback;
        this.superMenu = superMenu;
    }

    @Override
    public @NotNull Inventory getInventory() {
        String title = ColorUtils.colorize(MainConfig.GUI_CONFIRMATION_TITLE.getValue());
        int size = 27;

        Inventory inventory = Bukkit.createInventory(this, size, title);

        if (MainConfig.GUI_CONFIRMATION_BACKGROUND_ENABLED.getValue()) {
            ItemStack background = MainConfig.GUI_CONFIRMATION_BACKGROUND_DISPLAY.getValue().build(player);
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        ItemStack yes = MainConfig.GUI_CONFIRMATION_ACCEPT.getValue().build(player);
        ItemStack no = MainConfig.GUI_CONFIRMATION_DENY.getValue().build(player);

        inventory.setItem(10, no);
        inventory.setItem(11, no);
        inventory.setItem(12, no);
        inventory.setItem(13, display);
        inventory.setItem(14, yes);
        inventory.setItem(15, yes);
        inventory.setItem(16, yes);

        return inventory;
    }

    @Override
    public void onClick(int slot) {
        if (slot == 10 || slot == 11 || slot == 12) {
            if (superMenu != null) {
                superMenu.open(player);
            } else {
                player.closeInventory();
            }
        } else if (slot == 14 || slot == 15 || slot == 16) {
            callback.run();
            if (superMenu != null) {
                superMenu.open(player);
            }
        }
    }

    @Override
    public void onClose() {
        if (superMenu != null) {
            Scheduler.CURRENT.runEntityTask(plugin, player, () -> superMenu.open(player), false);
        }
    }

}