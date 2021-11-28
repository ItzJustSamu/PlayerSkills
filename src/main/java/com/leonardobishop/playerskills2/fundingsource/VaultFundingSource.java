package com.leonardobishop.playerskills2.fundingsource;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultFundingSource implements FundingSource {

    private Economy economy;

    public VaultFundingSource(PlayerSkills plugin) {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.logError("Could not find a registered economy for Vault.");
            return;
        }
        economy = rsp.getProvider();
    }

    @Override
    public boolean doTransaction(SPlayer sPlayer, int price, Player player) {
        if (economy.getBalance(player) > price) {
            economy.withdrawPlayer(player, price);
            return true;
        }
        return false;
    }


}
