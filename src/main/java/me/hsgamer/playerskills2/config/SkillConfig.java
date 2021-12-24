package me.hsgamer.playerskills2.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.playerskills2.skill.Skill;

import java.io.File;

public class SkillConfig extends BukkitConfig {
    public SkillConfig(Skill skill) {
        super(new File(skill.getPlugin().getDataFolder(), "skills" + File.separator + skill.getConfigName() + ".yml"));
    }
}
