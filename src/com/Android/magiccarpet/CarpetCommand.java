package com.Android.magiccarpet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CarpetCommand implements CommandExecutor {
	private MagicCarpet plugin;

	public CarpetCommand(MagicCarpet plug) {
		plugin = plug;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Sorry, only players can use the carpet!");
			return true;
		}
		Player player = (Player)sender;
		int c = 5;
		Carpet carpet = plugin.carpets.get(player.getName());
		if(plugin.canFly(player)) {
			if(carpet == null) {
				if(args.length < 1) {
					player.sendMessage("A glass carpet appears below your feet.");
					Carpet newCarpet = new Carpet(plugin.glowCenter);
					newCarpet.currentBlock = player.getLocation().getBlock();
					if(plugin.carpSize == 3 || plugin.carpSize == 5 || plugin.carpSize == 7) newCarpet.setSize(plugin.carpSize);
					else newCarpet.setSize(5);
					newCarpet.setLights(plugin.lights.contains(player.getName()));
					plugin.carpets.put(player.getName(), newCarpet);
				} else {
					try {
						c = Integer.valueOf(args[0]);
					} catch(NumberFormatException e) {
						player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, or 7!");
						return false;
					}
					
					if(c != 3 && c != 5 && c != 7) {
						player.sendMessage("The size can only be 3, 5, or 7. Please enter a proper number");
						return false;
					}
					player.sendMessage("A glass carpet appears below your feet.");
					Carpet newCarpet = new Carpet(plugin.glowCenter);
					newCarpet.currentBlock = player.getLocation().getBlock();
					newCarpet.setSize(c);
					newCarpet.setLights(plugin.lights.contains(player.getName()));
					plugin.carpets.put(player.getName(), newCarpet);
				}
				
			}
			if(carpet != null) {
				if(args.length == 1) {
					try {
						c = Integer.valueOf(args[0]);
					} catch(NumberFormatException e) {
						player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, or 7!");
						return false;
					}
					
					if(c != 3 && c != 5 && c != 7) {
						player.sendMessage("The size can only be 3, 5, or 7. Please enter a proper number");
						return false;
					}
					if(c != carpet.size) {
						player.sendMessage("The carpet seems to react to your words, and suddenly changes shape!");
						carpet.changeCarpet(c);
					} else {
						player.sendMessage("Poof! The magic carpet disappears.");
						plugin.carpets.remove(player.getName());
						carpet.removeCarpet();
					}
				} else {
					player.sendMessage("Poof! The magic carpet disappears.");
					plugin.carpets.remove(player.getName());
					carpet.removeCarpet();
				}
				
			}
			return true;
		} else {
			player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
			return true;
		}
	
	}
	
}
