package net.digiex.magiccarpet.commands;

import net.digiex.magiccarpet.Carpet;
import net.digiex.magiccarpet.Config;
import net.digiex.magiccarpet.MagicCarpet;
import net.digiex.magiccarpet.Permissions;
import net.digiex.magiccarpet.plugins.Plugins;

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
public class Light implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            MagicCarpet.log().info("Sorry, only players can use the carpet!");
            return true;
        }
        final Player player = (Player) sender;
        if (Plugins.isVaultEnabled()) {
            if (Config.getChargeTimeBased()) {
                if (MagicCarpet.getCarpets().getTime(player) <= 0L) {
                    player.sendMessage("You've ran out of time to use the Magic Carpet. Please refill using /mcb");
                    return true;
                }
            } else if (!MagicCarpet.getCarpets().hasPaidFee(player)) {
                player.sendMessage("You need to pay a one time fee before you can use Magic Carpet. Use /mcb");
                return true;
            }
        } else if (!Permissions.canLight(player)) {
            player.sendMessage("You do not have permission to use the magic light.");
            return true;
        }
        final Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
        if (carpet == null || !carpet.isVisible()) {
            player.sendMessage("You do not have a carpet yet, use /mc");
            return true;
        }
        if (args.length < 1) {
            if (MagicCarpet.getCarpets().hasLight(player))
                carpet.lightOff();
            else
                carpet.lightOn();
        } else if (MagicCarpet.getCarpets().hasLight(player)) {
            String word = "";
            for (final String a : args)
                if (word.isEmpty())
                    word = a;
                else
                    word += " " + a;
            final Material m = Material.getMaterial(word.toUpperCase().replace(" ", "_"));
            if (m != null)
                carpet.setLight(m);
            else
                player.sendMessage("Material error; Material may be entered as JACK_O_LANTERN or jack o lantern");
        } else
            player.sendMessage("You have not enabled the magic light yet.");
        return true;
    }
}
