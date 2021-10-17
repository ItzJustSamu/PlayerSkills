package com.leonardobishop.playerskills2.menu;

import org.bukkit.inventory.Inventory;

public interface Menu {

    Inventory toInventory();

    void onClick(int slot);

    default void onClose() {
        // EMPTY
    }

}
