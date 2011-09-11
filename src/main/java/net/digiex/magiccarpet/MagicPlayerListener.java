package net.digiex.magiccarpet;

import java.util.Hashtable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
 * MagicPlayerListener.java
 * <br /><br />
 * Listens for calls for the magic carpet, makes a carpet when a player logs on, removes one when a player logs off,
 * and moves the carpet when the player moves.
 *
 * @author Android <spparr@gmail.com>
 */
public class MagicPlayerListener extends PlayerListener {

    private Hashtable<String, Carpet> carpets = new Hashtable<String, Carpet>();
    private MagicCarpet plugin = null;

    public MagicPlayerListener(MagicCarpet plug) {
        plugin = plug;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Carpet carpet = (Carpet) carpets.get(player.getName());
        if (carpet == null) {
            return;
        }
        carpet.drawCarpet();
    }
    
    @Override
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Carpet carpet = (Carpet) carpets.get(player.getName());
        if (carpet == null) {
            return;
        }
        carpet.removeCarpet();
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Carpet carpet = (Carpet) carpets.get(player.getName());
        if (carpet == null) {
            return;
        }
        carpet.removeCarpet();
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Location to = event.getTo().clone();
        Location from = event.getFrom().clone();
        Player player = event.getPlayer();
        Carpet carpet = (Carpet) carpets.get(player.getName());
        if (carpet == null) {
            return;
        }
        to.setY(to.getY() - 1);
        from.setY(from.getY() - 1);

        if (from.getY() > to.getY()) {
            to.setY(from.getY());
        }

        carpet.removeCarpet();
        if (plugin.canFly(player)) {
            carpet.currentBlock = to.getBlock();
            carpet.drawCarpet();
        } else {
            carpets.remove(player.getName());
        }
        
        if (player.isSneaking()) {
            carpet.removeCarpet();
            carpet.currentBlock = carpet.currentBlock.getRelative(0, -1, 0);
            carpet.drawCarpet();
        }
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location to = event.getTo().clone();
        Player player = event.getPlayer();
        Carpet carpet = (Carpet) carpets.get(player.getName());
        if (carpet == null) {
            return;
        }

        to.setY(to.getY() - 1);
        Location last = carpet.currentBlock.getLocation();
        if (last.getBlockX() == to.getBlockX()
                && last.getBlockY() == to.getBlockY()
                && last.getBlockZ() == to.getBlockZ()) {
            return;
        }

        carpet.removeCarpet();
        carpet.currentBlock = to.getBlock();
        carpet.drawCarpet();
    }

    public Hashtable<String, Carpet> getCarpets() {
        return carpets;
    }

    public void setCarpets(Hashtable<String, Carpet> carp) {
        carpets = carp;
    }
}