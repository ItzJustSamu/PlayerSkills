package me.fatpigsarefat.skills.managers;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SkillManager {
    private FileManager.Config data = PlayerSkills.getFileManager().getConfig("data");
    private FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");

    public SkillManager() {
    }

    public int getSkillLevel(Player player, Skill skill) {
        return this.data.get().contains(player.getUniqueId() + "." + skill.toString()) ? this.data.get().getInt(player.getUniqueId() + "." + skill.toString()) : 1;
    }

    public int getSkillPoints(Player player) {
        return this.data.get().contains(player.getUniqueId() + ".points") ? this.data.get().getInt(player.getUniqueId() + ".points") : 0;
    }

    public int getTotalPointsSpent(Player player) {
        int points = this.getSkillPoints(player);
        int damage = this.getSkillLevel(player, Skill.STRENGTH) - 1;
        int criticals = this.getSkillLevel(player, Skill.CRITICALS) - 1;
        int resistance = this.getSkillLevel(player, Skill.RESISTANCE) - 1;
        int archery = this.getSkillLevel(player, Skill.ARCHERY) - 1;
        int health = this.getSkillLevel(player, Skill.HEALTH) - 1;
        int total = points + damage + criticals + resistance + archery + health;
        return total;
    }

    public void setSkillPoints(Player player, int points) {
        this.data.set(player.getUniqueId() + ".points", points);
        this.data.save();
    }

    public void setSkillLevel(Player player, Skill skill, int level) {
        this.data.set(player.getUniqueId() + "." + skill.toString(), level);
        this.data.save();
    }

    public int getSkillLevel(OfflinePlayer player, Skill skill) {
        if (!player.hasPlayedBefore()) {
            return 1;
        } else {
            return this.data.get().contains(player.getUniqueId() + "." + skill.toString()) ? this.data.get().getInt(player.getUniqueId() + "." + skill.toString()) : 1;
        }
    }

    public int getSkillPoints(OfflinePlayer player) {
        if (!player.hasPlayedBefore()) {
            return 0;
        } else {
            return this.data.get().contains(player.getUniqueId() + ".points") ? this.data.get().getInt(player.getUniqueId() + ".points") : 0;
        }
    }

    public int getPointPrice(Player player) {
        int xpPrice = true;
        int xpPrice = this.config.get().getInt("xp.price");
        if (this.config.get().getBoolean("xp.add-total-to-price")) {
            xpPrice += this.getTotalPointsSpent(player) * this.config.get().getInt("xp.add-total-to-price-multiplier");
        }

        return xpPrice;
    }

    public void resetAll(Player player) {
        this.setSkillPoints((Player)player, 0);
        this.setSkillLevel((Player)player, Skill.HEALTH, 1);
        this.setSkillLevel((Player)player, Skill.ARCHERY, 1);
        this.setSkillLevel((Player)player, Skill.RESISTANCE, 1);
        this.setSkillLevel((Player)player, Skill.CRITICALS, 1);
        this.setSkillLevel((Player)player, Skill.STRENGTH, 1);
    }

    public void buySkillPoint(Player player) {
        player.setLevel(player.getLevel() - this.getPointPrice(player));
        this.setSkillPoints(player, this.getSkillPoints(player) + 1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 100.0F, 100.0F);
    }

    public int getTotalPointsSpent(OfflinePlayer player) {
        if (!player.hasPlayedBefore()) {
            return 0;
        } else {
            int points = this.getSkillPoints(player);
            int damage = this.getSkillLevel(player, Skill.STRENGTH) - 1;
            int criticals = this.getSkillLevel(player, Skill.CRITICALS) - 1;
            int resistance = this.getSkillLevel(player, Skill.RESISTANCE) - 1;
            int archery = this.getSkillLevel(player, Skill.ARCHERY) - 1;
            int health = this.getSkillLevel(player, Skill.HEALTH) - 1;
            int total = points + damage + criticals + resistance + archery + health;
            return total;
        }
    }

    public void setSkillPoints(OfflinePlayer player, int points) {
        this.data.set(player.getUniqueId() + ".points", points);
        this.data.save();
    }

    public void setSkillLevel(OfflinePlayer player, Skill skill, int level) {
        this.data.set(player.getUniqueId() + "." + skill.toString(), level);
        this.data.save();
    }

    public int getMaximumLevel(Skill skill) {
        return this.config.get().getInt("skills." + skill.toString().toLowerCase() + ".max-level");
    }
}
