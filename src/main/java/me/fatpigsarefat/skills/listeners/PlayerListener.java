package me.fatpigsarefat.skills.listeners;

import me.fatpigsarefat.skills.PlayerSkills;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    public PlayerListener() {
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onPlayerJoin(PlayerJoinEvent e) {
        PlayerSkills.getInstance().checkUpdates(e.getPlayer());
        if (PlayerSkills.useHolograms) {
            PlayerSkills.getHologramManager().show(e.getPlayer());
        }

    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onPlayerLeave(PlayerQuitEvent e) {
        PlayerSkills.potionEffect.remove(e.getPlayer());

        if (PlayerSkills.useHolograms) {
            PlayerSkills.getHologramManager().remove(e.getPlayer());
        }

    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onPlayerLeave(PlayerDeathEvent e) {
        Player p = e.getEntity();
        PlayerSkills.potionEffect.remove(p);

    }
}
