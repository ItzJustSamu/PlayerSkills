package me.hsgamer.playerskills.skill;

import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.Paths;
import me.hsgamer.playerskills.util.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public abstract class SkillEffect {

    public static final ConfigPath<String> PARTICLE_EFFECT = Paths.stringPath("particle-effect", "EXPLOSION_LARGE");
    public static final ConfigPath<Integer> PARTICLE_AMOUNT = Paths.integerPath("particle-amount", 1);
    public static final ConfigPath<String> SOUND_EFFECT = Paths.stringPath("sound-effect", "ENTITY_GENERIC_EXPLODE");

    public static void playParticles(Player player, Location location) {
        // Play particle effect
        try {
            String particleEffectName = PARTICLE_EFFECT.getValue();
            Particle particleEffect = Particle.valueOf(particleEffectName);
            player.spawnParticle(particleEffect, location, PARTICLE_AMOUNT.getValue());
        } catch (IllegalArgumentException | NullPointerException e) {
            Utils.logError("Error while handling ParticleEffect: " + e.getMessage());
        }
    }

    public static void playSound(Player player) {
        // Play sound effect
        try {
            String soundEffectName = SOUND_EFFECT.getValue();
            Sound soundEffect = Sound.valueOf(soundEffectName);
            player.playSound(player.getLocation(), soundEffect, 5, 1);
        } catch (IllegalArgumentException | NullPointerException e) {
            Utils.logError("Error while handling SoundEffect: " + e.getMessage());
        }
    }
}
