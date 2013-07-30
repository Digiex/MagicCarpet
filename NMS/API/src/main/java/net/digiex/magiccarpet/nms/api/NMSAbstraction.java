package net.digiex.magiccarpet.nms.api;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;

public interface NMSAbstraction {

	public boolean setBlockFast(World world, int x, int y, int z, int blockId,
			byte data);

	public void forceBlockLightLevel(World world, int x, int y, int z, int level);

	public void playFirework(Location loc, FireworkEffect effect);
}