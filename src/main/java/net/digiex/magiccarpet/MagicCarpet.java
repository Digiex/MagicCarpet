package net.digiex.magiccarpet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/*
 * Magic Carpet 2.0
 * Copyright (C) 2011 Celtic Minstrel
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
	private final MagicDamageListener damageListener = new MagicDamageListener(this);
	private Configuration config;
    public MagicCarpetLogging log = new MagicCarpetLogging();
    private File file = new File("plugins" + File.separator + "MagicCarpet", "config.yml");
	CarpetStorage carpets = new CarpetStorage().attach(this);
	boolean crouchDef = true;
	boolean glowCenter = true;
	int carpSize = 5;
    public Material carpMaterial = Material.GLASS;
    public Material lightMaterial = Material.GLOWSTONE;
    public boolean autoLight = false;
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		String name = pdfFile.getName();
		if( !getDataFolder().exists()) getDataFolder().mkdirs();
		config = getConfiguration();
		loadConfig();
		loadCarpets();
		
		log.info("[" + name + "] " + name + " version " + pdfFile.getVersion() + " is enabled!");
		log.info("[" + name + "] Take yourself wonder by wonder, using /magiccarpet or /mc. ");
		registerEvents();
	}
	
	public void loadConfig() {
		config.load();
		crouchDef = config.getBoolean("crouch-descent", config.getBoolean("Crouch Default", true));
		glowCenter = config.getBoolean("center-light", config.getBoolean("Put glowstone for light in center", false));
		carpSize = config.getInt("default-size", config.getInt("Default size for carpet", 5));
        carpMaterial = Material.getMaterial((Integer) config.getProperty("Carpet Material"));
        if (!acceptableMaterial(carpMaterial)) {
            carpMaterial = Material.GLASS;
        }
        lightMaterial = Material.getMaterial((Integer) config.getProperty("Carpet Light Material"));
        if (!acceptableMaterial(lightMaterial)) {
            lightMaterial = Material.GLOWSTONE;
        }
        autoLight = config.getBoolean("Use expiremential lightning", autoLight);
		config.removeProperty("Use Properties Permissions");
		config.removeProperty("Crouch Default");
		config.removeProperty("Put glowstone for light in center");
		config.removeProperty("Default size for carpet");
		saveConfig();
	}
	
	public void saveConfig() {
		config.setProperty("Crouch Default", crouchDef);
		config.setProperty("Put glowstone for light in center", glowCenter);
		config.setProperty("Default size for carpet", carpSize);
		config.save();
	}
	
	private File carpetsFile() {
		return new File(getDataFolder(), "carpets.dat");
	}
	
	public void loadCarpets() {
		File carpetDat = carpetsFile();
		if(!carpetDat.exists()) return;
		log.info("Loading saved carpets...");
		try {
			FileInputStream file = new FileInputStream(carpetDat);
			ObjectInputStream in = new ObjectInputStream(file);
			carpets = (CarpetStorage)in.readObject();
			carpets.attach(this);
			in.close();
		} catch(IOException e) {
			log.warning("Error writing to carpets.dat; carpets data has not been saved!");
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			log.severe("CarpetStorage class not found! This should never happen!");
			e.printStackTrace();
		}
	}
	
	public void saveCarpets() {
		File carpetDat = carpetsFile();
		log.info("Saving carpets...");
		try {
			FileOutputStream file = new FileOutputStream(carpetDat);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(carpets);
			out.close();
		} catch(IOException e) {
			log.warning("Error writing to carpets.dat; carpets data has not been saved!");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		saveCarpets();
		carpets.clear();
		System.out.println("Magic Carpet disabled. Thanks for trying the plugin!");
	}
	
	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_TOGGLE_SNEAK, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_BREAK, damageListener, damageListener.executor, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PHYSICS, damageListener, damageListener.executor, Priority.Normal, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, damageListener, damageListener.executor, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PISTON_RETRACT, blockListener, Priority.Normal, this);
		getCommand("magiccarpet").setExecutor(new CarpetCommand(this));
		getCommand("magiclight").setExecutor(new LightCommand(this));
		getCommand("carpetswitch").setExecutor(new SwitchCommand(this));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (commandName.equals("mr")) {
            if (canReload(player)) {
                Enumeration<String> e = carpets.keys();
                while (e.hasMoreElements()) {
                    String name = e.nextElement();
                    Carpet cc = carpets.get(name);
                    cc.removeCarpet();
                }
                carpets.clear();
                loadConfig();
                player.sendMessage("MagicCarpet reloaded!");
            } else {
                player.sendMessage("You do not have permission to reload MagicCarpet");
            }
        }
		sender.sendMessage("Error: unexpected command '" + command.getName() + "'; please report!");
		return false;
	}
	
	public boolean canFly(Player player) {
		return player.hasPermission("magiccarpet.mc");
	}
	
	public boolean canLight(Player player) {
		return player.hasPermission("magiccarpet.ml");
	}
	
	public boolean canSwitch(Player player) {
		return player.hasPermission("magiccarpet.mcs");
	}

    public boolean canReload(Player player) {
        if (player.hasPermission("magiccarpet.mr") || player.hasPermission("magiccarpet.*") || player.hasPermission("*")) {
            return true;
        }
        return false;
    }

    public boolean acceptableMaterial(Material material) {
        int id = material.getId();
        switch (id) {
            case 1:
                return true;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
                return true;
            case 5:
                return true;
            case 7:
                return true;
            case 12:
                return true;
            case 13:
                return true;
            case 14:
                return true;
            case 15:
                return true;
            case 16:
                return true;
            case 17:
                return true;
            case 18:
                return true;
            case 19:
                return true;
            case 20:
                return true;
            case 21:
                return true;
            case 22:
                return true;
            case 23:
                return true;
            case 24:
                return true;
            case 25:
                return true;
            case 29:
                return true;
            case 33:
                return true;
            case 35:
                return true;
            case 41:
                return true;
            case 42:
                return true;
            case 43:
                return true;
            case 45:
                return true;
            case 46:
                return true;
            case 47:
                return true;
            case 48:
                return true;
            case 49:
                return true;
            case 54:
                return true;
            case 56:
                return true;
            case 57:
                return true;
            case 58:
                return true;
            case 60:
                return true;
            case 61:
                return true;
            case 62:
                return true;
            case 73:
                return true;
            case 74:
                return true;
            case 79:
                return true;
            case 80:
                return true;
            case 82:
                return true;
            case 84:
                return true;
            case 86:
                return true;
            case 87:
                return true;
            case 88:
                return true;
            case 89:
                return true;
            case 91:
                return true;
            case 95:
                return true;
            default:
                return false;
        }
    }
}
