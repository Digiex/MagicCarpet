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
public class LightCommand implements CommandExecutor {

	private MagicCarpet plugin;

	public LightCommand(MagicCarpet plug) {
		plugin = plug;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Sorry, only players can use the carpet!");
			return true;
		}
		Player player = (Player) sender;
		if (plugin.canFly(player) && plugin.canLight(player)) {
                        Carpet carpet = plugin.carpets.get(player);
                        if (carpet != null && carpet.isVisible()) {
                                plugin.checkCarpet(carpet);
                        } else {
                                player.sendMessage("You don't have a carpet yet, use /mc!");
                                return true;
                        }
			if (args.length < 1) {
				hideOrShow(player);
			} else {
				Material material = Material.getMaterial(args[0]);
				if (material == null
						|| !MagicCarpet.acceptableLight.contains(material)) {
					player.sendMessage("A carpet of that material would not support you!");
				} else {
					carpet.setLights(material);
					plugin.carpets.update(player);
					player.sendMessage("The carpet seems to react to your words, and suddenly changes material!");
					return true;
				}
				return false;
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
}
