package net.digiex.magiccarpet.nms.v1_4_5;

import net.digiex.magiccarpet.nms.api.Abstraction;
import net.minecraft.server.v1_4_5.Chunk;
import net.minecraft.server.v1_4_5.World;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_4_5.CraftWorld;

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

public abstract class Handler implements Abstraction {

	@Override
	public boolean setBlockFast(org.bukkit.World world, int x, int y, int z,
			Material material, byte data) {
		World w = ((CraftWorld) world).getHandle();
		Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		return chunk.a(x & 0x0f, y, z & 0x0f, material.getId(), data);
	}
}