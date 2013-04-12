package net.digiex.magiccarpet;

import static org.bukkit.Material.BEDROCK;
import static org.bukkit.Material.BOOKSHELF;
import static org.bukkit.Material.BRICK;
import static org.bukkit.Material.CLAY;
import static org.bukkit.Material.COAL_ORE;
import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.DIAMOND_BLOCK;
import static org.bukkit.Material.DIAMOND_ORE;
import static org.bukkit.Material.DIRT;
import static org.bukkit.Material.DOUBLE_STEP;
import static org.bukkit.Material.ENDER_STONE;
import static org.bukkit.Material.GLASS;
import static org.bukkit.Material.GLOWSTONE;
import static org.bukkit.Material.GOLD_BLOCK;
import static org.bukkit.Material.GOLD_ORE;
import static org.bukkit.Material.GRASS;
import static org.bukkit.Material.HUGE_MUSHROOM_1;
import static org.bukkit.Material.HUGE_MUSHROOM_2;
import static org.bukkit.Material.IRON_BLOCK;
import static org.bukkit.Material.IRON_ORE;
import static org.bukkit.Material.JACK_O_LANTERN;
import static org.bukkit.Material.LAPIS_BLOCK;
import static org.bukkit.Material.LAPIS_ORE;
import static org.bukkit.Material.LEAVES;
import static org.bukkit.Material.LOG;
import static org.bukkit.Material.MELON_BLOCK;
import static org.bukkit.Material.MOSSY_COBBLESTONE;
import static org.bukkit.Material.MYCEL;
import static org.bukkit.Material.NETHERRACK;
import static org.bukkit.Material.NETHER_BRICK;
import static org.bukkit.Material.NOTE_BLOCK;
import static org.bukkit.Material.OBSIDIAN;
import static org.bukkit.Material.PUMPKIN;
import static org.bukkit.Material.SANDSTONE;
import static org.bukkit.Material.SNOW_BLOCK;
import static org.bukkit.Material.SOIL;
import static org.bukkit.Material.SOUL_SAND;
import static org.bukkit.Material.SPONGE;
import static org.bukkit.Material.STONE;
import static org.bukkit.Material.WOOD;
import static org.bukkit.Material.WOOL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.digiex.magiccarpet.Metrics.Graph;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Magic Carpet 3.0 Copyright (C) 2012-2013 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
public class MagicCarpet extends JavaPlugin {

	private static EnumSet<Material> acceptableCarpet = EnumSet.of(STONE,
			GRASS, DIRT, COBBLESTONE, WOOD, BEDROCK, GOLD_ORE, IRON_ORE,
			COAL_ORE, LOG, LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK,
			SANDSTONE, NOTE_BLOCK, WOOL, GOLD_BLOCK, IRON_BLOCK, DOUBLE_STEP,
			BRICK, BOOKSHELF, MOSSY_COBBLESTONE, OBSIDIAN, DIAMOND_ORE,
			DIAMOND_BLOCK, SOIL, SNOW_BLOCK, CLAY, PUMPKIN, NETHERRACK,
			SOUL_SAND, MYCEL, NETHER_BRICK, ENDER_STONE, HUGE_MUSHROOM_1,
			HUGE_MUSHROOM_2, MELON_BLOCK);
	private static EnumSet<Material> acceptableLight = EnumSet.of(GLOWSTONE,
			JACK_O_LANTERN);
	private static CarpetStorage carpets = new CarpetStorage();
	private MagicListener magicListener = new MagicListener();
	private static Magic magic = new Magic();
	private VaultHandler vault;
	private FileConfiguration config;
	private File configFile;
	private Logger log;

	Material carpMaterial = GLASS;
	int carpSize = 5;
	boolean crouchDef = true;
	boolean customCarpets = false;
	boolean glowCenter = false;
	Material lightMaterial = GLOWSTONE;
	int maxCarpSize = 9;
	boolean saveCarpets = true;
	boolean lights = false;
	boolean customLights = false;
	boolean charge = false;
	double chargeAmount = 20.0;
	String changeLiquids = "true";
	boolean tools = false;
	List<?> chargePackages = Arrays.asList("alpha:3600:5.0", "beta:7200:10.0");
	long chargeTime = 1800;
	boolean chargeTimeBased = false;

	private String saveString(String s) {
		return s.toLowerCase().replace("_", " ");
	}

