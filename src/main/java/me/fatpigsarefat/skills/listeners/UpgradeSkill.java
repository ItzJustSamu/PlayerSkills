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
    public UpgradeSkill() {
    }

    @EventHandler
    public void onUpgradeSkill(UpgradeSkillEvent e) {
        FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");
        Player player = e.getPlayer();
        SkillManager sm = e.getSkillManager();
        Skill skill = e.getSkill();
        MessageHelper messageHelper = new MessageHelper();
        if (config.get().getBoolean("permissions.use")) {
            if (player.hasPermission("playerskills." + skill.name().toLowerCase())) {
                if (config.get().getBoolean("permissions.level-perms") && !player.hasPermission("playerskills." + skill.name() + "." + sm.getSkillLevel(player, skill))) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
                    player.sendMessage(messageHelper.getMessage("skill_upgrade_false_perms", new String[0]));
                    return;
                }

                if (sm.getSkillPoints(player) <= 0) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
                    player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
                    return;
                }

                if (sm.getSkillLevel(player, skill) >= sm.getMaximumLevel(skill)) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
                    player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
                    return;
                }

                sm.setSkillPoints(player, sm.getSkillPoints(player) - 1);
                sm.setSkillLevel(player, skill, sm.getSkillLevel(player, skill) + 1);
                InventoryClick.reconstructInventory(player, false);
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 100.0F, 100.0F);
                player.sendMessage(messageHelper.getMessage("skill_upgrade", new String[]{skill.name().toLowerCase()}));
            } else {
                player.sendMessage(messageHelper.getMessage("no_permissions_message", new String[0]));
            }
        } else {
            if (sm.getSkillPoints(player) <= 0) {
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
                player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
                return;
            }

            if (sm.getSkillLevel(player, skill) >= sm.getMaximumLevel(skill)) {
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 100.0F, 100.0F);
                player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
                return;
            }

            sm.setSkillPoints(player, sm.getSkillPoints(player) - 1);
            sm.setSkillLevel(player, skill, sm.getSkillLevel(player, skill) + 1);
            InventoryClick.reconstructInventory(player, false);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 100.0F, 100.0F);
            player.sendMessage(messageHelper.getMessage("skill_upgrade", new String[]{skill.name().toLowerCase()}));
        }

        if (PlayerSkills.useHolograms) {
            PlayerSkills.getHologramManager().update(player);
        }

    }
}
