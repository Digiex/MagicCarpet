package net.digiex.magiccarpet;

import java.util.logging.Logger;

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
public class ReloadCommand implements CommandExecutor {

    private Logger log;
    private MagicCarpet plugin;

    public ReloadCommand(MagicCarpet plug) {
        plugin = plug;
        log = plug.log;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            reload();
            log.info("has been reloaded!");
            return true;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.canReload(player)) {
                reload();
                player.sendMessage("MagicCarpet has been reloaded!");
            } else {
                player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
            }
            return true;
        }
        return false;
    }

    public void reload() {
        for (Carpet c : plugin.carpets.all()) {
            if (c == null || !c.isVisible()) {
                continue;
            }
            c.hide();
        }
        plugin.carpets.clear();
        plugin.loadSettings();
        plugin.loadCarpets();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.carpets.has(p)) {
                Carpet.create(p, plugin).show();
                Carpet c = plugin.carpets.get(p);
                c.checkCarpet();
            }
        }
    }
}
