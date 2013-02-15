package net.digiex.magiccarpet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/*
 * Magic Carpet 2.2 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
	private static final long serialVersionUID = 5679237239554542205L;

	private class CarpetEntry implements Serializable {
		private static final long serialVersionUID = -1655870844571882460L;
		
		public transient Carpet carpet;
        public boolean crouch = plugin.crouchDef;
        public boolean hasCarpet = false;
        public int lastSize = plugin.carpSize;
        public Material light = plugin.lightMaterial;
        public boolean lightsOn = plugin.glowCenter;
        public Material thread = plugin.carpMaterial;
        public boolean given = false;
        public boolean tools = false;
    }
    private HashMap<String, CarpetEntry> carpets = new HashMap<String, CarpetEntry>();
    private transient MagicCarpet plugin;
    
    public CarpetStorage attach(MagicCarpet plug) {
        plugin = plug;
        return this;
    }
    
    private CarpetEntry getEntry(Player player) {
        if (carpets.containsKey(player.getName())) {
            return carpets.get(player.getName());
        }
        return null;
    }

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
                            toRemove.carpet.removeCarpet();
                        }
                        toRemove.carpet = null;
                    }
                };
            }
        };
    }

    public void assign(Player player, Carpet carpet) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            entry = new CarpetEntry();
            carpets.put(player.getName(), entry);
        }
        if (entry.carpet != null) {
            entry.carpet.removeCarpet();
        }
        entry.carpet = carpet;
    }
    
    public Carpet getCarpet(Player player) {
        if (carpets.containsKey(player.getName())) {
            return carpets.get(player.getName()).carpet;
        }
        return null;
    }
    
    public void remove(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return;
        }
        if (entry.carpet != null) {
            entry.carpet.removeCarpet();
            entry.carpet = null;
        }
    }
    
    public void clear() {
        for (CarpetEntry entry : carpets.values()) {
            if (entry.carpet == null) {
                continue;
            }
            entry.carpet.removeCarpet();
        }
        carpets.clear();
    }
    
    void update(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return;
        }
        if (entry.carpet == null) {
            entry.hasCarpet = false;
            return;
        }
        entry.lastSize = entry.carpet.getSize();
        entry.hasCarpet = entry.carpet.isVisible();
        entry.lightsOn = entry.carpet.hasLight();
        entry.thread = entry.carpet.getThread();
        entry.light = entry.carpet.getShine();
        entry.tools = entry.carpet.hasTools();
    }
    
    void checkCarpets() {
        for (CarpetEntry entry : carpets.values()) {
            if (!plugin.getAcceptableCarpetMaterial().contains(entry.thread)) {
                entry.thread = plugin.carpMaterial;
            }
            if (!plugin.getAcceptableLightMaterial().contains(entry.light)) {
                entry.light = plugin.lightMaterial;
            }
            if (entry.lastSize > plugin.maxCarpSize) {
                entry.lastSize = plugin.carpSize;
            }
            if (entry.thread != plugin.carpMaterial && !plugin.customCarpets) {
                entry.thread = plugin.carpMaterial;
            }
            if (entry.light != plugin.lightMaterial && !plugin.customLights) {
                entry.light = plugin.lightMaterial;
            }
            if (entry.lightsOn && !plugin.lights) {
                entry.lightsOn = false;
            }
            if (entry.tools && !plugin.tools) {
            	entry.tools = false;
            }
        }
    }

    boolean crouches(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return plugin.crouchDef;
        }
        return entry.crouch;
    }

    int getLastSize(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return plugin.carpSize;
        }
        return entry.lastSize;
    }
    
    Material getMaterial(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return plugin.carpMaterial;
        }
        return entry.thread;
    }

    Material getLightMaterial(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return plugin.lightMaterial;
        }
        return entry.light;
    }

    boolean has(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return false;
        }
        return entry.hasCarpet;
    }

    boolean hasLight(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return plugin.glowCenter;
        }
        return entry.lightsOn;
    }

    void toggleCrouch(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return;
        }
        entry.crouch = !entry.crouch;
    }

    boolean wasGiven(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return false;
        }
        return entry.given;
    }

    void setGiven(Player player, Boolean given) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
        	entry = new CarpetEntry();
            carpets.put(player.getName(), entry);
        }
        entry.given = given;
    }
    
    boolean hasTools(Player player) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            return false;
        }
        return entry.tools;
    }
}
