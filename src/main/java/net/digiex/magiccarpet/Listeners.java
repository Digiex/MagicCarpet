package net.digiex.magiccarpet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

/*
 * Magic Carpet 2.4 Copyright (C) 2012-2014 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
public class Listeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (Storage.has(player))
            new Carpet(player).show();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Storage.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Carpet carpet = Storage.getCarpet(player);
        if (carpet == null || !carpet.isVisible())
            return;
        Location to = event.getTo();
        final Location from = event.getFrom();
        if (from.getWorld() == to.getWorld() && from.distance(to) == 0)
            return;
        if (player.getLocation().getBlock().isLiquid() && !player.getEyeLocation().getBlock().isLiquid() && to.getY() > from.getY())
            player.setVelocity(player.getVelocity().add(new Vector(0, 0.2, 0)));
        to = to.clone();
        if (player.isSneaking()) {
            if (!carpet.isDescending())
                to.setY(to.getY() - 1);
            carpet.setDescending(true);
        }
        if (from.getY() > to.getY() && !carpet.isDescending())
            to.setY(from.getY());
        carpet.moveTo(to);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Carpet carpet = Storage.getCarpet(event.getPlayer());
        if (carpet == null || !carpet.isVisible())
            return;
        carpet.moveTo(event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
        final Carpet carpet = Storage.getCarpet(event.getPlayer());
        if (carpet == null || !carpet.isVisible())
            return;
        if (event.isSneaking())
            carpet.descend();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(final PlayerKickEvent event) {
        final Player who = event.getPlayer();
        final Carpet carpet = Storage.getCarpet(who);
        if (carpet != null && carpet.isVisible()) {
            final String reason = event.getReason();
            if (reason != null && reason.equals("Flying is not enabled on this server") && who.isSneaking())
                event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(final BlockPhysicsEvent event) {
        final Block block = event.getBlock();
        switch (block.getType()) {
        case SAND:
            break;
        case CACTUS:
            break;
        case VINE:
            break;
        default:
            return;
        }
        for (final BlockFace face : BlockFace.values())
            if (block.getRelative(face).hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockForm(final BlockFormEvent event) {
        if (event.getBlock().getRelative(BlockFace.DOWN).hasMetadata("Carpet"))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.getBlock().hasMetadata("Carpet"))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        for (final BlockFace face : BlockFace.values())
            if (event.getBlock().getRelative(face).hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
        for (final Block block : event.getBlocks())
            if (block.hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (event.isSticky())
            for (final Block block : event.getBlocks())
                if (block.hasMetadata("Carpet")) {
                    event.setCancelled(true);
                    return;
                }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity))
            return;
        switch (event.getCause()) {
        case SUFFOCATION:
            for (final BlockFace face : BlockFace.values())
                if (event.getEntity().getLocation().getBlock().getRelative(face).hasMetadata("Carpet")) {
                    event.setCancelled(true);
                    return;
                }
        case FALL:
            if (!(event.getEntity() instanceof Player))
                return;
            final Carpet c = Storage.getCarpet((Player) event.getEntity());
            if (c == null)
                return;
            if (c.isVisible() || c.isFalling()) {
                event.setCancelled(true);
                c.setFalling(false);
            }
        case PROJECTILE:
        case ENTITY_EXPLOSION:
        case ENTITY_ATTACK:
            if (!(event instanceof EntityDamageByEntityEvent))
                return;
            final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if (e.getEntity() instanceof Player) {
                final Player player = (Player) e.getEntity();
                final Carpet carpet = Storage.getCarpet(player);
                if (carpet != null && carpet.isVisible()) {
                    carpet.hide();
                    player.sendMessage("The carpet cannot be used while in PVP/PVE combat.");
                    event.setCancelled(true);
                }
            }
            if (e.getDamager() instanceof Player) {
                final Player player = (Player) e.getDamager();
                final Carpet carpet = Storage.getCarpet(player);
                if (carpet != null && carpet.isVisible()) {
                    carpet.hide();
                    player.sendMessage("The carpet cannot be used while in PVP/PVE combat.");
                    event.setCancelled(true);
                }
            }
        default:
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        for (final Block block : event.blockList())
            if (block.hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHangingingBreak(final HangingBreakEvent event) {
        for (final BlockFace face : BlockFace.values())
            if (event.getEntity().getLocation().getBlock().getRelative(face).hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
    }
}
