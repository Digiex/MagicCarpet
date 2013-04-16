package net.digiex.magiccarpet;

import static java.lang.Math.abs;
import net.digiex.magiccarpet.events.CarpetMoveEvent;
import net.digiex.magiccarpet.events.CarpetSpawnEvent;
import net.digiex.magiccarpet.events.CarpetTeleportEvent;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/*
 * Magic Carpet 3.0 Copyright (C) 2012-2013 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

		boolean shouldGlow() {
			if (!light) {
				return false;
			}
			if (!MagicCarpet.canLight(who)) {
				light = false;
				who.sendMessage("The luminous stones in the carpet slowly fade away.");
				MagicCarpet.getCarpets().update(who);
				return false;
			}
			if (dx == 0 && dz == 0) {
				return true;
			}
			return false;
		}

		boolean shouldEnder() {
			if (!tools) {
				return false;
			}
			if (!MagicCarpet.canTool(who)) {
				tools = false;
				who.sendMessage("The magic tools have disappeared.");
				MagicCarpet.getCarpets().update(who);
				return false;
			}
			if (dx == 2 && dz == 0) {
				return true;
			}
			return false;
		}

		boolean shouldWork() {
			if (!tools) {
				return false;
			}
			if (!MagicCarpet.canTool(who)) {
				tools = false;
				who.sendMessage("The magic tools have disappeared.");
				MagicCarpet.getCarpets().update(who);
				return false;
			}
			if (dx == -2 && dz == 0) {
				return true;
			}
			return false;
		}

		void update() {
			if (!block.hasMetadata("Carpet")) {
				return;
			}
			block.removeMetadata("Carpet", plugin);
			block.update(true);
		}
	}

	private final MagicCarpet plugin = (MagicCarpet) Bukkit.getServer().getPluginManager().getPlugin("MagicCarpet");
	private Carpet carpet = this;

	public static Carpet create(Player player) {
		int sz = MagicCarpet.getCarpets().getLastSize(player);
		boolean light = MagicCarpet.getCarpets().hasLight(player);
		Material thread = MagicCarpet.getCarpets().getMaterial(player);
		Material shine = MagicCarpet.getCarpets().getLightMaterial(player);
		boolean tools = MagicCarpet.getCarpets().hasTools(player);
		Carpet carpet = new Carpet(player, sz, light, thread, shine, tools);
		MagicCarpet.getCarpets().assign(player, carpet);
		return carpet;
	}

	private Block currentCentre;
	private int edge = 0, area = 0, rad = 0, radplsq = 0;
	private CarpetFibre[] fibres;
	private boolean hidden, light, tools;
	private Material thread, shine;
	private Player who;

	private Carpet(Player p, int s, boolean o, Material m, Material l, boolean t) {
		setSize(s);
		who = p;
		currentCentre = p.getLocation().getBlock();
		light = o;
		hidden = true;
		thread = m;
		shine = l;
		tools = t;
	}

	private void drawCarpet() {
		if (!MagicCarpet.canFly(who)) {
			hide();
			return;
		}
		for (CarpetFibre fibre : fibres) {
				Block bl = currentCentre.getRelative(fibre.dx, fibre.dy, fibre.dz);
				Material type = bl.getType();
				if (!canReplace(type)) {
					fibre.block = null;
					continue;
				}
				fibre.block = bl.getState();
				if (fibre.shouldGlow()) {
					fibre.set(bl, getShine());
				} else if (fibre.shouldEnder()) {
					fibre.set(bl, Material.ENDER_CHEST);
				} else if (fibre.shouldWork()) {
					fibre.set(bl, Material.WORKBENCH);
				} else {
					fibre.set(bl, thread);
				}
			}
	}

	private boolean canReplace(Material type) {
		switch (type) {
		case AIR:
			return true;
		case WATER:
			return plugin.canChangeLiquids("water");
		case STATIONARY_WATER:
			return plugin.canChangeLiquids("water");
		case LAVA:
			return plugin.canChangeLiquids("lava");
		case STATIONARY_LAVA:
			return plugin.canChangeLiquids("lava");
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
		edge = size;
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
	
	private void makeMagic(Color color) {
		MagicCarpet.addMagic(currentCentre.getWorld(),
				currentCentre.getLocation(), color);
	}

	public void changeCarpet(int sz) {
		if (sz % 2 == 0 || sz < 1 || sz > plugin.maxCarpSize) {
			who.sendMessage("The size must be an odd number from 1 to "
					+ String.valueOf(plugin.maxCarpSize) + ".");
			return;
		}
		if (sz == edge) {
			who.sendMessage("The carpet size is already equal to " + sz);
			return;
		}
		if (!plugin.canFlyAt(who, sz)) {
			who.sendMessage("A carpet of that size is not allowed for you.");
			return;
		}
		removeCarpet();
		setSize(sz);
		drawCarpet();
		who.sendMessage("The carpet reacts to your words and suddenly changes!");
		MagicCarpet.getCarpets().update(who);
	}

	public void changeCarpet(Material material) {
		if (!plugin.customCarpets) {
			who.sendMessage("The carpet isn't allowed to change material.");
			return;
		}
		if (!MagicCarpet.getAcceptableCarpetMaterial().contains(material)) {
			who.sendMessage("A carpet of that material would not support you!");
			return;
		}
		removeCarpet();
		thread = material;
		drawCarpet();
		who.sendMessage("The carpet reacts to your words and suddenly changes!");
		MagicCarpet.getCarpets().update(who);
	}

	public void descend() {
		removeCarpet();
		currentCentre = currentCentre.getLocation().getBlock().getRelative(0, -1, 0);
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

	public void hide() {
		if (!hidden) {
			hidden = true;
			removeCarpet();
			makeMagic(Color.RED);
			MagicCarpet.getCarpets().update(who);
			who.sendMessage("Poof! The magic carpet disappears.");
		}
	}

	public boolean isCustom() {
		if (thread != plugin.carpMaterial || shine != plugin.lightMaterial) {
			return true;
		}
		return false;
	}

	public boolean isVisible() {
		return !hidden;
	}

	public boolean hasLight() {
		return light;
	}

	public void lightOff() {
		removeCarpet();
		light = false;
		drawCarpet();
		who.sendMessage("The luminous stones in the carpet slowly fade away.");
		MagicCarpet.getCarpets().update(who);
	}

	public void lightOn() {
		if (!plugin.lights) {
			who.sendMessage("The magic light is disabled");
			return;
		}
		removeCarpet();
		light = true;
		drawCarpet();
		who.sendMessage("A bright flash shines as glowing stones appear in the carpet.");
		MagicCarpet.getCarpets().update(who);
	}

	public void moveTo(Location to) {
		CarpetMoveEvent event = new CarpetMoveEvent(this, to);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}
		removeCarpet();
		currentCentre = to.getBlock();
		drawCarpet();
	}

	public void teleportTo(Location to) {
		CarpetTeleportEvent event = new CarpetTeleportEvent(this, to);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}
		removeCarpet();
		currentCentre = to.getBlock();
		drawCarpet();
	}

	public void setLight(Material material) {
		if (!plugin.customLights) {
			who.sendMessage("The magic light isn't allowed to change material.");
			return;
		}
		if (!MagicCarpet.getAcceptableLightMaterial().contains(material)) {
			who.sendMessage("A magic light of that material would not light up!");
			return;
		}
		removeCarpet();
		shine = material;
		drawCarpet();
		who.sendMessage("The carpet reacts to your words and suddenly changes!");
		MagicCarpet.getCarpets().update(who);
	}

	public void show() {
		if (hidden) {
			CarpetSpawnEvent event = new CarpetSpawnEvent(this);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return;
			}
			currentCentre = who.getLocation().getBlock();
			hidden = false;
			drawCarpet();
			makeMagic(Color.BLUE);
			MagicCarpet.getCarpets().update(who);
			who.sendMessage("Poof! The magic carpet appears below your feet!");
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

	public boolean hasTools() {
		return tools;
	}

	public void toolsOff() {
		removeCarpet();
		tools = false;
		drawCarpet();
		who.sendMessage("The magic tools have disappeared.");
		MagicCarpet.getCarpets().update(who);
	}

	public void toolsOn() {
		if (!plugin.tools) {
			who.sendMessage("The magic tools are not enabled.");
			return;
		}
		removeCarpet();
		tools = true;
		drawCarpet();
		who.sendMessage("The magic tools have appeared!");
		MagicCarpet.getCarpets().update(who);
	}

	public void removeCarpet() {
		for (CarpetFibre fibre : fibres) {
			if (fibre.block != null) {
				fibre.update();
			}
			fibre.block = null;
		}
	}
}
