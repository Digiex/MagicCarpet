package com.Android.magiccarpet;

import org.bukkit.block.Block;
import org.bukkit.Material;

/**
* Magic Carpet 1.5
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
	int rad = 0;
	boolean lights = false;
	boolean glowCenter = false;

	public Carpet(boolean cent){
		setSize(5);
		glowCenter = cent;
	}

	public class CarpetFiber
	{
		@SuppressWarnings("hiding")
		public CarpetFiber(int x, int y, int z, int type, boolean torch)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.type = type;
			this.torch = torch;
		}
		int x,y,z,type = 0;
		boolean torch = false;
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
			if (fibers[i].block != null && (fibers[i].block.getType().equals(Material.GLASS) || fibers[i].block.getType().equals(Material.GLOWSTONE)))
					bl.setType(Material.AIR);
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
					if(lights){
						if(!glowCenter){
							if(fibers[i].x == rad || fibers[i].x == -rad || fibers[i].z == rad || fibers[i].z == -rad)
								bl.setType(Material.GLOWSTONE);
							else
								bl.setType(Material.GLASS);
						}else{
							if(fibers[i].x == 0 && fibers[i].z == 0){
								bl.setType(Material.GLOWSTONE);
							}else{
								bl.setType(Material.GLASS);
							}
						}
					}else{
						bl.setType(Material.GLASS);
					}
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
	
	public void setLights(boolean li){
		lights = li;
	}
	
	public boolean checkGlowstone(Block bl){
		boolean sameBlock = false;
		for(int i = 0; i < fibers.length; i++){
			Block fiber = fibers[i].block;
			if (fiber != null){
				if (fiber.equals(bl))
					sameBlock = true;
			}
				
		}
		
		return sameBlock;
	}

	// Changes the carpet size
	@SuppressWarnings("hiding")
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
		for (int x = -size; x <= size; x++){
			for (int z = -size; z <= size; z++) {
				fibers[i] = new CarpetFiber(x, 0, z, 20,false);
				i++;
			}
		}
		
		this.rad = size;
	}
}