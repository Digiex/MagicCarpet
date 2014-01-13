package net.digiex.magiccarpet.plugins;

import java.util.Iterator;
import java.util.Set;

import net.digiex.magiccarpet.MagicCarpet;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

/*
 * Magic Carpet 2.4 Copyright (C) 2012-2014 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
public class WorldGuard {

	private final MagicCarpet plugin;
	private WorldGuardPlugin worldGuard;

	public WorldGuard(MagicCarpet plugin) {
		this.plugin = plugin;
		getWorldGuard();
	}

	private void getWorldGuard() {
		Plugin p = plugin.getServer().getPluginManager()
				.getPlugin("WorldGuard");
		if (p == null
				|| !(p instanceof com.sk89q.worldguard.bukkit.WorldGuardPlugin)) {
			return;
		}
		worldGuard = (WorldGuardPlugin) p;
	}

	public boolean isEnabled() {
		return (worldGuard != null) ? true : false;
	}

	public boolean canFlyHere(Location location) {
		try {
			RegionManager regionManager = worldGuard.getRegionManager(location
					.getWorld());
			ApplicableRegionSet set = regionManager
					.getApplicableRegions(location);
			Set<String> flag = set
					.getFlag(com.sk89q.worldguard.protection.flags.DefaultFlag.BLOCKED_CMDS);
			for (Iterator<String> it = flag.iterator(); it.hasNext();) {
				String blocked = it.next();
				if (blocked == null) {
					continue;
				}
				if (blocked.contains("/mc") || blocked.contains("/magiccarpet")) {
					return false;
				}
			}
		} catch (Exception e) {
			return true;
		}
		return true;
	}
}