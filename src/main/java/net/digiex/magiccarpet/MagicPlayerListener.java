package net.digiex.magiccarpet;

import static java.lang.Math.abs;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

/*
 * Magic Carpet 2.0 Copyright (C) 2011 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
public class MagicPlayerListener implements Listener {

    private MagicCarpet plugin;
    private boolean falling = false;

    public MagicPlayerListener(MagicCarpet plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.carpets.has(player)) {
            Carpet.create(player, plugin).show();
            Carpet c = plugin.carpets.get(player);
            c.checkCarpet();
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player who = event.getPlayer();
        Carpet carpet = plugin.carpets.get(who);
        if (carpet != null && carpet.isVisible()) {
            String reason = event.getReason();
            if (reason != null && reason.equals("Flying is not enabled on this server") && who.isSneaking()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Location to = event.getTo().clone();
        Location from = event.getFrom().clone();
        Player player = event.getPlayer();
        Carpet carpet = plugin.carpets.get(player);
        if (carpet == null || !carpet.isVisible()) {
            return;
        }
        if (!plugin.canFly(player)) {
            carpet.hide();
            return;
        }
        if (!plugin.canFlyAt(player, carpet.getSize())) {
            carpet.changeCarpet(plugin.carpSize);
            plugin.carpets.update(player);
        }

        if (player.getLocation().getBlock().isLiquid()
                && !player.getEyeLocation().getBlock().isLiquid()
                && to.getY() > from.getY()) {
            player.setVelocity(player.getVelocity().add(new Vector(0, 0.1, 0)));
        }
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
        if (plugin.carpets.crouches(player)) {
            if (player.isSneaking()) {
                if (!falling) {
                    to.setY(to.getY() - 1);
                }
                falling = true;
            }
        } else {
            if (from.getPitch() == 90
                    && (to.getX() != from.getX() || to.getZ() != from.getZ())) {
                if (!falling) {
                    to.setY(to.getY() - 1);
                }
                falling = true;
            }
        }

        if (from.getY() > to.getY() && !falling) {
            to.setY(from.getY());
        }
        carpet.moveTo(to);
        falling = false;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.carpets.remove(player);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Location to = event.getTo().clone();
        Player player = event.getPlayer();
        Carpet carpet = plugin.carpets.get(player);
        if (carpet == null || !carpet.isVisible()) {
            return;
        }
        Location last = carpet.getLocation();
        if (last.getBlockX() == to.getBlockX()
                && last.getBlockY() == to.getBlockY()
                && last.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (!plugin.canFly(player)) {
            carpet.hide();
            return;
        }
        if (!plugin.canTeleFly(player) && falling == false) {
            if (to.getWorld().equals(carpet.getLocation().getWorld()) && abs(to.getY() - carpet.getLocation().getY()) < 2) {
                return;
            }
            player.sendMessage("Your carpet cannot follow you there!");
            carpet.hide();
            return;
        }
        carpet.moveTo(to);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
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