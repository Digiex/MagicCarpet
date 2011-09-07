package com.Android.magiccarpet;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

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

public class MagicPlayerListener extends PlayerListener {
	private ArrayList<String> crouchers = new ArrayList<String>();
	private MagicCarpet plugin = null;
	boolean falling = false;
	
	public MagicPlayerListener(MagicCarpet plug){
		plugin = plug;
	}
		
	@Override
	//TODO:When a player joins the game, if they had a carpet when they logged out it puts it back.
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		//Carpet.create(player, plugin).show();
	}

	@Override
	//When a player quits, it removes the carpet from the server
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		plugin.carpets.remove(player.getName());
	}

	@Override
	//Lets the carpet move with the player
	public void onPlayerMove(PlayerMoveEvent event) {
		falling = false;
		Location to = event.getTo().clone();
		Location from = event.getFrom().clone();
		Player player = event.getPlayer();
		Carpet carpet = plugin.carpets.get(player.getName());
		if (carpet == null)
			return;
		if(!plugin.canFly(player)) {
			carpet.suppress();
			return;
		}
		//to.setY(to.getY()-1);
		//from.setY(from.getY()-1);
		if (!plugin.crouchDef){
			if(crouchers.contains(player.getName())){
				if(player.isSneaking()){
					to.setY(to.getY()-1);
					falling = true;
				}
			}else{
				if(from.getPitch() == 90 && (to.getX() != from.getX() || to.getZ() != from.getZ())){
					to.setY(to.getY()-1);
					falling = true;
				}
			}
		}else{
			if(crouchers.contains(player.getName())){
				if(from.getPitch() == 90 && (to.getX() != from.getX() || to.getZ() != from.getZ())){
					to.setY(to.getY()-1);
					falling = true;
				}
			}else{
				if(player.isSneaking()){
					to.setY(to.getY()-1);
					falling = true;
				}
			}
		}
		
		if (from.getY() > to.getY() && !falling) to.setY(from.getY());
		carpet.moveTo(to);
	}
	
	@Override
	public void onPlayerTeleport (PlayerTeleportEvent event) {
		Location to = event.getTo().clone();
		Player player = event.getPlayer();
		// Check if the player has a carpet
		Carpet carpet = plugin.carpets.get(player.getName());
		if (carpet == null)
			return;
	   
		// Check if the player moved 1 block
		to.setY(to.getY()-1);
		Location last = carpet.getLocation();
		if (last.getBlockX() == to.getBlockX() &&
			last.getBlockY() == to.getBlockY() &&
			last.getBlockZ() == to.getBlockZ())
				return;
	   
		// Move the carpet
		carpet.moveTo(to);		
	}
	
	@Override
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event){
		Player player = event.getPlayer();
		// Check if the player has a carpet
		Carpet carpet = plugin.carpets.get(player.getName());
		if (carpet == null)
			return;
		if(plugin.crouchDef){
			if(!crouchers.contains(player.getName())){
				if(!player.isSneaking()) carpet.descend();
			}
		} else {
			if(crouchers.contains(player.getName())){
				if(!player.isSneaking()) carpet.descend();
			}
		}
	}
	
	public boolean CarpetSwitch(String name){
		if(crouchers.contains(name)){
			crouchers.remove(name);
			return false;
		}else{
			crouchers.add(name);
			return true;
		}
	}
}