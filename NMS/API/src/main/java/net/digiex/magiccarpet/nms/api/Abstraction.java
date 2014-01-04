package net.digiex.magiccarpet.nms.api;

import java.util.EnumSet;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

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

public interface Abstraction {

	public boolean setBlockFast(World world, int x, int y, int z,
			Material material, byte data);

	public void playFirework(Location loc, FireworkEffect effect);
	
	public EnumSet<Material> getAcceptableCarpetMaterial();
	
	public EnumSet<Material> getAcceptableLightMaterial();
}