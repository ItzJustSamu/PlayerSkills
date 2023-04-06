package me.fatpigsarefat.skills.events;

import me.fatpigsarefat.skills.managers.SkillManager;
import me.fatpigsarefat.skills.utils.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResetSkillEvent extends Event {
    private final Player player;
    private final SkillManager skillManager;
    private final Skill skill;
    private static final HandlerList handlers = new HandlerList();

    public ResetSkillEvent(Player player, SkillManager skillManager, Skill skill) {
        this.player = player;
        this.skillManager = skillManager;
        this.skill = skill;
    }

    public Player getPlayer() {
        return this.player;
    }

    public SkillManager getSkillManager() {
        return this.skillManager;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
