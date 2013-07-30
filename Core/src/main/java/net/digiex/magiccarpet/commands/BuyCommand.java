package net.digiex.magiccarpet.commands;

import java.util.Map.Entry;

import net.digiex.magiccarpet.Carpets;
import net.digiex.magiccarpet.Config;
import net.digiex.magiccarpet.MagicCarpet;
import net.digiex.magiccarpet.lib.VaultHandler.TimePackage;

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
public class BuyCommand implements CommandExecutor {

	private final MagicCarpet plugin;
	private final Config config;
	private final Carpets carpets;

	public BuyCommand(MagicCarpet plugin) {
		this.plugin = plugin;
		this.config = plugin.getMCConfig();
		this.carpets = plugin.getCarpets();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (plugin.getVault() == null) {
			sender.sendMessage("Economy support is not enabled or the required dependencies are missing.");
			return true;
		}
		if (!(sender instanceof Player)) {
			if (args.length == 2) {
				Player who = null;
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (p.getName().toLowerCase()
							.contains(args[0].toLowerCase())
							|| p.getName().equalsIgnoreCase(args[0])) {
						who = p;
						break;
					}
				}
				try {
					long time = Long.valueOf(args[1]);
					plugin.getVault().addTime(who, time);
					sender.sendMessage("Time sent to " + who.getName() + ".");
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
		} else {
			Player player = (Player) sender;
			if (args.length == 0) {
				if (!plugin.canFly(player)) {
					player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
					return true;
				}
				if (plugin.canNotPay(player)) {
					player.sendMessage("You don't need to use this. You have unlimited time to use MagicCarpet.");
					return true;
				}
				if (!carpets.hasPaidFee(player)) {
					player.sendMessage("You need to pay a one time fee of "
							+ String.valueOf(config.getDefaultChargeAmount())
							+ " "
							+ plugin.getVault().getCurrencyNamePlural()
							+ " before you can use Magic Carpet. Use /mcb -b to accept this charge.");
					return true;
				}
				if (!config.getDefaultChargeTimeBased()) {
					return true;
				}
				player.sendMessage("You have "
						+ plugin.getVault().getTime(player) + " of time left.");
				if (carpets.canAutoRenew(player)) {
					player.sendMessage("You have auto-renew enabled for plan "
							+ carpets.getAutoPackage(player)
							+ ".");
					return true;
				}
				if (plugin.getVault().get(player) <= 300) {
					player.sendMessage("You are running low on time. Take a look a /mcb -p for a list of available plans you can purchase.");
				}
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("-p")) {
				if (!plugin.canFly(player)) {
					player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
					return true;
				}
				if (plugin.canNotPay(player)) {
					player.sendMessage("You don't need to use this. You have unlimited time to use MagicCarpet.");
					return true;
				}
				if (!carpets.hasPaidFee(player)) {
					player.sendMessage("You need to pay a one time fee of "
							+ String.valueOf(config.getDefaultChargeAmount())
							+ " "
							+ plugin.getVault().getCurrencyNamePlural()
							+ " before you can use Magic Carpet. Use /mcb -b to accept this charge.");
					return true;
				}
				if (!config.getDefaultChargeTimeBased()) {
					return true;
				}
				player.sendMessage("Here are some of the time packages currently available.");
				for (Entry<String, TimePackage> set : plugin.getVault()
						.getPackages().entrySet()) {
					TimePackage tp = set.getValue();
					player.sendMessage("Plan '" + set.getKey() + "' gives "
							+ plugin.getVault().getTime(tp.getTime())
							+ " and costs "
							+ plugin.getVault().format(tp.getAmount()) + ".");
				}
				player.sendMessage("Use /mcb to purchase plan by typing it's name in.");
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("-a")) {
				if (!plugin.canFly(player)) {
					player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
					return true;
				}
				if (plugin.canNotPay(player)) {
					player.sendMessage("You don't need to use this. You have unlimited time to use MagicCarpet.");
					return true;
				}
				if (!carpets.hasPaidFee(player)) {
					player.sendMessage("You need to pay a one time fee of "
							+ String.valueOf(config.getDefaultChargeAmount())
							+ " "
							+ plugin.getVault().getCurrencyNamePlural()
							+ " before you can use Magic Carpet. Use /mcb -b to accept this charge.");
					return true;
				}
				if (!config.getDefaultChargeTimeBased()) {
					return true;
				}
				if (carpets.canAutoRenew(player)) {
					carpets.setAutoRenew(player, false);
					player.sendMessage("You've disabled auto-renew for your Magic Carpet.");
					return true;
				}
				if (!carpets.canAutoRenew(player)) {
					carpets.setAutoRenew(player, true);
					player.sendMessage("You've re-activated auto-renew for plan "
							+ carpets.getAutoPackage(player));
					return true;
				}
			} else if (args.length == 1 && args[0].equalsIgnoreCase("-b")) {
				if (!plugin.canFly(player)) {
					player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
					return true;
				}
				if (plugin.canNotPay(player)) {
					player.sendMessage("You don't need to use this. You have unlimited time to use MagicCarpet.");
					return true;
				}
				if (carpets.hasPaidFee(player)) {
					player.sendMessage("You've already paid the fee, use /mc!");
					return true;
				}
				if (plugin.getVault().hasEnough(player.getName(),
						config.getDefaultChargeAmount())) {
					plugin.getVault().subtract(player.getName(),
							config.getDefaultChargeAmount());
					carpets.setPaidFee(player, true);
					player.sendMessage("You have successfully paid the one time fee. Use /mc!");
					return true;
				} else {
					player.sendMessage("You don't have enough "
							+ plugin.getVault().getCurrencyNamePlural() + ".");
					return true;
				}
			} else if (args.length == 2 && args[1].equalsIgnoreCase("-a")) {
				if (!plugin.canFly(player)) {
					player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
					return true;
				}
				if (plugin.canNotPay(player)) {
					player.sendMessage("You don't need to use this. You have unlimited time to use MagicCarpet.");
					return true;
				}
				if (!carpets.hasPaidFee(player)) {
					player.sendMessage("You need to pay a one time fee of "
							+ String.valueOf(config.getDefaultChargeAmount())
							+ " "
							+ plugin.getVault().getCurrencyNamePlural()
							+ " before you can use Magic Carpet. Use /mcb -b to accept this charge.");
					return true;
				}
				if (!config.getDefaultChargeTimeBased()) {
					return true;
				}
				if (plugin.getVault().getPackage(args[0]) == null) {
					player.sendMessage("That plan doesn't exist");
					return true;
				}
				if (carpets.canAutoRenew(player)
						&& carpets.getAutoPackage(player) == args[0]) {
					player.sendMessage("You've already activated auto-renew for that plan.");
					return true;
				}
				if (!carpets.canAutoRenew(player)) {
					carpets.setAutoRenew(player, true);
					carpets.setAutoPackage(player, args[0]);
					player.sendMessage("Auto-renew activated for plan "
							+ args[0] + ".");
					return true;
				}
			} else if (args.length == 1) {
				if (!plugin.canFly(player)) {
					player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
					return true;
				}
				if (plugin.canNotPay(player)) {
					player.sendMessage("You don't need to use this. You have unlimited time to use MagicCarpet.");
					return true;
				}
				if (!carpets.hasPaidFee(player)) {
					player.sendMessage("You need to pay a one time fee of "
							+ String.valueOf(config.getDefaultChargeAmount())
							+ " "
							+ plugin.getVault().getCurrencyNamePlural()
							+ " before you can use Magic Carpet. Use /mcb -b to accept this charge.");
					return true;
				}
				if (!config.getDefaultChargeTimeBased()) {
					return true;
				}
				TimePackage tp = plugin.getVault().getPackage(args[0]);
				if (tp != null && tp.getName().equalsIgnoreCase(args[0])) {
					if (plugin.getVault().addTime(player, tp.getTime(),
							tp.getAmount())) {
						player.sendMessage("You have purchased plan "
								+ tp.getName() + " with "
								+ plugin.getVault().getTime(tp.getTime())
								+ " of time and was charged "
								+ plugin.getVault().format(tp.getAmount())
								+ ".");
					}
					return true;
				}
			}
		}
		return false;
	}

}
