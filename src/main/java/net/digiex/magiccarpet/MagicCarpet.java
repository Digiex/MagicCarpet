package net.digiex.magiccarpet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

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

    private Logger log;
    private static Storage carpets = new Storage();

    @Override
    public void onDisable() {
        saveCarpets();
        log.info("is now disabled!");
    }

    @Override
    public void onEnable() {
        log = getLogger();
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        loadCarpets();
        registerEvents();
        registerCommands();
        log.info("is now enabled!");
    }

    private void registerCommands() {
        getCommand("magiccarpet").setExecutor(new Command());
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    private void saveCarpets() {
        final File carpetDat = new File(getDataFolder(), "carpets.dat");
        log.info("Saving carpets...");
        if (!carpetDat.exists())
            try {
                carpetDat.createNewFile();
            } catch (final IOException e) {
                log.severe("Unable to create carpets.dat; IOException");
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

    private void loadCarpets() {
        final File carpetDat = new File(getDataFolder(), "carpets.dat");
        if (!carpetDat.exists())
            return;
        log.info("Loading carpets...");
        try {
            final FileInputStream file = new FileInputStream(carpetDat);
            final ObjectInputStream in = new ObjectInputStream(file);
            carpets = (Storage) in.readObject();
            in.close();
        } catch (final Exception e) {
            log.warning("Error loading carpets.dat; carpets data has not been loaded.");
        }
    }
    
    public static Storage getCarpets() {
        return carpets;
    }
}