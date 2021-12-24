package me.hsgamer.playerskills2.fundingsource;

import me.hsgamer.playerskills2.player.SPlayer;
import org.bukkit.entity.Player;

public interface FundingSource {

    String getSymbol(int price);

    boolean doTransaction(SPlayer sPlayer, int price, Player player);

    String getName();
}
