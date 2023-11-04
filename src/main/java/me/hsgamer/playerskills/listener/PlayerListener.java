package me.hsgamer.playerskills.listener;

import me.hsgamer.playerskills.player.SPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        SPlayer.load(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        SPlayer.save(SPlayer.get(event.getPlayer().getUniqueId()));
        SPlayer.unload(event.getPlayer().getUniqueId());
    }
}
