package com.Android.magiccarpet;

import org.bukkit.block.Block;
import org.bukkit.Material;

/**
* Magic Carpet 1.4
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
	Block currentBlock;
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
		Block block = null;
	}

	public CarpetFiber[] fibers;

//Goes through a grid of the area underneath the player, and if the block is glass that is part of the magic carpet, it is removed
	public void removeCarpet() {
		Block bl;
		if (currentBlock == null)
			return;
		for(int i = 0; i < fibers.length; i++)
		{
			bl = fibers[i].block;
			if (fibers[i].block != null) bl.setType(Material.AIR);
			fibers[i].block = null;
		}
	}

//Places glass in a 5x5 area underneath the player if the block was just air previously
	public void drawCarpet() {
		Block bl;
		for(int i = 0; i < fibers.length; i++)
		{
			if (currentBlock != null){
			bl = currentBlock.getRelative(fibers[i].x,fibers[i].y,fibers[i].z);
			if (bl.getType().equals(Material.AIR) &&
					bl.getRelative(-1, 0, 0).getTypeId() != 81 && // 81 is Cactus
					bl.getRelative( 1, 0, 0).getTypeId() != 81 &&
					bl.getRelative( 0, 0, -1).getTypeId() != 81 &&
					bl.getRelative( 0, 0, 1).getTypeId() != 81) {
				fibers[i].block = bl;
				bl.setType(Material.GLASS);
			} else {
				fibers[i].block = null;
			}
		}
		}
	}

	public void changeCarpet(int si){
		removeCarpet();
		setSize(si);
		drawCarpet();
	}

// Changes the carpet size
	protected void setSize(int size) {
		if (size < 0) size -= size; // Sanity check
		this.size = size;

		fibers = new CarpetFiber[size*size];
		switch(size){
		case 3: size = 1; break;
		case 5: size = 2; break;
		case 7: size = 3; break;
		default: size = 2; break;
		}

		int i = 0;
		for (int x = -size; x <= size; x++)
			for (int z = -size; z <= size; z++) {
				fibers[i] = new CarpetFiber(x, 0, z, 20);
				i++;
			}
		}
	}