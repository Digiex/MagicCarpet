package net.digiex.magiccarpet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
public class CarpetStorage implements Serializable {

    private class CarpetEntry implements Serializable {

        private static final long serialVersionUID = -7853484578047997719L;
        public transient Carpet carpet;
        public boolean crouch = plugin.crouchDef;
        public boolean hasCarpet = false;
        public int lastSize = plugin.carpSize;
        public Material light = plugin.lightMaterial;
        public boolean lightsOn = plugin.glowCenter;
        public Material thread = plugin.carpMaterial;
    }
    private static final long serialVersionUID = 1168884913848994599L;
    private HashMap<String, CarpetEntry> carpets = new HashMap<String, CarpetEntry>();
    private transient MagicCarpet plugin;

    public Iterable<Carpet> all() {
        return new Iterable<Carpet>() {

            @Override
            public Iterator<Carpet> iterator() {
                return new Iterator<Carpet>() {

                    private Iterator<CarpetEntry> iter = carpets.values().iterator();
                    private CarpetEntry toRemove = null;

                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public Carpet next() {
                        toRemove = iter.next();
                        return toRemove.carpet;
                    }

                    @Override
                    public void remove() {
                        if (toRemove == null) {
                            throw new IllegalStateException();
                        }
                        if (toRemove.carpet != null) {
                            toRemove.carpet.hide();
                        }
                        toRemove.carpet = null;
                    }
                };
            }
        };
    }

    public void assign(Player player, Carpet carpet) {
        CarpetEntry entry = entry(player);
        if (entry.carpet != null) {
            entry.carpet.hide();
        }
        entry.carpet = carpet;
    }

    public CarpetStorage attach(MagicCarpet plug) {
        plugin = plug;
        return this;
    }

    public void clear() {
        for (CarpetEntry entry : carpets.values()) {
            if (entry.carpet == null) {
                continue;
            }
            entry.carpet.hide();
        }
        carpets.clear();
    }

    public boolean crouches(Player player) {
        return entry(player).crouch;
    }

    public Carpet get(Player player) {
        if (carpets.containsKey(player.getName())) {
            return carpets.get(player.getName()).carpet;
        }
        return null;
    }

    public int getLastSize(Player player) {
        return entry(player).lastSize;
    }

    public Material getLightMaterial(Player player) {
        return entry(player).light;
    }

    public Material getMaterial(Player player) {
        return entry(player).thread;
    }

    public boolean has(Player player) {
        CarpetEntry entry = carpets.get(player.getName());
        if (entry == null) {
            return false;
        }
        return entry.hasCarpet;
    }

    public boolean hasLight(Player player) {
        return entry(player).lightsOn;
    }

    public void lightOff(Player player) {
        CarpetEntry entry = entry(player);
        entry.lightsOn = false;
        if (entry.hasCarpet && entry.carpet != null) {
            entry.carpet.lightsOff();
        }
    }

    public void lightOn(Player player) {
        CarpetEntry entry = entry(player);
        entry.lightsOn = true;
        if (entry.hasCarpet && entry.carpet != null) {
            entry.carpet.lightsOn();
        }
    }

    public void remove(Player player) {
        CarpetEntry entry = entry(player);
        if (entry.carpet != null) {
            entry.carpet.hide();
        }
        entry.carpet = null;
    }

    public void toggleCrouch(Player player) {
        CarpetEntry entry = entry(player);
        entry.crouch = !entry.crouch;
    }

    public void update(Player player) {
        CarpetEntry entry = entry(player);
        if (entry.carpet == null) {
            entry.hasCarpet = false;
            return;
        }
        entry.lastSize = entry.carpet.getSize();
        entry.hasCarpet = entry.carpet.isVisible();
        entry.lightsOn = entry.carpet.hasLights();
        entry.thread = entry.carpet.getThread();
        entry.light = entry.carpet.getShine();
    }

    private CarpetEntry entry(Player player) {
        if (!carpets.containsKey(player.getName())) {
            carpets.put(player.getName(), new CarpetEntry());
        }
        return carpets.get(player.getName());
    }
    
    public void checkCarpets() {
        for (CarpetEntry entry : carpets.values()) {
            if (!MagicCarpet.acceptableCarpet.contains(entry.thread)) {
                entry.thread = plugin.carpMaterial;
            }
            if (!MagicCarpet.acceptableLight.contains(entry.light)) {
                entry.light = plugin.lightMaterial;
            }
            if (entry.lastSize > plugin.maxCarpSize) {
                entry.lastSize = plugin.carpSize;
            }
            if (entry.thread != plugin.carpMaterial && !plugin.customCarpets) {
                entry.thread = plugin.carpMaterial;
            }
            if (entry.light != plugin.lightMaterial && !plugin.customCarpets) {
                entry.light = plugin.lightMaterial;
            }
        }
    }
}
