package com.Android.magiccarpet;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.EventExecutor;

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

public class MagicDamageListener implements Listener {
	public EventExecutor executor = new EventExecutor() {
		@Override@SuppressWarnings("incomplete-switch")
		public void execute(Listener listener, Event event) {
			switch(event.getType()) {
			case BLOCK_BREAK:
				((MagicDamageListener)listener).onBlockBreak((BlockBreakEvent)event);
				break;
			case ENTITY_DAMAGE:
				((MagicDamageListener)listener).onEntityDamage((EntityDamageEvent)event);
				break;
			}
		}
	};
	private MagicCarpet plugin;
	
	public MagicDamageListener(MagicCarpet plug) {
		plugin = plug;
	}
	
	//When a player joins the game, if they had a carpet when the logged out it puts it back.
	public void onBlockBreak(BlockBreakEvent event) {
		for(Carpet carpet : plugin.carpets.all()){
			if(carpet == null || !carpet.isVisible()) continue;
			if(carpet.isCovering(event.getBlock())) event.setCancelled(true);
		}
	}
	
	// Prevent carpets from suffocating players (and mobs too!)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getCause() != DamageCause.SUFFOCATION) return;
		if(!(event.getEntity() instanceof LivingEntity)) return;
		Block block = ((LivingEntity)event.getEntity()).getEyeLocation().getBlock();
		for(Carpet carpet : plugin.carpets.all()){
			if(carpet == null || !carpet.isVisible()) continue;
			if(carpet.isCovering(block)) event.setCancelled(true);
		}
	}
}
