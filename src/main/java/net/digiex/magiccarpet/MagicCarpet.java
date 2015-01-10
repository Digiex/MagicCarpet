package net.digiex.magiccarpet;

import static org.bukkit.Material.BEDROCK;
import static org.bukkit.Material.BOOKSHELF;
import static org.bukkit.Material.BRICK;
import static org.bukkit.Material.CLAY;
import static org.bukkit.Material.COAL_BLOCK;
import static org.bukkit.Material.COAL_ORE;
import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.DIAMOND_BLOCK;
import static org.bukkit.Material.DIAMOND_ORE;
import static org.bukkit.Material.DIRT;
import static org.bukkit.Material.DOUBLE_STEP;
import static org.bukkit.Material.EMERALD_BLOCK;
import static org.bukkit.Material.ENDER_STONE;
import static org.bukkit.Material.GLASS;
import static org.bukkit.Material.GLOWSTONE;
import static org.bukkit.Material.GOLD_BLOCK;
import static org.bukkit.Material.GOLD_ORE;
import static org.bukkit.Material.GRASS;
import static org.bukkit.Material.HARD_CLAY;
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
import static org.bukkit.Material.OBSIDIAN;
import static org.bukkit.Material.PUMPKIN;
import static org.bukkit.Material.QUARTZ_BLOCK;
import static org.bukkit.Material.SANDSTONE;
import static org.bukkit.Material.SNOW_BLOCK;
import static org.bukkit.Material.SPONGE;
import static org.bukkit.Material.STAINED_CLAY;
import static org.bukkit.Material.STAINED_GLASS;
import static org.bukkit.Material.STONE;
import static org.bukkit.Material.WOOD;
import static org.bukkit.Material.WOOL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumSet;
import java.util.logging.Logger;

import net.digiex.magiccarpet.plugins.Plugins;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
public class MagicCarpet extends JavaPlugin {

    private static EnumSet<Material> acceptableCarpet = EnumSet.of(STONE, GRASS, DIRT, COBBLESTONE, WOOD, BEDROCK, GOLD_ORE, IRON_ORE, COAL_ORE, LOG, LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK, SANDSTONE, WOOL, GOLD_BLOCK, IRON_BLOCK, DOUBLE_STEP, BRICK, BOOKSHELF, MOSSY_COBBLESTONE, OBSIDIAN, DIAMOND_ORE, DIAMOND_BLOCK, SNOW_BLOCK, CLAY, PUMPKIN, NETHERRACK, MYCEL, NETHER_BRICK, ENDER_STONE, HUGE_MUSHROOM_1, HUGE_MUSHROOM_2, MELON_BLOCK, COAL_BLOCK, EMERALD_BLOCK, HARD_CLAY, QUARTZ_BLOCK, STAINED_GLASS, STAINED_CLAY);
    private static EnumSet<Material> acceptableLight = EnumSet.of(GLOWSTONE, JACK_O_LANTERN);

    private static Logger log;
    private static Storage carpets;
    private static Magic magic = new Magic();

    private void registerCommands() {
        getCommand("magiccarpet").setExecutor(new net.digiex.magiccarpet.commands.Carpet());
        getCommand("magiclight").setExecutor(new net.digiex.magiccarpet.commands.Light());
        getCommand("magiccarpetbuy").setExecutor(new net.digiex.magiccarpet.commands.Buy());
        getCommand("magicreload").setExecutor(new net.digiex.magiccarpet.commands.Reload());
        getCommand("carpetswitch").setExecutor(new net.digiex.magiccarpet.commands.Switch());
        getCommand("magictools").setExecutor(new net.digiex.magiccarpet.commands.Tool());
    }

    private void registerEvents(final Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private File carpetsFile() {
        return new File(getDataFolder(), "carpets.dat");
    }

    @Override
    public void onDisable() {
        if (Config.getSaveCarpets())
            saveCarpets();
        else
            for (final Carpet c : carpets.all()) {
                if (c == null || !c.isVisible())
                    continue;
                c.removeCarpet();
            }
        log.info("is now disabled!");
    }

    @Override
    public void onEnable() {
        log = getLogger();
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        new Config(this);
        carpets = new Storage();
        new Plugins(this);
        if (Config.getSaveCarpets())
            loadCarpets();
        new Permissions();
        registerEvents(new Listeners());
        registerCommands();
        log.info("is now enabled!");
    }

    public void saveCarpets() {
        final File carpetDat = carpetsFile();
        log.info("Saving carpets...");
        if (!carpetDat.exists())
            try {
                carpetDat.createNewFile();
            } catch (final Exception e) {
                log.severe("Unable to create carpets.dat");
            }
        try {
            final FileOutputStream file = new FileOutputStream(carpetDat);
            final ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(carpets);
            out.close();
        } catch (final Exception e) {
            log.warning("Error writing to carpets.dat; carpets data has not been saved!");
        }
        carpets.clear();
    }

    public void loadCarpets() {
        final File carpetDat = carpetsFile();
        if (!carpetDat.exists())
            return;
        log.info("Loading carpets...");
        try {
            final FileInputStream file = new FileInputStream(carpetDat);
            final ObjectInputStream in = new ObjectInputStream(file);
            carpets = (Storage) in.readObject();
            in.close();
        } catch (final Exception e) {
            log.warning("Error loading carpets.dat; it may be corrupt and will be overwritten with new data.");
            return;
        }
        carpets.checkCarpets();
    }

    public static Storage getCarpets() {
        return carpets;
    }

    public static Logger log() {
        return log;
    }

    public static Magic getMagic() {
        return magic;
    }

    public static EnumSet<Material> getAcceptableCarpetMaterial() {
        return acceptableCarpet;
    }

    public static EnumSet<Material> getAcceptableLightMaterial() {
        return acceptableLight;
    }
}
