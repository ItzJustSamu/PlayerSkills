package me.fatpigsarefat.skills.utils;

public enum Skill {
    STRENGTH, CRITICALS, RESISTANCE, ARCHERY, HEALTH;

    public static Skill getSkillByName(String s) {
        return switch (s) {
            case "strength" -> STRENGTH;
            case "criticals" -> CRITICALS;
            case "resistance" -> RESISTANCE;
            case "archery" -> ARCHERY;
            case "health" -> HEALTH;
            default -> null;
        };
    }
}
