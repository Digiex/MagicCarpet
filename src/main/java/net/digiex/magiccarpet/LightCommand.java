package net.digiex.magiccarpet;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Magic Carpet 2.2 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

    private final MagicCarpet plugin;

    public LightCommand(MagicCarpet plugin) {
        this.plugin = plugin;
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
            Carpet carpet = MagicCarpet.carpets.getCarpet(player);
            if (carpet == null || !carpet.isVisible()) {
                player.sendMessage("You don't have a carpet yet, use /mc!");
                return true;
            }
            if (args.length < 1) {
                hideOrShow(player);
            } else {
                if (MagicCarpet.carpets.hasLight(player)) {
                    if (plugin.customLights) {
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
                            if (MagicCarpet.acceptableLight.contains(m)) {
                                player.sendMessage("The carpet reacts to your words and suddenly changes!");
                                carpet.setLights(m);
                            } else {
                                player.sendMessage("A magic light of that material would not light up!");
                                return true;
                            }
                        } else {
                            player.sendMessage("Material error; Material may be entered as JACK_O_LANTERN or just plain jack o lantern");
                            return true;
                        }
                    } else {
                        player.sendMessage("The magic light isn't allowed to change material.");
                        return true;
                    }
                } else {
                    player.sendMessage("You haven't enabled the magic light yet.");
                    return true;
                }
            }
        } else {
            if (plugin.canFly(player)) {
                player.sendMessage("You do not have permission to use magic light!");
                return true;
            } else {
                player.sendMessage("You aren't allowed to use the magic carpet!");
                return true;
            }
        }
        MagicCarpet.carpets.update(player);
        return true;
    }

    private void hideOrShow(Player player) {
        if (!plugin.lights) {
            player.sendMessage("The magic light is disabled");
            return;
        }
        if (MagicCarpet.carpets.hasLight(player)) {
            MagicCarpet.carpets.lightOff(player);
            player.sendMessage("The luminous stones in the carpet slowly fade away.");
        } else {
            MagicCarpet.carpets.lightOn(player);
            player.sendMessage("A bright flash shines as glowing stones appear in the carpet.");
        }
    }
}
