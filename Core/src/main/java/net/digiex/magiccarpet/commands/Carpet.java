package net.digiex.magiccarpet.commands;

import net.digiex.magiccarpet.MagicCarpet;
import net.digiex.magiccarpet.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
public class Carpet implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
				Player who = null;
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.getName().toLowerCase()
							.contains(args[1].toLowerCase())
							|| p.getName().equalsIgnoreCase(args[1])) {
						who = p;
					}
				}
				if (who != null) {
					MagicCarpet.getCarpets().setGiven(who, true);
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
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.getName().toLowerCase()
							.contains(args[1].toLowerCase())
							|| p.getName().equalsIgnoreCase(args[1])) {
						who = p;
					}
				}
				if (who != null) {
					if (MagicCarpet.getCarpets().has(who)) {
						MagicCarpet.getCarpets().getCarpet(who).hide();
					}
					MagicCarpet.getCarpets().setGiven(who, false);
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
		net.digiex.magiccarpet.Carpet carpet = MagicCarpet.getCarpets()
				.getCarpet(player);
		if (carpet == null) {
			if (player.getFallDistance() > 0
					&& !player.getLocation().getBlock().isLiquid()) {
				return true;
			}
			if (!Permissions.canFlyHere(player.getLocation())) {
				player.sendMessage("The magic carpet is not allowed in this area.");
				return true;
			}
			if (MagicCarpet.getCarpets().wasGiven(player)) {
				new net.digiex.magiccarpet.Carpet(player).show();
				return true;
			}
			if (MagicCarpet.getVault().isEnabled()) {
				if (Permissions.canNotPay(player)) {
					new net.digiex.magiccarpet.Carpet(player).show();
					return true;
				}
				if (MagicCarpet.getMagicConfig().getChargeTimeBased()) {
					if (MagicCarpet.getCarpets().getTime(player) == 0L) {
						player.sendMessage("You have ran out of time to use the Magic Carpet. Please refill using /mcb");
						return true;
					}
					new net.digiex.magiccarpet.Carpet(player).show();
					return true;
				} else {
					if (!MagicCarpet.getCarpets().hasPaidFee(player)) {
						player.sendMessage("You need to pay a one time fee before you can use Magic Carpet. Use /mcb.");
						return true;
					}
					new net.digiex.magiccarpet.Carpet(player).show();
					return true;
				}
			}
			if (!Permissions.canFly(player)) {
				player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
				return true;
			}
			new net.digiex.magiccarpet.Carpet(player).show();
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
				if (!Permissions.canFlyHere(player.getLocation())) {
					player.sendMessage("The magic carpet is not allowed in this area.");
					return true;
				}
				if (MagicCarpet.getCarpets().wasGiven(player)) {
					carpet.show();
					return true;
				}
				if (MagicCarpet.getVault().isEnabled()) {
					if (Permissions.canNotPay(player)) {
						carpet.show();
						return true;
					}
					if (MagicCarpet.getMagicConfig().getChargeTimeBased()) {
						if (MagicCarpet.getCarpets().getTime(player) <= 0L) {
							player.sendMessage("You've ran out of time to use the Magic Carpet. Please refill using /mcb");
							return true;
						}
						carpet.show();
						return true;
					} else {
						if (!MagicCarpet.getCarpets().hasPaidFee(player)) {
							player.sendMessage("You need to pay a one time fee before you can use Magic Carpet. Use /mcb.");
							return true;
						}
						carpet.show();
						return true;
					}
				}
				if (!Permissions.canFly(player)) {
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
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (p.getName().toLowerCase()
								.contains(args[1].toLowerCase())
								|| p.getName().equalsIgnoreCase(args[1])) {
							who = p;
						}
					}
					if (who != null) {
						MagicCarpet.getCarpets().setGiven(who, true);
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
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (p.getName().toLowerCase()
								.contains(args[1].toLowerCase())
								|| p.getName().equalsIgnoreCase(args[1])) {
							who = p;
						}
					}
					if (who != null) {
						if (MagicCarpet.getCarpets().has(who)) {
							MagicCarpet.getCarpets().getCarpet(who).hide();
						}
						MagicCarpet.getCarpets().setGiven(who, false);
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
					word = word.toLowerCase();
					byte d = (byte) carpet.getData();
					if (word.contains("white")) {
						d = (byte) 0;
						word = word.replace("white", "");
					} else if (word.contains("orange")) {
						d = (byte) 1;
						word = word.replace("orange", "");
					} else if (word.contains("magenta")) {
						d = (byte) 2;
						word = word.replace("magenta", "");
					} else if (word.contains("light blue")) {
						d = (byte) 3;
						word = word.replace("light blue", "");
					} else if (word.contains("yellow")) {
						d = (byte) 4;
						word = word.replace("yellow", "");
					} else if (word.contains("lime")) {
						d = (byte) 5;
						word = word.replace("lime", "");
					} else if (word.contains("pink")) {
						d = (byte) 6;
						word = word.replace("pink", "");
					} else if (word.contains("gray")) {
						d = (byte) 7;
						word = word.replace("gray", "");
					} else if (word.contains("light gray")) {
						d = (byte) 8;
						word = word.replace("light gray", "");
					} else if (word.contains("cyan")) {
						d = (byte) 9;
						word = word.replace("cyan", "");
					} else if (word.contains("purple")) {
						d = (byte) 10;
						word = word.replace("purple", "");
					} else if (word.contains("blue")) {
						d = (byte) 11;
						word = word.replace("blue", "");
					} else if (word.contains("brown")) {
						d = (byte) 12;
						word = word.replace("brown", "");
					} else if (word.contains("green")) {
						d = (byte) 13;
						word = word.replace("green", "");
					} else if (word.contains("red")) {
						d = (byte) 14;
						word = word.replace("red", "");
					} else if (word.contains("black")) {
						d = (byte) 15;
						word = word.replace("black", "");
					}
					word = word.trim();
					Material m = Material.matchMaterial(word);
					if (m != null) {
						carpet.changeCarpet(m, d);
						return true;
					} else if (carpet.getData() != d
							&& canHaveData(carpet.getThread())) {
						carpet.setData(d);
						return true;
					} else {
						player.sendMessage("Material error; Usage example: /mc green stained glass");
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

	private boolean canHaveData(Material material) {
		switch (material) {
		case WOOL:
			return true;
		case STAINED_GLASS:
			return true;
		case STAINED_CLAY:
			return true;
		default:
			return false;
		}
	}
}
