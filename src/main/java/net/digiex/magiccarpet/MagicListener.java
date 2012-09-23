package net.digiex.magiccarpet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;
import org.bukkit.material.Redstone;
import org.bukkit.util.Vector;

/*
 * Magic Carpet 2.2 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

    private final MagicCarpet plugin;
    private boolean falling = false;

    public MagicListener(MagicCarpet plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.carpets.has(player)) {
            Carpet.create(player, plugin).show();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        Player who = event.getPlayer();
        Carpet carpet = plugin.carpets.getCarpet(who);
        if (carpet != null && carpet.isVisible()) {
            String reason = event.getReason();
            if (reason != null && reason.equals("Flying is not enabled on this server") && who.isSneaking()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Carpet carpet = plugin.carpets.getCarpet(player);
        if (carpet == null || !carpet.isVisible()) {
            return;
        }
        if (carpet.getLocation() == event.getTo()) {
            return;
        }
        if (!plugin.canFly(player)) {
            carpet.hide();
            plugin.carpets.update(player);
            return;
        }
        if (!plugin.canFlyAt(player, carpet.getSize())) {
            carpet.changeCarpet(plugin.carpSize);
            plugin.carpets.update(player);
        }
        if (!plugin.canFlyHere(player)) {
            player.sendMessage("Your carpet is forbidden in this area!");
            carpet.hide();
            plugin.carpets.update(player);
            return;
        }
        Location to = event.getTo().clone();
        Location from = event.getFrom();
        if (player.getLocation().getBlock().isLiquid()
                && !player.getEyeLocation().getBlock().isLiquid()
                && to.getY() > from.getY()) {
            player.setVelocity(player.getVelocity().add(new Vector(0, 0.1, 0)));
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Carpet carpet = plugin.carpets.getCarpet(player);
        if (carpet == null || !carpet.isVisible()) {
            return;
        }
        Location to = event.getTo();
        if (carpet.getLocation() == to) {
            return;
        }
        if (!plugin.canFly(player)) {
            carpet.hide();
            plugin.carpets.update(player);
            return;
        }
        if (!plugin.canFlyAt(player, carpet.getSize())) {
            carpet.changeCarpet(plugin.carpSize);
            plugin.carpets.update(player);
        }
        if (!plugin.canFlyHere(player)) {
            player.sendMessage("Your carpet is forbidden in this area!");
            carpet.hide();
            plugin.carpets.update(player);
            return;
        }
        carpet.moveTo(to);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Carpet carpet = plugin.carpets.getCarpet(player);
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
            if (carpet.isCarpet(event.getBlock())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block == null) {
            // Should fix a NPE
            return;
        }
        if (!block.getType().isBlock()) {
            // Hopes this fixes Tekkits custom blocks
            return;
        }
        if (event.getChangedType().getNewData((byte) 0) instanceof Redstone) {
            return;
        }
        switch(block.getType()) {
            // To prevent flying rails / torches and such. Hopefully,,,
            case TORCH:
                return;
            case REDSTONE_TORCH_ON:
                return;
            case REDSTONE_TORCH_OFF:
                return;
            case RAILS:
                return;
            default:
                break;
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
                if (carpet.isCarpet(block)) {
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
                if (carpet.isCarpet(event.getRetractLocation().getBlock())) {
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
                if (carpet.isCarpet(eyes)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }
            if (plugin.carpets.has((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Carpet carpet : plugin.carpets.all()) {
            if (carpet == null || !carpet.isVisible()) {
                continue;
            }
            for (Block block : event.blockList()) {
                if (carpet.isCarpet(block)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}