package com.leonardobishop.playerskills2.skill.config;

import com.leonardobishop.playerskills2.skill.Skill;

public class SkillNumberConfigValue extends SkillConfigValue<Number> {
    public SkillNumberConfigValue(Skill skill, String location, Number defaultValue) {
        super(skill, Number.class, location, defaultValue);
    }

    public int getInt() {
        return get().intValue();
    }

    public long getLong() {
        return get().longValue();
    }

    public short getShort() {
        return get().shortValue();
    }

    public byte getByte() {
        return get().byteValue();
    }

    public float getFloat() {
        return get().floatValue();
    }

    public double getDouble() {
        return get().doubleValue();
    }
}
