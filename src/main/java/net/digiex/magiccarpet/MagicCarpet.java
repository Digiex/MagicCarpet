package net.digiex.magiccarpet;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * Magic Carpet 1.5
 * Copyright (C) 2011 Android <spparr@gmail.com>
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

    private final MagicPlayerListener playerListener = new MagicPlayerListener(this);
    private final MagicBlockListener blockListener = new MagicBlockListener(this, playerListener);
    private File file = new File("plugins" + File.separator + "MagicCarpet", "config.yml");
    private Configuration config = new Configuration(file);
    public static final Logger log = Logger.getLogger("Minecraft");
    private ArrayList<String> lights = new ArrayList<String>();
    private boolean glowCenter = false;
    private int carpSize = 5;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        String name = pdfFile.getName();
        
        if (file.exists()) {
            loadConfig();
        } else {
            saveConfig();
        }

        registerEvents();

        log.info("[" + name + "] " + name + " version " + pdfFile.getVersion() + " is enabled!");
        log.info("[" + name + "] Take yourself wonder by wonder, using /magiccarpet or /mc. ");
    }

    public void loadConfig() {
        config.load();
        glowCenter = config.getBoolean("Put glowstone for light in center", glowCenter);
        carpSize = config.getInt("Default size for carpet", carpSize);
    }

    public void saveConfig() {
        config.load();
        config.setProperty("Put glowstone for light in center", glowCenter);
        config.setProperty("Default size for carpet", carpSize);
        config.save();
    }

    @Override
    public void onDisable() {
        Hashtable<String, Carpet> carpets = playerListener.getCarpets();
        Enumeration<String> e = carpets.keys();
        while (e.hasMoreElements()) {
            String name = e.nextElement();
            Carpet c = carpets.get(name);
            c.removeCarpet();
        }
        carpets.clear();
        System.out.println("Magic Carpet disabled. Thanks for trying the plugin!");
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PISTON_RETRACT, blockListener, Priority.Normal, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();
        int c = 5;
        Hashtable<String, Carpet> carpets = playerListener.getCarpets();
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            return true;
        }
        Carpet carpet = (Carpet) carpets.get(player.getName());

        if (commandName.equals("mc") || commandName.equals("magiccarpet")) {
            if (canFly(player)) {
                if (carpet == null) {
                    if (split.length < 1) {
                        player.sendMessage("A glass carpet appears below your feet.");
                        Carpet newCarpet = new Carpet(glowCenter);
                        newCarpet.currentBlock = player.getLocation().getBlock();
                        if (carpSize == 3 || carpSize == 5 || carpSize == 7 || carpSize == 9) {
                            newCarpet.setSize(carpSize);
                        } else {
                            newCarpet.setSize(5);
                        }
                        newCarpet.setLights(lights.contains(player.getName()));
                        carpets.put(player.getName(), newCarpet);
                        playerListener.setCarpets(carpets);
                    } else {
                        try {
                            c = Integer.valueOf(split[0]);
                        } catch (NumberFormatException e) {
                            player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, 7, or 9!");
                            return false;
                        }

                        if (c != 3 && c != 5 && c != 7 && c != 9) {
                            player.sendMessage("The size can only be 3, 5, 7, or 9. Please enter a proper number");
                            return false;
                        }
                        player.sendMessage("A glass carpet appears below your feet.");
                        Carpet newCarpet = new Carpet(glowCenter);
                        newCarpet.currentBlock = player.getLocation().getBlock();
                        newCarpet.setSize(c);
                        newCarpet.setLights(lights.contains(player.getName()));
                        carpets.put(player.getName(), newCarpet);
                        playerListener.setCarpets(carpets);
                    }

                }
                if (carpet != null) {
                    if (split.length == 1) {
                        try {
                            c = Integer.valueOf(split[0]);
                        } catch (NumberFormatException e) {
                            player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, 7, or 9!");
                            return false;
                        }

                        if (c != 3 && c != 5 && c != 7 && c != 9) {
                            player.sendMessage("The size can only be 3, 5, 7, or 9. Please enter a proper number");
                            return false;
                        }
                        if (c != carpet.size) {
                            player.sendMessage("The carpet seems to react to your words, and suddenly changes shape!");
                            carpet.changeCarpet(c);
                        } else {
                            player.sendMessage("Poof! The magic carpet disappears.");
                            carpets.remove(player.getName());
                            carpet.removeCarpet();
                            playerListener.setCarpets(carpets);
                        }
                    } else {
                        player.sendMessage("Poof! The magic carpet disappears.");
                        carpets.remove(player.getName());
                        carpet.removeCarpet();
                        playerListener.setCarpets(carpets);
                    }

                }
            } else {
                player.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
            }
        } else {
            if (commandName.equals("ml")) {
                if (canLight(player)) {
                    if (lights.contains(player.getName())) {
                        lights.remove(player.getName());
                        player.sendMessage("The luminous stones in the carpet slowly fade away.");
                        if (carpet != null) {
                            carpet.setLights(false);
                        }
                    } else {
                        lights.add(player.getName());
                        player.sendMessage("A bright flash shines as glowing stones appear in the carpet.");
                        if (carpet != null) {
                            carpet.setLights(true);
                        }
                    }
                } else {
                    player.sendMessage("You do not have permission to use Magic Light!");
                }
            }
        }
        return true;
    }

    public boolean canFly(Player player) {
        if (player.hasPermission("magiccarpet.mc") || player.hasPermission("magiccarpet.*") || player.hasPermission("*")) {
            return true;
        }
        return false;
    }

    public boolean canLight(Player player) {
        if (player.hasPermission("magiccarpet.ml") || player.hasPermission("magiccarpet.*") || player.hasPermission("*")) {
            return true;
        }
        return false;
    }
}