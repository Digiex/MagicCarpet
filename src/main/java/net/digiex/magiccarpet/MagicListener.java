package net.digiex.magiccarpet;

import static java.lang.Math.abs;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.material.Redstone;
import org.bukkit.util.Vector;

/*
 * Magic Carpet 2.1 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
public class MagicListener implements Listener {

    private MagicCarpet plugin;
    private boolean falling = false;

    public MagicListener(MagicCarpet instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.carpets.has(player)) {
            Carpet.create(player, plugin).show();
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.carpets.remove(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        Player who = event.getPlayer();
        Carpet carpet = plugin.carpets.get(who);
        if (carpet != null && carpet.isVisible()) {
            String reason = event.getReason();
            if (reason != null && reason.equals("Flying is not enabled on this server") && who.isSneaking()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
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
        Location to = event.getTo();
        Location from = event.getFrom();
        if (player.getLocation().getBlock().isLiquid()
                && !player.getEyeLocation().getBlock().isLiquid()
                && to.getY() > from.getY()) {
            player.setVelocity(player.getVelocity().add(new Vector(0, 0.1, 0)));
        }
        /*if (from.getX() > to.getX()) {
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
        }*/
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
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Carpet carpet = plugin.carpets.get(player);
        if (carpet == null || !carpet.isVisible()) {
            return;
        }
        Location to = event.getTo();
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
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
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        for (Carpet carpet : plugin.carpets.all()) {
            if (carpet == null || !carpet.isVisible() || !carpet.hasLights()) {
                continue;
            }
            if (carpet.touches(event.getBlock())) {
                event.setCancelled(true);
                return;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        for (Carpet carpet : plugin.carpets.all()) {
            if (carpet == null || !carpet.isVisible()) {
                continue;
            }
            if (carpet.touches(event.getBlock())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        for (Carpet carpet : plugin.carpets.all()) {
            if (carpet == null || !carpet.isVisible()) {
                continue;
            }
            if (carpet.isCovering(event.getBlock())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.getChangedType().getNewData((byte) 0) instanceof Redstone) {
            return;
        }
        for (Carpet carpet : plugin.carpets.all()) {
            if (carpet == null || !carpet.isVisible()) {
                continue;
            }
            if (carpet.touches(event.getBlock())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Carpet carpet : plugin.carpets.all()) {
            if (carpet == null || !carpet.isVisible()) {
                continue;
            }
            for (Block block : event.getBlocks()) {
                if (carpet.isCovering(block)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.isSticky()) {
            for (Carpet carpet : plugin.carpets.all()) {
                if (carpet == null || !carpet.isVisible()) {
                    continue;
                }
                if (carpet.isCovering(event.getRetractLocation().getBlock())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            if (!(event.getEntity() instanceof LivingEntity)) {
                return;
            }
            Block eyes = ((LivingEntity) event.getEntity()).getEyeLocation().getBlock();
            for (Carpet carpet : plugin.carpets.all()) {
                if (carpet == null || !carpet.isVisible()) {
                    continue;
                }
                if (carpet.isCovering(eyes)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }
            if (plugin.carpets.has((Player) event.getEntity())) {
                Carpet c = plugin.carpets.get((Player) event.getEntity());
                if (c.isVisible()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}