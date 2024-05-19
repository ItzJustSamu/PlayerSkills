package me.itzjustsamu.playerskills.fundingsource;

import me.itzjustsamu.playerskills.player.SPlayer;
import me.itzjustsamu.playerskills.util.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultFundingSource implements FundingSource {
    private Economy economy;

    public VaultFundingSource() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
                return;
        }
        economy = rsp.getProvider();
    }

    @Override
    public String getSymbol(int price) {
        return price <= 1 ? economy.currencyNameSingular() : economy.currencyNamePlural();
    }

    @Override
    public boolean doTransaction(SPlayer sPlayer, int price, Player player) {
        double balance = economy.getBalance(player);
        if (balance >= price) {
            economy.withdrawPlayer(player, price);
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "VAULT";
    }
}
