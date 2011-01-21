package com.Android.bukkit.magiccarpet;

import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;


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

public class MagicCarpet extends JavaPlugin {
	private final MagicPlayerListener playerListener = new MagicPlayerListener(this);

	public MagicCarpet(PluginLoader pluginLoader, Server instance,
			PluginDescriptionFile desc, File folder, File plugin,
			ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		
		registerEvents();
	}

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events
        

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
    	playerListener.loadSettings();
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( "Take yourself wonder by wonder, using /magiccarpet or /mc. " + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	Hashtable<String, Carpet> carpets = playerListener.getCarpets();
    	Enumeration<String> e = carpets.keys();
		//iterate through Hashtable keys Enumeration
		while(e.hasMoreElements()) {
			String name = e.nextElement();
			Player player = getServer().getPlayer(name);
			Carpet c = carpets.get(name);
			c.removeCarpet(player.getWorld());
		}
		carpets.clear();
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Thanks for trying the plugin!");
    }
    
    private void registerEvents(){
    	getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
    }
    public boolean isDebugging(final Player player) {
    	return false;
    }

    public void setDebugging(final Player player, final boolean value) { }


}
