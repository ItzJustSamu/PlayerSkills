package me.fatpigsarefat.skills.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigurationManager {
    private FileManager.Config gui = PlayerSkills.getFileManager().getConfig("gui");
    private FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");

    public ConfigurationManager() {
    }

    public ItemStack getItemStack(String s, Player player) {
        String pathRoot = "gui.display." + s + ".";
        String name = ChatColor.translateAlternateColorCodes('&', (String)Objects.requireNonNull(this.gui.get().getString(pathRoot + "name")));
        name = this.placeholder(name, player);
        ArrayList<String> lore = new ArrayList();
        if (this.gui.get().contains(pathRoot + "lore")) {
            Iterator var6 = this.gui.get().getStringList(pathRoot + "lore").iterator();

            while(var6.hasNext()) {
                String key = (String)var6.next();
                key = ChatColor.translateAlternateColorCodes('&', key);
                key = this.placeholder(key, player);
                lore.add(key);
            }
        }

        ItemStack itemToGive = new ItemStack((Material)Objects.requireNonNull(Material.getMaterial((String)Objects.requireNonNull(this.gui.get().getString(pathRoot + "material")))));
        ItemMeta itemToGiveMeta = itemToGive.getItemMeta();
        itemToGiveMeta.setDisplayName(name);
        itemToGiveMeta.setLore(lore);
        itemToGive.setItemMeta(itemToGiveMeta);
        return itemToGive;
    }

    private String placeholder(String s, Player player) {
        SkillManager sm = PlayerSkills.getSkillManager();
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
        int xpPrice;
        int healthSkillAfter;
        if (s.contains("strength")) {
            s = s.replace("%strength%", "[" + strength + "/" + sm.getMaximumLevel(Skill.STRENGTH) + "]");
            s = s.replace("%strengthincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH)).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.STRENGTH) - 1) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH) + 100;
            healthSkillAfter = sm.getSkillLevel(player, Skill.STRENGTH) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH) + 100;
            if (sm.getSkillLevel(player, Skill.STRENGTH) >= sm.getMaximumLevel(Skill.STRENGTH)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%strengthprogress%", ChatColor.GREEN.toString() + xpPrice + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "%");
        }

        if (s.contains("criticals")) {
            s = s.replace("%criticals%", "[" + criticals + "/" + sm.getMaximumLevel(Skill.CRITICALS) + "]");
            s = s.replace("%criticalsincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS)).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.CRITICALS) - 1) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS) + 150;
            healthSkillAfter = sm.getSkillLevel(player, Skill.CRITICALS) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS) + 150;
            if (sm.getSkillLevel(player, Skill.CRITICALS) >= sm.getMaximumLevel(Skill.CRITICALS)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%criticalsprogress%", ChatColor.GREEN.toString() + xpPrice + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "%");
        }

        if (s.contains("resistance")) {
            s = s.replace("%resistance%", "[" + resistance + "/" + sm.getMaximumLevel(Skill.RESISTANCE) + "]");
            s = s.replace("%resistanceincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE)).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.RESISTANCE) - 1) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE);
            healthSkillAfter = sm.getSkillLevel(player, Skill.RESISTANCE) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE);
            if (sm.getSkillLevel(player, Skill.RESISTANCE) >= sm.getMaximumLevel(Skill.RESISTANCE)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%resistanceprogress%", ChatColor.GREEN.toString() + xpPrice + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "%");
        }

        if (s.contains("archery")) {
            s = s.replace("%archery%", "[" + archery + "/" + sm.getMaximumLevel(Skill.ARCHERY) + "]");
            s = s.replace("%archeryincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY)).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.ARCHERY) - 1) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY) + 100;
            healthSkillAfter = sm.getSkillLevel(player, Skill.ARCHERY) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY) + 100;
            if (sm.getSkillLevel(player, Skill.ARCHERY) >= sm.getMaximumLevel(Skill.ARCHERY)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%archeryprogress%", ChatColor.GREEN.toString() + xpPrice + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "%");
        }

        if (s.contains("health")) {
            s = s.replace("%health%", "[" + health + "/" + sm.getMaximumLevel(Skill.HEALTH) + "]");
            s = s.replace("%healthincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.HEALTH)).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.HEALTH) - 1) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.HEALTH) + 20;
            healthSkillAfter = sm.getSkillLevel(player, Skill.HEALTH) * (Integer)PlayerSkills.getSkillMultipliers().get(Skill.HEALTH) + 20;
            if (sm.getSkillLevel(player, Skill.HEALTH) >= sm.getMaximumLevel(Skill.HEALTH)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%healthprogress%", ChatColor.GREEN.toString() + xpPrice + "HP " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "HP");
        }

        int xpPrice = true;
        xpPrice = this.config.get().getInt("xp.price");
        if (this.config.get().getBoolean("xp.add-total-to-price")) {
            xpPrice += sm.getTotalPointsSpent(player) * this.config.get().getInt("xp.add-total-to-price-multiplier");
        }

        s = s.replace("%pointsprice%", xpPrice + "XP");
        s = s.replace("%points%", sm.getSkillPoints(player) + "");
        s = s.replace("%username%", player.getName());
        s = s.replace("%expierencelevel%", player.getLevel() + "");
        if (s.contains("expierence")) {
            String expierenceBar = "";

            for(double f = 0.0; f <= (double)player.getExp(); f += 0.03) {
                expierenceBar = expierenceBar + ChatColor.GREEN + "|";
            }

            int toAdd = 30 - ChatColor.stripColor(expierenceBar).length();

            for(int i = 0; i <= toAdd; ++i) {
                expierenceBar = expierenceBar + ChatColor.GRAY + "|";
            }

            s = s.replace("%expierencebar%", expierenceBar);
        }

        s = s.replace("%totalspent%", sm.getTotalPointsSpent(player) + "");
        s = s.replace("%strength-points-spend%", strength - 1 + "");
        s = s.replace("%criticals-points-spend%", criticals - 1 + "");
        s = s.replace("%resistance-points-spend%", resistance - 1 + "");
        s = s.replace("%archery-points-spend%", archery - 1 + "");
        s = s.replace("%health-points-spend%", health - 1 + "");
        return s;
    }
}
