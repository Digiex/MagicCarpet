package net.digiex.magiccarpet;

import static java.lang.Math.abs;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

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
public class Carpet {

	private class CarpetFibre {

		BlockState block;
		int dx, dy, dz;

		CarpetFibre(int dx, int dy, int dz) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}

		void setFast(Block bl, Material material) {
			bl.setMetadata("Carpet",
					new FixedMetadataValue(plugin, who.getName()));
			Helper.getHandler().setBlockFast(bl.getWorld(), bl.getX(),
					bl.getY(), bl.getZ(), material, (byte) 0);
		}

		void setFast(Block bl, Material material, byte data) {
			bl.setMetadata("Carpet",
					new FixedMetadataValue(plugin, who.getName()));
			Helper.getHandler().setBlockFast(bl.getWorld(), bl.getX(),
					bl.getY(), bl.getZ(), material, data);
		}

		boolean shouldGlow() {
			if (!light) {
				return false;
			}
			if (!Permissions.canLight(who)) {
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
			if (!Permissions.canTool(who)) {
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
			if (!Permissions.canTool(who)) {
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
			block.update(true);
			block.removeMetadata("Carpet", plugin);
		}
	}

	private final MagicCarpet plugin = (MagicCarpet) Bukkit.getServer()
			.getPluginManager().getPlugin("MagicCarpet");

	private Block currentCentre;
	private int edge = 0, area = 0, rad = 0, radplsq = 0;
	private CarpetFibre[] fibres;
	private boolean hidden = true, light, tools, falling, descending;
	private Material thread, shine;
	private Player who;
	private byte data;

	public Carpet(Player player) {
		who = player;
		currentCentre = player.getLocation().getBlock();
		light = MagicCarpet.getCarpets().hasLight(player);
		thread = MagicCarpet.getCarpets().getMaterial(player);
		shine = MagicCarpet.getCarpets().getLightMaterial(player);
		tools = MagicCarpet.getCarpets().hasTools(player);
		data = MagicCarpet.getCarpets().getData(player);
		setSize(MagicCarpet.getCarpets().getLastSize(player));
		MagicCarpet.getCarpets().assign(player, this);
	}

	private boolean drawCarpet() {
		if (!Permissions.canFly(who)) {
			hide();
			return false;
		}
		for (CarpetFibre fibre : fibres) {
			Block bl = currentCentre.getRelative(fibre.dx, fibre.dy, fibre.dz);
			if (!canReplace(bl.getType())) {
				fibre.block = null;
				continue;
			}
			if (!Permissions.canFlyHere(bl.getLocation())) {
				hide("Poof! The magic carpet is not allowed in this area.");
				return false;
			}
			fibre.block = bl.getState();
			if (fibre.shouldGlow()) {
				fibre.setFast(bl, shine);
			} else if (fibre.shouldEnder()) {
				fibre.setFast(bl, Material.ENDER_CHEST);
			} else if (fibre.shouldWork()) {
				fibre.setFast(bl, Material.WORKBENCH);
			} else {
				if (canHaveData(thread)) {
					fibre.setFast(bl, thread, data);
				} else {
					fibre.setFast(bl, thread);
				}
			}
		}
		hidden = false;
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
		case DOUBLE_PLANT:
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
		if (Config.getMagicEffect()) {
			try {
				Helper.getHandler().playFirework(
						getLocation(),
						FireworkEffect.builder().with(Type.BALL_LARGE)
								.withColor(color).build());
			} catch (Exception e) {
			}
		}
	}

	private boolean canChangeLiquids(String type) {
		if (Config.getChangeLiquids().equals("false"))
			return false;
		else if (Config.getChangeLiquids().equals("true"))
			return true;
		else
			return Config.getChangeLiquids().equals(type);
	}

	public void hide(String message) {
		if (!hidden) {
			removeCarpet();
			makeMagic(Color.RED);
			MagicCarpet.getCarpets().update(who);
			who.sendMessage(message);
			if (who.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
				falling = true;
			}
		}
	}

	public void changeCarpet(int sz) {
		if (sz % 2 == 0 || sz < 1 || sz > Config.getMaxCarpetSize()) {
			who.sendMessage("The size must be an odd number from 1 to "
					+ String.valueOf(Config.getMaxCarpetSize()) + ".");
			return;
		}
		if (sz == edge) {
			who.sendMessage("The carpet size is already equal to " + sz);
			return;
		}
		if (!Permissions.canFlyAt(who, sz)) {
			who.sendMessage("A carpet of that size is not allowed for you.");
			return;
		}
		removeCarpet();
		setSize(sz);
		if (drawCarpet()) {
			makeMagic(Color.SILVER);
			who.sendMessage("The carpet reacts to your words and suddenly changes!");
		}
		MagicCarpet.getCarpets().update(who);
	}

	public void changeCarpet(Material material) {
		if (!Config.getCustomCarpets()) {
			who.sendMessage("The carpet isn't allowed to change material.");
			return;
		}
		if (!Helper.getHandler().getAcceptableCarpetMaterial()
				.contains(material)) {
			who.sendMessage("A carpet of that material would not support you!");
			return;
		}
		removeCarpet();
		thread = material;
		if (drawCarpet()) {
			makeMagic(Color.SILVER);
			who.sendMessage("The carpet reacts to your words and suddenly changes!");
		}
		MagicCarpet.getCarpets().update(who);
	}

	public void changeCarpet(Material material, byte data) {
		if (!Config.getCustomCarpets()) {
			who.sendMessage("The carpet isn't allowed to change material.");
			return;
		}
		if (!Helper.getHandler().getAcceptableCarpetMaterial()
				.contains(material)) {
			who.sendMessage("A carpet of that material would not support you!");
			return;
		}
		removeCarpet();
		thread = material;
		this.data = data;
		if (drawCarpet()) {
			makeMagic(getMagicColor());
			who.sendMessage("The carpet reacts to your words and suddenly changes!");
		}
		MagicCarpet.getCarpets().update(who);
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
			removeCarpet();
			makeMagic(Color.RED);
			MagicCarpet.getCarpets().update(who);
			who.sendMessage("Poof! The magic carpet disappears.");
			if (who.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
				falling = true;
			}
		}
	}

	public boolean isCustom() {
		if (thread != Config.getCarpetMaterial()
				|| shine != Config.getLightMaterial()) {
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
			makeMagic(Color.YELLOW);
			who.sendMessage("The luminous stones in the carpet slowly fade away.");
		}
		MagicCarpet.getCarpets().update(who);
	}

	public void lightOn() {
		if (!Config.getLights()) {
			who.sendMessage("The magic light is disabled");
			return;
		}
		removeCarpet();
		light = true;
		if (drawCarpet()) {
			makeMagic(Color.YELLOW);
			who.sendMessage("A bright flash shines as glowing stones appear in the carpet.");
		}
		MagicCarpet.getCarpets().update(who);
	}

	public void moveTo(Location to) {
		removeCarpet();
		currentCentre = to.getBlock();
		drawCarpet();
	}

	public void setLight(Material material) {
		if (!Config.getCustomLights()) {
			who.sendMessage("The magic light isn't allowed to change material.");
			return;
		}
		if (!Helper.getHandler().getAcceptableLightMaterial()
				.contains(material)) {
			who.sendMessage("A magic light of that material would not light up!");
			return;
		}
		removeCarpet();
		shine = material;
		if (drawCarpet()) {
			who.sendMessage("The carpet reacts to your words and suddenly changes!");
		}
		MagicCarpet.getCarpets().update(who);
	}

	public void show() {
		if (hidden) {
			currentCentre = who.getLocation().getBlock().getRelative(0, -1, 0);
			if (drawCarpet()) {
				makeMagic(Color.BLUE);
				who.sendMessage("Poof! The magic carpet appears below your feet!");
			}
			MagicCarpet.getCarpets().update(who);
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
		MagicCarpet.getCarpets().update(who);
	}

	public void toolsOn() {
		if (!Config.getTools()) {
			who.sendMessage("The magic tools are not enabled.");
			return;
		}
		removeCarpet();
		tools = true;
		if (drawCarpet()) {
			who.sendMessage("The magic tools have appeared!");
		}
		MagicCarpet.getCarpets().update(who);
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		removeCarpet();
		this.data = data;
		if (drawCarpet()) {
			makeMagic(getMagicColor());
			who.sendMessage("The carpet reacts to your words and suddenly changes!");
		}
		MagicCarpet.getCarpets().update(who);
	}

	void removeCarpet() {
		for (CarpetFibre fibre : fibres) {
			if (fibre.block != null) {
				fibre.update();
			}
			fibre.block = null;
		}
		hidden = true;
	}

	public boolean canHaveData(Material material) {
		switch (material) {
		case WOOL:
			return true;
		case STAINED_GLASS:
			return true;
		case STAINED_CLAY:
			return true;
		default:
			return false;
		}
	}

	private Color getMagicColor() {
		switch (data) {
		case 1:
			return Color.ORANGE;
		case 2:
			return Color.fromRGB(255, 0, 255);
		case 3:
			return Color.fromRGB(173, 216, 230);
		case 4:
			return Color.YELLOW;
		case 5:
			return Color.LIME;
		case 6:
			return Color.fromRGB(255, 192, 203);
		case 7:
			return Color.GRAY;
		case 8:
			return Color.fromRGB(211, 211, 211);
		case 9:
			return Color.fromRGB(0, 255, 255);
		case 10:
			return Color.PURPLE;
		case 11:
			return Color.BLUE;
		case 12:
			return Color.fromRGB(165, 42, 42);
		case 13:
			return Color.GREEN;
		case 14:
			return Color.RED;
		case 15:
			return Color.BLACK;
		default:
			return Color.SILVER;
		}
	}

	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	public boolean isDescending() {
		return descending;
	}

	public void setDescending(boolean descending) {
		this.descending = descending;
	}
}
