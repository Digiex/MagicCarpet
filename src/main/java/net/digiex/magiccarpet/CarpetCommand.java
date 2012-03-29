package net.digiex.magiccarpet;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Magic Carpet 2.1 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
        Carpet carpet = plugin.carpets.getCarpet(player);
        if (!plugin.canFly(player)) {
            player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
            return true;
        }
        int c = plugin.carpSize;
        if (carpet == null) {
            carpet = Carpet.create(player, plugin);
        }
        if (args.length < 1) {
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
                    if (plugin.customCarpets) {
                        String word = "";
                        for (String a : args) {
                            if (word.isEmpty()) {
                                word = a;
                            } else {
                                word += " " + a;
                            }
                        }
                        Material m = Material.getMaterial(word.toUpperCase().replace(" ", "_"));
                        if (m != null) {
                            if (MagicCarpet.acceptableCarpet.contains(m)) {
                                player.sendMessage("The carpet reacts to your words and suddenly changes!");
                                carpet.changeCarpet(m);
                                plugin.carpets.update(player);
                                return true;
                            } else {
                                player.sendMessage("A carpet of that material would not support you!");
                                return true;
                            }
                        } else {
                            player.sendMessage("Material error; Material may be entered as GOLD_BLOCK or just plain gold block");
                            return false;
                        }
                    } else {
                        player.sendMessage("The carpet isn't allowed to change material.");
                        return true;
                    }
                }
                if (c % 2 == 0 || c < 1 || c > plugin.maxCarpSize) {
                    player.sendMessage("The size must be an odd number from 1 to " + String.valueOf(plugin.maxCarpSize) + ".");
                    return false;
                }
                if (c != carpet.getSize()) {
                    if (plugin.canFlyAt(player, c)) {
                        player.sendMessage("The carpet reacts to your words and suddenly changes!");
                        carpet.changeCarpet(c);
                    } else {
                        player.sendMessage("The carpet failed to expand, no permission.");
                        return true;
                    }
                } else {
                    player.sendMessage("The carpet size is already equal to " + c);
                    return true;
                }
            } else {
                player.sendMessage("You don't have a carpet yet.");
                return true;
            }
        }
        plugin.carpets.update(player);
        return true;
    }
}
