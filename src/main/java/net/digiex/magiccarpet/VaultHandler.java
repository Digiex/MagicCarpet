package net.digiex.magiccarpet;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/*
 * Magic Carpet 2.2 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
public class VaultHandler {

private Economy vaultPlugin;
	
	VaultHandler() {
		getEconomy();
	}

    boolean add(String player, double amount) {
        return vaultPlugin.depositPlayer(player, amount).transactionSuccess();
    }

    boolean subtract(String player, double amount) {
        return vaultPlugin.withdrawPlayer(player, amount).transactionSuccess();
    }

    boolean hasEnough(String player, double amount) {
        return vaultPlugin.has(player, amount);
    }

    double balance(String player) {
        return vaultPlugin.getBalance(player);
    }

    String format(double amount) {
        return vaultPlugin.format(amount);
    }

    String getPluginName() {
        if (vaultPlugin == null) {
            return "";
        } else {
            return vaultPlugin.getName();
        }
    }
    
    String getCurrencyName() {
    	return vaultPlugin.currencyNameSingular();
    }
    
    String getCurrencyNamePlural() {
    	return vaultPlugin.currencyNamePlural();
    }
    
    boolean isEnabled() {
    	return (vaultPlugin != null) ? true : false;
    }

    private void getEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        vaultPlugin = rsp.getProvider();
    }
}