package net.digiex.magiccarpet;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
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
    
    public Permission getPermissionProvider() {
        return permissionsProvider;
    }
    
    public Economy getEconomyProvider() {
        return economyProvider;
    }
}