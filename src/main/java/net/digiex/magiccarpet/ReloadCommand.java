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
public class ReloadCommand implements CommandExecutor {

    private MagicCarpet plugin;
    private MagicCarpetLogging log;

    public ReloadCommand(MagicCarpet plug) {
        plugin = plug;
        log = plug.log;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.loadConfig();
            log.info("has been reloaded!");
            return true;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.canReload(player)) {
                plugin.loadConfig();
                player.sendMessage("MagicCarpet has been reloaded!");
            } else {
                player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
            }
            return true;
        }
        return false;
    }
}
