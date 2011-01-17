
package com.Android.bukkit.magiccarpet;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
* Magic Carpet 1.0
* Copyright (C) 2011 Android <spparr@gmail.com>
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

/**
* MagicPlayerListener.java
* <br /><br />
* Listens for calls for the magic carpet, makes a carpet when a player logs on, removes one when a player logs off,
* and moves the carpet when the player moves.
*
* @author Android <spparr@gmail.com>
*/
public class MagicPlayerListener extends PlayerListener {
	private final MagicCarpet plugin;
    private static Logger a = Logger.getLogger("Minecraft");
	private Hashtable<String, Carpet> carpets = new Hashtable<String, Carpet>();

    public MagicPlayerListener(MagicCarpet instance) {
        plugin = instance;
    }

    @Override
    //When a player joins the game, if they had a carpet when the logged out it puts it back.
    public void onPlayerJoin(PlayerEvent event) {
    	Player player = event.getPlayer();
    	Carpet carpet = (Carpet)carpets.get(player.getName());
    	if (carpet == null)
    		return;
    	carpet.drawCarpet(player.getWorld());
    }

    @Override
    //When a player quits, it removes the carpet from the server
    public void onPlayerQuit(PlayerEvent event) {
    	Player player = event.getPlayer();
    	Carpet carpet = (Carpet)carpets.get(player.getName());
		if (carpet == null)
			return;
		carpet.removeCarpet(player.getWorld());
    }

    @Override
    //When the player inputs the mc command, it either puts the carpet in or removes it, depending on the previous state
    public void onPlayerCommand(PlayerChatEvent event) {
    	int c = 5;
        String[] split = event.getMessage().split(" ");
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (!event.isCancelled() && (split[0].equalsIgnoreCase("/magiccarpet") || split[0].equalsIgnoreCase("/mc"))) {
        	Carpet carpet = (Carpet)carpets.get(player.getName());
        	if (carpet == null)
        	{
        		if (split.length < 2){
        			player.sendMessage("A glass carpet appears below your feet.");
        			Carpet newCarpet = new Carpet();
        			newCarpet.size = 5;
        			carpets.put(player.getName(), newCarpet);
        		}else{
        			try {
        				c = Integer.valueOf(split[1]);
        			} catch(NumberFormatException e) {
        				player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, or 7!");
        				return;
        			}
        			
        			if (c != 3 && c != 5 && c != 7){
        				player.sendMessage("The size can only be 3, 5, or 7. Please enter a proper number");
        				return;
        			}
        			player.sendMessage("A glass carpet appears below your feet.");
        			Carpet newCarpet = new Carpet();
        			newCarpet.size = c;
        			carpets.put(player.getName(), newCarpet);
        		}
        		
        	}
        	if (carpet != null)
        	{
        		if(split.length > 1){
        			try {
        				c = Integer.valueOf(split[1]);
        			} catch(NumberFormatException e) {
        				player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, or 7!");
        				return;
        			}
        			
        			if (c != 3 && c != 5 && c != 7){
        				player.sendMessage("The size can only be 3, 5, or 7. Please enter a proper number");
        				return;
        			}
        			if(c != carpet.size){
        				player.sendMessage("The carpet seems to react to your words, and suddenly changes shape!");
        				carpet.changeCarpet(world, c);
        			}else{
        				player.sendMessage("Poof! The magic carpet disappears.");
                		carpets.remove(player.getName());
                		carpet.removeCarpet(world);
        			}
        		}else{
        			player.sendMessage("Poof! The magic carpet disappears.");
            		carpets.remove(player.getName());
            		carpet.removeCarpet(world);
        		}
        		
        	}
        	event.setCancelled(true);
        }
        else
        {
        	event.setCancelled(false);
        }
    }

    @Override
    //Lets the carpet move with the player
    public void onPlayerMove(PlayerMoveEvent event) {
    	Location from = event.getFrom().clone();
    	Location to = event.getTo().clone();
    	Player player = event.getPlayer();
    	Carpet carpet = (Carpet)carpets.get(player.getName());
    	if (carpet == null)
    		return;
    	carpet.removeCarpet(player.getWorld());
    		to.setY(to.getY()-1);
    	if(from.getPitch() == 90 && (to.getX() != from.getX() || to.getZ() != from.getZ()))
    		to.setY(to.getY()-1);
    	carpet.currentLoc = to.clone();
    	carpet.drawCarpet(player.getWorld());
    }
    
    public void onPlayerTeleport (PlayerMoveEvent event) {
    	Location to = event.getTo().clone();
    	Player player = event.getPlayer();
    	Carpet carpet = (Carpet)carpets.get(player.getName());
    	if (carpet == null)
    		return;
    	carpet.removeCarpet(player.getWorld());
    		to.setY(to.getY()-1);
    	carpet.currentLoc = to.clone();
    	carpet.drawCarpet(player.getWorld());
    }
    
    public Hashtable<String, Carpet> getCarpets(){
    	return carpets;
    }
}