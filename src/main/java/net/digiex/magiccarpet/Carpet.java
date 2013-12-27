package net.digiex.magiccarpet;

import static java.lang.Math.abs;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

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
public class Carpet {

	private class CarpetFibre {

		BlockState block;
		int dx, dy, dz;

		public CarpetFibre(int dx, int dy, int dz) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}

		void set(Block bl, Material material) {
			bl.setType(material);
			bl.setMetadata("Carpet", new FixedMetadataValue(plugin, carpet));
		}

		void update() {
			if (!block.hasMetadata("Carpet")) {
				return;
			}
			block.removeMetadata("Carpet", plugin);
			block.update(true);
		}
	}

	private final MagicCarpet plugin = (MagicCarpet) Bukkit.getServer()
			.getPluginManager().getPlugin("MagicCarpet");
	private Carpet carpet = this;

	public static Carpet create(Player player) {
		Carpet carpet = new Carpet(player);
		MagicCarpet.getCarpets().assign(player, carpet);
		return carpet;
	}

	private Block currentCentre;
	private int area = 0, rad = 0, radplsq = 0;
	private CarpetFibre[] fibres;
	private boolean hidden;
	private Player who;

	private Carpet(Player player) {
		setSize(5);
		who = player;
		currentCentre = player.getLocation().getBlock();
		hidden = true;
	}

	private void drawCarpet() {
		if (!MagicCarpet.canFly(who)) {
			hide();
			return;
		}
		for (CarpetFibre fibre : fibres) {
			Block bl = currentCentre.getRelative(fibre.dx, fibre.dy, fibre.dz);
			if (!canReplace(bl.getType())) {
				fibre.block = null;
				continue;
			}
			fibre.block = bl.getState();
			fibre.set(bl, Material.GLASS);
		}
	}

	private boolean canReplace(Material type) {
		switch (type) {
		case AIR:
			return true;
		case SNOW:
			return true;
		case LONG_GRASS:
			return true;
		case DEAD_BUSH:
			return true;
		case WATER_LILY:
			return true;
		case RED_ROSE:
			return true;
		case YELLOW_FLOWER:
			return true;
		case BROWN_MUSHROOM:
			return true;
		case RED_MUSHROOM:
			return true;
		case VINE:
			return true;
		default:
			return false;
		}
	}

	private void setSize(int size) {
		if (size < 0) {
			size = abs(size);
		}
		area = size * size;
		fibres = new CarpetFibre[area];
		rad = (size - 1) / 2;
		radplsq = (rad + 1) * (rad + 1) * 2;
		int i = 0;
		for (int x = -rad; x <= rad; x++) {
			for (int z = -rad; z <= rad; z++) {
				fibres[i] = new CarpetFibre(x, -1, z);
				i++;
			}
		}
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

	public void hide() {
		if (!hidden) {
			hidden = true;
			removeCarpet();
			MagicCarpet.getCarpets().update(who);
			who.sendMessage("Poof! The magic carpet disappears.");
		}
	}

	public boolean isVisible() {
		return !hidden;
	}

	public void moveTo(Location to) {
		removeCarpet();
		currentCentre = to.getBlock();
		drawCarpet();
	}

	public void show() {
		if (hidden) {
			currentCentre = who.getLocation().getBlock();
			hidden = false;
			drawCarpet();
			MagicCarpet.getCarpets().update(who);
			who.sendMessage("Poof! The magic carpet appears below your feet!");
		}
	}

	public void removeCarpet() {
		for (CarpetFibre fibre : fibres) {
			if (fibre.block != null) {
				fibre.update();
			}
		}
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
		return true;
	}
}
