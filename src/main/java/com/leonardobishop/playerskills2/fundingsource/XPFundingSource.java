package com.leonardobishop.playerskills2.fundingsource;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.config.Config;
import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.entity.Player;

public class XPFundingSource implements FundingSource {
    private final PlayerSkills playerSkills;

    public XPFundingSource(PlayerSkills playerSkills) {
        this.playerSkills = playerSkills;
    }

    @Override
    public String getSymbol(int price) {
        return Config.get(playerSkills, "messages.xp-symbol", "XP").getString();
    }

    @Override
    public boolean doTransaction(SPlayer sPlayer, int price, Player player) {
        if (player.getLevel() >= price) {
            player.setLevel(player.getLevel() - price);
            return true;
        }
        return false;
    }


}
