package com.Android.magiccarpet;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.bukkit.Permissions.Permissions;


/**
* Magic Carpet 1.4
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

public class MagicCarpet extends JavaPlugin {
	private final MagicPlayerListener playerListener = new MagicPlayerListener();
	public Permissions Permissions = null;
	private static Logger log = Logger.getLogger("Minecraft");
	private String name = "MagicCarpet";

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events
        

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
    	setupPermissions();
        PluginDescriptionFile pdfFile = this.getDescription();
        if(Permissions != null)
        	System.out.println( "Take yourself wonder by wonder, using /magiccarpet or /mc. " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
        registerEvents();
    }
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	Hashtable<String, Carpet> carpets = playerListener.getCarpets();
    	Enumeration<String> e = carpets.keys();
		//iterate through Hashtable keys Enumeration
		while(e.hasMoreElements()) {
			String name = e.nextElement();
			Carpet c = carpets.get(name);
			c.removeCarpet();
		}
		carpets.clear();
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Magic Carpet disabled. Thanks for trying the plugin!");
    }
    
    private void registerEvents(){
    	getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
    }
    
    @SuppressWarnings("static-access")
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();
        int c = 5;
        Hashtable<String, Carpet> carpets = playerListener.getCarpets();
        Player player;

        if (commandName.equals("mc") || commandName.equals("magiccarpet")) {
        	if (sender instanceof Player){
            	player = (Player)sender;
            }else{
            	return true;
            }
        	Carpet carpet = (Carpet)carpets.get(player.getName());
        	if (Permissions.Security.permission(player, "magiccarpet.mc")){
        		if (carpet == null)
        		{
        			if (split.length < 1){
        				player.sendMessage("A glass carpet appears below your feet.");
        				Carpet newCarpet = new Carpet();
        				newCarpet.setSize(5);
        				carpets.put(player.getName(), newCarpet);
        				playerListener.setCarpets(carpets);
        			}else{
        				try {
        					c = Integer.valueOf(split[0]);
        				} catch(NumberFormatException e) {
        					player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, or 7!");
        					return false;
        				}
        			
        				if (c != 3 && c != 5 && c != 7){
        					player.sendMessage("The size can only be 3, 5, or 7. Please enter a proper number");
        					return false;
        				}
        				player.sendMessage("A glass carpet appears below your feet.");
        				Carpet newCarpet = new Carpet();
        				newCarpet.setSize(c);
        				carpets.put(player.getName(), newCarpet);
        				playerListener.setCarpets(carpets);
        			}
        		
        		}
        		if (carpet != null)
        		{
        			if(split.length == 1){
        				try {
        					c = Integer.valueOf(split[0]);
        				} catch(NumberFormatException e) {
        					player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, or 7!");
        					return false;
        				}
        			
        				if (c != 3 && c != 5 && c != 7){
        					player.sendMessage("The size can only be 3, 5, or 7. Please enter a proper number");
        					return false;
        				}
        				if(c != carpet.size){
        					player.sendMessage("The carpet seems to react to your words, and suddenly changes shape!");
        					carpet.changeCarpet(c);
        				}else{
        					player.sendMessage("Poof! The magic carpet disappears.");
                			carpets.remove(player.getName());
                			carpet.removeCarpet();
                			playerListener.setCarpets(carpets);
        				}
        			}else{
        				player.sendMessage("Poof! The magic carpet disappears.");
            			carpets.remove(player.getName());
            			carpet.removeCarpet();
            			playerListener.setCarpets(carpets);
        			}
        		
        		}
        		return true;
        	}else{
        		player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
        		return true;
        	}
        }
        else
        {
        	return false;
        }
    }
    
    public void setupPermissions() {
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");


    	if(this.Permissions == null) {
    	     if(test != null) {
    	    	 this.Permissions = (Permissions)test;
    	     } else {
    	    	 log.info("[" + name + "] Permission system not enabled. Disabling plugin.");
    	    	 this.getServer().getPluginManager().disablePlugin(this);
    	     }
    	}
    }
    
    public boolean isDebugging(final Player player) {
    	return false;
    }

    public void setDebugging(final Player player, final boolean value) { }


}