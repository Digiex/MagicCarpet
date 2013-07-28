package net.digiex.magiccarpet.nms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import net.digiex.magiccarpet.nms.NMSHelper;
import net.digiex.magiccarpet.nms.api.NMSAbstraction;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

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

public class CraftBlockUpdate implements BlockUpdate, Runnable {
	private final Plugin plugin;
	private final World world;
	private final NMSAbstraction nms;

	private RelightingStrategy relightingStrategy = RelightingStrategy.IMMEDIATE;

	private static final int MAX_BLOCKS_PER_TIME_CHECK = 1000;
	private Queue<DeferredBlock> deferredBlocks = new ArrayDeque<DeferredBlock>();
	private BukkitTask relightTask = null;
	private long maxRelightTimePerTick = TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS);

	private int minX = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;
	private int blocksModified = 0;

	public CraftBlockUpdate(Plugin plugin, org.bukkit.World world) {
		this.plugin = plugin;
		this.world = world;
		this.nms = NMSHelper.getNMS();
		if (nms == null) {
			throw new IllegalStateException("NMS abstraction API is not available");
		}
	}

	public boolean setBlock(int x, int y, int z, int blockId) {
		return setBlock(x, y, z, blockId, 0);
	}

	public boolean setBlock(int x, int y, int z, int blockId, int data) {
		minX = Math.min(minX, x);
		minZ = Math.min(minZ, z);
		maxX = Math.max(maxX, x);
		maxZ = Math.max(maxZ, z);

		blocksModified++;
		int oldBlockId = world.getBlockTypeIdAt(x, y, z);
		boolean res = nms.setBlockFast(world, x, y, z, blockId, (byte)data);

		if (relightingStrategy != RelightingStrategy.NEVER) {
			if (nms.getBlockLightBlocking(oldBlockId) != nms.getBlockLightBlocking(blockId)
					|| nms.getBlockLightEmission(oldBlockId) != nms.getBlockLightEmission(blockId)) {
				if (relightingStrategy == RelightingStrategy.IMMEDIATE) {
					nms.recalculateBlockLighting(world, x, y, z);
				} else if (relightingStrategy == RelightingStrategy.DEFERRED) {
					deferredBlocks.add(new DeferredBlock(x, y, z));
				}
			}
		}
		return res;
	}

	public void notifyClients() {
		if (relightingStrategy == RelightingStrategy.DEFERRED) {
			relightTask = Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L);
		} else {
			for (ChunkCoords cc : calculateChunks()) {
				world.refreshChunk(cc.x, cc.z);
			}
		}
	}

	public void run() {
		long now = System.nanoTime();
		int n = 1;

		while (deferredBlocks.peek() != null) {
			DeferredBlock db = deferredBlocks.poll();
			nms.recalculateBlockLighting(world, db.x, db.y, db.z);
			if (n++ % MAX_BLOCKS_PER_TIME_CHECK == 0) {
				if (System.nanoTime() - now > maxRelightTimePerTick) {
					break;
				}
			}
		}

		if (deferredBlocks.isEmpty()) {
			relightTask.cancel();
			relightTask = null;
			for (ChunkCoords cc : calculateChunks()) {
				world.refreshChunk(cc.x, cc.z);
			}
		}
	}

	public void setRelightingStrategy(RelightingStrategy strategy) {
		this.relightingStrategy = strategy;
	}

	public void setMaxRelightTimePerTick(long value, TimeUnit timeUnit) {
		maxRelightTimePerTick = timeUnit.toNanos(value);
	}

	public int getBlocksToRelight() {
		return deferredBlocks.size();
	}

	public void setDeferredBufferSize(int size) {
		if (!deferredBlocks.isEmpty()) {
			throw new IllegalStateException("setDeferredBufferSize() called after block updates made");
		}
		if (relightingStrategy != RelightingStrategy.DEFERRED) {
			throw new IllegalStateException("setDeferredBufferSize() called when relighting strategy not DEFERRED");
		}
		deferredBlocks = new ArrayDeque<CraftBlockUpdate.DeferredBlock>(size);
	}

	private List<ChunkCoords> calculateChunks() {
		List<ChunkCoords> res = new ArrayList<ChunkCoords>();
		if (blocksModified == 0) {
			return res;
		}
		int x1 = minX >> 4; int x2 = maxX >> 4;
		int z1 = minZ >> 4; int z2 = maxZ >> 4;
		for (int x = x1; x <= x2; x++) {
			for (int z = z1; z <= z2; z++) {
				res.add(new ChunkCoords(x, z));
			}
		}
		return res;
	}
	
	public static BlockUpdate createBlockUpdater(Plugin plugin, org.bukkit.World world) {
		return new CraftBlockUpdate(plugin, world);
	}

	private class ChunkCoords {
		public final int x, z;
		public ChunkCoords(int x, int z) {
			this.x = x;
			this.z = z;
		}
	}

	private class DeferredBlock {
		public final int x, y, z;

		public DeferredBlock(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}