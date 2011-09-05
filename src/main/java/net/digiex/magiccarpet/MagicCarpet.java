package net.digiex.magiccarpet;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;

import org.bukkit.Material;
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

    private ArrayList<String> lights = new ArrayList<String>();
    private final MagicPlayerListener playerListener = new MagicPlayerListener(this);
    private final MagicBlockListener blockListener = new MagicBlockListener(this, playerListener);
    public MagicCarpetLogging log = new MagicCarpetLogging();
    private File file = new File("plugins" + File.separator + "MagicCarpet", "config.yml");
    private Configuration config = new Configuration(file);
    private int carpSize = 5;
    private boolean glowCenter = false;
    public Material carpMaterial = Material.GLASS;
    public Material lightMaterial = Material.GLOWSTONE;
    public boolean autoLight = false;

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
        carpMaterial = Material.getMaterial((Integer) config.getProperty("Carpet Material"));
        if (!acceptableMaterial(carpMaterial)) {
            carpMaterial = Material.GLASS;
        }
        lightMaterial = Material.getMaterial((Integer) config.getProperty("Carpet Light Material"));
        if (!acceptableMaterial(lightMaterial)) {
            lightMaterial = Material.GLOWSTONE;
        }
        autoLight = config.getBoolean("Use expiremential lightning", autoLight);
    }

    public void saveConfig() {
        config.load();
        config.setProperty("Put glowstone for light in center", glowCenter);
        config.setProperty("Default size for carpet", carpSize);
        config.setProperty("Carpet Material", carpMaterial.getId());
        config.setProperty("Carpet Light Material", lightMaterial.getId());
        config.setProperty("Use expiremential lighting", autoLight);
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
                        Carpet newCarpet = new Carpet(this, glowCenter);
                        newCarpet.currentBlock = player.getLocation().getBlock();
                        if (carpSize == 3 || carpSize == 5 || carpSize == 7 || carpSize == 9 || carpSize == 11 || carpSize == 13 || carpSize == 15) {
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
                            player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, 7, 9, 11, 13, 15!");
                            return false;
                        }

                        if (c != 3 && c != 5 && c != 7 && c != 9 && c != 11 && c != 13 && c != 15) {
                            player.sendMessage("The size can only be 3, 5, 7, 9, 11, 13, or 15. Please enter a proper number");
                            return false;
                        }
                        player.sendMessage("A glass carpet appears below your feet.");
                        Carpet newCarpet = new Carpet(this, glowCenter);
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
                            player.sendMessage("Correct usage is: /magiccarpet (size) or /mc (size). The size is optional, and can only be 3, 5, 7, 9, 11, 13, 15!");
                            return false;
                        }

                        if (c != 3 && c != 5 && c != 7 && c != 9 && c != 11 && c != 13 && c != 15) {
                            player.sendMessage("The size can only be 3, 5, 7, 9, 11, 13, 15!. Please enter a proper number");
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
        } else if (commandName.equals("ml")) {
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
        } else if (commandName.equals("mr")) {
            if (canReload(player)) {
                Enumeration<String> e = carpets.keys();
                while (e.hasMoreElements()) {
                    String name = e.nextElement();
                    Carpet cc = carpets.get(name);
                    cc.removeCarpet();
                }
                carpets.clear();
                loadConfig();
                player.sendMessage("MagicCarpet reloaded!");
            } else {
                player.sendMessage("You do not have permission to reload MagicCarpet");
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

    public boolean canReload(Player player) {
        if (player.hasPermission("magiccarpet.mr") || player.hasPermission("magiccarpet.*") || player.hasPermission("*")) {
            return true;
        }
        return false;
    }

    public boolean acceptableMaterial(Material material) {
        int id = material.getId();
        switch (id) {
            case 1:
                return true;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
                return true;
            case 5:
                return true;
            case 7:
                return true;
            case 12:
                return true;
            case 13:
                return true;
            case 14:
                return true;
            case 15:
                return true;
            case 16:
                return true;
            case 17:
                return true;
            case 18:
                return true;
            case 19:
                return true;
            case 20:
                return true;
            case 21:
                return true;
            case 22:
                return true;
            case 23:
                return true;
            case 24:
                return true;
            case 25:
                return true;
            case 29:
                return true;
            case 33:
                return true;
            case 35:
                return true;
            case 41:
                return true;
            case 42:
                return true;
            case 43:
                return true;
            case 45:
                return true;
            case 46:
                return true;
            case 47:
                return true;
            case 48:
                return true;
            case 49:
                return true;
            case 54:
                return true;
            case 56:
                return true;
            case 57:
                return true;
            case 58:
                return true;
            case 60:
                return true;
            case 61:
                return true;
            case 62:
                return true;
            case 73:
                return true;
            case 74:
                return true;
            case 79:
                return true;
            case 80:
                return true;
            case 82:
                return true;
            case 84:
                return true;
            case 86:
                return true;
            case 87:
                return true;
            case 88:
                return true;
            case 89:
                return true;
            case 91:
                return true;
            case 95:
                return true;
            default:
                return false;
        }
    }
}