package net.digiex.magiccarpet;

import org.bukkit.Material;
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
public class LightCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Sorry, only players can use the carpet!");
			return true;
		}
		Player player = (Player) sender;
		if (MagicCarpet.canFly(player) && MagicCarpet.canLight(player)) {
			Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
			if (carpet == null || !carpet.isVisible()) {
				player.sendMessage("You don't have a carpet yet, use /mc!");
				return true;
			}
			if (args.length < 1) {
				if (MagicCarpet.getCarpets().hasLight(player)) {
					carpet.lightOff();
				} else {
					carpet.lightOn();
				}
			} else {
				if (MagicCarpet.getCarpets().hasLight(player)) {
					String word = "";
					for (String a : args) {
						if (word.isEmpty()) {
							word = a;
						} else {
							word += " " + a;
						}
					}
					Material m = Material.getMaterial(word.toUpperCase()
							.replace(" ", "_"));
					if (m != null) {
						carpet.setLight(m);
						return true;
					} else {
						player.sendMessage("Material error; Material may be entered as JACK_O_LANTERN or just plain jack o lantern");
						return true;
					}
				} else {
					player.sendMessage("You haven't enabled the magic light yet.");
					return true;
				}
			}
		} else {
			if (MagicCarpet.canFly(player)) {
				player.sendMessage("You do not have permission to use magic light!");
				return true;
			} else {
				player.sendMessage("You are not allowed to use the magic carpet!");
				return true;
			}
		}
		return true;
	}
}
