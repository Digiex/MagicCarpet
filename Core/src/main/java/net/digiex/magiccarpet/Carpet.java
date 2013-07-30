package net.digiex.magiccarpet;

import static java.lang.Math.abs;
import net.digiex.magiccarpet.nms.NMSHelper;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

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
			if (bl.hasMetadata("Carpet")) {
				return;
			}
			if (NMSHelper.isEnabled()) {
				NMSHelper.getNMS().setBlockFast(bl.getWorld(), bl.getX(),
						bl.getY(), bl.getZ(), material.getId(), (byte) 0);
			} else {
				bl.setType(material);
			}
			bl.setMetadata("Carpet", new FixedMetadataValue(plugin, who.getName()));
		}

		boolean shouldGlow() {
			if (!light) {
				return false;
			}
			if (!plugin.canLight(who)) {
				light = false;
				who.sendMessage("The luminous stones in the carpet slowly fade away.");
				carpets.update(who);
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
			if (!plugin.canTool(who)) {
				tools = false;
				who.sendMessage("The magic tools have disappeared.");
				carpets.update(who);
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
			if (!plugin.canTool(who)) {
				tools = false;
				who.sendMessage("The magic tools have disappeared.");
				carpets.update(who);
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
			if (block.getMetadata("Carpet").get(0).asString() != who.getName()) {
				return;
			}
			block.removeMetadata("Carpet", plugin);
			block.update(true);
		}
	}

	private final MagicCarpet plugin = (MagicCarpet) Bukkit.getServer()
			.getPluginManager().getPlugin("MagicCarpet");
	private final Config config = plugin.getMCConfig();
	private final Carpets carpets = plugin.getCarpets();

	private Block currentCentre;
	private int edge = 0, area = 0, rad = 0, radplsq = 0;
	private CarpetFibre[] fibres;
	private boolean hidden, light, tools;
	private Material thread, shine;
	private Player who;

	public Carpet(Player player) {
		who = player;
		hidden = true;
		setSize(carpets.getLastSize(player));
		currentCentre = player.getLocation().getBlock();
		light = carpets.hasLight(player);
		thread = carpets.getMaterial(player);
		shine = carpets.getLightMaterial(player);
		tools = carpets.hasTools(player);
		carpets.assign(player, this);
	}

	private void drawCarpet() {
		if (!plugin.canFly(who)) {
			hide();
			return;
		}
		if (!plugin.canFlyHere(currentCentre.getLocation())) {
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
				fibre.set(bl, shine);
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
			return canChangeLiquids("water");
		case STATIONARY_WATER:
			return canChangeLiquids("water");
		case LAVA:
			return canChangeLiquids("lava");
		case STATIONARY_LAVA:
			return canChangeLiquids("lava");
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
		if (config.getDefaultMagicEffect() && NMSHelper.isEnabled()) {
			try {
				NMSHelper.getNMS().playFirework(
						getLocation(),
						FireworkEffect.builder().with(Type.BALL_LARGE)
								.withColor(color).build());
			} catch (Exception e) {
			}
		}
	}

	private boolean canChangeLiquids(String type) {
		if (config.getDefaultChangeLiquids().equals("false"))
			return false;
		else if (config.getDefaultChangeLiquids().equals("true"))
			return true;
		else
			return config.getDefaultChangeLiquids().equals(type);
	}

	public void changeCarpet(int sz) {
		if (sz % 2 == 0 || sz < 1 || sz > config.getDefaultMaxCarpetSize()) {
			who.sendMessage("The size must be an odd number from 1 to "
					+ String.valueOf(config.getDefaultMaxCarpetSize()) + ".");
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
		carpets.update(who);
	}

	public void changeCarpet(Material material) {
		if (!config.getDefaultCustomCarpets()) {
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
		carpets.update(who);
	}

	public void descend() {
		removeCarpet();
		currentCentre = currentCentre.getLocation().getBlock()
				.getRelative(0, -1, 0);
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
			carpets.update(who);
			who.sendMessage("Poof! The magic carpet disappears.");
		}
	}

	public boolean isCustom() {
		if (thread != config.getDefaultCarpetMaterial()
				|| shine != config.getDefaultLightMaterial()) {
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
		carpets.update(who);
	}

	public void lightOn() {
		if (!config.getDefaultLights()) {
			who.sendMessage("The magic light is disabled");
			return;
		}
		removeCarpet();
		light = true;
		drawCarpet();
		who.sendMessage("A bright flash shines as glowing stones appear in the carpet.");
		carpets.update(who);
	}

	public void moveTo(Location to) {
		removeCarpet();
		currentCentre = to.getBlock();
		drawCarpet();
	}

	public void setLight(Material material) {
		if (!config.getDefaultCustomLights()) {
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
		carpets.update(who);
	}

	public void show() {
		if (hidden) {
			currentCentre = who.getLocation().getBlock().getRelative(0, -1, 0);
			hidden = false;
			drawCarpet();
			makeMagic(Color.BLUE);
			carpets.update(who);
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
		carpets.update(who);
	}

	public void toolsOn() {
		if (!config.getDefaultTools()) {
			who.sendMessage("The magic tools are not enabled.");
			return;
		}
		removeCarpet();
		tools = true;
		drawCarpet();
		who.sendMessage("The magic tools have appeared!");
		carpets.update(who);
	}

	void removeCarpet() {
		for (CarpetFibre fibre : fibres) {
			if (fibre.block != null) {
				fibre.update();
			}
			fibre.block = null;
		}
	}
}
