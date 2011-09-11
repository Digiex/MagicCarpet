package net.digiex.magiccarpet;

import org.bukkit.block.Block;
import org.bukkit.Material;

/**
 * Magic Carpet 1.5
 * Copyright (C) 2011 Android <spparr@gmail.com>
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
/**
 * Carpet.java
 * <br /><br />
 * Defines the basic 5x5 carpet object that is placed underneath the player.
 *
 *
 * @author Android <spparr@gmail.com>
 */
public class Carpet {

    public class CarpetFiber {

        public CarpetFiber(int x, int y, int z, int type, boolean torch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
            this.torch = torch;
        }
        int x, y, z, type = 0;
        boolean torch = false;
        Block block = null;
    }

    private MagicCarpet plugin;
    Block currentBlock;
    int size = 0;
    int rad = 0;
    boolean lights = false;
    boolean glowCenter = false;

    public Carpet(MagicCarpet plugin, boolean cent) {
        this.plugin = plugin;
        setSize(5);
        glowCenter = cent;
    }
    public CarpetFiber[] fibers;

    public void removeCarpet() {
        Block bl;
        if (currentBlock == null) {
            return;
        }
        for (int i = 0; i < fibers.length; i++) {
            bl = fibers[i].block;
            if (fibers[i].block != null && (fibers[i].block.getType().equals(plugin.carpMaterial) || fibers[i].block.getType().equals(plugin.lightMaterial))) {
                bl.setType(Material.AIR);
            }
            fibers[i].block = null;
        }
    }

    public void drawCarpet() {
        Block bl;
        for (int i = 0; i < fibers.length; i++) {
            if (currentBlock != null) {
                bl = currentBlock.getRelative(fibers[i].x, fibers[i].y, fibers[i].z);
                if (bl.getTypeId() == 0
                        && bl.getRelative(-1, 0, 0).getTypeId() != 81
                        && bl.getRelative(1, 0, 0).getTypeId() != 81
                        && bl.getRelative(0, 0, -1).getTypeId() != 81
                        && bl.getRelative(0, 0, 1).getTypeId() != 81) {
                    fibers[i].block = bl;
                    if (lights) {
                        if (!glowCenter) {
                            if (fibers[i].x == rad || fibers[i].x == -rad || fibers[i].z == rad || fibers[i].z == -rad) {
                                bl.setType(plugin.lightMaterial);
                            } else {
                                bl.setType(plugin.carpMaterial);
                            }
                        } else {
                            if (fibers[i].x == 0 && fibers[i].z == 0) {
                                bl.setType(plugin.lightMaterial);
                            } else {
                                bl.setType(plugin.carpMaterial);
                            }
                        }
                    } else {
                        bl.setType(plugin.carpMaterial);
                    }
                } else {
                    fibers[i].block = null;
                }
            }
        }
    }

    public void changeCarpet(int si) {
        removeCarpet();
        setSize(si);
        drawCarpet();
    }

    public void setLights(boolean li) {
        lights = li;
    }

    public boolean checkBlock(Block bl) {
        boolean sameBlock = false;
        for (int i = 0; i < fibers.length; i++) {
            Block fiber = fibers[i].block;
            if (fiber != null) {
                if (fiber.equals(bl)) {
                    sameBlock = true;
                }
            }

        }
        return sameBlock;
    }

    protected final void setSize(int size) {
        if (size < 0) {
            size -= size;
        }
        this.size = size;

        fibers = new CarpetFiber[size * size];
        switch (size) {
            case 3:
                size = 1;
                break;
            case 5:
                size = 2;
                break;
            case 7:
                size = 3;
                break;
            case 9:
                size = 4;
                break;
            case 11:
                size = 5;
                break;
            case 13:
                size = 6;
                break;
            case 15:
                size = 7;
                break;
            default:
                size = 2;
                break;
        }

        int i = 0;
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                fibers[i] = new CarpetFiber(x, 0, z, 20, false);
                i++;
            }
        }

        this.rad = size;
    }
}