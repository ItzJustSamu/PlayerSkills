package me.hsgamer.playerskills.storage;

import me.hsgamer.playerskills.player.SPlayer;

import java.util.UUID;

public interface PlayerStorage {
    SPlayer load(UUID uuid);

    void save(SPlayer player);

    String getName();
}
