package me.itzjustsamu.playerskills.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IDurabilityItem {
    ItemStack getItem();
    boolean hasDurability();
    int getDurability();
    int getMaxDurability();
    void setDurability(int durability);
    void createDurability(int maxDurability, int durability);
    void damageItem(int amount);
    void repairItem(int amount);
    void printDebugInfo(Player player);

}

