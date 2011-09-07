package com.Android.magiccarpet;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

/**
* Magic Carpet 2.0
* Copyright (C) 2011 Celtic Minstrel
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

public class MagicBlockListener extends BlockListener {
	private MagicCarpet plugin;
	
	public MagicBlockListener(MagicCarpet plug) {
		plugin = plug;
	}
	
	@Override
	//When a player joins the game, if they had a carpet when the logged out it puts it back.
	public void onBlockBreak(BlockBreakEvent event) {
		for(Carpet carpet : plugin.carpets.values()){
			if(carpet == null || !carpet.isVisible()) continue;
			if(carpet.isCovering(event.getBlock())) event.setCancelled(true);
		}
	}
}
