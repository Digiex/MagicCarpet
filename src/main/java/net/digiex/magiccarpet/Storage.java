package net.digiex.magiccarpet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.entity.Player;

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
public class Storage {

    private static class CarpetEntry implements Serializable {
        private static final long serialVersionUID = 1L;

        public transient Carpet carpet;
        public boolean hasCarpet = false;
    }

    private static HashMap<UUID, CarpetEntry> carpets = new HashMap<UUID, CarpetEntry>();
    private final MagicCarpet magiccarpet;
    private final File carpetDat;

    Storage(final MagicCarpet plugin) {
        magiccarpet = plugin;
        carpetDat = new File(plugin.getDataFolder(), "carpets.dat");
    }

    private static CarpetEntry getEntry(final Player player) {
        if (carpets.containsKey(player.getUniqueId()))
            return carpets.get(player.getUniqueId());
        return null;
    }

    public static Iterable<Carpet> all() {
        return new Iterable<Carpet>() {
            @Override
            public Iterator<Carpet> iterator() {
                return new Iterator<Carpet>() {
                    private final Iterator<CarpetEntry> iter = carpets.values().iterator();
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
                        if (toRemove == null)
                            throw new IllegalStateException();
                        if (toRemove.carpet != null)
                            toRemove.carpet.hide();
                        toRemove.carpet = null;
                    }
                };
            }
        };
    }

    void saveCarpets() {
        magiccarpet.getLogger().info("Saving carpets...");
        if (!carpetDat.exists())
            try {
                carpetDat.createNewFile();
            } catch (final IOException e) {
                magiccarpet.getLogger().severe("Unable to create carpets.dat; IOException");
            }
        try {
            final FileOutputStream file = new FileOutputStream(carpetDat);
            final ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(carpets);
            out.close();
        } catch (final Exception e) {
            magiccarpet.getLogger().warning("Error writing to carpets.dat; carpets data has not been saved.");
        }
        clear();
    }

    @SuppressWarnings("unchecked")
    void loadCarpets() {
        if (!carpetDat.exists())
            return;
        magiccarpet.getLogger().info("Loading carpets...");
        try {
            final FileInputStream file = new FileInputStream(carpetDat);
            final ObjectInputStream in = new ObjectInputStream(file);
            carpets = (HashMap<UUID, CarpetEntry>) in.readObject();
            in.close();
        } catch (final Exception e) {
            magiccarpet.getLogger().warning("Error loading carpets.dat; carpets data has not been loaded.");
        }
    }

    static void assign(final Player player, final Carpet carpet) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            entry = new CarpetEntry();
            carpets.put(player.getUniqueId(), entry);
        }
        if (entry.carpet != null)
            entry.carpet.hide();
        entry.carpet = carpet;
    }

    static void remove(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        if (entry.carpet != null) {
            entry.carpet.removeCarpet();
            entry.carpet = null;
        }
    }

    static void clear() {
        for (final CarpetEntry entry : carpets.values()) {
            if (entry.carpet == null || !entry.carpet.isVisible())
                continue;
            entry.carpet.hide();
        }
        carpets.clear();
    }

    static void update(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        if (entry.carpet == null) {
            entry.hasCarpet = false;
            return;
        }
        entry.hasCarpet = entry.carpet.isVisible();
    }

    public static boolean has(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return false;
        return entry.hasCarpet;
    }

    public static Carpet getCarpet(final Player player) {
        if (carpets.containsKey(player.getUniqueId()))
            return carpets.get(player.getUniqueId()).carpet;
        return null;
    }
}
