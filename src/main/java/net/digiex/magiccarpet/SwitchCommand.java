package net.digiex.magiccarpet;

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
public class SwitchCommand implements CommandExecutor {

	private MagicCarpet plugin;

	public SwitchCommand(MagicCarpet plug) {
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
		if (plugin.canFly(player) && plugin.canSwitch(player)) {
			plugin.carpets.toggleCrouch(player);
			if (plugin.carpets.crouches(player)) {
				player.sendMessage("You now crouch to descend");
			} else {
				player.sendMessage("You now look down to descend");
			}
		} else {
			if (plugin.canFly(player)) {
				player.sendMessage("You don't have permission to switch your mode of descent.");
			} else {
				player.sendMessage("You aren't allowed to use the magic carpet!");
			}
		}
		return true;
	}
}
