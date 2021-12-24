package me.hsgamer.playerskills2.fundingsource;

import me.hsgamer.playerskills2.config.MessageConfig;
import me.hsgamer.playerskills2.player.SPlayer;
import org.bukkit.entity.Player;

public class XPFundingSource implements FundingSource {
    @Override
    public String getSymbol(int price) {
        return MessageConfig.XP_SYMBOL.getValue();
    }

    @Override
    public boolean doTransaction(SPlayer sPlayer, int price, Player player) {
        if (player.getLevel() >= price) {
            player.setLevel(player.getLevel() - price);
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "XP";
    }
}
