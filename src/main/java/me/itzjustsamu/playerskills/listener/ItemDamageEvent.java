package me.itzjustsamu.playerskills.listener;

import me.itzjustsamu.playerskills.player.Durability;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemDamageEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageEvent(PlayerItemDamageEvent event) {
        if(event.isCancelled()) return;
        final Durability item = new Durability(event.getItem());
        if(item.hasDurability()) {
            item.damageItem(event.getDamage());
            event.setDamage(0);
            item.updateLoreDurability(event.getPlayer());
        }
    }
}