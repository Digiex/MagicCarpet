package net.digiex.magiccarpet.commands;

import net.digiex.magiccarpet.Carpet;
import net.digiex.magiccarpet.Storage;
import net.digiex.magiccarpet.Config;
import net.digiex.magiccarpet.MagicCarpet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
public class Magic implements CommandExecutor {

	private final MagicCarpet plugin;

	public Magic(MagicCarpet plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
				Player who = null;
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (p.getName().toLowerCase()
							.contains(args[1].toLowerCase())
							|| p.getName().equalsIgnoreCase(args[1])) {
						who = p;
					}
				}
				if (who != null) {
					getCarpets().setGiven(who, true);
					who.sendMessage("The magic carpet has been given to you. Use /mc");
					sender.sendMessage("The magic carpet was given to "
							+ who.getName());
					return true;
				} else {
					sender.sendMessage("Can't find player " + args[1]);
					return true;
				}
			} else if (args.length == 2 && args[0].equalsIgnoreCase("take")) {
				Player who = null;
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (p.getName().toLowerCase()
							.contains(args[1].toLowerCase())
							|| p.getName().equalsIgnoreCase(args[1])) {
						who = p;
					}
				}
				if (who != null) {
					if (getCarpets().has(who)) {
						getCarpets().getCarpet(who).hide();
					}
					getCarpets().setGiven(who, false);
					who.sendMessage("The magic carpet has been taken from you.");
					sender.sendMessage("The magic carpet was taken from "
							+ who.getName());
					return true;
				} else {
					sender.sendMessage("Can't find player " + args[1]);
					return true;
				}
			} else {
				sender.sendMessage("Sorry, only players can use the carpet!");
				return true;
			}
		}
		int c;
		Player player = (Player) sender;
		Carpet carpet = getCarpets().getCarpet(player);
		if (carpet == null) {
			if (player.getFallDistance() > 0
					&& !player.getLocation().getBlock().isLiquid()) {
				return true;
			}
			if (!canFlyHere(player.getLocation())) {
				player.sendMessage("The magic carpet is not allowed in this area.");
				return true;
			}
			if (getCarpets().wasGiven(player)) {
				new Carpet(player).show();
				return true;
			}
			if (plugin.getVault().isEnabled()) {
				if (plugin.canNotPay(player)) {
					new Carpet(player).show();
					return true;
				}
				if (getConfig().getDefaultChargeTimeBased()) {
					if (getCarpets().getTime(player) == 0L) {
						player.sendMessage("You have ran out of time to use the Magic Carpet. Please refill using /mcb");
						return true;
					}
					new Carpet(player).show();
					return true;
				} else {
					if (!getCarpets().hasPaidFee(player)) {
						player.sendMessage("You need to pay a one time fee before you can use Magic Carpet. Use /mcb.");
						return true;
					}
					new Carpet(player).show();
					return true;
				}
			}
			if (!plugin.canFly(player)) {
				player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
				return true;
			}
			new Carpet(player).show();
			return true;
		}
		if (args.length < 1) {
			if (carpet.isVisible()) {
				carpet.hide();
				return true;
			} else {
				if (player.getFallDistance() > 0
						&& !player.getLocation().getBlock().isLiquid()) {
					return true;
				}
				if (!canFlyHere(player.getLocation())) {
					player.sendMessage("The magic carpet is not allowed in this area.");
					return true;
				}
				if (getCarpets().wasGiven(player)) {
					carpet.show();
					return true;
				}
				if (plugin.getVault().isEnabled()) {
					if (plugin.canNotPay(player)) {
						carpet.show();
						return true;
					}
					if (getConfig().getDefaultChargeTimeBased()) {
						if (getCarpets().getTime(player) <= 0L) {
							player.sendMessage("You've ran out of time to use the Magic Carpet. Please refill using /mcb");
							return true;
						}
						carpet.show();
						return true;
					} else {
						if (!getCarpets().hasPaidFee(player)) {
							player.sendMessage("You need to pay a one time fee before you can use Magic Carpet. Use /mcb.");
							return true;
						}
						carpet.show();
						return true;
					}
				}
				if (!plugin.canFly(player)) {
					player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
					return true;
				}
				carpet.show();
				return true;
			}
		} else {
			if (args.length == 2 && args[0].equals("give")) {
				if (player.isOp()) {
					Player who = null;
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (p.getName().toLowerCase()
								.contains(args[1].toLowerCase())
								|| p.getName().equalsIgnoreCase(args[1])) {
							who = p;
						}
					}
					if (who != null) {
						getCarpets().setGiven(who, true);
						who.sendMessage("The magic carpet has been given to you. Use /mc");
						player.sendMessage("You've given the magic carpet to "
								+ who.getName());
						return true;
					} else {
						player.sendMessage("Can't find player " + args[1]);
						return true;
					}
				} else {
					player.sendMessage("You don't have permission to use this.");
					return true;
				}
			} else if (args.length == 2 && args[0].equals("take")) {
				if (player.isOp()) {
					Player who = null;
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (p.getName().toLowerCase()
								.contains(args[1].toLowerCase())
								|| p.getName().equalsIgnoreCase(args[1])) {
							who = p;
						}
					}
					if (who != null) {
						if (getCarpets().has(who)) {
							getCarpets().getCarpet(who).hide();
						}
						getCarpets().setGiven(who, false);
						who.sendMessage("The magic carpet has been taken from you.");
						player.sendMessage("You've taken the magic carpet from "
								+ who.getName());
						return true;
					} else {
						player.sendMessage("Can't find player " + args[1]);
						return true;
					}
				} else {
					player.sendMessage("You don't have permission to use this.");
					return true;
				}
			}
			if (carpet.isVisible()) {
				try {
					c = Integer.valueOf(args[0]);
				} catch (NumberFormatException e) {
					String word = "";
					for (String a : args) {
						if (word.isEmpty()) {
							word = a;
						} else {
							word += " " + a;
						}
					}
					Material m = Material.getMaterial(word.toUpperCase()
							.replace(" ", "_"));
					if (m != null) {
						carpet.changeCarpet(m);
						return true;
					} else {
						player.sendMessage("Material error; Material may be entered as GOLD_BLOCK or just plain gold block");
						return true;
					}
				}
				carpet.changeCarpet(c);
				return true;
			} else {
				player.sendMessage("You don't have a carpet yet.");
				return true;
			}
		}
	}

	private Storage getCarpets() {
		return plugin.getCarpets();
	}

	private Config getConfig() {
		return plugin.getMCConfig();
	}

	private boolean canFlyHere(Location location) {
		return (!plugin.getWorldGuard().isEnabled()) ? true : plugin
				.getWorldGuard().canFlyHere(location);
	}
}
