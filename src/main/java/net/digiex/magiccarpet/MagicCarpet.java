package net.digiex.magiccarpet;

import static org.bukkit.Material.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
public class MagicCarpet extends JavaPlugin {

	static final EnumSet<Material> acceptableCarpet = EnumSet.of(STONE, GRASS,
			DIRT, COBBLESTONE, WOOD, BEDROCK, GRAVEL, GOLD_ORE, IRON_ORE,
			COAL_ORE, LOG, LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK,
			SANDSTONE, NOTE_BLOCK, WOOL, GOLD_BLOCK, IRON_BLOCK, DOUBLE_STEP,
			BRICK, TNT, BOOKSHELF, MOSSY_COBBLESTONE, OBSIDIAN, DIAMOND_ORE,
			DIAMOND_BLOCK, WORKBENCH, SOIL, SNOW_BLOCK, CLAY, PUMPKIN,
			NETHERRACK, SOUL_SAND);
	static final EnumSet<Material> acceptableLight = EnumSet.of(GLOWSTONE,
			JACK_O_LANTERN);
	public MagicCarpetLogging log = new MagicCarpetLogging();
	private FileConfiguration config;
	private File configFile;
	private final MagicDamageListener damageListener = new MagicDamageListener(
			this);
	private final MagicPlayerListener playerListener = new MagicPlayerListener(
			this);
	String allowedmaterial;
	CarpetStorage carpets = new CarpetStorage().attach(this);
	Material carpMaterial = GLASS;
	int carpSize = 5;
	boolean crouchDef = true;
	boolean customCarpets = true;
	boolean glowCenter = false;
	Material lightMaterial = GLOWSTONE;
	int maxCarpSize = 9;
	boolean teleportBlock = false;

	public boolean canFly(Player player) {
		return player.hasPermission("magiccarpet.mc");
	}

	public boolean canFlyAt(Player player, Integer i) {
		return player.hasPermission("magiccarpet.mc." + i);
	}

	public boolean canLight(Player player) {
		return player.hasPermission("magiccarpet.ml");
	}

	public boolean canReload(Player player) {
		return player.hasPermission("magiccarpet.mr");
	}

	public boolean canSwitch(Player player) {
		return player.hasPermission("magiccarpet.mcs");
	}

	public boolean canTeleFly(Player player) {
		return player.hasPermission("magiccarpet.tp");
	}

	public void checkCarpet(Carpet carpet) {
		if (carpet.getSize() > maxCarpSize) {
			carpet.changeCarpet(carpSize);
		}
		if (carpet.isCustom() && !customCarpets) {
			carpet.changeCarpet(carpMaterial);
			carpet.setLights(lightMaterial);
		}
		carpets.update(carpet.getPlayer());
	}

	public void loadCarpets() {
		File carpetDat = carpetsFile();
		if (!carpetDat.exists()) {
			return;
		}
		log.info("Loading saved carpets...");
		try {
			FileInputStream file = new FileInputStream(carpetDat);
			ObjectInputStream in = new ObjectInputStream(file);
			carpets = (CarpetStorage) in.readObject();
			carpets.attach(this);
			in.close();
		} catch (IOException e) {
			log.warning("Error writing to carpets.dat; carpets data has not been saved!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.severe("CarpetStorage class not found! This should never happen!");
			e.printStackTrace();
		}
	}

	public void loadSettings() {
		try {
			config.load(configFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		crouchDef = config.getBoolean("crouch-descent",
				config.getBoolean("Crouch Default", true));
		glowCenter = config.getBoolean("center-light",
				config.getBoolean("Put glowstone for light in center", false));
		carpSize = config.getInt("default-size",
				config.getInt("Default size for carpet", 5));
		carpMaterial = Material.getMaterial(config.getInt("carpet",
				config.getInt("Carpet Material", GLASS.getId())));
		if (!acceptableCarpet.contains(carpMaterial)) {
			carpMaterial = GLASS;
		}
		lightMaterial = Material.getMaterial(config.getInt("carpet-light",
				config.getInt("Carpet Light Material", GLOWSTONE.getId())));
		if (!acceptableLight.contains(lightMaterial)) {
			lightMaterial = GLOWSTONE;
		}
		maxCarpSize = config.getInt("max-size", 9);
		teleportBlock = config.getBoolean("teleport-block", false);
		customCarpets = config.getBoolean("allow-custom", true);
	}

	@Override
	public void onDisable() {
		saveCarpets();
		carpets.clear();
	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		config = getConfig();

		configFile = new File(getDataFolder(), "config.yml");
		if (configFile.exists()) {
			loadSettings();
		} else {
			saveSettings();
		}
		loadCarpets();
		registerEvents();
		registerCommands();

		log.info("Version " + pdfFile.getVersion() + " is enabled!");
	}

	public void saveCarpets() {
		File carpetDat = carpetsFile();
		log.info("Saving carpets...");
		try {
			FileOutputStream file = new FileOutputStream(carpetDat);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(carpets);
			out.close();
		} catch (IOException e) {
			log.warning("Error writing to carpets.dat; carpets data has not been saved!");
			e.printStackTrace();
		}
	}

	public void saveSettings() {
		config.set("crouch-descent", crouchDef);
		config.set("center-light", glowCenter);
		config.set("default-size", carpSize);
		config.set("carpet", carpMaterial.getId());
		config.set("carpet-light", lightMaterial.getId());
		config.set("max-size", maxCarpSize);
		config.set("teleport-block", teleportBlock);
		config.set("allow-custom", customCarpets);
		config.options().header(
				"Be sure to use /mr if you change any settings here.");
		try {
			config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File carpetsFile() {
		return new File(getDataFolder(), "carpets.dat");
	}

	private void registerCommands() {
		getCommand("magiccarpet").setExecutor(new CarpetCommand(this));
		getCommand("magiclight").setExecutor(new LightCommand(this));
		getCommand("carpetswitch").setExecutor(new SwitchCommand(this));
		getCommand("magicreload").setExecutor(new ReloadCommand(this));
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Normal,
				this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Normal,
				this);
		pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Normal,
				this);
		pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.Normal,
				this);
		pm.registerEvent(Type.PLAYER_TOGGLE_SNEAK, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_BREAK, damageListener,
				damageListener.executor, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PHYSICS, damageListener,
				damageListener.executor, Priority.Normal, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, damageListener,
				damageListener.executor, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PISTON_RETRACT, damageListener,
				damageListener.executor, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PISTON_EXTEND, damageListener,
				damageListener.executor, Priority.Normal, this);
	}
}
