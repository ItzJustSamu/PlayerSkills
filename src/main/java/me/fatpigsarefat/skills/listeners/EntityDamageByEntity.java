package me.fatpigsarefat.skills.listeners;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class EntityDamageByEntity implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityByEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            if (PlayerSkills.instance.getConfig().getBoolean("worlds.restricted") && !PlayerSkills.instance.getConfig().getStringList("worlds.allowed-worlds").contains(player.getLocation().getWorld().getName()))
                return;
            SkillManager sm = PlayerSkills.getSkillManager();
            int skill = sm.getSkillLevel(player, Skill.STRENGTH) - 1;
            double d = e.getDamage() / 100.0D;
            d *= (Integer) PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH);
            double finalDamage = skill * d;
            e.setDamage(e.getDamage() + finalDamage);
            boolean result = (player.getFallDistance() > 0.0F && !player.isOnGround() && !player.hasPotionEffect(PotionEffectType.BLINDNESS) && player.getVehicle() == null && !player.isSprinting() && !player.getLocation().getBlock().isLiquid() && !player.getLocation().add(0.0D, 0.0D, 1.0D).getBlock().getType().equals(Material.LADDER));
            double dmg = e.getDamage();
            if (result && dmg > 0.0D) {
                int sk = sm.getSkillLevel(player, Skill.CRITICALS) - 1;
                double damage = e.getDamage() / 150.0D;
                damage *= (Integer) PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS);
                double fdamage = sk * damage;
                e.setDamage(e.getDamage() + fdamage);
            }
        } else if (e.getDamager() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player player2) {
                if (PlayerSkills.instance.getConfig().getBoolean("worlds.restricted") && !PlayerSkills.instance.getConfig().getStringList("worlds.allowed-worlds").contains(player2.getLocation().getWorld().getName()))
                    return;
                SkillManager sm2 = PlayerSkills.getSkillManager();
                int skill2 = sm2.getSkillLevel(player2, Skill.ARCHERY) - 1;
                double d2 = e.getDamage() / 100.0D;
                d2 *= (Integer) PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY);
                double finalDamage2 = skill2 * d2;
                e.setDamage(e.getDamage() + finalDamage2);
            }
        }
    }
}
