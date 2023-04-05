package me.fatpigsarefat.skills.utils;

public enum Skill {
    STRENGTH,
    CRITICALS,
    RESISTANCE,
    ARCHERY,
    HEALTH;

    private Skill() {
    }

    public static Skill getSkillByName(String s) {
        switch (s) {
            case "strength":
                return STRENGTH;
            case "criticals":
                return CRITICALS;
            case "resistance":
                return RESISTANCE;
            case "archery":
                return ARCHERY;
            case "health":
                return HEALTH;
            default:
                return null;
        }
    }
}
