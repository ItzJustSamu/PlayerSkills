package com.leonardobishop.playerskills2.menu;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;

public class MenuController implements Listener {

    private static final HashMap<HumanEntity, Menu> tracker = new HashMap<>();

    public static void open(Player player, Menu menu) {
        player.openInventory(menu.toInventory());
        tracker.put(player, menu);
    }

    public static boolean isMenuOpenElsewhere(Class<?> type) {
        for (Menu menu : tracker.values()) {
            if (menu.getClass() == type) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (tracker.containsKey(event.getWhoClicked())) {
            if ((event.getWhoClicked().getOpenInventory() != null)
                    && (event.getClickedInventory() == event.getWhoClicked().getOpenInventory().getTopInventory())) {
                event.setCancelled(true);
                Menu menu = tracker.get(event.getWhoClicked());
                menu.onClick(event.getSlot());
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (tracker.containsKey(event.getPlayer())) {
            Menu menu = tracker.get(event.getPlayer());
            tracker.remove(event.getPlayer());
            menu.onClose();
        }
    }

}
