package net.digiex.magiccarpet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.material.Redstone;

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
public class MagicDamageListener implements Listener {

    private MagicCarpet plugin;

    public MagicDamageListener(MagicCarpet plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        for (Carpet carpet : plugin.carpets.all()) {
            if (carpet == null || !carpet.isVisible()) {
                continue;
            }
            if (carpet.isCovering(event.getBlock())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getChangedType().getNewData((byte) 0) instanceof Redstone) {
            return;
        }
        if (event.getBlock().getType().equals(Material.TORCH)) {
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

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled()) {
            return;
        }
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

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.isCancelled()) {
            return;
        }
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

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getCause() == DamageCause.SUFFOCATION) {
            if (!(event.getEntity() instanceof LivingEntity)) {
                return;
            }
            Block eyes = ((LivingEntity) event.getEntity()).getEyeLocation().getBlock();
            Block block = event.getEntity().getLocation().getBlock();
            if (plugin.carpets.has((Player) event.getEntity())) {
                Carpet c = plugin.carpets.get((Player) event.getEntity());
                if (c != null && c.isVisible()) {
                    if (c.touches(eyes)) {
                        event.setCancelled(true);
                    } else if (c.touches(block)) {
                        event.setCancelled(true);
                    }
                }
            }
        } else if (event.getCause() == DamageCause.FALL) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }
            if (plugin.carpets.has((Player) event.getEntity())) {
                Carpet c = plugin.carpets.get((Player) event.getEntity());
                if (c != null && c.isVisible()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
