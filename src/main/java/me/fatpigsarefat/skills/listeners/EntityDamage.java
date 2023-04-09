package me.fatpigsarefat.skills.listeners;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamage implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player)e.getEntity();
            if (PlayerSkills.instance.getConfig().getBoolean("worlds.restricted") && !PlayerSkills.instance.getConfig().getStringList("worlds.allowed-worlds").contains(player.getLocation().getWorld().getName()))
                return;
            SkillManager sm = PlayerSkills.getSkillManager();
            int skill = sm.getSkillLevel(player, Skill.RESISTANCE) - 1;
            double d = e.getDamage() / 100.0D;
            d *= ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE)).intValue();
            double finalDamage = skill * d;
            e.setDamage(e.getDamage() - finalDamage);
        }
    }
}
