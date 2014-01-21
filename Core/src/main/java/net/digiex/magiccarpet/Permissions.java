package net.digiex.magiccarpet;

import net.digiex.magiccarpet.plugins.Plugins;
import net.digiex.magiccarpet.plugins.WorldGuard;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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

public final class Permissions {

	private final static String c = "magiccarpet.mc";
	private final static String l = "magiccarpet.ml";
	private final static String s = "magiccarpet.mcs";
	private final static String r = "magiccarpet.mr";
	private final static String t = "magiccarpet.mct";
	private final static String p = "magiccarpet.np";
	private final static String i = "magiccarpet.mc.";
	private final static String a = "magiccarpet.*";

	private static boolean hasPermission(Player player, String permission) {
		if (MagicCarpet.getCarpets().wasGiven(player)) {
			return true;
		}
		if (Plugins.isVaultEnabled()) {
			if (player.hasPermission(p)) {
				return true;
			}
			if (Config.getChargeTimeBased()) {
				return (MagicCarpet.getCarpets().getTime(player) <= 0L) ? false
						: true;
			} else {
				return MagicCarpet.getCarpets().hasPaidFee(player);
			}
		}
		return player.hasPermission(permission);
	}

	public static boolean canFly(Player player) {
		return hasPermission(player, c);
	}

	public static boolean canLight(Player player) {
		return hasPermission(player, l);
	}

	public static boolean canSwitch(Player player) {
		return hasPermission(player, s);
	}

	public static boolean canTool(Player player) {
		return hasPermission(player, t);
	}

	public static boolean canNotPay(Player player) {
		return hasPermission(player, p);
	}

	public static boolean canReload(Player player) {
		return player.hasPermission(r);
	}

	public static boolean canFlyAt(Player player, Integer size) {
		if (size == Config.getCarpSize()) {
			return true;
		} else if (player.hasPermission(a)) {
			return true;
		}
		return hasPermission(player, i + size);
	}

	public static boolean canFlyHere(Location location) {
		return (!Plugins.isWorldGuardEnabled()) ? true : WorldGuard.canFlyHere(location);
	}
}