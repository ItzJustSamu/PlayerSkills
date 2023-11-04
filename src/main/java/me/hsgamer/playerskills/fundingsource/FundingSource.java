package me.hsgamer.playerskills.fundingsource;

import me.hsgamer.playerskills.player.SPlayer;
import org.bukkit.entity.Player;

public interface FundingSource {

    String getSymbol(int price);

    boolean doTransaction(SPlayer sPlayer, int price, Player player);

    String getName();
}
