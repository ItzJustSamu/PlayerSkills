package com.leonardobishop.playerskills2.storage;

import com.leonardobishop.playerskills2.player.SPlayer;

import java.util.UUID;

public interface PlayerStorage {
    SPlayer load(UUID uuid);

    void save(SPlayer player);

    String getName();
}
