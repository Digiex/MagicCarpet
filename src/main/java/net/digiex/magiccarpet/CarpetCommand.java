package net.digiex.magiccarpet;

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
public class CarpetCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Sorry, only players can use the carpet!");
			return true;
		}
		Player player = (Player) sender;
		Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
		if (!MagicCarpet.canFly(player)) {
			player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
			return true;
		}
		if (player.getFallDistance() > 0
				&& !player.getLocation().getBlock().isLiquid()) {
			return true;
		}
		if (carpet == null) {
			Carpet.create(player).show();
		} else {
			if (carpet.isVisible()) {
				carpet.hide();
			} else {
				carpet.show();
			}
		}
		return true;
	}
}