	private String loadString(String s) {
		return s.toUpperCase().replace(" ", "_");
	}

	private File carpetsFile() {
		return new File(getDataFolder(), "carpets.dat");
	}

	private void registerCommands() {
		getCommand("magiccarpet").setExecutor(new CarpetCommand(this));
		getCommand("magiclight").setExecutor(new LightCommand());
		getCommand("carpetswitch").setExecutor(new SwitchCommand());
		getCommand("magicreload").setExecutor(new ReloadCommand(this));
		getCommand("magiccarpetbuy").setExecutor(new CarpetBuyCommand(this));
	}

	private void registerEvents(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	private void startStats() {
		try {
			Metrics metrics = new Metrics(this);
			Graph graph = metrics.createGraph("Carpets");
			graph.addPlotter(new Metrics.Plotter("Total") {
				@Override
				public int getValue() {
					int i = 0;
					for (Iterator<Carpet> iterator = carpets.all().iterator(); iterator
							.hasNext();) {
						i = i + 1;
						iterator.next();
					}
					return i;
				}
			});
			graph.addPlotter(new Metrics.Plotter("Current") {
				@Override
				public int getValue() {
					int i = 0;
					for (Carpet c : carpets.all()) {
						if (c == null || !c.isVisible()) {
							continue;
						}
						i = i + 1;
					}
					return i;
				}
			});
			metrics.start();
		} catch (IOException e) {
			log.warning("Failed to submit stats.");
		}
	}

	@Override
	public void onDisable() {
		if (saveCarpets) {
			saveCarpets();
		} else {
			for (Carpet c : carpets.all()) {
				if (c == null || !c.isVisible()) {
					continue;
				}
				c.removeCarpet();
			}
		}
		log.info("is now disabled!");
	}

	@Override
	public void onEnable() {
		carpets = carpets.attach(this);
		log = getLogger();
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
		if (saveCarpets) {
			loadCarpets();
		}
		registerEvents(magicListener);
		registerCommands();
		getVault();
		startStats();
		log.info("is now enabled!");
	}

	VaultHandler getVault() {
		if (!charge) {
			return null;
		}
		if (vault != null) {
			return vault;
		}
		Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
		if (plugin == null || !(plugin instanceof net.milkbowl.vault.Vault)) {
			return null;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return null;
		}
		return vault = new VaultHandler(this, rsp.getProvider());
	}

	public static CarpetStorage getCarpets() {
		return carpets;
	}

	public static boolean canFly(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player.hasPermission("magiccarpet.mc");
	}

	public static boolean canLight(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player.hasPermission("magiccarpet.ml");
	}

	public static boolean canSwitch(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player.hasPermission("magiccarpet.mcs");
	}

	public static boolean canTool(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player.hasPermission("magiccarpet.mct");
	}

	public static boolean canReload(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player.hasPermission("magiccarpet.mr");
	}
	
	public static boolean canNotPay(Player player) {
		return (getCarpets().wasGiven(player)) ? true : player.hasPermission("magiccarpet.np");
	}

	public boolean canFlyAt(Player player, int i) {
		if (i == carpSize) {
			return true;
		}
		if (carpets.wasGiven(player)) {
			return true;
		}
		if (player.hasPermission("magiccarpet.*")) {
			return true;
		}
		return player.hasPermission("magiccarpet.mc." + i);
	}

	public static EnumSet<Material> getAcceptableCarpetMaterial() {
		return acceptableCarpet;
	}

	public static EnumSet<Material> getAcceptableLightMaterial() {
		return acceptableLight;
	}

	void saveCarpets() {
		File carpetDat = carpetsFile();
		log.info("Saving carpets...");
		if (!carpetDat.exists()) {
			try {
				carpetDat.createNewFile();
			} catch (IOException e) {
				log.severe("Unable to create carpets.dat; IOException");
			}
		}
		try {
			FileOutputStream file = new FileOutputStream(carpetDat);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(carpets);
			out.close();
		} catch (IOException e) {
			log.warning("Error writing to carpets.dat; carpets data has not been saved!");
		}
		carpets.clear();
	}

	void loadCarpets() {
		File carpetDat = carpetsFile();
		if (!carpetDat.exists()) {
			return;
		}
		log.info("Loading carpets...");
		try {
			FileInputStream file = new FileInputStream(carpetDat);
			ObjectInputStream in = new ObjectInputStream(file);
			carpets = (CarpetStorage) in.readObject();
			carpets.attach(this);
			in.close();
		} catch (IOException e) {
			log.warning("Error loading carpets.dat; carpets data has not been loaded.");
		} catch (ClassNotFoundException e) {
			log.severe("CarpetStorage class not found! This should never happen!");
		}
		carpets.checkCarpets();
	}

	void saveSettings() {
		config.set("crouch-descent", crouchDef);
		config.set("center-light", glowCenter);
		config.set("default-size", carpSize);
		config.set("carpet-material", saveString(carpMaterial.name()));
		config.set("light-material", saveString(lightMaterial.name()));
		config.set("max-size", maxCarpSize);
		config.set("custom-carpets", customCarpets);
		config.set("custom-lights", customLights);
		config.set("lights", lights);
		config.set("save-carpets", saveCarpets);
		config.set("change-liquids", changeLiquids);
		config.set("tools", tools);
		config.set("charge", charge);
		config.set("charge-timebased", chargeTimeBased);
		config.set("charge-amount", chargeAmount);
		config.set("charge-time", chargeTime);
		config.set("charge-packages", chargePackages);
		config.options()
				.header("Be sure to use /mr if you change any settings here while the server is running.");
		try {
			config.save(configFile);
		} catch (IOException e) {
			log.severe("Unable to create config.yml; IOException");
		}
	}

	void loadSettings() {
		try {
			config.load(configFile);
		} catch (FileNotFoundException e) {
			log.warning("Error loading config.yml; file not found.");
			log.warning("Creating new config.yml since the old one has disappeared.");
			saveSettings();
		} catch (IOException e) {
			log.warning("Error loading config.yml; IOException");
		} catch (InvalidConfigurationException e) {
			log.warning("Error loading config.yml; InvalidConfigurationException");
		}
		crouchDef = config.getBoolean("crouch-descent", true);
		glowCenter = config.getBoolean("center-light", false);
		carpSize = config.getInt("default-size", 5);
		carpMaterial = Material.getMaterial(loadString(config.getString(
				"carpet-material", GLASS.name())));
		if (carpMaterial == null) {
			carpMaterial = Material.getMaterial(config.getInt(
					"carpet-material", GLASS.getId()));
		}
		if (!acceptableCarpet.contains(carpMaterial)) {
			carpMaterial = GLASS;
			log.warning("Config error; Invaild carpet material.");
		}
		lightMaterial = Material.getMaterial(loadString(config.getString(
				"light-material", GLOWSTONE.name())));
		if (lightMaterial == null) {
			lightMaterial = Material.getMaterial(config.getInt(
					"light-material", GLOWSTONE.getId()));
		}
		if (!acceptableLight.contains(lightMaterial)) {
			lightMaterial = GLOWSTONE;
			log.warning("Config error; Invalid light material.");
		}
		maxCarpSize = config.getInt("max-size", 9);
		if (carpSize > maxCarpSize) {
			carpSize = 5;
			maxCarpSize = 9;
			log.warning("Config error; Default-size is larger than max-size.");
		}
		customCarpets = config.getBoolean("custom-carpets", false);
		customLights = config.getBoolean("custom-lights", false);
		saveCarpets = config.getBoolean("save-carpets", true);
		lights = config.getBoolean("lights", false);
		charge = config.getBoolean("charge", false);
		chargeAmount = config.getDouble("charge-amount", 5.0);
		changeLiquids = config.getString("change-liquids", "true");
		if (!changeLiquids.equals("lava") && !changeLiquids.equals("water")
				&& !changeLiquids.equals("false"))
			changeLiquids = "true";
		tools = config.getBoolean("tools", false);
		chargeTime = config.getLong("charge-time", 1800);
		chargePackages = config.getList("charge-packages",
				Arrays.asList("alpha:3600:5.0", "beta:7200:10.0"));
		chargeTimeBased = config.getBoolean("charge-timebased", false);
	}

	boolean canChangeLiquids(String type) {
		if (changeLiquids.equals("false"))
			return false;
		else if (changeLiquids.equals("true"))
			return true;
		else
			return changeLiquids.equals(type);
	}
	
	static void addMagic(World world, Location location, Color color) {
		try {
			magic.playFirework(
					world,
					location,
					FireworkEffect.builder().with(Type.BALL_LARGE)
							.withColor(color).build());
		} catch (Exception e) {
		}
	}
}
