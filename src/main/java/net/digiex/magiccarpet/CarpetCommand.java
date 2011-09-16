package net.digiex.magiccarpet;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Magic Carpet 2.0
 * Copyright (C) 2011 Android, Celtic Minstrel, xzKinGzxBuRnzx
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
public class CarpetCommand implements CommandExecutor {

    private MagicCarpet plugin;

    public CarpetCommand(MagicCarpet plug) {
        plugin = plug;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sorry, only players can use the carpet!");
            return true;
        }
        Player player = (Player) sender;
        Carpet carpet = plugin.carpets.get(player);
        if (!canFly(player, carpet)) {
            player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
            return true;
        }
        int c = 5;
        if (carpet == null) {
            carpet = Carpet.create(player, plugin);
        }
        if (args.length < 1 || !plugin.canFly(player)) {
            if (carpet.isVisible()) {
                player.sendMessage("Poof! The magic carpet disappears.");
                carpet.hide();
            } else {
                player.sendMessage("A glass carpet appears below your feet.");
                carpet.show();
            }
        } else {
            if (carpet.isVisible()) {
                try {
                    c = Integer.valueOf(args[0]);
                } catch (NumberFormatException e) {
                    Material material = Material.getMaterial(args[0]);
                    if (material == null) {
                        player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be an odd number from 3 to 15.");
                    } else if (!MagicCarpet.acceptableMaterial.contains(material)) {
                        player.sendMessage("A carpet of that material would not support you!");
                    } else {
                        carpet.changeCarpet(material);
                        player.sendMessage("The carpet seems to react to your words, and suddenly changes material!");
                        return true;
                    }
                    return false;
                }
                if (c % 2 == 0 || c < 3 || c > 15) {
                    player.sendMessage("The size must be an odd number from 3 to 15.");
                    return false;
                }
                if (c != carpet.getSize()) {
                    if (c > plugin.maxCarpSize) {
                        player.sendMessage("A carpet of that size is not allowed.");
                        return false;
                    }
                    player.sendMessage("The carpet seems to react to your words, and suddenly changes size!");
                    carpet.changeCarpet(c);
                } else {
                    player.sendMessage("Poof! The magic carpet disappears.");
                    carpet.hide();
                }
            } else {
                try {
                    c = Integer.valueOf(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be an odd number from 3 to 15.");
                    return false;
                }
                if (c % 2 == 0 || c < 3 || c > 15) {
                    player.sendMessage("The size can only be an odd number from 3 to 15. Please enter a proper number");
                    return false;
                }
                if (c > plugin.maxCarpSize) {
                    player.sendMessage("A carpet of that size is not allowed.");
                    return false;
                }
                player.sendMessage("A glass carpet appears below your feet.");
                carpet.show();
            }
        }
        plugin.carpets.update(player);
        return true;
    }

    private boolean canFly(Player player, Carpet carpet) {
        if (plugin.canFly(player)) {
            return true;
        }
        if (carpet != null && carpet.isVisible()) {
            return true;
        }
        return false;
    }
}
