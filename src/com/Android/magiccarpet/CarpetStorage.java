package com.Android.magiccarpet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class CarpetStorage implements Serializable {
	private class CarpetEntry implements Serializable {
		public int lastSize = plugin.carpSize;
		public boolean lightsOn = false;
		public boolean hasCarpet = false;
		public boolean crouch = plugin.crouchDef;
		public Carpet.LightMode lightsMode = plugin.glowCenter ? Carpet.LightMode.CENTRE : Carpet.LightMode.RING;
		public transient Carpet carpet;
	}
	private transient MagicCarpet plugin;
	private Map<String,CarpetEntry> carpets = new HashMap<String,CarpetEntry>();
	
	public CarpetStorage(MagicCarpet plug) {
		plugin = plug;
	}
	
	private CarpetEntry entry(Player player) {
		if(!carpets.containsKey(player.getName()))
			carpets.put(player.getName(), new CarpetEntry());
		return carpets.get(player.getName());
	}
	
	// Accessors
	public Carpet get(Player player) {
		if(carpets.containsKey(player.getName()))
			return carpets.get(player.getName()).carpet;
		return null;
	}
	
	public boolean hasLight(Player player) {
		return entry(player).lightsOn;
	}

	public boolean has(Player player) {
		return entry(player).hasCarpet;
	}

	public Carpet.LightMode getLightMode(Player player) {
		return entry(player).lightsMode;
	}

	public int getLastSize(Player player) {
		return entry(player).lastSize;
	}

	public boolean crouches(Player player) {
		return entry(player).crouch;
	}
	
	// Mutators
	public void lightOn(Player player) {
		CarpetEntry entry = entry(player);
		entry.lightsOn = true;
		if(entry.hasCarpet && entry.carpet != null)
			entry.carpet.lightsOn();
	}
	
	public void lightOff(Player player) {
		CarpetEntry entry = entry(player);
		entry.lightsOn = false;
		if(entry.hasCarpet && entry.carpet != null)
			entry.carpet.lightsOff();
	}
	
	public void clear() {
		for(CarpetEntry entry : carpets.values()) {
			if(entry.carpet == null) continue;
			entry.carpet.suppress();
		}
		carpets.clear();
	}

	public void remove(Player player) {
		CarpetEntry entry = entry(player);
		if(entry.carpet != null)
			entry.carpet.suppress();
		entry.carpet = null;
		entry.hasCarpet = false;
	}

	public void assign(Player player, Carpet carpet) {
		CarpetEntry entry = entry(player);
		if(entry.carpet != null) entry.carpet.suppress();
		entry.carpet = carpet;
	}

	public void toggleCrouch(Player player) {
		CarpetEntry entry = entry(player);
		entry.crouch = !entry.crouch;
	}
	
	public void update(Player player) {
		CarpetEntry entry = entry(player);
		if(entry.carpet == null) {
			entry.hasCarpet = false;
			return;
		}
		entry.lastSize = entry.carpet.getSize();
		entry.hasCarpet = entry.carpet.isVisible();
		entry.lightsMode = entry.carpet.getLights();
		entry.lightsOn = entry.carpet.hasLights();
	}
}
