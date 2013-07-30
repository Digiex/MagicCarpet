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

public final class Config {

	private HashMap<String, Object> options = new HashMap<String, Object>();
	private final FileConfiguration config;
	private final File configFile;
	private final Logger log;

	public Config(MagicCarpet plugin) {
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

		if (configFile.exists()) {
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
	private int maxCarpSize = 9;
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

	public Material getDefaultCarpetMaterial() {
		return carpMaterial;
	}

	public void setDefaultCarpetMaterial(Material material) {
		this.carpMaterial = material;
	}

	public int getDefaultCarpSize() {
		return carpSize;
	}

	public void setDefaultCarpSize(int carpSize) {
		this.carpSize = carpSize;
	}

	public boolean getDefaultCrouch() {
		return crouchDef;
	}

	public void setDefaultCrouch(boolean crouchDef) {
		this.crouchDef = crouchDef;
	}

	public boolean getDefaultCustomCarpets() {
		return customCarpets;
	}

	public void setDefaultCustomCarpets(boolean customCarpets) {
		this.customCarpets = customCarpets;
	}

	public boolean getDefaultGlowing() {
		return glowCenter;
	}

	public void setDefaultGlowing(boolean glowCenter) {
		this.glowCenter = glowCenter;
	}

	public Material getDefaultLightMaterial() {
		return lightMaterial;
	}

	public void setDefaultLightMaterial(Material lightMaterial) {
		this.lightMaterial = lightMaterial;
	}

	public int getDefaultMaxCarpetSize() {
		return maxCarpSize;
	}

	public void setDefaultMaxCarpetSize(int maxCarpSize) {
		this.maxCarpSize = maxCarpSize;
	}

	public boolean getDefaultSaveCarpets() {
		return saveCarpets;
	}

	public void setDefaultSaveCarpets(boolean saveCarpets) {
		this.saveCarpets = saveCarpets;
	}

	public boolean getDefaultLights() {
		return lights;
	}

	public void setDefaultLights(boolean lights) {
		this.lights = lights;
	}

	public boolean getDefaultCustomLights() {
		return customLights;
	}

	public void setDefaultCustomLights(boolean customLights) {
		this.customLights = customLights;
	}

	public boolean getDefaultCharge() {
		return charge;
	}

	public void setDefaultCharge(boolean charge) {
		this.charge = charge;
	}

	public double getDefaultChargeAmount() {
		return chargeAmount;
	}

	public void setDefaultChargeAmount(double chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public String getDefaultChangeLiquids() {
		return changeLiquids;
	}

	public void setDefaultChangeLiquids(String changeLiquids) {
		this.changeLiquids = changeLiquids;
	}

	public boolean getDefaultTools() {
		return tools;
	}

	public void setDefaultTools(boolean tools) {
		this.tools = tools;
	}

	public List<?> getDefaultChargePackages() {
		return chargePackages;
	}

	public void setDefaultChargePackages(List<?> chargePackages) {
		this.chargePackages = chargePackages;
	}

	public long getDefaultChargeTime() {
		return chargeTime;
	}

	public void setDefaultChargeTime(long chargeTime) {
		this.chargeTime = chargeTime;
	}

	public boolean getDefaultChargeTimeBased() {
		return chargeTimeBased;
	}

	public void setChargeTimeBased(boolean chargeTimeBased) {
		this.chargeTimeBased = chargeTimeBased;
	}

	public boolean getDefaultMagicEffect() {
		return magicEffect;
	}

	public void setDefaultMagicEffect(boolean magicEffect) {
		this.magicEffect = magicEffect;
	}

	public boolean getDefaultPvp() {
		return pvp;
	}

	public void setDefaultPvp(boolean pvp) {
		this.pvp = pvp;
	}

	public void saveSettings() {
		for (Entry<String, Object> o : options.entrySet()) {
			config.set(o.getKey(), o.getValue());
		}
		config.options()
				.header("Be sure to use /mr if you change any settings here while the server is running.");
		try {
			config.save(configFile);
		} catch (IOException e) {
			log.severe("Unable to create config.yml; IOException");
		}
	}

	public void loadSettings() {
		crouchDef = config.getBoolean("crouch-descent", true);
		glowCenter = config.getBoolean("center-light", false);
		carpSize = config.getInt("default-size", 5);
		carpMaterial = Material.getMaterial(loadString(config.getString(
				"carpet-material", GLASS.name())));
		if (carpMaterial == null) {
			carpMaterial = Material.getMaterial(config.getInt(
					"carpet-material", GLASS.getId()));
		}
		if (!MagicCarpet.getAcceptableCarpetMaterial().contains(carpMaterial)) {
			carpMaterial = GLASS;
			log.warning("Config error; Invaild carpet material.");
		}
		lightMaterial = Material.getMaterial(loadString(config.getString(
				"light-material", GLOWSTONE.name())));
		if (lightMaterial == null) {
			lightMaterial = Material.getMaterial(config.getInt(
					"light-material", GLOWSTONE.getId()));
		}
		if (!MagicCarpet.getAcceptableLightMaterial().contains(lightMaterial)) {
			lightMaterial = GLOWSTONE;
			log.warning("Config error; Invalid light material.");
		}
		maxCarpSize = config.getInt("max-size", 9);
		if (carpSize > maxCarpSize) {
			setDefaultCarpSize(5);
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
		magicEffect = config.getBoolean("magic", true);
		pvp = config.getBoolean("pvp", true);
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
		} catch (IOException e) {
			log.severe("Unable to modify config.yml; IOException");
		}
		if (updated) {
			log.info("New options have been added to config");
		}
	}

	private String saveString(String s) {
		return s.toLowerCase().replace("_", " ");
	}

	private String loadString(String s) {
		return s.toUpperCase().replace(" ", "_");
	}
}