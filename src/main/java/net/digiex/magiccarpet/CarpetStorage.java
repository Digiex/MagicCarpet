package net.digiex.magiccarpet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.entity.Player;

/*
 * Magic Carpet 2.3 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
	private static final long serialVersionUID = -7050427936329191014L;

	private class CarpetEntry implements Serializable {
		private static final long serialVersionUID = -3263203886057095731L;
		
		public transient Carpet carpet;
		public boolean hasCarpet = false;
	}

	private HashMap<String, CarpetEntry> carpets = new HashMap<String, CarpetEntry>();

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
					private Iterator<CarpetEntry> iter = carpets.values()
							.iterator();
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
			if (entry.carpet == null || !entry.carpet.isVisible()) {
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
		entry.hasCarpet = entry.carpet.isVisible();
	}

	boolean has(Player player) {
		CarpetEntry entry = carpets.get(player.getName());
		if (entry == null) {
			return false;
		}
		return entry.hasCarpet;
	}
}
