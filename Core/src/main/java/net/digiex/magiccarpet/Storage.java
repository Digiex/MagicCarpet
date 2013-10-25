package net.digiex.magiccarpet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import net.digiex.magiccarpet.Carpet.LightMode;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/*
 * Magic Carpet 2.3 Copyright (C) 2012-2013 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
	private static final long serialVersionUID = -4171824321514205419L;

	private class CarpetEntry implements Serializable {
		private static final long serialVersionUID = -4877931595830392092L;

		public transient Carpet carpet;

		public boolean hasCarpet = false;
		public boolean given = false;
		public boolean tools = false;
		public boolean autoRenew = false;
		public boolean oneTimeFee = false;
		public String autoPackage = null;

		public boolean crouch = getConfig().getDefaultCrouch();
		public int lastSize = getConfig().getDefaultCarpSize();
		public Material light = getConfig().getDefaultLightMaterial();
		public boolean lightsOn = getConfig().getDefaultGlowing();
		public Material thread = getConfig().getDefaultCarpetMaterial();
		public long time = getConfig().getDefaultChargeTime();
		public LightMode lightMode = getConfig().getLightMode();
	}

	private HashMap<String, CarpetEntry> carpets = new HashMap<String, CarpetEntry>();
	private transient MagicCarpet plugin;

	Storage attach(MagicCarpet plugin) {
		this.plugin = plugin;
		return this;
	}

	private Config getConfig() {
		return plugin.getMCConfig();
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

	public void update(Player player) {
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
		entry.lightMode = entry.carpet.getLights();
	}

	public void checkCarpets() {
		for (CarpetEntry entry : carpets.values()) {
			if (!MagicCarpet.getAcceptableCarpetMaterial().contains(
					entry.thread)) {
				entry.thread = getConfig().getDefaultCarpetMaterial();
			}
			if (!MagicCarpet.getAcceptableLightMaterial().contains(entry.light)) {
				entry.light = getConfig().getDefaultLightMaterial();
			}
			if (entry.lastSize > getConfig().getDefaultMaxCarpetSize()) {
				entry.lastSize = getConfig().getDefaultCarpSize();
			}
			if (entry.thread != getConfig().getDefaultCarpetMaterial()
					&& !getConfig().getDefaultCustomCarpets()) {
				entry.thread = getConfig().getDefaultCarpetMaterial();
			}
			if (entry.light != getConfig().getDefaultLightMaterial()
					&& !getConfig().getDefaultCustomLights()) {
				entry.light = getConfig().getDefaultLightMaterial();
			}
			if (entry.lightsOn && !getConfig().getDefaultLights()) {
				entry.lightsOn = false;
			}
			if (entry.tools && !getConfig().getDefaultTools()) {
				entry.tools = false;
			}
			if (plugin.getVault().isEnabled() && getConfig().getDefaultCharge()) {
				if (getConfig().getDefaultChargeTimeBased()) {
					if (entry.hasCarpet && entry.time <= 0L && !entry.given) {
						entry.hasCarpet = false;
					}
					if (plugin.getVault().getPackage(entry.autoPackage) == null) {
						entry.autoPackage = null;
						entry.autoRenew = false;
					}
				} else {
					if (entry.hasCarpet && !entry.oneTimeFee && !entry.given) {
						entry.hasCarpet = false;
					}
				}
			}
		}
	}

	public boolean crouches(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return getConfig().getDefaultCrouch();
		}
		return entry.crouch;
	}

	public int getLastSize(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return getConfig().getDefaultCarpSize();
		}
		return entry.lastSize;
	}

	public Material getMaterial(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return getConfig().getDefaultCarpetMaterial();
		}
		return entry.thread;
	}

	public Material getLightMaterial(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return getConfig().getDefaultLightMaterial();
		}
		return entry.light;
	}

	public boolean has(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return false;
		}
		return entry.hasCarpet;
	}

	public boolean hasLight(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return false;
		}
		return entry.lightsOn;
	}

	public void toggleCrouch(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return;
		}
		entry.crouch = !entry.crouch;
	}

	public boolean wasGiven(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return false;
		}
		return entry.given;
	}

	public void setGiven(Player player, Boolean given) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			entry = new CarpetEntry();
			carpets.put(player.getName(), entry);
		}
		entry.given = given;
	}

	public boolean hasTools(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return false;
		}
		return entry.tools;
	}

	public void setTime(Player player, Long time) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return;
		}
		entry.time = time;
	}

	public long getTime(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return getConfig().getDefaultChargeTime();
		}
		return entry.time;
	}

	public boolean canAutoRenew(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return false;
		}
		return entry.autoRenew;
	}

	public void setAutoRenew(Player player, Boolean renew) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return;
		}
		entry.autoRenew = renew;
	}

	public String getAutoPackage(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return null;
		}
		return entry.autoPackage;
	}

	public void setAutoPackage(Player player, String auto) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return;
		}
		entry.autoPackage = auto;
	}

	public boolean hasPaidFee(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return false;
		}
		return entry.oneTimeFee;
	}

	public void setPaidFee(Player player, Boolean paid) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			entry = new CarpetEntry();
			carpets.put(player.getName(), entry);
		}
		entry.oneTimeFee = paid;
	}
	
	public LightMode getLightMode(Player player) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return getConfig().getLightMode();
		}
		return entry.lightMode;
	}
	
	public void setLightMode(Player player, LightMode lightmode) {
		CarpetEntry entry = getEntry(player);
		if (entry == null) {
			return;
		}
		entry.lightMode = lightmode;
	}
}
