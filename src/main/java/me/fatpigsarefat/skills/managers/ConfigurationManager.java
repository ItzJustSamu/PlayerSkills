package me.fatpigsarefat.skills.managers;

import java.util.ArrayList;
import java.util.Objects;
import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigurationManager {
    private final FileManager.Config gui = PlayerSkills.getFileManager().getConfig("gui");

    private final FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");

    public ItemStack getItemStack(String s, Player player) {
        String pathRoot = "gui.display." + s + ".";
        String name = ChatColor.translateAlternateColorCodes('&', Objects.<String>requireNonNull(this.gui.get().getString(pathRoot + "name")));
        name = placeholder(name, player);
        ArrayList<String> lore = new ArrayList<>();
        if (this.gui.get().contains(pathRoot + "lore"))
            for (String key : this.gui.get().getStringList(pathRoot + "lore")) {
                key = ChatColor.translateAlternateColorCodes('&', key);
                key = placeholder(key, player);
                lore.add(key);
            }
        ItemStack itemToGive = new ItemStack(Objects.<Material>requireNonNull(Material.getMaterial(Objects.<String>requireNonNull(this.gui.get().getString(pathRoot + "material")))));
        ItemMeta itemToGiveMeta = itemToGive.getItemMeta();
        itemToGiveMeta.setDisplayName(name);
        itemToGiveMeta.setLore(lore);
        itemToGive.setItemMeta(itemToGiveMeta);
        return itemToGive;
    }

    private String placeholder(String s, Player player) {
        SkillManager sm = PlayerSkills.getSkillManager();
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
        if (s.contains("strength")) {
            s = s.replace("%strength%", "[" + strength + "/" + sm.getMaximumLevel(Skill.STRENGTH) + "]");
            s = s.replace("%strengthincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH)).toString());
            int strengthSkillBefore = (sm.getSkillLevel(player, Skill.STRENGTH) - 1) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH) + 100;
            int strengthSkillAfter = sm.getSkillLevel(player, Skill.STRENGTH) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH) + 100;
            if (sm.getSkillLevel(player, Skill.STRENGTH) >= sm.getMaximumLevel(Skill.STRENGTH))
                strengthSkillAfter = strengthSkillBefore;
            s = s.replace("%strengthprogress%", ChatColor.GREEN.toString() + strengthSkillBefore + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + strengthSkillAfter + "%");
        }
        if (s.contains("criticals")) {
            s = s.replace("%criticals%", "[" + criticals + "/" + sm.getMaximumLevel(Skill.CRITICALS) + "]");
            s = s.replace("%criticalsincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS)).toString());
            int criticalsSkillBefore = (sm.getSkillLevel(player, Skill.CRITICALS) - 1) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS) + 150;
            int criticalsSkillAfter = sm.getSkillLevel(player, Skill.CRITICALS) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS) + 150;
            if (sm.getSkillLevel(player, Skill.CRITICALS) >= sm.getMaximumLevel(Skill.CRITICALS))
                criticalsSkillAfter = criticalsSkillBefore;
            s = s.replace("%criticalsprogress%", ChatColor.GREEN.toString() + criticalsSkillBefore + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + criticalsSkillAfter + "%");
        }
        if (s.contains("resistance")) {
            s = s.replace("%resistance%", "[" + resistance + "/" + sm.getMaximumLevel(Skill.RESISTANCE) + "]");
            s = s.replace("%resistanceincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE)).toString());
            int resistanceSkillBefore = (sm.getSkillLevel(player, Skill.RESISTANCE) - 1) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE);
            int resistanceSkillAfter = sm.getSkillLevel(player, Skill.RESISTANCE) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE);
            if (sm.getSkillLevel(player, Skill.RESISTANCE) >= sm.getMaximumLevel(Skill.RESISTANCE))
                resistanceSkillAfter = resistanceSkillBefore;
            s = s.replace("%resistanceprogress%", ChatColor.GREEN.toString() + resistanceSkillBefore + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + resistanceSkillAfter + "%");
        }
        if (s.contains("archery")) {
            s = s.replace("%archery%", "[" + archery + "/" + sm.getMaximumLevel(Skill.ARCHERY) + "]");
            s = s.replace("%archeryincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY)).toString());
            int archerySkillBefore = (sm.getSkillLevel(player, Skill.ARCHERY) - 1) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY) + 100;
            int archerySkillAfter = sm.getSkillLevel(player, Skill.ARCHERY) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY) + 100;
            if (sm.getSkillLevel(player, Skill.ARCHERY) >= sm.getMaximumLevel(Skill.ARCHERY))
                archerySkillAfter = archerySkillBefore;
            s = s.replace("%archeryprogress%", ChatColor.GREEN.toString() + archerySkillBefore + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + archerySkillAfter + "%");
        }
        if (s.contains("health")) {
            s = s.replace("%health%", "[" + health + "/" + sm.getMaximumLevel(Skill.HEALTH) + "]");
            s = s.replace("%healthincrement%", ((Integer)PlayerSkills.getSkillMultipliers().get(Skill.HEALTH)).toString());
            int healthSkillBefore = (sm.getSkillLevel(player, Skill.HEALTH) - 1) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.HEALTH) + 20;
            int healthSkillAfter = sm.getSkillLevel(player, Skill.HEALTH) * (Integer) PlayerSkills.getSkillMultipliers().get(Skill.HEALTH) + 20;
            if (sm.getSkillLevel(player, Skill.HEALTH) >= sm.getMaximumLevel(Skill.HEALTH))
                healthSkillAfter = healthSkillBefore;
            s = s.replace("%healthprogress%", ChatColor.GREEN.toString() + healthSkillBefore + "HP " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "HP");
        }
        int xpPrice = 1;
        xpPrice = this.config.get().getInt("xp.price");
        if (this.config.get().getBoolean("xp.add-total-to-price"))
            xpPrice += sm.getTotalPointsSpent(player) * this.config.get().getInt("xp.add-total-to-price-multiplier");
        s = s.replace("%pointsprice%", xpPrice + "XP");
        s = s.replace("%points%", String.valueOf(sm.getSkillPoints(player)));
        s = s.replace("%username%", player.getName());
        s = s.replace("%expierencelevel%", String.valueOf(player.getLevel()));
        if (s.contains("expierence")) {
            StringBuilder experienceBar = new StringBuilder();
            double f;
            for (f = 0.0D; f <= player.getExp(); f += 0.03D)
                experienceBar.append(ChatColor.GREEN).append("|");
            for (int toAdd = 30 - ChatColor.stripColor(experienceBar.toString()).length(), i = 0; i <= toAdd; i++)
                experienceBar.append(ChatColor.GRAY).append("|");
            s = s.replace("%expierencebar%", experienceBar.toString());
        }
        s = s.replace("%totalspent%", String.valueOf(sm.getTotalPointsSpent(player)));
        s = s.replace("%strength-points-spend%", String.valueOf(strength - 1));
        s = s.replace("%criticals-points-spend%", String.valueOf(criticals - 1));
        s = s.replace("%resistance-points-spend%", String.valueOf(resistance - 1));
        s = s.replace("%archery-points-spend%", String.valueOf(archery - 1));
        s = s.replace("%health-points-spend%", String.valueOf(health - 1));
        return s;
    }
}
