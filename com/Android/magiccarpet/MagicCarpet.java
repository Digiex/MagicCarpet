package com.Android.magiccarpet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.Properties;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.bukkit.Permissions.Permissions;


/**
* Magic Carpet 1.5
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
	public Permissions permissions = null;
	private static Logger log = Logger.getLogger("Minecraft");
	private ArrayList<String> owners = new ArrayList<String>();
	private ArrayList<String> bums = new ArrayList<String>();
	private boolean ignore = false;
	private boolean all_can_fly = true;

    public void onEnable() {
    	setupPermissions();
        PluginDescriptionFile pdfFile = this.getDescription();
        String name = pdfFile.getName();
        log.info( "[" + name + "] " + name + " version " + pdfFile.getVersion() + " is enabled!" );
        log.info( "[" + name + "] Take yourself wonder by wonder, using /magiccarpet or /mc. " );
        if(permissions != null) log.info("[" + name + "] Using Permissions.");
        else if(all_can_fly) log.info("[" + name + "] Anyone can use the Magic Carpet.");
        else if(ignore) log.info("[" + name + "] Ignore: " + bums.toString());
        else log.info("[" + name + "] Restricted to: " + owners.toString());
        registerEvents();
    }
    public void onDisable() {
    	Hashtable<String, Carpet> carpets = playerListener.getCarpets();
    	Enumeration<String> e = carpets.keys();
		//iterate through Hashtable keys Enumeration
		while(e.hasMoreElements()) {
			String name = e.nextElement();
			Carpet c = carpets.get(name);
			c.removeCarpet();
		}
		carpets.clear();
        System.out.println("Magic Carpet disabled. Thanks for trying the plugin!");
    }
    
    private void registerEvents(){
    	getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TOGGLE_SNEAK, playerListener, Priority.Normal, this);
    }
    
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
        	if (canFly(player)){
        		if (carpet == null)
        		{
        			if (split.length < 1){
        				player.sendMessage("A glass carpet appears below your feet.");
        				Carpet newCarpet = new Carpet();
        				newCarpet.currentBlock = player.getLocation().getBlock();
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
        				newCarpet.currentBlock = player.getLocation().getBlock();
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
private boolean canFly(Player player) {
    if(all_can_fly) return true;
    if(permissions != null)
        return Permissions.Security.permission(player, "magiccarpet.mc");
    else if(ignore)
        return !bums.contains( player.getName().toLowerCase());
    else
        return owners.contains( player.getName().toLowerCase());
    }
private static String config_comment = "Magic Carpet permissions file";
    
    public void saveDefaultSettings(boolean trust){
    	Properties props = new Properties();
    	if(trust)
    		props.setProperty("can-fly","trusted_users_here,maybe_here_too");
    	else
    		props.setProperty("cannot-fly","untrusted_users_here,maybe_here_too");
    	try{
    		OutputStream propOut = new FileOutputStream(new File("magiccarpet.properties"));
    		props.store(propOut, config_comment);
    	} catch (IOException ioe) {
    		System.out.print(ioe.getMessage());
    	}
    }
    
    public void loadSettings(){
    	Properties props = new Properties();
    	try {
    		props.load(new FileInputStream("magiccarpet.properties"));
    		if (props.containsKey("can-fly")){
    			all_can_fly = false;
    			String dreamers = props.getProperty("can-fly","");
    			ignore = false;
    			if(dreamers.length() > 0){
    				String[] fliers = dreamers.toLowerCase().split(",");
    				if (fliers.length > 0)
    				{
    					owners = new ArrayList<String>(Arrays.asList(fliers));
    				}else{
    					this.saveDefaultSettings(true);
    				}
    			}else{
    				this.saveDefaultSettings(true);
    			}
    		}else{
    			if(props.containsKey("cannot-fly")){
    				all_can_fly = false;
    				String paupers = props.getProperty("cannot-fly","");
    				ignore = true;
    				if(paupers.length() > 0){
    					String[] penniless = paupers.toLowerCase().split(",");
    					if (penniless.length > 0)
    					{
    						bums = new ArrayList<String>(Arrays.asList(penniless));
    					}else{
    						this.saveDefaultSettings(false);
    					}
    				}else{
    					this.saveDefaultSettings(false);
    				}
    			}else{
    				this.saveDefaultSettings(true);
    			}
    		}
    	} catch (IOException ioe) {
    		this.saveDefaultSettings(true);
    	}
    }
    
    public void setupPermissions() {
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");


    	if(this.permissions == null) {
    	     if(test != null) {
    	    	 this.permissions = (Permissions)test;
    	     } else {
    	    	 loadSettings();
    	     }
    	}
    }


}