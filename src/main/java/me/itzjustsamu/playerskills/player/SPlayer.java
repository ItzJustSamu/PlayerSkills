package me.itzjustsamu.playerskills.player;

import me.itzjustsamu.playerskills.config.MainConfig;
import me.itzjustsamu.playerskills.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SPlayer {
    private static final HashMap<UUID, SPlayer> players = new HashMap<>();

    private final UUID player;
    private final Map<String, Integer> skills;
    private final Map<String, Integer> increments;
    private int points;

    public SPlayer(UUID player) {
        this.player = player;
        this.skills = new HashMap<>();
        this.increments = new HashMap<>();
    }

    public static void load(UUID uuid) {
        SPlayer sPlayer = MainConfig.OPTIONS_PLAYER_STORAGE.getValue().load(uuid);
        players.put(uuid, sPlayer);
    }

    public static SPlayer get(UUID uuid) {
        return players.get(uuid);
    }

    public static void unload(UUID uuid) {
        players.remove(uuid);
    }

    public static void save(SPlayer sPlayer) {
        if (sPlayer == null) {
            if (MainConfig.isVerboseLogging()) {
                Utils.logInfo("Plugin tried to save an SPlayer for a null player.");
            }
            return;
        }
        MainConfig.OPTIONS_PLAYER_STORAGE.getValue().save(sPlayer);
    }

    public static HashMap<UUID, SPlayer> getPlayers() {
        return players;
    }

    public UUID getPlayer() {
        return player;
    }

    public Map<String, Integer> getSkills() {
        return skills;
    }

    public Map<String, Integer> getIncrements() {
        return increments;
    }

    public int Level(String skill) {
        return skills.getOrDefault(skill, 0);
    }

    public void setLevel(String skill, int level) {
        skills.put(skill, level);
    }

    public int getIncrements(String skill) {
        return increments.getOrDefault(skill, 0);
    }


    public void setIncrement(String skill, int increment) {
        increments.put(skill, increment);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getNextPointPrice() {
        int base = MainConfig.POINTS_PRICE.getValue();
        int playerPoints = getPoints();
        for (int increment : getIncrements().values()) {
            playerPoints += increment;
        }
        return base + (playerPoints * MainConfig.POINTS_INCREMENT_PRICE.getValue());
    }
}
