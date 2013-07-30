package net.digiex.magiccarpet.commands;

import net.digiex.magiccarpet.Carpet;
import net.digiex.magiccarpet.Carpets;
import net.digiex.magiccarpet.MagicCarpet;

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
public class ToolCommand implements CommandExecutor {
	
	private final MagicCarpet plugin;
	private final Carpets carpets;
	
	public ToolCommand(MagicCarpet plugin) {
		this.plugin = plugin;
		this.carpets = plugin.getCarpets();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Sorry, this command is in game only.");
			return true;
		}
		Player player = (Player) sender;
		if (!plugin.canTool(player)) {
			player.sendMessage("You cannot use the magic tools, no permission.");
			return true;
		}
		Carpet carpet = carpets.getCarpet(player);
		if (carpet == null || !carpet.isVisible()) {
			player.sendMessage("You must activate the carpet first using /mc.");
			return true;
		}
		if (carpet.hasTools()) {
			carpet.toolsOff();
			return true;
		}
		carpet.toolsOn();
		return true;
	}
}