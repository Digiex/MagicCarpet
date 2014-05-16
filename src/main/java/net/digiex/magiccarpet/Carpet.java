package net.digiex.magiccarpet;

import static java.lang.Math.abs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

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
public class Carpet {

    private class CarpetFibre {

        BlockState block;
        int dx, dy, dz;

        CarpetFibre(final int dx, final int dy, final int dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        void set(final Block bl, final Material material) {
            bl.setMetadata("Carpet", new FixedMetadataValue(plugin, who.getUniqueId()));
            bl.setType(material);
        }

        void update() {
            if (!block.hasMetadata("Carpet"))
                return;
            block.update(true);
            block.removeMetadata("Carpet", plugin);
        }
    }

    private final MagicCarpet plugin = (MagicCarpet) Bukkit.getServer().getPluginManager().getPlugin("MagicCarpet");

    private Block currentCentre;
    private int area = 0, rad = 0, radplsq = 0;
    private CarpetFibre[] fibres;
    private boolean hidden, falling, descending;
    private final Player who;

    public Carpet(final Player player) {
        setSize(5);
        who = player;
        currentCentre = player.getLocation().getBlock();
        MagicCarpet.getCarpets().assign(player, this);
    }

    private void drawCarpet() {
        if (!MagicCarpet.canFly(who)) {
            hide();
            return;
        }
        for (final CarpetFibre fibre : fibres) {
            final Block bl = currentCentre.getRelative(fibre.dx, fibre.dy, fibre.dz);
            if (!canReplace(bl.getType())) {
                fibre.block = null;
                continue;
            }
            fibre.block = bl.getState();
            fibre.set(bl, Material.GLASS);
        }
    }

    private boolean canReplace(final Material type) {
        switch (type) {
        case AIR:
            return true;
        case SNOW:
            return true;
        case LONG_GRASS:
            return true;
        case DEAD_BUSH:
            return true;
        case RED_ROSE:
            return true;
        case YELLOW_FLOWER:
            return true;
        case BROWN_MUSHROOM:
            return true;
        case RED_MUSHROOM:
            return true;
        case VINE:
            return true;
        default:
            return false;
        }
    }

    private void setSize(int size) {
        if (size < 0)
            size = abs(size);
        area = size * size;
        fibres = new CarpetFibre[area];
        rad = (size - 1) / 2;
        radplsq = (rad + 1) * (rad + 1) * 2;
        int i = 0;
        for (int x = -rad; x <= rad; x++)
            for (int z = -rad; z <= rad; z++) {
                fibres[i] = new CarpetFibre(x, -1, z);
                i++;
            }
    }

    public void descend() {
        removeCarpet();
        currentCentre = currentCentre.getRelative(0, -1, 0);
        drawCarpet();
    }

    public Location getLocation() {
        return currentCentre.getLocation();
    }

    public Player getPlayer() {
        return who;
    }

    public void hide() {
        if (!hidden) {
            hidden = true;
            removeCarpet();
            MagicCarpet.getCarpets().update(who);
            who.sendMessage("Poof! The magic carpet disappears.");
            if (who.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
                falling = true;
        }
    }

    public boolean isVisible() {
        return !hidden;
    }

    public void moveTo(final Location to) {
        removeCarpet();
        currentCentre = to.getBlock();
        drawCarpet();
    }

    public void show() {
        if (hidden) {
            currentCentre = who.getLocation().getBlock();
            hidden = false;
            drawCarpet();
            MagicCarpet.getCarpets().update(who);
            who.sendMessage("Poof! The magic carpet appears below your feet!");
        }
    }

    public void removeCarpet() {
        for (final CarpetFibre fibre : fibres)
            if (fibre.block != null)
                fibre.update();
    }

    public boolean touches(final Block block) {
        if (currentCentre == null || block == null)
            return false;
        if (block.getLocation().getWorld() != getLocation().getWorld())
            return false;
        if (block.getLocation().distanceSquared(getLocation()) > radplsq)
            return false;
        return true;
    }

    public boolean isFalling() {
        return falling;
    }

    public void setFalling(final boolean falling) {
        this.falling = falling;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(final boolean descending) {
        this.descending = descending;
    }
}
