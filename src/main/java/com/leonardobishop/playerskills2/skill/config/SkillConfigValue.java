package com.leonardobishop.playerskills2.skill.config;

import com.leonardobishop.playerskills2.skill.Skill;

public class SkillConfigValue<T> {
    public final Skill skill;
    public final Class<T> type;
    public final String location;
    public final T defaultValue;

    public SkillConfigValue(Skill skill, Class<T> type, String location, T defaultValue) {
        this.skill = skill;
        this.type = type;
        this.location = location;
        this.defaultValue = defaultValue;
    }

    public T get() {
        return skill.getConfigValue(location, type, defaultValue);
    }
}
