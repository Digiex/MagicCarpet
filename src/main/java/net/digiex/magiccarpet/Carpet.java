package net.digiex.magiccarpet;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

/*
 * Magic Carpet 2.0
 * Copyright (C) 2011 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
 * Carpet.java <br />
 * <br />
 * Defines the basic 5x5 carpet object that is placed underneath the player.
 * 
 * @author Android <spparr@gmail.com>
 */
public class Carpet {

	private class CarpetFibre {

		BlockState block;

		int dx, dy, dz;
		Material strand;

		@SuppressWarnings("hiding")
		public CarpetFibre(int dx, int dy, int dz) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}

		void set(Block bl, Material material) {
			bl.setTypeId(material.getId(), false);
			strand = material;

		}

		boolean shouldGlow() {
			if (!lightsOn) {
				return false;
			}
			if (dx == 0 && dz == 0) {
				return true;
			}
			return false;
		}

		void update() {
			if (block.getBlock().getType() != strand) {
				return;
			}
			block.update(true);
		}
	}

	private static final int MAX_SUPPORTED_SIZE = 9;
        private static int defaultSize = 5, maxSize = MAX_SUPPORTED_SIZE;

	public static Carpet create(Player player, MagicCarpet plugin) {
		int sz = plugin.carpets.getLastSize(player);
		boolean light = plugin.carpets.hasLight(player);
		Material thread = plugin.carpets.getMaterial(player);
		Material shine = plugin.carpets.getLightMaterial(player);
		Carpet carpet = new Carpet(player, sz, light, thread, shine);
		plugin.carpets.assign(player, carpet);
		defaultSize = plugin.carpSize;
		maxSize = min(plugin.maxCarpSize, MAX_SUPPORTED_SIZE);
		return carpet;
	}

	private Block currentCentre;
	private int edge = 0, area = 0, rad = 0, radsq = 0, radplsq = 0;
	private CarpetFibre[] fibres;
	private boolean hidden;
	private boolean lightsOn;
	private boolean suppressed;
	private Material thread, shine;

	private Player who;

	private Carpet(Player player, int sz, boolean on, Material mat, Material light) {
		setSize(sz);
		who = player;
		currentCentre = player.getLocation().getBlock();
		lightsOn = on;
		hidden = true;
		suppressed = false;
		thread = mat;
		shine = light;
	}

	public void changeCarpet(int sz) {
		removeCarpet();
		setSize(sz);
		drawCarpet();
	}

	public void changeCarpet(Material material) {
		removeCarpet();
		thread = material;
		drawCarpet();
	}

	public void descend() {
		removeCarpet();
		currentCentre = currentCentre.getRelative(0, -1, 0);
		drawCarpet();
	}

	public Location getLocation() {
		return currentCentre.getLocation();
	}

	public Player getPlayer() {
		return who;
	}

	public Material getShine() {
		return shine;
	}

	public int getSize() {
		return edge;
	}

	public Material getThread() {
		return thread;
	}

	public boolean hasLights() {
		return lightsOn;
	}

	public void hide() {
		if (!hidden && !suppressed) {
			removeCarpet();
		}
		hidden = true;
	}

	public boolean isCovering(Block block) {
		// TODO: Is the distance between adjacent blocks 1?
		if (currentCentre == null || block == null) {
			return false;
		}
		if (block.getLocation().getWorld() != getLocation().getWorld()) {
			return false;
		}
		if (block.getLocation().distanceSquared(getLocation()) > radsq) {
			return false;
		}
		for (CarpetFibre fibre : fibres) {
			if (fibre.block == null) {
				continue;
			}
			if (fibre.block.getBlock().getLocation()
					.equals(block.getLocation())) {
				return true;
			}
		}
		return false;
	}

	public boolean isCustom() {
		if (getThread() != Material.GLASS || getShine() != Material.GLOWSTONE) {
			return true;
		}
		return false;
	}

	public boolean isVisible() {
		return !hidden;
	}

	public void lightsOff() {
		removeCarpet();
		lightsOn = false;
		drawCarpet();
	}

	public void lightsOn() {
		removeCarpet();
		lightsOn = true;
		drawCarpet();
	}

	public void moveTo(Location to) {
		removeCarpet();
		currentCentre = to.getBlock();
		drawCarpet();
	}

	public void setLights(Material material) {
		removeCarpet();
		shine = material;
		drawCarpet();
	}

	public void show() {
		if (hidden || suppressed) {
			drawCarpet();
		}
		hidden = false;
	}

	public void suppress() {
		if (!suppressed) {
			removeCarpet();
		}
		suppressed = true;
	}

	public boolean touches(Block block) {
		if (currentCentre == null || block == null) {
			return false;
		}
		if (block.getLocation().getWorld() != getLocation().getWorld()) {
			return false;
		}
		if (block.getLocation().distanceSquared(getLocation()) > radplsq) {
			return false;
		}
		if (abs(block.getY() - currentCentre.getY()) > 1) {
			return false;
		}
		return true;
	}

	// Places glass in a 5x5 area underneath the player if the block was just
	// air previously
	private void drawCarpet() {
		suppressed = false;
		Block bl;
		for (CarpetFibre fibre : fibres) {
			if (currentCentre != null) {
				bl = currentCentre.getRelative(fibre.dx, fibre.dy, fibre.dz);
				Material type = bl.getType();
				if (!isAir(type) && !isFluid(type)) {
					fibre.block = null;
					continue;
				}
				fibre.block = bl.getState();
				if (fibre.shouldGlow() && shouldChange() && !lightWater(bl)) {
					fibre.set(bl, getShine());
				} else {
					fibre.set(bl, getThread());
				}
			}
		}
	}

	private boolean isAir(Material type) {
		if (type == Material.AIR) {
			return true;
		}
		return false;
	}
        
    private boolean isFluid(Material type) {
        if (type == Material.WATER) {
        	return true;
		}
		if (type == Material.STATIONARY_WATER) {
			return true;
		}
		if (type == Material.LAVA) {
			return true;
		}
		if (type == Material.STATIONARY_LAVA) {
			return true;
		}
		return false;
    }
    
    private boolean lightWater(Block b) {
        if (touches(b) && isFluid(b.getType())) {
            return true;
        }
        return false;
    }

	// Goes through a grid of the area underneath the player, and if the block
	// is glass that is part of the magic carpet, it is removed
	private void removeCarpet() {
		if (currentCentre == null) {
			return;
		}
		for (CarpetFibre fibre : fibres) {
			if (fibre.block != null) {
				fibre.update();
			}
			fibre.block = null;
		}
	}

	// Changes the carpet size
	private void setSize(int size) {
		if (size < 0) {
			size = abs(size); // Sanity check
		} else if (size > maxSize) {
			size = defaultSize;
		}
		edge = size;
		area = size * size;
		fibres = new CarpetFibre[area];
		rad = (size - 1) / 2;
		radsq = rad * rad * 2;
		radplsq = (rad + 1) * (rad + 1) * 2;

		int i = 0;
		for (int x = -rad; x <= rad; x++) {
			for (int z = -rad; z <= rad; z++) {
				fibres[i] = new CarpetFibre(x, -1, z);
				i++;
			}
		}
	}

	private boolean shouldChange() {
		if (getThread() == Material.GLASS || getThread() == Material.LEAVES) {
			return true;
		}
		return false;
	}
}
