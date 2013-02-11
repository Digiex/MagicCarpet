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
public class CarpetCommand implements CommandExecutor {

    private final MagicCarpet plugin;

    public CarpetCommand(MagicCarpet plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                Player who = null;
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (p.getName().toLowerCase().contains(args[1].toLowerCase()) || p.getName().equalsIgnoreCase(args[1])) {
                        who = p;
                    }
                }
                if (who != null) {
                    Carpet.create(who, plugin).show();
                    MagicCarpet.carpets.setGiven(who, true);
                    who.sendMessage("The magic carpet has been given to you.");
                    sender.sendMessage("The magic carpet was given to " + who.getName());
                    MagicCarpet.carpets.update(who);
                    return true;
                } else {
                    sender.sendMessage("Can't find player " + args[1]);
                    return true;
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("take")) {
                Player who = null;
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (p.getName().toLowerCase().contains(args[1].toLowerCase()) || p.getName().equalsIgnoreCase(args[1])) {
                        who = p;
                    }
                }
                if (who != null) {
                    if (MagicCarpet.carpets.has(who)) {
                        MagicCarpet.carpets.getCarpet(who).hide();
                        MagicCarpet.carpets.update(who);
                    }
                    MagicCarpet.carpets.setGiven(who, false);
                    who.sendMessage("The magic carpet has been taken from you.");
                    sender.sendMessage("The magic carpet was taken from " + who.getName());
                    return true;
                } else {
                    sender.sendMessage("Can't find player " + args[1]);
                    return true;
                }
            } else {
                sender.sendMessage("Sorry, only players can use the carpet!");
                return true;
            }
        }
        Player player = (Player) sender;
        Carpet carpet = MagicCarpet.carpets.getCarpet(player);
        if (!plugin.canFly(player)) {
            player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
            return true;
        }
        int c;
        if (carpet == null) {
            if (MagicCarpet.carpets.getGiven(player)) {
                carpet = Carpet.create(player, plugin);
                player.sendMessage("A glass carpet appears below your feet.");
                carpet.show();
                return true;
            }
            if (plugin.charge) {
                if (plugin.vault != null) {
                    if (plugin.vault.getEconomyProvider().has(player.getName(), plugin.chargeAmount)) {
                        plugin.vault.getEconomyProvider().withdrawPlayer(player.getName(), plugin.chargeAmount);
                        player.sendMessage("You've been charged " + plugin.vault.getEconomyProvider().format(plugin.chargeAmount).toLowerCase() + " and now have " + plugin.vault.getEconomyProvider().format(plugin.vault.getEconomyProvider().getBalance(player.getName())).toLowerCase() + " left.");
                    } else {
                        player.sendMessage("You don't have enough " + plugin.vault.getEconomyProvider().currencyNamePlural().toLowerCase() + ".");
                        return true;
                    }
                }
            }
            carpet = Carpet.create(player, plugin);
            player.sendMessage("A glass carpet appears below your feet.");
            carpet.show();
            MagicCarpet.carpets.update(player);
            return true;
        }
        if (args.length < 1) {
            if (carpet.isVisible()) {
                player.sendMessage("Poof! The magic carpet disappears.");
                carpet.hide();
                MagicCarpet.carpets.update(player);
                return true;
            } else {
                if (MagicCarpet.carpets.getGiven(player)) {
                    player.sendMessage("A glass carpet appears below your feet.");
                    carpet.show();
                    MagicCarpet.carpets.update(player);
                    return true;
                }
                if (plugin.charge) {
                    if (plugin.vault != null) {
                        if (plugin.vault.getEconomyProvider().has(player.getName(), plugin.chargeAmount)) {
                            plugin.vault.getEconomyProvider().withdrawPlayer(player.getName(), plugin.chargeAmount);
                            player.sendMessage("You've been charged " + plugin.vault.getEconomyProvider().format(plugin.chargeAmount).toLowerCase() + " and now have " + plugin.vault.getEconomyProvider().format(plugin.vault.getEconomyProvider().getBalance(player.getName())).toLowerCase() + " left.");
                        } else {
                            player.sendMessage("You don't have enough " + plugin.vault.getEconomyProvider().currencyNamePlural().toLowerCase() + ".");
                            return true;
                        }
                    }
                }
                player.sendMessage("A glass carpet appears below your feet.");
                carpet.show();
                MagicCarpet.carpets.update(player);
                return true;
            }
        } else {
            if (args.length == 2 && args[0].equals("give")) {
                if (player.isOp()) {
                    Player who = null;
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        if (p.getName().toLowerCase().contains(args[1].toLowerCase()) || p.getName().equalsIgnoreCase(args[1])) {
                            who = p;
                        }
                    }
                    if (who != null) {
                        Carpet.create(who, plugin).show();
                        MagicCarpet.carpets.setGiven(who, true);
                        who.sendMessage("The magic carpet has been given to you.");
                        player.sendMessage("You've given the magic carpet to " + who.getName());
                        MagicCarpet.carpets.update(who);
                        return true;
                    } else {
                        player.sendMessage("Can't find player " + args[1]);
                        return true;
                    }
                } else {
                    player.sendMessage("You don't have permission to use this.");
                    return true;
                }
            } else if (args.length == 2 && args[0].equals("take")) {
                if (player.isOp()) {
                    Player who = null;
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        if (p.getName().toLowerCase().contains(args[1].toLowerCase()) || p.getName().equalsIgnoreCase(args[1])) {
                            who = p;
                        }
                    }
                    if (who != null) {
                        if (MagicCarpet.carpets.has(who)) {
                            MagicCarpet.carpets.getCarpet(who).hide();
                            MagicCarpet.carpets.update(who);
                        }
                        MagicCarpet.carpets.setGiven(who, false);
                        who.sendMessage("The magic carpet has been taken from you.");
                        player.sendMessage("You've taken the magic carpet from " + who.getName());
                        return true;
                    } else {
                        player.sendMessage("Can't find player " + args[1]);
                        return true;
                    }
                } else {
                    player.sendMessage("You don't have permission to use this.");
                    return true;
                }
            } else if (args.length == 1 && args[0].equals("t") || args.length == 1 && args[0].equals("tools")) {
            	if (!plugin.canTool(player)) {
            		player.sendMessage("You cannot use the magic tools, no permission.");
            		return true;
            	}
            	if (!plugin.tools) {
            		player.sendMessage("The magic tools are not enabled.");
            		return true;
            	}
            	if (carpet.hasTools()) {
            		carpet.toolsOff();
            		MagicCarpet.carpets.update(player);
            		player.sendMessage("The magic tools have disappeared.");
            		return true;
            	}
            	carpet.toolsOn();
            	MagicCarpet.carpets.update(player);
            	player.sendMessage("The magic tools have appeared!");
            	return true;
            }
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
                                MagicCarpet.carpets.update(player);
                                return true;
                            } else {
                                player.sendMessage("A carpet of that material would not support you!");
                                return true;
                            }
                        } else {
                            player.sendMessage("Material error; Material may be entered as GOLD_BLOCK or just plain gold block");
                            return true;
                        }
                    } else {
                        player.sendMessage("The carpet isn't allowed to change material.");
                        return true;
                    }
                }
                if (c % 2 == 0 || c < 1 || c > plugin.maxCarpSize) {
                    player.sendMessage("The size must be an odd number from 1 to " + String.valueOf(plugin.maxCarpSize) + ".");
                    return true;
                }
                if (c != carpet.getSize()) {
                    carpet.changeCarpet(c);
                    MagicCarpet.carpets.update(player);
                    player.sendMessage("The carpet reacts to your words and suddenly changes!");
                    return true;
                } else {
                    player.sendMessage("The carpet size is already equal to " + c);
                    return true;
                }
            } else {
                player.sendMessage("You don't have a carpet yet.");
                return true;
            }
        }
    }
}
