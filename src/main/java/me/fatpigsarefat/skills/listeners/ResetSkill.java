package me.fatpigsarefat.skills.listeners;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.events.ResetSkillEvent;
import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
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
        sm.setSkillPoints(player, sm.getSkillPoints(player) + sm.getSkillLevel(player, skill) - 1);
        sm.setSkillLevel(player, skill, 1);
        InventoryClick.reconstructInventory(player, false);
        player.sendMessage(messageHelper.getMessage("skill_reset", new String[] { skill.name().toLowerCase() }));
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 60.0F, 100.0F);
    }
}
