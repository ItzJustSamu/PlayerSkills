package me.fatpigsarefat.skills.managers;

import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SkillManager {
    private final FileManager.Config data = PlayerSkills.getFileManager().getConfig("data");

    private final FileManager.Config config = PlayerSkills.getFileManager().getConfig("config");

    public int getSkillLevel(Player player, Skill skill) {
        if (this.data.get().contains(player.getUniqueId() + "." + skill.toString()))
            return this.data.get().getInt(player.getUniqueId() + "." + skill);
        return 1;
    }

    public int getSkillPoints(Player player) {
        if (this.data.get().contains(player.getUniqueId() + "points"))
            return this.data.get().getInt(player.getUniqueId() + "points");
        return 0;
    }

    public int getTotalPointsSpent(Player player) {
        int points = getSkillPoints(player);
        int damage = getSkillLevel(player, Skill.STRENGTH) - 1;
        int criticals = getSkillLevel(player, Skill.CRITICALS) - 1;
        int resistance = getSkillLevel(player, Skill.RESISTANCE) - 1;
        int archery = getSkillLevel(player, Skill.ARCHERY) - 1;
        int health = getSkillLevel(player, Skill.HEALTH) - 1;
        return points + damage + criticals + resistance + archery + health;
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
        if (!player.hasPlayedBefore())
            return 1;
        if (this.data.get().contains(player.getUniqueId() + "." + skill.toString()))
            return this.data.get().getInt(player.getUniqueId() + "." + skill);
        return 1;
    }

    public int getSkillPoints(OfflinePlayer player) {
        if (!player.hasPlayedBefore())
            return 0;
        if (this.data.get().contains(player.getUniqueId() + ".points"))
            return this.data.get().getInt(player.getUniqueId() + ".points");
        return 0;
    }

    public int getPointPrice(Player player) {
        var xpPrice = 1;
        xpPrice = this.config.get().getInt("xp.price");
        if (this.config.get().getBoolean("xp.total"))
            xpPrice += getTotalPointsSpent(player) * this.config.get().getInt("xp.add-total-to-price-multiplier");
        return xpPrice;
    }

    public void resetAll(Player player) {
        setSkillPoints(player, 0);
        setSkillLevel(player, Skill.HEALTH, 1);
        setSkillLevel(player, Skill.ARCHERY, 1);
        setSkillLevel(player, Skill.RESISTANCE, 1);
        setSkillLevel(player, Skill.CRITICALS, 1);
        setSkillLevel(player, Skill.STRENGTH, 1);
    }

    public void buySkillPoint(Player player) {
        player.setLevel(player.getLevel() - getPointPrice(player));
        setSkillPoints(player, getSkillPoints(player) + 1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 60.0F, 100.0F);
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
