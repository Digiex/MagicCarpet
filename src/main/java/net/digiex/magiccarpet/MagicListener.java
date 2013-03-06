package net.digiex.magiccarpet;

import org.bukkit.Location;
import org.bukkit.block.Block;
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
 * Magic Carpet 3.0 Copyright (C) 2012-2013 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

	MagicListener(MagicCarpet plugin) {
		this.plugin = plugin;
	}

	@EventHandler()
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (MagicCarpet.getCarpets().has(player)) {
			Carpet.create(player, plugin).show();
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		MagicCarpet.getCarpets().remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		Player who = event.getPlayer();
		Carpet carpet = MagicCarpet.getCarpets().getCarpet(who);
		if (carpet != null && carpet.isVisible()) {
			String reason = event.getReason();
			if (reason != null
					&& reason.equals("Flying is not enabled on this server")
					&& who.isSneaking()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
		if (carpet == null || !carpet.isVisible()) {
			return;
		}
		if (carpet.getLocation() == event.getTo()) {
			return;
		}
		Location to = event.getTo().clone();
		Location from = event.getFrom();
		if (player.getLocation().getBlock().isLiquid()
				&& !player.getEyeLocation().getBlock().isLiquid()
				&& to.getY() > from.getY()) {
			player.setVelocity(player.getVelocity().add(new Vector(0, 0.1, 0)));
		}
		if (MagicCarpet.getCarpets().crouches(player)) {
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
		Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
		if (carpet == null || !carpet.isVisible()) {
			return;
		}
		Location to = event.getTo();
		if (carpet.getLocation() == to) {
			return;
		}
		carpet.moveTo(to);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
		if (carpet == null || !carpet.isVisible()) {
			return;
		}
		if (!MagicCarpet.getCarpets().crouches(player)) {
			return;
		}
		if (event.isSneaking()) {
			falling = true;
			carpet.descend();
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent event) {
		for (Carpet carpet : MagicCarpet.getCarpets().all()) {
			if (carpet == null || !carpet.isVisible() || !carpet.hasLight()) {
				continue;
			}
			if (carpet.touches(event.getBlock())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent event) {
		for (Carpet carpet : MagicCarpet.getCarpets().all()) {
			if (carpet == null || !carpet.isVisible()) {
				continue;
			}
			if (carpet.touches(event.getBlock())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().hasMetadata("Carpet")) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		switch (event.getBlock().getType()) {
		case REDSTONE:
			return;
		case DIODE_BLOCK_ON:
			return;
		case DIODE_BLOCK_OFF:
			return;
		case REDSTONE_WIRE:
			return;
		case REDSTONE_LAMP_ON:
			return;
		case REDSTONE_LAMP_OFF:
			return;
		case RAILS:
			return;
		case POWERED_RAIL:
			return;
		case DETECTOR_RAIL:
			return;
		case TORCH:
			return;
		case REDSTONE_TORCH_ON:
			return;
		case REDSTONE_TORCH_OFF:
			return;
		case LEVER:
			return;
		default:
			break;
		}
		for (Carpet carpet : MagicCarpet.getCarpets().all()) {
			if (carpet == null || !carpet.isVisible()) {
				continue;
			}
			if (carpet.touches(event.getBlock())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		for (Block block : event.getBlocks()) {
			if (block.hasMetadata("Carpet")) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (event.isSticky()) {
			if (event.getRetractLocation().getBlock().hasMetadata("Carpet")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
			if (!(event.getEntity() instanceof LivingEntity)) {
				return;
			}
			Block eyes = ((LivingEntity) event.getEntity()).getEyeLocation()
					.getBlock();
			if (eyes.hasMetadata("Carpet")) {
				event.setCancelled(true);
			}
		} else if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			if (!(event.getEntity() instanceof Player)) {
				return;
			}
			if (MagicCarpet.getCarpets().has((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		for (Block block : event.blockList()) {
			if (block.hasMetadata("Carpet")) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingingBreak(HangingBreakEvent event) {
		for (Carpet carpet : MagicCarpet.getCarpets().all()) {
			if (carpet == null || !carpet.isVisible()) {
				continue;
			}
			if (carpet.touches(event.getEntity().getLocation().getBlock())) {
				event.setCancelled(true);
				return;
			}
		}
	}
}