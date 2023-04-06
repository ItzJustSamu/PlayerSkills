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

    public ConfigurationManager() {
    }

    public ItemStack getItemStack(String s, Player player) {
        String pathRoot = "gui.display." + s + ".";
        String name = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.gui.get().getString(pathRoot + "name")));
        name = this.placeholder(name, player);
        ArrayList<String> lore = new ArrayList<>();
        if (this.gui.get().contains(pathRoot + "lore")) {

            for (String key : this.gui.get().getStringList(pathRoot + "lore")) {
                key = ChatColor.translateAlternateColorCodes('&', key);
                key = this.placeholder(key, player);
                lore.add(key);
            }
        }

        ItemStack itemToGive = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(this.gui.get().getString(pathRoot + "material")))));
        ItemMeta itemToGiveMeta = itemToGive.getItemMeta();
        itemToGiveMeta.setDisplayName(name);
        itemToGiveMeta.setLore(lore);
        itemToGive.setItemMeta(itemToGiveMeta);
        return itemToGive;
    }

    private String placeholder(String s, Player player) {
        SkillManager sm = PlayerSkills.getSkillManager();
        int strength = sm.getSkillLevel(player, Skill.STRENGTH);
        int criticals = sm.getSkillLevel(player, Skill.CRITICALS);
        int resistance = sm.getSkillLevel(player, Skill.RESISTANCE);
        int archery = sm.getSkillLevel(player, Skill.ARCHERY);
        int health = sm.getSkillLevel(player, Skill.HEALTH);
        int xpPrice;
        int healthSkillAfter;
        if (s.contains("strength")) {
            s = s.replace("%strength%", "[" + strength + "/" + sm.getMaximumLevel(Skill.STRENGTH) + "]");
            s = s.replace("%strengthincrement%", PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.STRENGTH) - 1) * PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH) + 100;
            healthSkillAfter = sm.getSkillLevel(player, Skill.STRENGTH) * PlayerSkills.getSkillMultipliers().get(Skill.STRENGTH) + 100;
            if (sm.getSkillLevel(player, Skill.STRENGTH) >= sm.getMaximumLevel(Skill.STRENGTH)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%strengthprogress%", ChatColor.GREEN.toString() + xpPrice + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "%");
        }

        if (s.contains("criticals")) {
            s = s.replace("%criticals%", "[" + criticals + "/" + sm.getMaximumLevel(Skill.CRITICALS) + "]");
            s = s.replace("%criticalsincrement%", PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.CRITICALS) - 1) * PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS) + 150;
            healthSkillAfter = sm.getSkillLevel(player, Skill.CRITICALS) * PlayerSkills.getSkillMultipliers().get(Skill.CRITICALS) + 150;
            if (sm.getSkillLevel(player, Skill.CRITICALS) >= sm.getMaximumLevel(Skill.CRITICALS)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%criticalsprogress%", ChatColor.GREEN.toString() + xpPrice + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "%");
        }

        if (s.contains("resistance")) {
            s = s.replace("%resistance%", "[" + resistance + "/" + sm.getMaximumLevel(Skill.RESISTANCE) + "]");
            s = s.replace("%resistanceincrement%", PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.RESISTANCE) - 1) * PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE);
            healthSkillAfter = sm.getSkillLevel(player, Skill.RESISTANCE) * PlayerSkills.getSkillMultipliers().get(Skill.RESISTANCE);
            if (sm.getSkillLevel(player, Skill.RESISTANCE) >= sm.getMaximumLevel(Skill.RESISTANCE)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%resistanceprogress%", ChatColor.GREEN.toString() + xpPrice + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "%");
        }

        if (s.contains("archery")) {
            s = s.replace("%archery%", "[" + archery + "/" + sm.getMaximumLevel(Skill.ARCHERY) + "]");
            s = s.replace("%archeryincrement%", PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.ARCHERY) - 1) * PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY) + 100;
            healthSkillAfter = sm.getSkillLevel(player, Skill.ARCHERY) * PlayerSkills.getSkillMultipliers().get(Skill.ARCHERY) + 100;
            if (sm.getSkillLevel(player, Skill.ARCHERY) >= sm.getMaximumLevel(Skill.ARCHERY)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%archeryprogress%", ChatColor.GREEN.toString() + xpPrice + "%  " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "%");
        }

        if (s.contains("health")) {
            s = s.replace("%health%", "[" + health + "/" + sm.getMaximumLevel(Skill.HEALTH) + "]");
            s = s.replace("%healthincrement%", PlayerSkills.getSkillMultipliers().get(Skill.HEALTH).toString());
            xpPrice = (sm.getSkillLevel(player, Skill.HEALTH) - 1) * PlayerSkills.getSkillMultipliers().get(Skill.HEALTH) + 20;
            healthSkillAfter = sm.getSkillLevel(player, Skill.HEALTH) * PlayerSkills.getSkillMultipliers().get(Skill.HEALTH) + 20;
            if (sm.getSkillLevel(player, Skill.HEALTH) >= sm.getMaximumLevel(Skill.HEALTH)) {
                healthSkillAfter = xpPrice;
            }

            s = s.replace("%healthprogress%", ChatColor.GREEN.toString() + xpPrice + "HP " + ChatColor.GRAY + " >>>   " + ChatColor.GREEN + healthSkillAfter + "HP");
        }

        xpPrice = this.config.get().getInt("xp.price");
        if (this.config.get().getBoolean("xp.add-total-to-price")) {
            xpPrice += sm.getTotalPointsSpent(player) * this.config.get().getInt("xp.add-total-to-price-multiplier");
        }

        s = s.replace("%pointsprice%", xpPrice + "XP");
        s = s.replace("%points%", String.valueOf(sm.getSkillPoints(player)));
        s = s.replace("%username%", player.getName());
        s = s.replace("%expierencelevel%", String.valueOf(player.getLevel()));
        if (s.contains("expierence")) {
            StringBuilder expierenceBar = new StringBuilder();

            for(double f = 0.0; f <= (double)player.getExp(); f += 0.03) {
                expierenceBar.append(ChatColor.GREEN).append("|");
            }

            int toAdd = 30 - ChatColor.stripColor(expierenceBar.toString()).length();

            for(int i = 0; i <= toAdd; ++i) {
                expierenceBar.append(ChatColor.GRAY).append("|");
            }

            s = s.replace("%expierencebar%", expierenceBar.toString());
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
