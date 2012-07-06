package net.digiex.magiccarpet;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/*
 * Magic Carpet 2.1 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

    private final MagicCarpet plugin;
    private Permission permissionsProvider;
    private Economy economyProvider;

    public VaultHandler(MagicCarpet plugin) {
        this.plugin = plugin;
    }

    public VaultHandler setup() {
        RegisteredServiceProvider<Permission> p = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (p != null) {
            permissionsProvider = p.getProvider();
        }
        RegisteredServiceProvider<Economy> e = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (e != null) {
            economyProvider = e.getProvider();
        }
        return this;
    }

    public boolean isPermissionsEnabled() {
        return permissionsProvider != null;
    }

    public boolean isEconomyEnabled() {
        return economyProvider != null;
    }

    public boolean hasPermission(Player player, String permission) {
        return permissionsProvider.has(player, permission);
    }

    public String getCurrencyName() {
        return economyProvider.currencyNamePlural();
    }

    public Double getBalance(Player player) {
        return economyProvider.getBalance(player.getName());
    }
    
    public String formatBalance(Double d) {
        return economyProvider.format(d);
    }

    public boolean hasAmount(Player player, Double d) {
        Double balance = getBalance(player);
        if (balance >= d) {
            return true;
        }
        return false;
    }

    public boolean subtractAmount(Player player, Double d) {
        EconomyResponse r = economyProvider.withdrawPlayer(player.getName(), d);
        return r.transactionSuccess();
    }
}