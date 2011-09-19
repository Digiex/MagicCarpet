package net.digiex.magiccarpet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumSet;

import static org.bukkit.Material.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

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

    static final EnumSet<Material> acceptableMaterial = EnumSet.of(
            STONE, GRASS, DIRT, COBBLESTONE, WOOD, BEDROCK, GRAVEL, GOLD_ORE, IRON_ORE, COAL_ORE, LOG,
            LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK, /*DISPENSER,*/ SANDSTONE, NOTE_BLOCK, PISTON_STICKY_BASE,
            PISTON_BASE, WOOL, GOLD_BLOCK, IRON_BLOCK, DOUBLE_STEP, /*STEP,*/ BRICK, TNT, BOOKSHELF, MOSSY_COBBLESTONE,
            OBSIDIAN, /*CHEST,*/ DIAMOND_ORE, DIAMOND_BLOCK, WORKBENCH, SOIL, /*FURNACE,*/ REDSTONE_ORE, SNOW_BLOCK,
            CLAY, /*JUKEBOX,*/ PUMPKIN, NETHERRACK, SOUL_SAND, GLOWSTONE, JACK_O_LANTERN/*, LOCKED_CHEST*/);
    private final MagicPlayerListener playerListener = new MagicPlayerListener(this);
    private final MagicDamageListener damageListener = new MagicDamageListener(this);
    private Configuration config;
    public MagicCarpetLogging log = new MagicCarpetLogging();
    CarpetStorage carpets = new CarpetStorage().attach(this);
    boolean crouchDef = true;
    boolean glowCenter = true;
    int carpSize = 5;
    Material carpMaterial = GLASS;
    Material lightMaterial = GLOWSTONE;
    int maxCarpSize = 15;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        config = getConfiguration();

        if (new File(getDataFolder(), "config.yml").exists()) {
            loadConfig();
        } else {
            saveConfig();
        }
        loadCarpets();
        registerEvents();
        registerCommands();

        log.info("version " + pdfFile.getVersion() + " is enabled!");
        log.info("Take yourself wonder by wonder, using /magiccarpet or /mc. ");
    }

    public void loadConfig() {
        config.load();
        crouchDef = config.getBoolean("crouch-descent", config.getBoolean("Crouch Default", true));
        glowCenter = config.getBoolean("center-light", config.getBoolean("Put glowstone for light in center", false));
        carpSize = config.getInt("default-size", config.getInt("Default size for carpet", 5));
        carpMaterial = Material.getMaterial(config.getInt("carpet", config.getInt("Carpet Material", GLASS.getId())));
        if (!acceptableMaterial.contains(carpMaterial)) {
            carpMaterial = GLASS;
        }
        lightMaterial = Material.getMaterial(config.getInt("carpet-light", config.getInt("Carpet Light Material", GLOWSTONE.getId())));
        if (!acceptableMaterial.contains(lightMaterial)) {
            lightMaterial = GLOWSTONE;
        }
        maxCarpSize = config.getInt("max-size", 15);
    }

    public void saveConfig() {
        config.setProperty("crouch-descent", crouchDef);
        config.setProperty("center-light", glowCenter);
        config.setProperty("default-size", carpSize);
        config.setProperty("carpet", carpMaterial.getId());
        config.setProperty("carpet-light", lightMaterial.getId());
        config.setProperty("max-size", maxCarpSize);
        config.save();
    }

    private File carpetsFile() {
        return new File(getDataFolder(), "carpets.dat");
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
        pm.registerEvent(Type.BLOCK_PISTON_RETRACT, damageListener, damageListener.executor, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_PISTON_EXTEND, damageListener, damageListener.executor, Priority.Normal, this);
    }

    private void registerCommands() {
        getCommand("magiccarpet").setExecutor(new CarpetCommand(this));
        getCommand("magiclight").setExecutor(new LightCommand(this));
        getCommand("carpetswitch").setExecutor(new SwitchCommand(this));
        getCommand("magicreload").setExecutor(new ReloadCommand(this));
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
        return player.hasPermission("magiccarpet.mr");
    }
}
