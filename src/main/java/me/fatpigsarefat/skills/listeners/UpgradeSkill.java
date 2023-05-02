package me.fatpigsarefat.skills.listeners;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.events.UpgradeSkillEvent;
import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.managers.FileManager;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UpgradeSkill implements Listener {

    @EventHandler
    public void onUpgradeSkill(UpgradeSkillEvent e) {
        FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");
        Player player = e.getPlayer();
        SkillManager sm = e.getSkillManager();
        Skill skill = e.getSkill();
        MessageHelper messageHelper = new MessageHelper();

        if (config.get().getBoolean("permissions.use")) {
            if (!player.hasPermission("playerskills." + skill.name().toLowerCase())) {
                player.sendMessage(messageHelper.getMessage("no_permissions_message", new String[0]));
                return;
            } else if (config.get().getBoolean("permissions.level-perms")
                    && !player.hasPermission("playerskills." + skill.name() + "." + sm.getSkillLevel(player, skill))) {
                try {
                    player.playSound(player.getLocation(), Sound.valueOf("ANVIL_LAND"), 0.8F, 0.8F);
                } catch (IllegalArgumentException ignored) {
                    player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 0.8F, 0.8F);
                }
                player.sendMessage(messageHelper.getMessage("skill_upgrade_false_perms", new String[0]));
                return;
            }
        }

        if (sm.getSkillPoints(player) <= 0) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf("ANVIL_LAND"), 0.8F, 0.8F);
            } catch (IllegalArgumentException ignored) {
                player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 0.8F, 0.8F);
            }
            player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
            return;
        }

        if (sm.getSkillLevel(player, skill) >= sm.getMaximumLevel(skill)) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf("ANVIL_LAND"), 0.8F, 0.8F);
            } catch (IllegalArgumentException ignored) {
                player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 0.8F, 0.8F);
            }
            player.sendMessage(messageHelper.getMessage("skill_upgrade_limit", new String[] { skill.name().toLowerCase() }));
            return;
        }

        sm.setSkillPoints(player, sm.getSkillPoints(player) - 1);
        sm.setSkillLevel(player, skill, sm.getSkillLevel(player, skill) + 1);
        InventoryClick.reconstructInventory(player, false);
        try {
            player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 0.8F, 0.8F);
        } catch (IllegalArgumentException ignored) {
            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 0.8F, 0.8F);
        }
        player.sendMessage(messageHelper.getMessage("skill_upgrade", new String[] { skill.name().toLowerCase() }));
    }
}
