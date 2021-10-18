package com.leonardobishop.playerskills2.menu;

import com.cryptomorin.xseries.XMaterial;
import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    public Inventory getInventory() {
        String title = Config.get(plugin, "gui-confirmation.title").getColoredString();
        int size = 27;

        Inventory inventory = Bukkit.createInventory(this, size, title);

        if (plugin.getConfig().getBoolean("gui-confirmation.background.enabled")) {
            ItemStack background = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
            assert background != null;
            ItemMeta backgroundm = background.getItemMeta();
            backgroundm.setDisplayName(" ");
            background.setItemMeta(backgroundm);

            ItemStack config;
            if ((config = Config.get(plugin, "gui-confirmation.background").getItemStack()) != null) {
                background = config;
            }
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        ItemStack yes = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
        assert yes != null;
        ItemMeta yesm = yes.getItemMeta();
        yesm.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Confirm");
        yes.setItemMeta(yesm);
        ItemStack yesconfig;
        if ((yesconfig = Config.get(plugin, "gui-confirmation.accept").getItemStack()) != null) {
            yes = yesconfig;
        }

        ItemStack no = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
        assert no != null;
        ItemMeta nom = no.getItemMeta();
        nom.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "Decline");
        no.setItemMeta(nom);
        ItemStack noconfig;
        if ((noconfig = Config.get(plugin, "gui-confirmation.deny").getItemStack()) != null) {
            no = noconfig;
        }

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
            superMenu.open(player);
        }
    }

}
