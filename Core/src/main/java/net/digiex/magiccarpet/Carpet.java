package net.digiex.magiccarpet;

import static java.lang.Math.abs;
import net.digiex.magiccarpet.nms.Helper;
import net.digiex.magiccarpet.plugins.Vault;
import net.digiex.magiccarpet.plugins.WorldGuard;

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

		CarpetFibre(int dx, int dy, int dz) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}

		void set(Block bl, Material material) {
			bl.setMetadata("Carpet",
					new FixedMetadataValue(plugin, who.getName()));
			bl.setType(material);
		}

		void setFast(Block bl, Material material) {
			bl.setMetadata("Carpet",
					new FixedMetadataValue(plugin, who.getName()));
			Helper.getNMS().setBlockFast(bl.getWorld(), bl.getX(), bl.getY(),
					bl.getZ(), material.getId(), (byte) 0);
		}

		boolean shouldGlow() {
			if (!light) {
				return false;
			}
			if (!canLight()) {
				light = false;
				who.sendMessage("The luminous stones in the carpet slowly fade away.");
				getCarpets().update(who);
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
			if (!canTool()) {
				tools = false;
				who.sendMessage("The magic tools have disappeared.");
				getCarpets().update(who);
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
			if (!canTool()) {
				tools = false;
				who.sendMessage("The magic tools have disappeared.");
				getCarpets().update(who);
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

	private final MagicCarpet plugin = (MagicCarpet) Bukkit.getServer()
			.getPluginManager().getPlugin("MagicCarpet");

	private Block currentCentre;
	private int edge = 0, area = 0, rad = 0, radplsq = 0;
	private CarpetFibre[] fibres;
	private boolean hidden = true, light, tools;
	private Material thread, shine;
	private Player who;

	public Carpet(Player player) {
		who = player;
		currentCentre = player.getLocation().getBlock();
		light = getCarpets().hasLight(player);
		thread = getCarpets().getMaterial(player);
		shine = getCarpets().getLightMaterial(player);
		tools = getCarpets().hasTools(player);
		setSize(getCarpets().getLastSize(player));
		getCarpets().assign(player, this);
	}

	private Storage getCarpets() {
		return plugin.getCarpets();
	}

	private Config getConfig() {
		return plugin.getMCConfig();
	}

	private Vault getVault() {
		return plugin.getVault();
	}

	private boolean drawCarpet() {
		if (!canFly()) {
			hide();
			return false;
		}
		for (CarpetFibre fibre : fibres) {
			Block bl = currentCentre.getRelative(fibre.dx, fibre.dy, fibre.dz);
			Material type = bl.getType();
			if (!canReplace(type)) {
				fibre.block = null;
				continue;
			}
			if (!canFlyHere(bl.getLocation())) {
				hide("Poof! The magic carpet is not allowed in this area.");
				return false;
			}
			fibre.block = bl.getState();
			if (Helper.isEnabled()) {
				if (fibre.shouldGlow()) {
					fibre.setFast(bl, shine);
				} else if (fibre.shouldEnder()) {
					fibre.setFast(bl, Material.ENDER_CHEST);
				} else if (fibre.shouldWork()) {
					fibre.setFast(bl, Material.WORKBENCH);
				} else {
					fibre.setFast(bl, thread);
				}
			} else {
				if (fibre.shouldEnder()) {
					fibre.set(bl, Material.ENDER_CHEST);
				} else if (fibre.shouldWork()) {
					fibre.set(bl, Material.WORKBENCH);
				} else {
					fibre.set(bl, thread);
				}
			}
		}
		return true;
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
		if (getConfig().getDefaultMagicEffect() && Helper.isEnabled()) {
			try {
				Helper.getNMS().playFirework(
						getLocation(),
						FireworkEffect.builder().with(Type.BALL_LARGE)
								.withColor(color).build());
			} catch (Exception e) {
			}
		}
	}

	private boolean canChangeLiquids(String type) {
		if (getConfig().getDefaultChangeLiquids().equals("false"))
			return false;
		else if (getConfig().getDefaultChangeLiquids().equals("true"))
			return true;
		else
			return getConfig().getDefaultChangeLiquids().equals(type);
	}

	public void hide(String message) {
		if (!hidden) {
			hidden = true;
			removeCarpet();
			makeMagic(Color.RED);
			getCarpets().update(who);
			who.sendMessage(message);
		}
	}

	public void changeCarpet(int sz) {
		if (sz % 2 == 0 || sz < 1 || sz > getConfig().getDefaultMaxCarpetSize()) {
			who.sendMessage("The size must be an odd number from 1 to "
					+ String.valueOf(getConfig().getDefaultMaxCarpetSize())
					+ ".");
			return;
		}
		if (sz == edge) {
			who.sendMessage("The carpet size is already equal to " + sz);
			return;
		}
		if (!canFlyAt()) {
			who.sendMessage("A carpet of that size is not allowed for you.");
			return;
		}
		removeCarpet();
		setSize(sz);
		if (drawCarpet()) {
			who.sendMessage("The carpet reacts to your words and suddenly changes!");
		}
		getCarpets().update(who);
	}

	public void changeCarpet(Material material) {
		if (!getConfig().getDefaultCustomCarpets()) {
			who.sendMessage("The carpet isn't allowed to change material.");
			return;
		}
		if (!MagicCarpet.getAcceptableCarpetMaterial().contains(material)) {
			who.sendMessage("A carpet of that material would not support you!");
			return;
		}
		removeCarpet();
		thread = material;
		if (drawCarpet()) {
			who.sendMessage("The carpet reacts to your words and suddenly changes!");
		}
		getCarpets().update(who);
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
			getCarpets().update(who);
			who.sendMessage("Poof! The magic carpet disappears.");
		}
	}

	public boolean isCustom() {
		if (thread != getConfig().getDefaultCarpetMaterial()
				|| shine != getConfig().getDefaultLightMaterial()) {
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
		if (drawCarpet()) {
			who.sendMessage("The luminous stones in the carpet slowly fade away.");
		}
		getCarpets().update(who);
	}

	public void lightOn() {
		if (!getConfig().getDefaultLights()) {
			who.sendMessage("The magic light is disabled");
			return;
		}
		removeCarpet();
		light = true;
		if (drawCarpet()) {
			who.sendMessage("A bright flash shines as glowing stones appear in the carpet.");
		}
		getCarpets().update(who);
	}

	public void moveTo(Location to) {
		removeCarpet();
		currentCentre = to.getBlock();
		drawCarpet();
	}

	public void setLight(Material material) {
		if (!getConfig().getDefaultCustomLights()) {
			who.sendMessage("The magic light isn't allowed to change material.");
			return;
		}
		if (!MagicCarpet.getAcceptableLightMaterial().contains(material)) {
			who.sendMessage("A magic light of that material would not light up!");
			return;
		}
		removeCarpet();
		shine = material;
		if (drawCarpet()) {
			who.sendMessage("The carpet reacts to your words and suddenly changes!");
		}
		getCarpets().update(who);
	}

	public void show() {
		if (hidden) {
			currentCentre = who.getLocation().getBlock().getRelative(0, -1, 0);
			hidden = false;
			if (drawCarpet()) {
				makeMagic(Color.BLUE);
				who.sendMessage("Poof! The magic carpet appears below your feet!");
			}
			getCarpets().update(who);
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
		if (drawCarpet()) {
			who.sendMessage("The magic tools have disappeared.");
		}
		getCarpets().update(who);
	}

	public void toolsOn() {
		if (!getConfig().getDefaultTools()) {
			who.sendMessage("The magic tools are not enabled.");
			return;
		}
		removeCarpet();
		tools = true;
		if (drawCarpet()) {
			who.sendMessage("The magic tools have appeared!");
		}
		getCarpets().update(who);
	}

	void removeCarpet() {
		for (CarpetFibre fibre : fibres) {
			if (fibre.block != null) {
				fibre.update();
			}
			fibre.block = null;
		}
	}

	private boolean canFly() {
		return hasPermission("magiccarpet.mc");
	}

	private boolean canLight() {
		return hasPermission("magiccarpet.ml");
	}

	private boolean canTool() {
		return hasPermission("magiccarpet.mct");
	}

	private boolean canFlyHere(Location location) {
		WorldGuard worldguard = plugin.getWorldGuard();
		return (!worldguard.isEnabled()) ? true : worldguard
				.canFlyHere(location);
	}

	private boolean canFlyAt() {
		if (edge == getConfig().getDefaultCarpSize()) {
			return true;
		}
		if (getCarpets().wasGiven(who)) {
			return true;
		}
		if (who.hasPermission("magiccarpet.*")) {
			return true;
		}
		if (getVault().isEnabled()) {
			if (who.hasPermission("magiccarpet.np")) {
				return true;
			}
			if (getConfig().getDefaultChargeTimeBased()) {
				return (getCarpets().getTime(who) <= 0L) ? false : true;
			} else {
				return getCarpets().hasPaidFee(who);
			}
		}
		return who.hasPermission("magiccarpet.mc." + edge);
	}

	private boolean hasPermission(String permission) {
		if (getCarpets().wasGiven(who)) {
			return true;
		}
		if (getVault().isEnabled()) {
			if (who.hasPermission("magiccarpet.np")) {
				return true;
			}
			if (getConfig().getDefaultChargeTimeBased()) {
				return (getCarpets().getTime(who) <= 0L) ? false : true;
			} else {
				return getCarpets().hasPaidFee(who);
			}
		}
		return who.hasPermission(permission);
	}
}
