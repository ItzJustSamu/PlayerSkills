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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {
    private MessageHelper messageHelper = new MessageHelper();

    public InventoryClick() {
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        FileManager.Config gui = PlayerSkills.getFileManager().getConfig("gui");
        FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");
        if (e.getClickedInventory() != null && e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', (String)Objects.requireNonNull(gui.get().getString("gui.title"))))) {
            e.setCancelled(true);
            Player player = (Player)e.getWhoClicked();
            SkillManager sm = PlayerSkills.getSkillManager();
            InventoryAction a;
            if (gui.get().getBoolean("gui.display.points-purchase.right-click")) {
                a = InventoryAction.PICKUP_HALF;
            } else {
                a = InventoryAction.PICKUP_ALL;
            }

            if (e.getSlot() == gui.get().getInt("gui.display.points-purchase.slot") && e.getAction().equals(a)) {
                if (player.getLevel() >= sm.getPointPrice(player)) {
                    if (config.get().getBoolean("points.restriction")) {
                        if (sm.getTotalPointsSpent(player) != config.get().getInt("points.restriction-per")) {
                            sm.buySkillPoint(player);
                            reconstructInventory(player, false);
                        } else {
                            player.sendMessage(this.messageHelper.getMessage("points_limit", new String[]{config.get().getString("points.restriction-per")}));
                        }
                    } else {
                        sm.buySkillPoint(player);
                        reconstructInventory(player, false);
                    }
                }
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-all.slot")) {
                if (!PlayerSkills.allowReset) {
                    return;
                }

                sm.resetAll(player);
                reconstructInventory(player, false);
                player.playSound(player.getLocation(), Sound.EXPLODE, 100.0F, 100.0F);
                player.sendMessage(this.messageHelper.getMessage("skill_full_reset", new String[0]));
            } else if (e.getSlot() == gui.get().getInt("gui.display.strength-normal.slot")) {
                this.updateSkill(sm, player, Skill.STRENGTH);
            } else if (e.getSlot() == gui.get().getInt("gui.display.criticals-normal.slot")) {
                this.updateSkill(sm, player, Skill.CRITICALS);
            } else if (e.getSlot() == gui.get().getInt("gui.display.resistance-normal.slot")) {
                this.updateSkill(sm, player, Skill.RESISTANCE);
            } else if (e.getSlot() == gui.get().getInt("gui.display.archery-normal.slot")) {
                this.updateSkill(sm, player, Skill.ARCHERY);
            } else if (e.getSlot() == gui.get().getInt("gui.display.health-normal.slot")) {
                this.updateSkill(sm, player, Skill.HEALTH);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-strength.slot")) {
                this.resetSkill(sm, player, Skill.STRENGTH);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-criticals.slot")) {
                this.resetSkill(sm, player, Skill.CRITICALS);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-resistance.slot")) {
                this.resetSkill(sm, player, Skill.RESISTANCE);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-archery.slot")) {
                this.resetSkill(sm, player, Skill.ARCHERY);
            } else if (e.getSlot() == gui.get().getInt("gui.display.reset-health.slot")) {
                this.resetSkill(sm, player, Skill.HEALTH);
            }
        }

    }

    public static void reconstructInventory(Player player, boolean completeUpdate) {
        FileManager.Config gui = PlayerSkills.getFileManager().getConfig("gui");
        SkillManager sm = PlayerSkills.getSkillManager();
        ConfigurationManager cm = new ConfigurationManager();
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, gui.get().getInt("gui.size") * 9, ChatColor.translateAlternateColorCodes('&', gui.get().getString("gui.title")));
        int strength = true;
        int criticals = true;
        int resistance = true;
        int archery = true;
        int health = true;
        int strength = sm.getSkillLevel(player, Skill.STRENGTH);
        int criticals = sm.getSkillLevel(player, Skill.CRITICALS);
        int resistance = sm.getSkillLevel(player, Skill.RESISTANCE);
        int archery = sm.getSkillLevel(player, Skill.ARCHERY);
        int health = sm.getSkillLevel(player, Skill.HEALTH);
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
        Bukkit.getPluginManager().callEvent(new UpgradeSkillEvent(player, sm, skill));
    }

    public void resetSkill(SkillManager sm, Player player, Skill skill) {
        Bukkit.getPluginManager().callEvent(new ResetSkillEvent(player, sm, skill));
    }
}
