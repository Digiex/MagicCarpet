package net.digiex.magiccarpet.commands;

import net.digiex.magiccarpet.Carpet;
import net.digiex.magiccarpet.Config;
import net.digiex.magiccarpet.MagicCarpet;
import net.digiex.magiccarpet.Permissions;
import net.digiex.magiccarpet.plugins.Plugins;

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
public class Switch implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sorry, only players can use the carpet!");
            return true;
        }
        final Player player = (Player) sender;
        if (Plugins.isVaultEnabled() && !MagicCarpet.getCarpets().wasGiven(player)) {
            if (!Permissions.canNotPay(player))
                if (Config.getChargeTimeBased()) {
                    if (MagicCarpet.getCarpets().getTime(player) <= 0L) {
                        player.sendMessage("You've ran out of time to use the Magic Carpet. Please refill using /mcb");
                        return true;
                    }
                } else if (!MagicCarpet.getCarpets().hasPaidFee(player)) {
                    player.sendMessage("You need to pay a one time fee before you can use Magic Carpet. Use /mcb");
                    return true;
                }
        } else if (!Permissions.canSwitch(player)) {
            player.sendMessage("You do not have permission to use the magic light.");
            return true;
        }
        final Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
        if (carpet == null || !carpet.isVisible()) {
            player.sendMessage("You don't have a carpet yet, use /mc!");
            return true;
        }
        MagicCarpet.getCarpets().toggleCrouch(player);
        if (MagicCarpet.getCarpets().crouches(player))
            player.sendMessage("You now crouch to descend");
        else
            player.sendMessage("You now look down to descend");
        return true;
    }
}
