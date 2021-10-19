package com.leonardobishop.playerskills2.skill.config;

import com.leonardobishop.playerskills2.skill.Skill;

public class SkillRawConfigValue<T> {
    public final Skill skill;
    public final String location;
    public final T defaultValue;

    public SkillRawConfigValue(Skill skill, String location, T defaultValue) {
        this.skill = skill;
        this.location = location;
        this.defaultValue = defaultValue;
    }

    public T get() {
        try {
            // noinspection unchecked
            return (T) skill.getConfig().getOrDefault(location, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
