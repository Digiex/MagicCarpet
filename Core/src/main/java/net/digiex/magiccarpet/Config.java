package net.digiex.magiccarpet;

import static org.bukkit.Material.GLASS;
import static org.bukkit.Material.GLOWSTONE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

/*
 * Magic Carpet 2.4 Copyright (C) 2012-2014 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

public final class Config {

	private HashMap<String, Object> options = new HashMap<String, Object>();
	private final FileConfiguration config;
	private final File configFile;
	private final Logger log;

	Config(MagicCarpet plugin) {
		this.log = plugin.getLogger();
		this.config = plugin.getConfig();
		this.configFile = new File(plugin.getDataFolder(), "config.yml");

		options.put("crouch-descent", this.crouchDef);
		options.put("center-light", this.glowCenter);
		options.put("default-size", this.carpSize);
		options.put("carpet-material", saveString(this.carpMaterial.name()));
		options.put("light-material", saveString(this.lightMaterial.name()));
		options.put("max-size", this.maxCarpSize);
		options.put("custom-carpets", this.customCarpets);
		options.put("custom-lights", this.customLights);
		options.put("lights", this.lights);
		options.put("save-carpets", this.saveCarpets);
		options.put("change-liquids", this.changeLiquids);
		options.put("tools", this.tools);
		options.put("charge", this.charge);
		options.put("charge-timebased", this.chargeTimeBased);
		options.put("charge-amount", this.chargeAmount);
		options.put("charge-time", this.chargeTime);
		options.put("charge-packages", this.chargePackages);
		options.put("magic", this.magicEffect);
		options.put("pvp", this.pvp);
		options.put("physics-fun", this.physics);

		if (configFile.exists()) {
			loadSettings();
		} else {
			saveSettings();
		}
	}

	private Material carpMaterial = GLASS;
	private int carpSize = 5;
	private boolean crouchDef = true;
	private boolean customCarpets = false;
	private boolean glowCenter = false;
	private Material lightMaterial = GLOWSTONE;
	private int maxCarpSize = 7;
	private boolean saveCarpets = true;
	private boolean lights = false;
	private boolean customLights = false;
	private boolean charge = false;
	private double chargeAmount = 20.0;
	private String changeLiquids = "true";
	private boolean tools = false;
	private List<?> chargePackages = Arrays.asList("alpha:3600:5.0",
			"beta:7200:10.0");
	private long chargeTime = 1800;
	private boolean chargeTimeBased = false;
	private boolean magicEffect = true;
	private boolean pvp = true;
	private boolean physics = false;

	private String saveString(String s) {
		return s.toLowerCase().replace("_", " ");
	}

	private String loadString(String s) {
		return s.toUpperCase().replace(" ", "_");
	}

	public Material getCarpetMaterial() {
		return carpMaterial;
	}

	public void setCarpetMaterial(Material material) {
		this.carpMaterial = material;
	}

	public int getCarpSize() {
		return carpSize;
	}

	public void setCarpSize(int carpSize) {
		this.carpSize = carpSize;
	}

	public boolean getCrouch() {
		return crouchDef;
	}

	public void setCrouch(boolean crouchDef) {
		this.crouchDef = crouchDef;
	}

	public boolean getCustomCarpets() {
		return customCarpets;
	}

	public void setCustomCarpets(boolean customCarpets) {
		this.customCarpets = customCarpets;
	}

	public boolean getGlowing() {
		return glowCenter;
	}

	public void setGlowing(boolean glowCenter) {
		this.glowCenter = glowCenter;
	}

	public Material getLightMaterial() {
		return lightMaterial;
	}

	public void setLightMaterial(Material lightMaterial) {
		this.lightMaterial = lightMaterial;
	}

	public int getMaxCarpetSize() {
		return maxCarpSize;
	}

	public void setMaxCarpetSize(int maxCarpSize) {
		this.maxCarpSize = maxCarpSize;
	}

	public boolean getSaveCarpets() {
		return saveCarpets;
	}

	public void setSaveCarpets(boolean saveCarpets) {
		this.saveCarpets = saveCarpets;
	}

	public boolean getLights() {
		return lights;
	}

	public void setLights(boolean lights) {
		this.lights = lights;
	}

	public boolean getCustomLights() {
		return customLights;
	}

	public void setCustomLights(boolean customLights) {
		this.customLights = customLights;
	}

	public boolean getCharge() {
		return charge;
	}

	public void setCharge(boolean charge) {
		this.charge = charge;
	}

	public double getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(double chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public String getChangeLiquids() {
		return changeLiquids;
	}

	public void setChangeLiquids(String changeLiquids) {
		this.changeLiquids = changeLiquids;
	}

	public boolean getTools() {
		return tools;
	}

	public void setTools(boolean tools) {
		this.tools = tools;
	}

	public List<?> getChargePackages() {
		return chargePackages;
	}

	public void setChargePackages(List<?> chargePackages) {
		this.chargePackages = chargePackages;
	}

	public long getChargeTime() {
		return chargeTime;
	}

	public void setChargeTime(long chargeTime) {
		this.chargeTime = chargeTime;
	}

	public boolean getChargeTimeBased() {
		return chargeTimeBased;
	}

	public void setChargeTimeBased(boolean chargeTimeBased) {
		this.chargeTimeBased = chargeTimeBased;
	}

	public boolean getMagicEffect() {
		return magicEffect;
	}

	public void setMagicEffect(boolean magicEffect) {
		this.magicEffect = magicEffect;
	}

	public boolean getPvp() {
		return pvp;
	}

	public void setPvp(boolean pvp) {
		this.pvp = pvp;
	}

	public boolean getPhysics() {
		return physics;
	}

	public void setPhysics(boolean physics) {
		this.physics = physics;
	}

	public void saveSettings() {
		for (Entry<String, Object> o : options.entrySet()) {
			config.set(o.getKey(), o.getValue());
		}
		config.options()
				.header("Be sure to use /mr if you change any settings here while the server is running.");
		try {
			config.save(configFile);
		} catch (Exception e) {
			log.severe("Unable to create config.yml");
		}
	}

	public void loadSettings() {
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
		checkConfig();
		crouchDef = config.getBoolean("crouch-descent", true);
		glowCenter = config.getBoolean("center-light", false);
		carpSize = config.getInt("default-size", 5);
		carpMaterial = Material.getMaterial(loadString(config.getString(
				"carpet-material", GLASS.name())));
		if (carpMaterial == null) {
			carpMaterial = Material.matchMaterial(config.getString(
					"carpet-material", GLASS.name()));

		}
		if (!Helper.getHandler().getAcceptableCarpetMaterial()
				.contains(carpMaterial)) {
			carpMaterial = GLASS;
			log.warning("Config error; Invaild carpet material.");
		}
		lightMaterial = Material.getMaterial(loadString(config.getString(
				"light-material", GLOWSTONE.name())));
		if (lightMaterial == null) {
			lightMaterial = Material.matchMaterial(config.getString(
					"light-material", GLOWSTONE.name()));
		}
		if (!Helper.getHandler().getAcceptableLightMaterial()
				.contains(lightMaterial)) {
			lightMaterial = GLOWSTONE;
			log.warning("Config error; Invalid light material.");
		}
		maxCarpSize = config.getInt("max-size", 7);
		if (carpSize > maxCarpSize) {
			setCarpSize(5);
			maxCarpSize = 7;
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
		magicEffect = config.getBoolean("magic", true);
		pvp = config.getBoolean("pvp", true);
		physics = config.getBoolean("physics-fun", false);
	}

	public void checkConfig() {
		boolean updated = false;
		for (Entry<String, Object> o : options.entrySet()) {
			String key = o.getKey();
			if (!config.contains(key)) {
				config.set(key, o.getValue());
				updated = true;
			}
		}
		try {
			config.save(configFile);
		} catch (Exception e) {
			log.warning("Unable to modify config.yml");
		}
		if (updated) {
			log.info("New options have been added to the config");
		}
	}
}