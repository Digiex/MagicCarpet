package net.digiex.magiccarpet;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

/*
 * Magic Carpet 2.0
 * Copyright (C) 2011 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

    private MagicCarpet plugin = null;
    private boolean falling = false;

    public MagicPlayerListener(MagicCarpet plug) {
        plugin = plug;
    }

    @Override
    //When a player joins the game, if they had a carpet when they logged out it puts it back.
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.carpets.has(player)) {
            Carpet.create(player, plugin).show();
        }
    }

    @Override
    //Don't allow kicking for flying while descending
    public void onPlayerKick(PlayerKickEvent event) {
        // TODO: This is hacky and likely won't work in all cases
        Player who = event.getPlayer();
        Carpet carpet = plugin.carpets.get(who);
        if (carpet != null) {
            String reason = event.getReason();
            if (reason != null && reason.equals("Flying is not enabled on this server") && who.isSneaking()) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    //When a player quits, it removes the carpet from the server
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.carpets.remove(player);
    }

    @Override
    //Lets the carpet move with the player
    public void onPlayerMove(PlayerMoveEvent event) {
        Location to = event.getTo().clone();
        Location from = event.getFrom().clone();
        Player player = event.getPlayer();
        Carpet carpet = plugin.carpets.get(player);
        if (carpet == null || !carpet.isVisible()) {
            return;
        }
        if (!plugin.canFly(player)) {
            carpet.suppress();
            return;
        }
        //to.setY(to.getY()-1);
        //from.setY(from.getY()-1);

        if (player.getLocation().getBlock().isLiquid() && !player.getEyeLocation().getBlock().isLiquid()
                && to.getY() > from.getY()) {
            player.setVelocity(player.getVelocity().add(new Vector(0, 0.1, 0)));
        }

        // FIXME: hacky fix from andrew http://forums.bukkit.org/posts/348324
        if (from.getX() > to.getX()) {
            to.setX(to.getX() - .5);
            from.setX(from.getX() - .5);
        } else {
            to.setX(to.getX() + .5);
            from.setX(from.getX() + .5);
        }
        if (from.getZ() > to.getZ()) {
            to.setZ(to.getZ() - .5);
            from.setZ(from.getZ() - .5);
        } else {
            to.setZ(to.getZ() + .5);
            from.setZ(from.getZ() + .5);
        }
        //</andrew>

        if (plugin.carpets.crouches(player)) {
            if (player.isSneaking()) {
                if (!falling) {
                    to.setY(to.getY() - 1);
                }
                falling = true;
            }
        } else {
            if (from.getPitch() == 90 && (to.getX() != from.getX() || to.getZ() != from.getZ())) {
                if (!falling) {
                    to.setY(to.getY() - 1);
                }
                falling = true;
            }
        }

        if (from.getY() > to.getY() && !falling) {
            to.setY(from.getY());
        }
        falling = false;
        carpet.moveTo(to);
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location to = event.getTo().clone();
        Player player = event.getPlayer();
        // Check if the player has a carpet
        Carpet carpet = plugin.carpets.get(player);
        if (carpet == null || !carpet.isVisible()) {
            return;
        }
        if (!plugin.canFly(player)) {
            carpet.suppress();
            return;
        }
        // Check if the player moved 1 block
        //to.setY(to.getY()-1);
        Location last = carpet.getLocation();
        if (last.getBlockX() == to.getBlockX()
                && last.getBlockY() == to.getBlockY()
                && last.getBlockZ() == to.getBlockZ()) {
            return;
        }

        // Move the carpet
        carpet.moveTo(to);
    }

    @Override
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        // Check if the player has a carpet
        Carpet carpet = plugin.carpets.get(player);
        if (carpet == null || !carpet.isVisible()) {
            return;
        }
        if (!plugin.carpets.crouches(player)) {
            return;
        }
        if (event.isSneaking()) {
            falling = true;
            carpet.descend();
        }
    }
}