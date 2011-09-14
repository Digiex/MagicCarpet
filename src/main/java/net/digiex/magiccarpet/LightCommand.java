package net.digiex.magiccarpet;

import net.digiex.magiccarpet.Carpet.LightMode;

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
public class LightCommand implements CommandExecutor {

    private MagicCarpet plugin;

    public LightCommand(MagicCarpet plug) {
        plugin = plug;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sorry, only players can use the carpet!");
            return true;
        }
        Player player = (Player) sender;
        if (plugin.canFly(player) && plugin.canLight(player)) {
            if (args.length < 1) {
                hideOrShow(player);
            } else {
                LightMode mode = parseMode(args, player);
                if (mode == null) {
                    Material material = Material.getMaterial(args[0]);
                    if (material == null || !MagicCarpet.acceptableMaterial.contains(material)) {
                        player.sendMessage("A carpet of that material would not support you!");
                    } else {
                        Carpet carpet = plugin.carpets.get(player);
                        carpet.setLights(material);
                        plugin.carpets.update(player);
                        player.sendMessage("The carpet seems to react to your words, and suddenly changes material!");
                        return true;
                    }
                    return false;
                }
                if (mode == plugin.carpets.getLightMode(player)) {
                    hideOrShow(player);
                } else {
                    player.sendMessage("The carpet seems to react to your words, and suddenly glows in different places!");
                    plugin.carpets.lightOn(player, mode);
                }
            }
        } else {
            if (plugin.canFly(player)) {
                player.sendMessage("You do not have permission to use Magic Light!");
            } else {
                player.sendMessage("You aren't allowed to use the magic carpet!");
            }
        }
        return true;
    }

    private void hideOrShow(Player player) {
        if (plugin.carpets.hasLight(player)) {
            plugin.carpets.lightOff(player);
            player.sendMessage("The luminous stones in the carpet slowly fade away.");
        } else {
            plugin.carpets.lightOn(player);
            player.sendMessage("A bright flash shines as glowing stones appear in the carpet.");
        }
    }

    private LightMode parseMode(String[] args, Player player) {
        LightMode mode;
        if (args[0].equalsIgnoreCase("ring")) {
            mode = LightMode.RING;
        } else if (args[0].equalsIgnoreCase("centre")) {
            mode = LightMode.CENTRE;
        } else if (args[0].equalsIgnoreCase("center")) {
            mode = LightMode.CENTRE;
        } else if (args[0].equalsIgnoreCase("both")) {
            mode = LightMode.BOTH;
        } else {
            player.sendMessage("Invalid light mode '" + args[0] + "'");
            return null;
        }
        if (args.length > 1) {
            if (mode != LightMode.BOTH) {
                if (mode == LightMode.CENTRE && args[1].equalsIgnoreCase("ring")) {
                    mode = LightMode.BOTH;
                } else if (mode == LightMode.RING && args[1].equalsIgnoreCase("centre")) {
                    mode = LightMode.BOTH;
                } else if (mode == LightMode.RING && args[1].equalsIgnoreCase("center")) {
                    mode = LightMode.BOTH;
                } else {
                    player.sendMessage("Invalid light mode '" + args[1] + "'");
                    return null;
                }
            } else {
                player.sendMessage("Invalid light mode '" + args[1] + "'");
                return null;
            }
            if (args.length > 2) {
                player.sendMessage("Too many arguments!");
                return null;
            }
        }
        return mode;
    }
}
