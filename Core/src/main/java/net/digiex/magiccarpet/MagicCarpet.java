package net.digiex.magiccarpet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.logging.Logger;

import net.digiex.magiccarpet.plugins.Plugins;

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

	private static Logger log;
	private static Storage carpets;

	private boolean init;

	private void registerCommands() {
		getCommand("magiccarpet").setExecutor(
				new net.digiex.magiccarpet.commands.Carpet());
		getCommand("magiclight").setExecutor(
				new net.digiex.magiccarpet.commands.Light());
		getCommand("magiccarpetbuy").setExecutor(
				new net.digiex.magiccarpet.commands.Buy());
		getCommand("magicreload").setExecutor(
				new net.digiex.magiccarpet.commands.Reload());
		getCommand("carpetswitch").setExecutor(
				new net.digiex.magiccarpet.commands.Switch());
		getCommand("magictools").setExecutor(
				new net.digiex.magiccarpet.commands.Tool());
	}

	private void registerEvents(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	private void startStats() {
		try {
			Metrics metrics = new Metrics(this);
			net.digiex.magiccarpet.Metrics.Graph graph = metrics
					.createGraph("Carpets");
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

	private File carpetsFile() {
		return new File(getDataFolder(), "carpets.dat");
	}

	@Override
	public void onDisable() {
		if (init)
			if (Config.getSaveCarpets()) {
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
		log = getLogger();
		new Helper(this);
		if (!Helper.isEnabled()) {
			log.severe("Unable to fully initialize; Please check this is the latest build.");
			getServer().getPluginManager().disablePlugin(this);
		}
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		new Config(this);
		carpets = new Storage();
		new Plugins(this);
		if (Config.getSaveCarpets()) {
			loadCarpets();
		}
		new Permissions();
		registerEvents(new Listeners());
		registerCommands();
		startStats();
		init = true;
		log.info("is now enabled!");
	}

	public void saveCarpets() {
		File carpetDat = carpetsFile();
		log.info("Saving carpets...");
		if (!carpetDat.exists()) {
			try {
				carpetDat.createNewFile();
			} catch (Exception e) {
				log.severe("Unable to create carpets.dat");
			}
		}
		try {
			FileOutputStream file = new FileOutputStream(carpetDat);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(carpets);
			out.close();
		} catch (Exception e) {
			log.warning("Error writing to carpets.dat; carpets data has not been saved!");
		}
		carpets.clear();
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
			carpets = (Storage) in.readObject();
			in.close();
		} catch (Exception e) {
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
}
