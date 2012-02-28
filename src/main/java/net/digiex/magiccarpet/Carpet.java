package net.digiex.magiccarpet;

import static java.lang.Math.abs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

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
public class Carpet {

    private class CarpetFibre {

        BlockState block;
        int dx, dy, dz;
        Material strand;

        @SuppressWarnings("hiding")
        public CarpetFibre(int dx, int dy, int dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        void set(Block bl, Material material) {
            bl.setTypeId(material.getId(), false);
            strand = material;

        }

        boolean shouldGlow() {
            if (!lightsOn) {
                return false;
            }
            if (dx == 0 && dz == 0) {
                return true;
            }
            return false;
        }

        void update() {
            if (block.getBlock().getType() != strand) {
                return;
            }
            block.update(true);
        }
    }
    private static MagicCarpet p;
    private static int defaultSize, maxSize;

    public static Carpet create(Player player, MagicCarpet plugin) {
        p = plugin;
        defaultSize = plugin.carpSize;
        maxSize = plugin.maxCarpSize;
        int sz = plugin.carpets.getLastSize(player);
        boolean light = plugin.carpets.hasLight(player);
        Material thread = plugin.carpets.getMaterial(player);
        Material shine = plugin.carpets.getLightMaterial(player);
        Carpet carpet = new Carpet(player, sz, light, thread, shine);
        plugin.carpets.assign(player, carpet);
        return carpet;
    }
    private Block currentCentre;
    private int edge = 0, area = 0, rad = 0, radplsq = 0;
    private CarpetFibre[] fibres;
    private boolean hidden;
    private boolean lightsOn;
    private Material thread, shine;
    private Player who;

    private Carpet(Player player, int sz, boolean on, Material mat, Material light) {
        setSize(sz);
        who = player;
        currentCentre = player.getLocation().getBlock();
        lightsOn = on;
        hidden = true;
        thread = mat;
        shine = light;
    }

    public void changeCarpet(int sz) {
        removeCarpet();
        setSize(sz);
        drawCarpet();
    }

    public void changeCarpet(Material material) {
        removeCarpet();
        thread = material;
        drawCarpet();
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

    public Material getShine() {
        return shine;
    }

    public int getSize() {
        return edge;
    }

    public Material getThread() {
        return thread;
    }

    public boolean hasLights() {
        return lightsOn;
    }

    public void hide() {
        if (!hidden) {
            removeCarpet();
            hidden = true;
        }
    }

    public boolean isCovering(Block block) {
        if (currentCentre == null || block == null) {
            return false;
        }
        if (block.getLocation().getWorld() != getLocation().getWorld()) {
            return false;
        }
        if (block.getLocation().distanceSquared(getLocation()) > radplsq) {
            return false;
        }
        for (CarpetFibre fibre : fibres) {
            if (fibre.block == null) {
                continue;
            }
            if (fibre.block.getBlock().equals(block)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCustom() {
        if (getThread() != p.carpMaterial || getShine() != p.lightMaterial) {
            return true;
        }
        return false;
    }

    public boolean isVisible() {
        return !hidden;
    }

    public void lightsOff() {
        removeCarpet();
        lightsOn = false;
        drawCarpet();
    }

    public void lightsOn() {
        removeCarpet();
        lightsOn = true;
        drawCarpet();
    }

    public void moveTo(Location to) {
        removeCarpet();
        currentCentre = to.getBlock();
        drawCarpet();
    }

    public void setLights(Material material) {
        removeCarpet();
        shine = material;
        drawCarpet();
    }

    public void show() {
        if (hidden) {
            currentCentre = getPlayer().getLocation().getBlock();
            drawCarpet();
        }
    }

    public boolean touches(Block block) {
        if (currentCentre == null || block == null) {
            return false;
        }
        if (block.getLocation().getWorld() != getLocation().getWorld()) {
            return false;
        }
        if (block.getLocation().distanceSquared(getLocation()) > radplsq) {
            return false;
        }
        return true;
    }

    private void drawCarpet() {
        hidden = false;
        Block bl;
        for (CarpetFibre fibre : fibres) {
            if (currentCentre != null) {
                bl = currentCentre.getRelative(fibre.dx, fibre.dy, fibre.dz);
                Material type = bl.getType();
                if (!canReplace(type)) {
                    fibre.block = null;
                    continue;
                }
                fibre.block = bl.getState();
                if (fibre.shouldGlow() && p.allowWaterLight && shouldLightWater(bl)) {
                    fibre.set(bl, getShine());
                } else if (fibre.shouldGlow() && p.allowCustomLight && shouldLightCustom()) {
                    fibre.set(bl, getShine());
                } else if (fibre.shouldGlow() && !p.allowWaterLight && shouldLightWater(bl)) {
                    fibre.set(bl, getThread());
                } else if (fibre.shouldGlow() && !p.allowCustomLight && !shouldLightCustom()) {
                    fibre.set(bl, getThread());
                } else {
                    if (fibre.shouldGlow()) {
                        fibre.set(bl, getShine());
                    } else {
                        fibre.set(bl, getThread());
                    }
                }
            }
        }
    }
    
    private boolean canReplace(Material type) {
        switch (type) {
            case AIR:
                return true;
            case WATER:
                return true;
            case STATIONARY_WATER:
                return true;
            case LAVA:
                return true;
            case STATIONARY_LAVA:
                return true;
            case SNOW:
                return true;
            case LONG_GRASS:
                return true;
            case DEAD_BUSH:
                return true;
            case WATER_LILY:
                return true;
            case RED_ROSE:
                return true;
            case YELLOW_FLOWER:
                return true;
            case BROWN_MUSHROOM:
                return true;
            case RED_MUSHROOM:
                return true;
            default:
                return false;
        }
    }

    private void removeCarpet() {
        if (currentCentre == null) {
            return;
        }
        for (CarpetFibre fibre : fibres) {
            if (fibre.block != null) {
                fibre.update();
            }
            fibre.block = null;
        }
    }

    private void setSize(int size) {
        if (size < 0) {
            size = abs(size);
        } else if (size > maxSize) {
            size = defaultSize;
        }
        edge = size;
        area = size * size;
        fibres = new CarpetFibre[area];
        rad = (size - 1) / 2;
        radplsq = (rad + 1) * (rad + 1) * 2;
        int i = 0;
        for (int x = -rad; x <= rad; x++) {
            for (int z = -rad; z <= rad; z++) {
                fibres[i] = new CarpetFibre(x, -1, z);
                i++;
            }
        }
    }

    private boolean shouldLightWater(Block b) {
        if (touches(b) && b.isLiquid()) {
            return true;
        }
        return false;
    }

    private boolean shouldLightCustom() {
        if (getThread() == Material.GLASS || getThread() == Material.LEAVES) {
            return true;
        }
        return false;
    }

    public void checkCarpet() {
        if (getPlayer().isOnline() && p.carpets.has(getPlayer())) {
            removeCarpet();
            if (getSize() > p.maxCarpSize) {
                setSize(p.carpSize);
            }
            if (isCustom() && !p.customCarpets) {
                thread = p.carpMaterial;
                shine = p.lightMaterial;
            }
            drawCarpet();
        }
        p.carpets.update(getPlayer());
    }
}
