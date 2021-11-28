package com.leonardobishop.playerskills2.fundingsource;

import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.entity.Player;

public interface FundingSource {

    boolean doTransaction(SPlayer sPlayer, int price, Player player);

}
