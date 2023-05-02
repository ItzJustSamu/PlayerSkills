package me.fatpigsarefat.skills.listeners;

import java.util.Objects;
import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.events.ResetSkillEvent;
import me.fatpigsarefat.skills.events.UpgradeSkillEvent;
import me.fatpigsarefat.skills.helper.MessageHelper;
import me.fatpigsarefat.skills.managers.ConfigurationManager;
import me.fatpigsarefat.skills.managers.FileManager;
import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {
    private final MessageHelper messageHelper = new MessageHelper();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        FileManager.Config gui = PlayerSkills.getFileManager().getConfig("gui");
        FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");
        if (e.getClick() != null && e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Objects.<String>requireNonNull(gui.get().getString("gui.title"))))) {
            InventoryAction a = e.getAction();
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            SkillManager sm = PlayerSkills.getSkillManager();
            if (gui.get().getBoolean("gui.display.points-purchase.right-click")) {
                a = InventoryAction.PICKUP_ALL;
            }
            if (a == InventoryAction.MOVE_TO_OTHER_INVENTORY || a == InventoryAction.COLLECT_TO_CURSOR || a == InventoryAction.HOTBAR_SWAP || a == InventoryAction.HOTBAR_MOVE_AND_READD || a == InventoryAction.PLACE_ALL || a == InventoryAction.PLACE_ONE || a == InventoryAction.PLACE_SOME || a == InventoryAction.SWAP_WITH_CURSOR) {
                e.setCancelled(true);
                player.updateInventory();
                player.setItemOnCursor(null);
            }

            if (e.getSlot() == gui.get().getInt("gui.display.points-purchase.slot") && e.getAction().equals(a)) {
                if (player.getLevel() >= sm.getPointPrice(player))
                    if (config.get().getBoolean("points.restriction")) {
                        if (sm.getTotalPointsSpent(player) != config.get().getInt("points.restriction")) {
                            sm.buySkillPoint(player);
                            reconstructInventory(player, false);
                        }
                    } else {
                        sm.buySkillPoint(player);
                        reconstructInventory(player, false);
                    }
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-all.slot")) {
                if (!PlayerSkills.allowReset)
                    return;
                sm.resetAll(player);
                reconstructInventory(player, false);
                player.sendMessage(this.messageHelper.getMessage("skill_full_reset", new String[0]));
            } else if (e.getSlot() == gui.get().getInt("gui.display.strength-normal.slot")) {
                updateSkill(sm, player, Skill.STRENGTH);
            } else if (e.getSlot() == gui.get().getInt("gui.display.criticals-normal.slot")) {
                updateSkill(sm, player, Skill.CRITICALS);
            } else if (e.getSlot() == gui.get().getInt("gui.display.resistance-normal.slot")) {
                updateSkill(sm, player, Skill.RESISTANCE);
            } else if (e.getSlot() == gui.get().getInt("gui.display.archery-normal.slot")) {
                updateSkill(sm, player, Skill.ARCHERY);
            } else if (e.getSlot() == gui.get().getInt("gui.display.health-normal.slot")) {
                updateSkill(sm, player, Skill.HEALTH);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-strength.slot")) {
                resetSkill(sm, player, Skill.STRENGTH);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-criticals.slot")) {
                resetSkill(sm, player, Skill.CRITICALS);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-resistance.slot")) {
                resetSkill(sm, player, Skill.RESISTANCE);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-archery.slot")) {
                resetSkill(sm, player, Skill.ARCHERY);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-health.slot")) {
                resetSkill(sm, player, Skill.HEALTH);
            }
        }
    }

    public static void reconstructInventory(Player player, boolean completeUpdate) {
        FileManager.Config gui = PlayerSkills.getFileManager().getConfig("gui");
        SkillManager sm = PlayerSkills.getSkillManager();
        ConfigurationManager cm = new ConfigurationManager();
        Inventory inv = Bukkit.createInventory(null, gui.get().getInt("gui.size") * 9, ChatColor.translateAlternateColorCodes('&', gui.get().getString("gui.title")));
        int strength = 1;
        int criticals = 1;
        int resistance = 1;
        int archery = 1;
        int health = 1;
        strength = sm.getSkillLevel(player, Skill.STRENGTH);
        criticals = sm.getSkillLevel(player, Skill.CRITICALS);
        resistance = sm.getSkillLevel(player, Skill.RESISTANCE);
        archery = sm.getSkillLevel(player, Skill.ARCHERY);
        health = sm.getSkillLevel(player, Skill.HEALTH);
        ItemStack skillpointsIs = new ItemStack(cm.getItemStack("points-purchase", player));
        if (sm.getSkillPoints(player) > 0) {
            skillpointsIs.setAmount(sm.getSkillPoints(player));
        } else {
            skillpointsIs.setAmount(1);
        }
        ItemStack playerstatsIs = cm.getItemStack("stats", player);
        playerstatsIs.setAmount(1);
        ItemStack strengthIs = cm.getItemStack("strength-normal", player);
        strengthIs.setAmount(strength);
        ItemStack criticalsIs = cm.getItemStack("criticals-normal", player);
        criticalsIs.setAmount(criticals);
        ItemStack resistanceIs = cm.getItemStack("resistance-normal", player);
        resistanceIs.setAmount(resistance);
        ItemStack archeryIs = cm.getItemStack("archery-normal", player);
        archeryIs.setAmount(archery);
        ItemStack healthIs = cm.getItemStack("health-normal", player);
        healthIs.setAmount(health);
        ItemStack rs = cm.getItemStack("reset-strength", player);
        rs.setAmount(1);
        ItemStack rc = cm.getItemStack("reset-criticals", player);
        rc.setAmount(1);
        ItemStack rr = cm.getItemStack("reset-resistance", player);
        rr.setAmount(1);
        ItemStack ra = cm.getItemStack("reset-archery", player);
        ra.setAmount(1);
        ItemStack rh = cm.getItemStack("reset-health", player);
        rh.setAmount(1);
        ItemStack barrier2Is = cm.getItemStack("reset-all", player);
        barrier2Is.setAmount(1);
        inv.setItem(gui.get().getInt("gui.display.points-purchase.slot"), skillpointsIs);
        inv.setItem(gui.get().getInt("gui.display.stats.slot"), playerstatsIs);
        inv.setItem(gui.get().getInt("gui.display.strength-normal.slot"), strengthIs);
        inv.setItem(gui.get().getInt("gui.display.criticals-normal.slot"), criticalsIs);
        inv.setItem(gui.get().getInt("gui.display.resistance-normal.slot"), resistanceIs);
        inv.setItem(gui.get().getInt("gui.display.archery-normal.slot"), archeryIs);
        inv.setItem(gui.get().getInt("gui.display.health-normal.slot"), healthIs);
        if (PlayerSkills.allowReset) {
            inv.setItem(gui.get().getInt("gui.display.reset-strength.slot"), rs);
            inv.setItem(gui.get().getInt("gui.display.reset-criticals.slot"), rc);
            inv.setItem(gui.get().getInt("gui.display.reset-resistance.slot"), rr);
            inv.setItem(gui.get().getInt("gui.display.reset-archery.slot"), ra);
            inv.setItem(gui.get().getInt("gui.display.reset-health.slot"), rh);
            inv.setItem(gui.get().getInt("gui.display.reset-all.slot", 5), barrier2Is);
        }
        if (!completeUpdate) {
            player.getOpenInventory().getTopInventory().setContents(inv.getContents());
        } else {
            player.closeInventory();
            player.openInventory(inv);
        }
        player.updateInventory();
    }

    public void updateSkill(SkillManager sm, Player player, Skill skill) {
        Bukkit.getPluginManager().callEvent((Event)new UpgradeSkillEvent(player, sm, skill));
    }

    public void resetSkill(SkillManager sm, Player player, Skill skill) {
        Bukkit.getPluginManager().callEvent((Event)new ResetSkillEvent(player, sm, skill));
    }
}
