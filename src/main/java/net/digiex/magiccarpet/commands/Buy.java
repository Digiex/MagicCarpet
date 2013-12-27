package net.digiex.magiccarpet.commands;

import java.util.Map.Entry;

import net.digiex.magiccarpet.MagicCarpet;
import net.digiex.magiccarpet.Permissions;
import net.digiex.magiccarpet.plugins.Vault.TimePackage;

import org.bukkit.Bukkit;
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
public class Buy implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (!MagicCarpet.getVault().isEnabled()) {
				MagicCarpet
						.getMagicLogger()
						.info("You have not enabled economy support in the config or the required dependencies are missing.");
				return true;
			}
			if (args.length == 2) {
				Player who = null;
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.getName().toLowerCase()
							.contains(args[0].toLowerCase())
							|| p.getName().equalsIgnoreCase(args[0])) {
						who = p;
						break;
					}
				}
				try {
					long time = Long.valueOf(args[1]);
					MagicCarpet.getVault().addTime(who, time);
					MagicCarpet.getMagicLogger().info(
							"Time sent to " + who.getName() + ".");
					return true;
				} catch (NumberFormatException e) {
					MagicCarpet.getMagicLogger().info("/mcb [player] [time]");
					return true;
				}
			}
			MagicCarpet.getMagicLogger().info("/mcb [player] [time]");
			return true;
		} else {
			Player player = (Player) sender;
			if (!MagicCarpet.getVault().isEnabled()) {
				sender.sendMessage("Economy support is not enabled.");
				return true;
			}
			if (Permissions.canNotPay(player)) {
				player.sendMessage("You don't need to use this. You have unlimited time to use MagicCarpet.");
				return true;
			}
			if (args.length == 0) {
				if (MagicCarpet.getMagicConfig().getChargeTimeBased()) {
					player.sendMessage("You have "
							+ MagicCarpet.getVault().getTime(player)
							+ " of time left.");
					if (MagicCarpet.getCarpets().canAutoRenew(player)) {
						player.sendMessage("You have auto-renew enabled for plan "
								+ MagicCarpet.getCarpets().getAutoPackage(
										player) + ".");
						return true;
					}
					if (MagicCarpet.getVault().get(player) == 0L) {
						player.sendMessage("You have ran out of time. Take a look a /mcb -l for a list of available plans you can purchase.");
					} else if (MagicCarpet.getVault().get(player) <= 300L) {
						player.sendMessage("You are running low on time. Take a look a /mcb -l for a list of available plans you can purchase.");
					}
				} else {
					if (!MagicCarpet.getCarpets().hasPaidFee(player)) {
						player.sendMessage("You need to pay a one time fee of "
								+ String.valueOf(MagicCarpet.getMagicConfig()
										.getChargeAmount())
								+ " "
								+ MagicCarpet.getVault()
										.getCurrencyNamePlural()
								+ " before you can use Magic Carpet. Use /mcb -b to accept this charge.");
						return true;
					}
				}
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("-l")) {
				if (MagicCarpet.getMagicConfig().getChargeTimeBased()) {
					player.sendMessage("Here are some of the time packages currently available.");
					for (Entry<String, TimePackage> set : MagicCarpet
							.getVault().getPackages().entrySet()) {
						TimePackage tp = set.getValue();
						player.sendMessage("Plan '" + set.getKey() + "' gives "
								+ MagicCarpet.getVault().getTime(tp.getTime())
								+ " and costs "
								+ MagicCarpet.getVault().format(tp.getAmount())
								+ ".");
					}
					player.sendMessage("Use /mcb to purchase a plan by typing its name in.");
				}
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("-a")) {
				if (MagicCarpet.getMagicConfig().getChargeTimeBased()) {
					if (MagicCarpet.getVault().getPackage(
							MagicCarpet.getCarpets().getAutoPackage(player)) == null) {
						player.sendMessage("You have not activated auto-renew yet");
						return true;
					}
					if (MagicCarpet.getCarpets().canAutoRenew(player)) {
						MagicCarpet.getCarpets().setAutoRenew(player, false);
						player.sendMessage("You have disabled auto-renew.");
					} else {
						MagicCarpet.getCarpets().setAutoRenew(player, true);
						player.sendMessage("You have re-activated auto-renew.");
					}
				}
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("-b")) {
				if (MagicCarpet.getMagicConfig().getChargeTimeBased()) {
					return true;
				}
				if (MagicCarpet.getCarpets().hasPaidFee(player)) {
					player.sendMessage("You've already paid the fee, use /mc!");
					return true;
				}
				if (MagicCarpet.getVault().hasEnough(player.getName(),
						MagicCarpet.getMagicConfig().getChargeAmount())) {
					MagicCarpet.getVault().subtract(player.getName(),
							MagicCarpet.getMagicConfig().getChargeAmount());
					MagicCarpet.getCarpets().setPaidFee(player, true);
					player.sendMessage("You have successfully paid the one time fee. Use /mc!");
					return true;
				} else {
					player.sendMessage("You don't have enough "
							+ MagicCarpet.getVault().getCurrencyNamePlural()
							+ ".");
					return true;
				}
			} else if (args.length == 2 && args[1].equalsIgnoreCase("-a")) {
				if (MagicCarpet.getMagicConfig().getChargeTimeBased()) {
					if (MagicCarpet.getVault().getPackage(args[0]) == null) {
						player.sendMessage("That plan doesn't exist");
						return true;
					}
					if (MagicCarpet.getCarpets().canAutoRenew(player)
							&& MagicCarpet.getCarpets().getAutoPackage(player) == args[0]) {
						player.sendMessage("You've already activated auto-renew for the "
								+ args[0] + " plan.");
						return true;
					}
					if (!MagicCarpet.getCarpets().canAutoRenew(player)) {
						MagicCarpet.getCarpets().setAutoRenew(player, true);
						MagicCarpet.getCarpets()
								.setAutoPackage(player, args[0]);
						player.sendMessage("Auto-renew activated for plan "
								+ args[0] + ".");
						return true;
					}
				}
			} else if (args.length == 1) {
				if (MagicCarpet.getMagicConfig().getChargeTimeBased()) {
					TimePackage tp = MagicCarpet.getVault().getPackage(args[0]);
					if (tp != null) {
						if (MagicCarpet.getVault().addTime(player,
								tp.getTime(), tp.getAmount())) {
							player.sendMessage("You have purchased plan "
									+ tp.getName()
									+ " with "
									+ MagicCarpet.getVault().getTime(
											tp.getTime())
									+ " of time and was charged "
									+ MagicCarpet.getVault().format(
											tp.getAmount()) + ".");
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}
