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

    private static HashMap<String, Object> options = new HashMap<String, Object>();
    private static FileConfiguration config;
    private static File configFile;
    private static Logger log;

    Config(final MagicCarpet plugin) {
        log = plugin.getLogger();
        config = plugin.getConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");

        options.put("crouch-descent", Config.crouchDef);
        options.put("center-light", Config.glowCenter);
        options.put("default-size", Config.carpSize);
        options.put("carpet-material", saveString(Config.carpMaterial.name()));
        options.put("light-material", saveString(Config.lightMaterial.name()));
        options.put("max-size", Config.maxCarpSize);
        options.put("custom-carpets", Config.customCarpets);
        options.put("custom-lights", Config.customLights);
        options.put("lights", Config.lights);
        options.put("save-carpets", Config.saveCarpets);
        options.put("change-liquids", Config.changeLiquids);
        options.put("tools", Config.tools);
        options.put("charge", Config.charge);
        options.put("charge-timebased", Config.chargeTimeBased);
        options.put("charge-amount", Config.chargeAmount);
        options.put("charge-time", Config.chargeTime);
        options.put("charge-packages", Config.chargePackages);
        options.put("magic", Config.magicEffect);
        options.put("pvp", Config.pvp);
        options.put("physics-fun", Config.physics);
        options.put("pvp-hide", Config.pvpHide);
        options.put("carpet-data", "white");
        options.put("fall-damage", Config.fall);

        if (configFile.exists())
            loadSettings();
        else
            saveSettings();
    }

    private static Material carpMaterial = GLASS;
    private static int carpSize = 5;
    private static boolean crouchDef = true;
    private static boolean customCarpets = false;
    private static boolean glowCenter = false;
    private static Material lightMaterial = GLOWSTONE;
    private static int maxCarpSize = 7;
    private static boolean saveCarpets = true;
    private static boolean lights = false;
    private static boolean customLights = false;
    private static boolean charge = false;
    private static double chargeAmount = 20.0;
    private static String changeLiquids = "true";
    private static boolean tools = false;
    private static List<?> chargePackages = Arrays.asList("alpha:3600:5.0", "beta:7200:10.0");
    private static long chargeTime = 1800;
    private static boolean chargeTimeBased = false;
    private static boolean magicEffect = true;
    private static boolean pvp = false;
    private static boolean physics = false;
    private static boolean pvpHide = true;
    private static Byte carpetData = 0;
    private static boolean fall = true;

    private static String saveString(final String s) {
        return s.toLowerCase().replace("_", " ");
    }

    private static String loadString(final String s) {
        return s.toUpperCase().replace(" ", "_");
    }

    private static Byte getColor(final String data) {
        if (data.equals("white"))
            return (byte) 0;
        else if (data.equals("orange"))
            return (byte) 1;
        else if (data.equals("magenta"))
            return (byte) 2;
        else if (data.equals("light blue"))
            return (byte) 3;
        else if (data.equals("yellow"))
            return (byte) 4;
        else if (data.equals("lime"))
            return (byte) 5;
        else if (data.equals("pink"))
            return (byte) 6;
        else if (data.equals("gray"))
            return (byte) 7;
        else if (data.equals("light gray"))
            return (byte) 8;
        else if (data.equals("cyan"))
            return (byte) 9;
        else if (data.equals("purple"))
            return (byte) 10;
        else if (data.equals("blue"))
            return (byte) 11;
        else if (data.equals("brown"))
            return (byte) 12;
        else if (data.equals("green"))
            return (byte) 13;
        else if (data.equals("red"))
            return (byte) 14;
        else if (data.equals("black"))
            return (byte) 15;
        else
            return (byte) 0;
    }

    public static Material getCarpetMaterial() {
        return carpMaterial;
    }

    public static void setCarpetMaterial(final Material material) {
        Config.carpMaterial = material;
    }

    public static int getCarpSize() {
        return carpSize;
    }

    public static void setCarpSize(final int carpSize) {
        Config.carpSize = carpSize;
    }

    public static boolean getCrouch() {
        return crouchDef;
    }

    public static void setCrouch(final boolean crouchDef) {
        Config.crouchDef = crouchDef;
    }

    public static boolean getCustomCarpets() {
        return customCarpets;
    }

    public static void setCustomCarpets(final boolean customCarpets) {
        Config.customCarpets = customCarpets;
    }

    public static boolean getGlowing() {
        return glowCenter;
    }

    public static void setGlowing(final boolean glowCenter) {
        Config.glowCenter = glowCenter;
    }

    public static Material getLightMaterial() {
        return lightMaterial;
    }

    public static void setLightMaterial(final Material lightMaterial) {
        Config.lightMaterial = lightMaterial;
    }

    public static int getMaxCarpetSize() {
        return maxCarpSize;
    }

    public static void setMaxCarpetSize(final int maxCarpSize) {
        Config.maxCarpSize = maxCarpSize;
    }

    public static boolean getSaveCarpets() {
        return saveCarpets;
    }

    public static void setSaveCarpets(final boolean saveCarpets) {
        Config.saveCarpets = saveCarpets;
    }

    public static boolean getLights() {
        return lights;
    }

    public static void setLights(final boolean lights) {
        Config.lights = lights;
    }

    public static boolean getCustomLights() {
        return customLights;
    }

    public static void setCustomLights(final boolean customLights) {
        Config.customLights = customLights;
    }

    public static boolean getCharge() {
        return charge;
    }

    public static void setCharge(final boolean charge) {
        Config.charge = charge;
    }

    public static double getChargeAmount() {
        return chargeAmount;
    }

    public static void setChargeAmount(final double chargeAmount) {
        Config.chargeAmount = chargeAmount;
    }

    public static String getChangeLiquids() {
        return changeLiquids;
    }

    public static void setChangeLiquids(final String changeLiquids) {
        Config.changeLiquids = changeLiquids;
    }

    public static boolean getTools() {
        return tools;
    }

    public static void setTools(final boolean tools) {
        Config.tools = tools;
    }

    public static List<?> getChargePackages() {
        return chargePackages;
    }

    public static void setChargePackages(final List<?> chargePackages) {
        Config.chargePackages = chargePackages;
    }

    public static long getChargeTime() {
        return chargeTime;
    }

    public static void setChargeTime(final long chargeTime) {
        Config.chargeTime = chargeTime;
    }

    public static boolean getChargeTimeBased() {
        return chargeTimeBased;
    }

    public static void setChargeTimeBased(final boolean chargeTimeBased) {
        Config.chargeTimeBased = chargeTimeBased;
    }

    public static boolean getMagicEffect() {
        return magicEffect;
    }

    public static void setMagicEffect(final boolean magicEffect) {
        Config.magicEffect = magicEffect;
    }

    public static boolean getPvp() {
        return pvp;
    }

    public static void setPvp(final boolean pvp) {
        Config.pvp = pvp;
    }

    public static boolean getPhysics() {
        return physics;
    }

    public static void setPhysics(final boolean physics) {
        Config.physics = physics;
    }

    public static boolean getPVPHide() {
        return pvpHide;
    }

    public static void setPVPHide(final boolean pvpHide) {
        Config.pvpHide = pvpHide;
    }

    public static Byte getCarpetData() {
        return carpetData;
    }

    public static void setCarpetData(final byte carpetData) {
        Config.carpetData = carpetData;
    }
    
    public static boolean getFallDamage() {
        return fall;
    }

    public static void setFallDamage(final boolean fall) {
        Config.fall = fall;
    }

    public static void saveSettings() {
        for (final Entry<String, Object> o : options.entrySet())
            config.set(o.getKey(), o.getValue());
        config.options().header("Be sure to use /mr if you change any settings here while the server is running.");
        try {
            config.save(configFile);
        } catch (final Exception e) {
            log.severe("Unable to create config.yml");
        }
    }

    public static void loadSettings() {
        try {
            config.load(configFile);
        } catch (final FileNotFoundException e) {
            log.warning("Error loading config.yml; file not found.");
            log.warning("Creating new config.yml since the old one has disappeared.");
            saveSettings();
        } catch (final IOException e) {
            log.warning("Error loading config.yml; IOException");
        } catch (final InvalidConfigurationException e) {
            log.warning("Error loading config.yml; InvalidConfigurationException");
        }
        checkConfig();
        crouchDef = config.getBoolean("crouch-descent", true);
        glowCenter = config.getBoolean("center-light", false);
        carpSize = config.getInt("default-size", 5);
        carpMaterial = Material.getMaterial(loadString(config.getString("carpet-material", GLASS.name())));
        if (carpMaterial == null)
            carpMaterial = Material.matchMaterial(config.getString("carpet-material", GLASS.name()));
        if (!MagicCarpet.getAcceptableCarpetMaterial().contains(carpMaterial)) {
            carpMaterial = GLASS;
            log.warning("Config error; Invaild carpet material.");
        }
        carpetData = getColor(config.getString("carpet-data", carpetData.toString()));
        lightMaterial = Material.getMaterial(loadString(config.getString("light-material", GLOWSTONE.name())));
        if (lightMaterial == null)
            lightMaterial = Material.matchMaterial(config.getString("light-material", GLOWSTONE.name()));
        if (!MagicCarpet.getAcceptableLightMaterial().contains(lightMaterial)) {
            lightMaterial = GLOWSTONE;
            log.warning("Config error; Invalid light material.");
        }
        maxCarpSize = config.getInt("max-size", 7);
        if (carpSize > maxCarpSize) {
            carpSize = 5;
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
        if (!changeLiquids.equals("lava") && !changeLiquids.equals("water") && !changeLiquids.equals("false"))
            changeLiquids = "true";
        tools = config.getBoolean("tools", false);
        chargeTime = config.getLong("charge-time", 1800);
        chargePackages = config.getList("charge-packages", Arrays.asList("alpha:3600:5.0", "beta:7200:10.0"));
        chargeTimeBased = config.getBoolean("charge-timebased", false);
        magicEffect = config.getBoolean("magic", true);
        pvp = config.getBoolean("pvp", false);
        physics = config.getBoolean("physics-fun", false);
        pvpHide = config.getBoolean("pvp-hide", true);
        fall = config.getBoolean("fall-damage", true);
    }

    public static void checkConfig() {
        boolean updated = false;
        for (final Entry<String, Object> o : options.entrySet()) {
            final String key = o.getKey();
            if (!config.contains(key)) {
                config.set(key, o.getValue());
                updated = true;
            }
        }
        try {
            config.save(configFile);
        } catch (final Exception e) {
            log.warning("Unable to modify config.yml");
        }
        if (updated)
            log.info("New options have been added to the config");
    }
}
