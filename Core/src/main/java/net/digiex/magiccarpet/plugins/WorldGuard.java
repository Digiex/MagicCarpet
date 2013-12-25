package net.digiex.magiccarpet.plugins;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.digiex.magiccarpet.MagicCarpet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;

/*
 * Magic Carpet 2.3 Copyright (C) 2012-2013 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

	public static class CarpetFlag extends StateFlag {
		public static CarpetFlag flag = new CarpetFlag();

		public CarpetFlag() {
			super("carpet", true);
		}

		private static List elements() {
			List<Flag> elements = new ArrayList(Arrays.asList(DefaultFlag
					.getFlags()));
			elements.add(flag);
			return elements;
		}

		static boolean setAllowsFlag(ApplicableRegionSet set) {
			return set.allows(flag);
		}

		static void injectHax() {
			try {
				Field field = DefaultFlag.class.getDeclaredField("flagsList");

				Field modifiersField = Field.class
						.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers()
						& ~Modifier.FINAL);

				field.setAccessible(true);

				List<Flag> elements = elements();

				Flag<?> list[] = new Flag<?>[elements.size()];
				for (int i = 0; i < elements.size(); i++) {
					list[i] = elements.get(i);
				}

				field.set(null, list);

				Field grm = WorldGuardPlugin.class
						.getDeclaredField("globalRegionManager");
				grm.setAccessible(true);
				GlobalRegionManager globalRegionManager = (GlobalRegionManager) grm
						.get(Bukkit.getServer().getPluginManager()
								.getPlugin("WorldGuard"));

				globalRegionManager.preload();

			} catch (Exception e) {
				Bukkit.getLogger()
						.severe("Oh noes! Something wrong happened! Be sure to paste that in your bug report:");
				e.printStackTrace();
			}
		}
	}

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
		CarpetFlag.injectHax();
	}

	private ApplicableRegionSet getApplicableRegions(Location location) {
		return worldGuard.getGlobalRegionManager().get(location.getWorld())
				.getApplicableRegions(BukkitUtil.toVector(location));
	}

	public boolean isEnabled() {
		return (worldGuard != null) ? true : false;
	}

	public boolean canFlyHere(Location location) {
		ApplicableRegionSet regions = getApplicableRegions(location);
		return CarpetFlag.setAllowsFlag(regions);
	}
}