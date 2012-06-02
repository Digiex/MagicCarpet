package net.digiex.magiccarpet;

import java.io.*;
import java.util.EnumSet;
import java.util.logging.Logger;
import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Magic Carpet 2.1 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

    static final EnumSet<Material> acceptableCarpet = EnumSet.of(STONE, GRASS,
            DIRT, COBBLESTONE, WOOD, BEDROCK, GOLD_ORE, IRON_ORE,
            COAL_ORE, LOG, LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK,
            SANDSTONE, NOTE_BLOCK, WOOL, GOLD_BLOCK, IRON_BLOCK, DOUBLE_STEP,
            BRICK, BOOKSHELF, MOSSY_COBBLESTONE, OBSIDIAN, DIAMOND_ORE,
            DIAMOND_BLOCK, SOIL, SNOW_BLOCK, CLAY, PUMPKIN,
            NETHERRACK, SOUL_SAND, MYCEL, NETHER_BRICK, ENDER_STONE,
            HUGE_MUSHROOM_1, HUGE_MUSHROOM_2, MELON_BLOCK);
    static final EnumSet<Material> acceptableLight = EnumSet.of(GLOWSTONE, JACK_O_LANTERN);
    
    private final MagicListener magicListener = new MagicListener(this);
    CarpetStorage carpets = new CarpetStorage().attach(this);
    private WorldGuardHandler worldGuardHandler;
    public VaultHandler vault;
    public Logger log;
    
    private FileConfiguration config;
    private File configFile;
    Material carpMaterial = GLASS;
    int carpSize = 5;
    boolean crouchDef = true;
    boolean customCarpets = true;
    boolean glowCenter = false;
    Material lightMaterial = GLOWSTONE;
    int maxCarpSize = 9;
    boolean lightWater = false;
    boolean lightCustom = false;
    boolean saveCarpets = true;
    boolean lights = false;
    boolean customLights = true;
    boolean charge = false;
    double chargeAmount = 1.0;

    public boolean canFly(Player player) {
        String s = "magiccarpet.mc";
        if (vault != null && vault.isPermissionsEnabled()) {
            return vault.hasPermission(player, s);
        }
        return player.hasPermission(s);
    }

    public boolean canFlyAt(Player player, int i) {
        if (i == carpSize) {
            return true;
        }
        String s = "magiccarpet.mc.";
        Permission permission = new Permission(s + i, "Allows the carpet to operate at size " + i, PermissionDefault.OP);
        permission.addParent("magiccarpet.*", true);
        if (vault != null && vault.isPermissionsEnabled()) {
            return vault.hasPermission(player, s + i);
        }
        return player.hasPermission(permission);
    }

    public boolean canFlyHere(Player player) {
        if (worldGuardHandler != null) {
            return worldGuardHandler.canFlyHere(player);
        }
        return true;
    }

    public boolean canLight(Player player) {
        String s = "magiccarpet.ml";
        if (vault != null && vault.isPermissionsEnabled()) {
            return vault.hasPermission(player, s);
        }
        return player.hasPermission(s);
    }

    public boolean canReload(Player player) {
        String s = "magiccarpet.mr";
        if (vault != null && vault.isPermissionsEnabled()) {
            return vault.hasPermission(player, s);
        }
        return player.hasPermission(s);
    }

    public boolean canSwitch(Player player) {
        String s = "magiccarpet.mcs";
        if (vault != null && vault.isPermissionsEnabled()) {
            return vault.hasPermission(player, s);
        }
        return player.hasPermission(s);
    }

    public void loadCarpets() {
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

    public void loadSettings() {
        try {
            config.load(configFile);
        } catch (FileNotFoundException e) {
            log.warning("Error loading config.yml; file not found.");
        } catch (IOException e) {
            log.warning("Error loading config.yml; IOException");
        } catch (InvalidConfigurationException e) {
            log.warning("Error loading config.yml; InvalidConfigurationException");
        }
        crouchDef = config.getBoolean("crouch-descent",
                config.getBoolean("Crouch Default", true));
        glowCenter = config.getBoolean("center-light",
                config.getBoolean("Put glowstone for light in center", false));
        carpSize = config.getInt("default-size",
                config.getInt("Default size for carpet", 5));
        carpMaterial = Material.getMaterial(loadString(config.getString("carpet",
                GLASS.name())));
        if (carpMaterial == null) {
            carpMaterial = Material.getMaterial(config.getInt("carpet",
                    config.getInt("Carpet Material", GLASS.getId())));
        }
        if (!acceptableCarpet.contains(carpMaterial)) {
            carpMaterial = GLASS;
            log.warning("Config error; Invaild carpet material.");
        }
        lightMaterial = Material.getMaterial(loadString(config.getString("carpet-light",
                GLOWSTONE.name())));
        if (lightMaterial == null) {
            lightMaterial = Material.getMaterial(config.getInt("carpet-light",
                    config.getInt("Carpet Light Material", GLOWSTONE.getId())));
        }
        if (!acceptableLight.contains(lightMaterial)) {
            lightMaterial = GLOWSTONE;
            log.warning("Config error; Invalid carpet light material.");
        }
        maxCarpSize = config.getInt("max-size", 9);
        if (carpSize > maxCarpSize) {
            carpSize = 5;
            maxCarpSize = 9;
            log.warning("Config error; Default-size is larger than max-size.");
        }
        customCarpets = config.getBoolean("custom-carpets", config.getBoolean("allow-custom", true));
        customLights = config.getBoolean("custom-lights", true);
        lightWater = config.getBoolean("light-water", config.getBoolean("water-light", config.getBoolean("allow-water-light", false)));
        lightCustom = config.getBoolean("light-custom", config.getBoolean("custom-light", config.getBoolean("allow-custom-light", false)));
        saveCarpets = config.getBoolean("save-carpets", true);
        lights = config.getBoolean("lights", false);
        charge = config.getBoolean("charge", false);
        chargeAmount = config.getDouble("charge-amount", 1.0);
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
                c.hide();
            }
        }
        log.info("is now disabled!");
    }

    @Override
    public void onEnable() {
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
        getWorldGuard();
        getVault();
        log.info("is now enabled!");
    }

    public void saveCarpets() {
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

    public void saveSettings() {
        config.set("crouch-descent", crouchDef);
        config.set("center-light", glowCenter);
        config.set("default-size", carpSize);
        config.set("carpet", saveString(carpMaterial.name()));
        config.set("carpet-light", saveString(lightMaterial.name()));
        config.set("max-size", maxCarpSize);
        config.set("custom-carpets", customCarpets);
        config.set("custom-lights", customLights);
        config.set("lights", lights);
        config.set("light-water", lightWater);
        config.set("light-custom", lightCustom);
        config.set("save-carpets", saveCarpets);
        config.set("charge", charge);
        config.set("charge-amount", chargeAmount);
        config.options().header(
                "Be sure to use /mr if you change any settings here while the server is running.");
        try {
            config.save(configFile);
        } catch (IOException e) {
            log.severe("Unable to create config.yml; IOException");
        }
    }

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
        getCommand("magiclight").setExecutor(new LightCommand(this));
        getCommand("carpetswitch").setExecutor(new SwitchCommand(this));
        getCommand("magicreload").setExecutor(new ReloadCommand(this));
    }

    private void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof com.sk89q.worldguard.bukkit.WorldGuardPlugin)) {
            return;
        }
        worldGuardHandler = new WorldGuardHandler((com.sk89q.worldguard.bukkit.WorldGuardPlugin) plugin);
    }
    
    private void getVault() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null || !(plugin instanceof net.milkbowl.vault.Vault)) {
            return;
        }
        vault = new VaultHandler(this).setup();
    }
}
