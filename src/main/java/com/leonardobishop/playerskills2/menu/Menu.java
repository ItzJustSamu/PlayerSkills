package com.leonardobishop.playerskills2.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public interface Menu extends InventoryHolder {

    void onClick(int slot);

    default void onClose() {
        // EMPTY
    }

    default void open(Player player) {
        player.openInventory(getInventory());
    }
}
