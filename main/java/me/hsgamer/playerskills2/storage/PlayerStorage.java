package me.hsgamer.playerskills2.storage;

import me.hsgamer.playerskills2.player.SPlayer;

import java.util.UUID;

public interface PlayerStorage {
    SPlayer load(UUID uuid);

    void save(SPlayer player);

    String getName();
}
