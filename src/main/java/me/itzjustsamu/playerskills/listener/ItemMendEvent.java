package me.itzjustsamu.playerskills.listener;

import me.itzjustsamu.playerskills.player.Durability;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;

public class ItemMendEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMendEvent(PlayerItemMendEvent event) {
        if(event.isCancelled()) return;
        final Durability item = new Durability(event.getItem());
        if(item.hasDurability()) {
            item.repairItem(event.getRepairAmount());
            event.setRepairAmount(0);
            item.updateLoreDurability(event.getPlayer());
        }
    }
}