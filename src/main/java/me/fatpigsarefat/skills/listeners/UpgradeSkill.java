package me.fatpigsarefat.skills.listeners;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.events.UpgradeSkillEvent;
import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.managers.FileManager;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.Bukkit;
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
            if (player.hasPermission("playerskills." + skill.name().toLowerCase())) {
                if (config.get().getBoolean("permissions.level-perms") &&
                        !player.hasPermission("playerskills." + skill.name() + "." + sm.getSkillLevel(player, skill))) {
                    String version = Bukkit.getServer().getVersion();
                    if (version.contains("1.8") || version.contains("1.9")) {
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    } else {
                        player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 1.0F, 1.0F);
                    }
                    player.sendMessage(messageHelper.getMessage("skill_upgrade_false_perms", new String[0]));
                    return;
                }
                if (sm.getSkillPoints(player) <= 0) {
                    String version = Bukkit.getServer().getVersion();
                    if (version.contains("1.8") || version.contains("1.9")) {
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    } else {
                        player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 1.0F, 1.0F);
                    }
                    player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
                    return;
                }
                if (sm.getSkillLevel(player, skill) >= sm.getMaximumLevel(skill)) {
                    String version = Bukkit.getServer().getVersion();
                    if (version.contains("1.8") || version.contains("1.9")) {
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    } else {
                        player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 1.0F, 1.0F);
                    }
                    player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
                    return;
                }
                sm.setSkillPoints(player, sm.getSkillPoints(player) - 1);
                sm.setSkillLevel(player, skill, sm.getSkillLevel(player, skill) + 1);
                InventoryClick.reconstructInventory(player, false);
                String version = Bukkit.getServer().getVersion();
                if (version.contains("1.8") || version.contains("1.9")) {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1.0F, 1.0F);
                }
                player.sendMessage(messageHelper.getMessage("skill_upgrade", new String[] { skill.name().toLowerCase() }));
            } else {
                player.sendMessage(messageHelper.getMessage("no_permissions_message", new String[0]));
            }
        } else {
            if (sm.getSkillPoints(player) <= 0) {
                String version = Bukkit.getServer().getVersion();
                if (version.contains("1.8") || version.contains("1.9")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 1.0F, 1.0F);
                }
                player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
                return;
            }
            if (sm.getSkillLevel(player, skill) >= sm.getMaximumLevel(skill)) {
                String version = Bukkit.getServer().getVersion();
                if (version.contains("1.8") || version.contains("1.9")) {
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                } else {
                    player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 1.0F, 1.0F);
                }
                player.sendMessage(messageHelper.getMessage("skill_upgrade_false", new String[0]));
                return;
            }
            sm.setSkillPoints(player, sm.getSkillPoints(player) - 1);
            sm.setSkillLevel(player, skill, sm.getSkillLevel(player, skill) + 1);
            InventoryClick.reconstructInventory(player, false);
            String version = Bukkit.getServer().getVersion();
            if (version.contains("1.8") || version.contains("1.9")) {
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            } else {
                player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 1.0F, 1.0F);
            }
            player.sendMessage(messageHelper.getMessage("skill_upgrade", new String[] { skill.name().toLowerCase() }));
        }
    }
}
