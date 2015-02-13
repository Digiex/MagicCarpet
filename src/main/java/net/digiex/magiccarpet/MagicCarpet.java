package net.digiex.magiccarpet;

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

    static Logger log;
    private static MagicCarpet instance;

    @Override
    public void onDisable() {
        Storage.saveCarpets();
    }

    @Override
    public void onEnable() {
        instance = this;
        log = getLogger();
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        Storage.loadCarpets();
        registerEvents();
        registerCommands();
    }

    private void registerCommands() {
        getCommand("magiccarpet").setExecutor(new Command());
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    static MagicCarpet getInstance() {
        return instance;
    }

    static Logger log() {
        return log;
    }
}
