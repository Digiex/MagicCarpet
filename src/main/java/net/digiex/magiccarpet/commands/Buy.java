package net.digiex.magiccarpet.commands;

import java.util.Map.Entry;

import net.digiex.magiccarpet.Config;
import net.digiex.magiccarpet.MagicCarpet;
import net.digiex.magiccarpet.Permissions;
import net.digiex.magiccarpet.plugins.Plugins;
import net.digiex.magiccarpet.plugins.Vault;
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
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            if (!Plugins.isVaultEnabled()) {
                MagicCarpet.log().info("You have not enabled economy support in the config or the required dependencies are missing.");
                return true;
            }
            if (args.length == 2) {
                Player who = null;
                for (final Player p : Bukkit.getServer().getOnlinePlayers())
                    if (p.getName().toLowerCase().contains(args[0].toLowerCase()) || p.getName().equalsIgnoreCase(args[0])) {
                        who = p;
                        break;
                    }
                try {
                    final long time = Long.valueOf(args[1]);
                    Vault.addTime(who, time);
                    MagicCarpet.log().info("Time sent to " + who.getName() + ".");
                    return true;
                } catch (final NumberFormatException e) {
                    MagicCarpet.log().info("/mcb [player] [time]");
                    return true;
                }
            }
            MagicCarpet.log().info("/mcb [player] [time]");
            return true;
        } else {
            final Player player = (Player) sender;
            if (!Plugins.isVaultEnabled()) {
                sender.sendMessage("Economy support is not enabled.");
                return true;
            }
            if (Permissions.canNotPay(player)) {
                player.sendMessage("You don't need to use this. You have unlimited time to use MagicCarpet.");
                return true;
            }
            if (args.length == 0) {
                if (Config.getChargeTimeBased()) {
                    player.sendMessage("You have " + Vault.getTime(player) + " of time left.");
                    if (MagicCarpet.getCarpets().canAutoRenew(player)) {
                        player.sendMessage("You have auto-renew enabled for plan " + MagicCarpet.getCarpets().getAutoPackage(player) + ".");
                        return true;
                    }
                    if (Vault.get(player) == 0L)
                        player.sendMessage("You have ran out of time. Take a look a /mcb -l for a list of available plans you can purchase.");
                    else if (Vault.get(player) <= 300L)
                        player.sendMessage("You are running low on time. Take a look a /mcb -l for a list of available plans you can purchase.");
                } else if (!MagicCarpet.getCarpets().hasPaidFee(player)) {
                    player.sendMessage("You need to pay a one time fee of " + String.valueOf(Config.getChargeAmount()) + " " + Vault.getCurrencyNamePlural() + " before you can use Magic Carpet. Use /mcb -b to accept this charge.");
                    return true;
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("-l")) {
                if (Config.getChargeTimeBased()) {
                    player.sendMessage("Here are some of the time packages currently available.");
                    for (final Entry<String, TimePackage> set : Vault.getPackages().entrySet()) {
                        final TimePackage tp = set.getValue();
                        player.sendMessage("Plan '" + set.getKey() + "' gives " + Vault.getTime(tp.getTime()) + " and costs " + Vault.format(tp.getAmount()) + ".");
                    }
                    player.sendMessage("Use /mcb to purchase a plan by typing its name in.");
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("-a")) {
                if (Config.getChargeTimeBased()) {
                    if (Vault.getPackage(MagicCarpet.getCarpets().getAutoPackage(player)) == null) {
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
                if (Config.getChargeTimeBased())
                    return true;
                if (MagicCarpet.getCarpets().hasPaidFee(player)) {
                    player.sendMessage("You've already paid the fee, use /mc!");
                    return true;
                }
                if (Vault.hasEnough(player.getName(), Config.getChargeAmount())) {
                    Vault.subtract(player.getName(), Config.getChargeAmount());
                    MagicCarpet.getCarpets().setPaidFee(player, true);
                    player.sendMessage("You have successfully paid the one time fee. Use /mc!");
                    return true;
                } else {
                    player.sendMessage("You don't have enough " + Vault.getCurrencyNamePlural() + ".");
                    return true;
                }
            } else if (args.length == 2 && args[1].equalsIgnoreCase("-a")) {
                if (Config.getChargeTimeBased()) {
                    if (Vault.getPackage(args[0]) == null) {
                        player.sendMessage("That plan doesn't exist");
                        return true;
                    }
                    if (MagicCarpet.getCarpets().canAutoRenew(player) && MagicCarpet.getCarpets().getAutoPackage(player) == args[0]) {
                        player.sendMessage("You've already activated auto-renew for the " + args[0] + " plan.");
                        return true;
                    }
                    if (!MagicCarpet.getCarpets().canAutoRenew(player)) {
                        MagicCarpet.getCarpets().setAutoRenew(player, true);
                        MagicCarpet.getCarpets().setAutoPackage(player, args[0]);
                        player.sendMessage("Auto-renew activated for plan " + args[0] + ".");
                        return true;
                    }
                }
            } else if (args.length == 1)
                if (Config.getChargeTimeBased()) {
                    final TimePackage tp = Vault.getPackage(args[0]);
                    if (tp != null) {
                        if (Vault.addTime(player, tp.getTime(), tp.getAmount()))
                            player.sendMessage("You have purchased plan " + tp.getName() + " with " + Vault.getTime(tp.getTime()) + " of time and was charged " + Vault.format(tp.getAmount()) + ".");
                        return true;
                    }
                }
        }
        return false;
    }
}
