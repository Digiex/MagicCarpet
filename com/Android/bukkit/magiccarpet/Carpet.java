package com.Android.bukkit.magiccarpet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
* Magic Carpet 1.0
* Copyright (C) 2011 Android <spparr@gmail.com>
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
* Carpet.java
* <br /><br />
* Defines the basic 5x5 carpet object that is placed underneath the player.
* 
*
* @author Android <spparr@gmail.com>
*/
public class Carpet {
	Location currentLoc = null;
	int size = 0;
	
	public Carpet() {
		setSize(5);
	}
	
	public class CarpetFiber
	{
		public CarpetFiber(int x, int y, int z, int type)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.type = type;
		}
		int x,y,z,type = 0;
		boolean imadeit = false;
	}
	
	public CarpetFiber[] fibers;
	
	//Goes through a grid of the area underneath the player, and if the block is glass that is part of the magic carpet, it is removed
	public void removeCarpet(World wo) {
		Block bl;
		for(int i = 0; i < fibers.length; i++)
		{
			bl = wo.getBlockAt(currentLoc.getBlockX() + fibers[i].x, currentLoc.getBlockY() - fibers[i].y, currentLoc.getBlockZ() + fibers[i].z);
			if (fibers[i].imadeit && bl.getTypeId() == 20) bl.setTypeId(0);
			fibers[i].imadeit = false;
		}
	}
	
	//Places glass in a 5x5 area underneath the player if the block was just air previously
	public void drawCarpet(World wo) {
		Block bl;
		for(int i = 0; i < fibers.length; i++)
		{
			bl = wo.getBlockAt(currentLoc.getBlockX() + fibers[i].x, currentLoc.getBlockY() - fibers[i].y, currentLoc.getBlockZ() + fibers[i].z);
			if (bl.getTypeId() == 0 &&
					bl.getRelative(-1, 0,  0).getTypeId() != 81 && // 81 is Cactus
					bl.getRelative( 1, 0,  0).getTypeId() != 81 &&
					bl.getRelative( 0, 0, -1).getTypeId() != 81 &&
					bl.getRelative( 0, 0,  1).getTypeId() != 81) {
				fibers[i].imadeit = true;
				bl.setTypeId(fibers[i].type);
			} else {
				fibers[i].imadeit = false;
			}
		}
	}
	
	public void changeCarpet(World wo, int si){
		removeCarpet(wo);
		setSize(si);
		drawCarpet(wo);
	}
	
	// Changes the carpet size 
	protected void setSize(int size) {
		if (size < 0) size -= size; // Sanity check
		this.size = size;
		
		fibers = new CarpetFiber[size*size];
		size >>= 1; // size /= 2;
		
		int i = 0;
		for (int x = -size; x <= size; x++)
			for (int z = -size; z <= size; z++) {
				fibers[i] = new CarpetFiber(x, 0, z, 20);
				i++;
			}
	}
}
