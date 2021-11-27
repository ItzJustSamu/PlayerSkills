package com.leonardobishop.playerskills2.config;

import com.leonardobishop.playerskills2.skill.Skill;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;

import java.io.File;

public class SkillConfig extends BukkitConfig {
    public SkillConfig(Skill skill) {
        super(new File(skill.getPlugin().getDataFolder(), "skills" + File.separator + skill.getConfigName() + ".yml"));
    }
}
