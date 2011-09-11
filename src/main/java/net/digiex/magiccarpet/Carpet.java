package net.digiex.magiccarpet;

import static java.lang.Math.abs;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Material;

/*
* Magic Carpet 2.0
* Copyright (C) 2011 Celtic Minstrel
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

/**
* Carpet.java
* <br /><br />
* Defines the basic 5x5 carpet object that is placed underneath the player.
*
*
* @author Android <spparr@gmail.com>
*/
public class Carpet {
	private class CarpetFibre {
		@SuppressWarnings("hiding")
		public CarpetFibre(int dx, int dy, int dz)
		{
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}
		int dx,dy,dz;
		boolean fake = false;
		BlockState block;
		
		void update() {
			if(fake) {
				Location loc = currentCentre.getRelative(dx, dy, dz).getLocation();
				who.sendBlockChange(loc, block.getType(), block.getRawData());
			} else block.update(true);
		}
		
		void set(Block bl, Material material) {
			if(fake) {
				Location loc = currentCentre.getRelative(dx, dy, dz).getLocation();
				who.sendBlockChange(loc, material, (byte)0);
			} else bl.setTypeId(material.getId(), false);
		}

		public boolean shouldGlow() {
			if(!lightsOn) return false;
			if(dx == 0 && dz == 0) return lightMode != LightMode.RING;
			if(dx == rad || dx == -rad || dz == rad || dz == -rad)
				return lightMode != LightMode.CENTRE;
			return false;
		}
	}
	public enum LightMode {RING, CENTRE, BOTH};
	private CarpetFibre[] fibres;
	private Block currentCentre;
	private int edge = 0, area = 0, rad = 0, radsq = 0, radplsq = 0;
	private LightMode lightMode;
	private boolean lightsOn;
	private boolean hidden;
	private boolean suppressed;
	private Player who;
	
	public static Carpet create(Player player, MagicCarpet plugin) {
		int sz = plugin.carpets.getLastSize(player);
		boolean light = plugin.carpets.hasLight(player);
		LightMode mode = plugin.carpets.getLightMode(player);
		Carpet carpet = new Carpet(player, sz, mode, light);
		plugin.carpets.assign(player, carpet);
		return carpet;
	}
	
	private Carpet(Player player, int sz, LightMode lights, boolean on) {
		setSize(sz);
		who = player;
		currentCentre = player.getLocation().getBlock();
		lightMode = lights == null ? LightMode.RING : lights;
		lightsOn = on;
		hidden = true;
		suppressed = false;
	}


	//Goes through a grid of the area underneath the player, and if the block is glass that is part of the magic carpet, it is removed
	private void removeCarpet() {
		if (currentCentre == null)
			return;
		for(CarpetFibre fibre : fibres) {
			if(fibre.block != null) fibre.update();
			fibre.block = null;
		}
	}

	//Places glass in a 5x5 area underneath the player if the block was just air previously
	private void drawCarpet() {
		suppressed = false;
		Block bl;
		for(CarpetFibre fibre : fibres) {
			if (currentCentre != null) {
				bl = currentCentre.getRelative(fibre.dx,fibre.dy,fibre.dz);
				Material type = bl.getType();
				if(!isAirOrFluid(type)) {
					fibre.block = null;
					continue;
				}
				// FIXME: Cactus hack
				if(bl.getRelative(BlockFace.NORTH).getType() == Material.CACTUS ||
					bl.getRelative(BlockFace.SOUTH).getType() == Material.CACTUS ||
					bl.getRelative(BlockFace.EAST).getType() == Material.CACTUS ||
					bl.getRelative(BlockFace.WEST).getType() == Material.CACTUS)
						fibre.fake = true;
				else fibre.fake = false;
				// End cactus hack
				fibre.block = bl.getState();
				if(fibre.shouldGlow()) fibre.set(bl, Material.GLOWSTONE);
				else fibre.set(bl, Material.GLASS);
			}
		}
	}

	private boolean isAirOrFluid(Material type) {
		if(type == Material.AIR) return true;
		if(type == Material.WATER) return true;
		if(type == Material.STATIONARY_WATER) return true;
		if(type == Material.LAVA) return true;
		if(type == Material.STATIONARY_LAVA) return true;
		return false;
	}

	public void changeCarpet(int si){
		removeCarpet();
		setSize(si);
		drawCarpet();
	}
	
	public void setLights(LightMode mode){
		lightMode = mode;
		lightsOn();
	}
	
	public void lightsOn() {
		removeCarpet();
		lightsOn = true;
		drawCarpet();
	}
	
	public void lightsOff() {
		removeCarpet();
		lightsOn = false;
		drawCarpet();
	}

	// Changes the carpet size
	private void setSize(int size) {
		if (size < 0) size = abs(size); // Sanity check
		this.edge = size;
		this.area = size*size;

		fibres = new CarpetFibre[area];
		switch(size){
		case 3: this.rad = 1; break;
		case 5: this.rad = 2; break;
		case 7: this.rad = 3; break;
        case 9:
            size = 4;
            break;
        case 11:
            size = 5;
            break;
        case 13:
            size = 6;
            break;
        case 15:
            size = 7;
		default: this.rad = 2; break;
		}
		this.radsq = rad*rad;
		this.radplsq = (rad+1)*(rad+1);

		int i = 0;
		for (int x = -rad; x <= rad; x++){
			for (int z = -rad; z <= rad; z++) {
				fibres[i] = new CarpetFibre(x, -1, z);
				i++;
			}
		}
	}

	public int getSize() {
		return edge;
	}

	public boolean isCovering(Block block) {
		// TODO: Is the distance between adjacent blocks 1?
		if(currentCentre == null || block == null) return false;
		if(block.getLocation().distanceSquared(currentCentre.getLocation()) > radsq) return false;
		for(CarpetFibre fibre : fibres) {
			if(fibre.block == null) continue;
			if(fibre.block.getBlock().getLocation().equals(block.getLocation())) return true;
		}
		return false;
	}

	public void moveTo(Location to) {
		removeCarpet();
		currentCentre = to.getBlock();
		drawCarpet();
	}

	public Location getLocation() {
		return currentCentre.getLocation();
	}

	public void descend() {
		removeCarpet();
		currentCentre = currentCentre.getRelative(0,-1,0);
		drawCarpet();
	}

	public void hide() {
		if(!hidden && !suppressed) removeCarpet();
		hidden = true;
	}

	public void show() {
		if(hidden || suppressed) drawCarpet();
		hidden = false;
	}
	
	public boolean isVisible() {
		return !hidden;
	}


	public void suppress() {
		if(!suppressed) removeCarpet();
		suppressed = true;
	}

	public LightMode getLights() {
		return lightMode;
	}

	public boolean hasLights() {
		return lightsOn;
	}

	public boolean touches(Block block) {
		if(currentCentre == null || block == null) return false;
		if(block.getLocation().distanceSquared(currentCentre.getLocation()) > radplsq) return false;
		if(abs(block.getY() - currentCentre.getY()) > 1) return false;
		return true;
	}
}
