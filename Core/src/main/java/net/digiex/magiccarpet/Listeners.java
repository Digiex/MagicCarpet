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
import org.bukkit.event.block.BlockFadeEvent;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (MagicCarpet.getCarpets().has(player))
            new Carpet(player).show();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        MagicCarpet.getCarpets().remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerKick(final PlayerKickEvent event) {
        final Player who = event.getPlayer();
        final Carpet carpet = MagicCarpet.getCarpets().getCarpet(who);
        if (carpet != null && carpet.isVisible()) {
            final String reason = event.getReason();
            if (reason != null && reason.equals("Flying is not enabled on this server") && who.isSneaking())
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
        if (carpet == null || !carpet.isVisible())
            return;
        Location to = event.getTo();
        final Location from = event.getFrom();
        if (from.getWorld() == to.getWorld() && from.distance(to) == 0)
            return;
        if (player.getLocation().getBlock().isLiquid() && !player.getEyeLocation().getBlock().isLiquid() && to.getY() > from.getY())
            player.setVelocity(player.getVelocity().add(new Vector(0, 0.1, 0)));
        to = to.clone();
        if (MagicCarpet.getCarpets().crouches(player)) {
            if (player.isSneaking()) {
                if (!carpet.isDescending())
                    to.setY(to.getY() - 1);
                carpet.setDescending(true);
            }
        } else if (from.getPitch() == 90 && (to.getX() != from.getX() || to.getZ() != from.getZ())) {
            if (!carpet.isDescending())
                to.setY(to.getY() - 1);
            carpet.setDescending(true);
        }
        if (from.getY() > to.getY() && !carpet.isDescending())
            to.setY(from.getY());
        carpet.moveTo(to);
        carpet.setDescending(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
        if (carpet == null || !carpet.isVisible())
            return;
        carpet.moveTo(event.getTo());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        final Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
        if (carpet == null || !carpet.isVisible())
            return;
        if (!MagicCarpet.getCarpets().crouches(player))
            return;
        if (event.isSneaking()) {
            carpet.setDescending(true);
            carpet.descend();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFade(final BlockFadeEvent event) {
        for (final Carpet carpet : MagicCarpet.getCarpets().all()) {
            if (carpet == null || !carpet.isVisible() || !carpet.hasLight())
                continue;
            if (carpet.touches(event.getBlock())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockForm(final BlockFormEvent event) {
        final Block block = event.getBlock().getRelative(BlockFace.DOWN);
        if (block.hasMetadata("Carpet"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.getBlock().hasMetadata("Carpet"))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(final BlockPhysicsEvent event) {
        final Block block = event.getBlock();
        if (!Config.getPhysics())
            switch (block.getType()) {
            case SAND:
                break;
            case CACTUS:
                break;
            case VINE:
                break;
            case STATIONARY_WATER:
                break;
            case WATER:
                break;
            case STATIONARY_LAVA:
                break;
            case LAVA:
                break;
            case WATER_LILY:
                break;
            default:
                return;
            }
        else
            switch (block.getType()) {
            case REDSTONE:
                return;
            case REDSTONE_WIRE:
                return;
            case DIODE_BLOCK_ON:
                return;
            case REDSTONE_LAMP_ON:
                return;
            case POWERED_RAIL:
                return;
            case REDSTONE_TORCH_ON:
                return;
            case DIODE_BLOCK_OFF:
                return;
            case REDSTONE_LAMP_OFF:
                return;
            case REDSTONE_TORCH_OFF:
                return;
            default:
                break;
            }
        for (final BlockFace face : BlockFace.values())
            if (block.getRelative(face).hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        for (final Block block : event.getBlocks())
            if (block.hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
        for (final BlockFace face : BlockFace.values()) {
            final Block block = event.getBlock().getRelative(face);
            if (block.hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (event.isSticky())
            if (event.getRetractLocation().getBlock().hasMetadata("Carpet"))
                event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        switch (event.getCause()) {
        case SUFFOCATION:
            if (!(event.getEntity() instanceof LivingEntity))
                return;
            for (final BlockFace face : BlockFace.values()) {
                final Block block = event.getEntity().getLocation().getBlock().getRelative(face);
                if (block.hasMetadata("Carpet")) {
                    event.setCancelled(true);
                    return;
                }
            }
        case FALL:
            if (!(event.getEntity() instanceof Player))
                return;
            final Carpet c = MagicCarpet.getCarpets().getCarpet((Player) event.getEntity());
            if (c == null)
                return;
            if (c.isVisible() || c.isFalling()) {
                event.setCancelled(true);
                c.setFalling(false);
            }
        case PROJECTILE:
        case ENTITY_ATTACK:
            if (Config.getPvp())
                return;
            if (!(event instanceof EntityDamageByEntityEvent))
                return;
            final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if (e.getEntity() instanceof Player) {
                final Player player = (Player) e.getEntity();
                final Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
                if (carpet != null && carpet.isVisible()) {
                    if (Config.getPVPHide()) {
                        carpet.hide();
                        player.sendMessage("The carpet cannot be used while in PVP/PVE combat.");
                    }
                    event.setCancelled(true);
                }
            }
            if (e.getDamager() instanceof Player) {
                final Player player = (Player) e.getDamager();
                final Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
                if (carpet != null && carpet.isVisible()) {
                    if (Config.getPVPHide()) {
                        carpet.hide();
                        player.sendMessage("The carpet cannot be used while in PVP/PVE combat.");
                    }
                    event.setCancelled(true);
                }
            }
        default:
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event) {
        for (final Block block : event.blockList())
            if (block.hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHangingingBreak(final HangingBreakEvent event) {
        for (final BlockFace face : BlockFace.values()) {
            final Block block = event.getEntity().getLocation().getBlock().getRelative(face);
            if (block.hasMetadata("Carpet")) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
