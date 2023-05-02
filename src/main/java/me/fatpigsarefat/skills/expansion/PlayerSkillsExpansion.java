package me.fatpigsarefat.skills.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.fatpigsarefat.skills.PlayerSkills;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.entity.Player;
public class PlayerSkillsExpansion extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "PlayerSkills";
    }

    @Override
    public String getAuthor() {
        return "ItzJustSamu";
    }

    @Override
    public String getVersion() {
        return "v2.2";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (p == null) {
            return "";
        }
        if (params.equals("strength_level")){
            return String.valueOf(PlayerSkills.getSkillManager().getSkillLevel(p, Skill.STRENGTH));
        }
        if (params.equals("critical_level")){
            return String.valueOf(PlayerSkills.getSkillManager().getSkillLevel(p, Skill.CRITICALS));
        }
        if (params.equals("archery_level")){
            return String.valueOf(PlayerSkills.getSkillManager().getSkillLevel(p, Skill.ARCHERY));
        }
        if (params.equals("health_level")){
            return String.valueOf(PlayerSkills.getSkillManager().getSkillLevel(p, Skill.HEALTH));
        }
        if (params.equals("resistance_level")){
            return String.valueOf(PlayerSkills.getSkillManager().getSkillLevel(p, Skill.RESISTANCE));
        }
        return null;
    }
}
