package me.fatpigsarefat.skills.listeners;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.events.ResetSkillEvent;
import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ResetSkill implements Listener {
    @EventHandler
    public void onSkillReset(ResetSkillEvent e) {
        MessageHelper messageHelper = new MessageHelper();
        Player player = e.getPlayer();
        SkillManager sm = e.getSkillManager();
        Skill skill = e.getSkill();
        if (!PlayerSkills.allowReset)
            return;

        String version = Bukkit.getServer().getVersion();
        if (version.contains("1.8") || version.contains("1.9")) {
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
        } else {
            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 1.0F, 1.0F);
        }

        sm.setSkillPoints(player, sm.getSkillPoints(player) + sm.getSkillLevel(player, skill) - 1);
        sm.setSkillLevel(player, skill, 1);
        InventoryClick.reconstructInventory(player, false);
        player.sendMessage(messageHelper.getMessage("skill_reset", new String[] { skill.name().toLowerCase() }));
    }
}


