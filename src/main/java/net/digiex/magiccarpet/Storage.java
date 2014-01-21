package net.digiex.magiccarpet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

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
public class Storage implements Serializable {
    private static final long serialVersionUID = -2070080026438450206L;

    private class CarpetEntry implements Serializable {
        private static final long serialVersionUID = -5947866865121964362L;

        public transient Carpet carpet;
        public boolean hasCarpet = false;
    }

    private final HashMap<String, CarpetEntry> carpets = new HashMap<String, CarpetEntry>();

    private CarpetEntry getEntry(final Player player) {
        if (carpets.containsKey(player.getName()))
            return carpets.get(player.getName());
        return null;
    }

    public Iterable<Carpet> all() {
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
                            toRemove.carpet.removeCarpet();
                        toRemove.carpet = null;
                    }
                };
            }
        };
    }

    public void assign(final Player player, final Carpet carpet) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            entry = new CarpetEntry();
            carpets.put(player.getName(), entry);
        }
        if (entry.carpet != null)
            entry.carpet.removeCarpet();
        entry.carpet = carpet;
    }

    public Carpet getCarpet(final Player player) {
        if (carpets.containsKey(player.getName()))
            return carpets.get(player.getName()).carpet;
        return null;
    }

    public void remove(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        if (entry.carpet != null) {
            entry.carpet.removeCarpet();
            entry.carpet = null;
        }
    }

    public void clear() {
        for (final CarpetEntry entry : carpets.values()) {
            if (entry.carpet == null || !entry.carpet.isVisible())
                continue;
            entry.carpet.removeCarpet();
        }
        carpets.clear();
    }

    public void update(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        if (entry.carpet == null) {
            entry.hasCarpet = false;
            return;
        }
        entry.hasCarpet = entry.carpet.isVisible();
    }

    public boolean has(final Player player) {
        final CarpetEntry entry = carpets.get(player.getName());
        if (entry == null)
            return false;
        return entry.hasCarpet;
    }
}
