package net.digiex.magiccarpet;

import static java.lang.Math.abs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

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
public class Carpet {

    private class CarpetFibre {

        BlockState block;
        int dx, dy, dz;

        public CarpetFibre(int dx, int dy, int dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        void set(Block bl, Material material) {
            bl.setTypeId(material.getId(), false);
            bl.setMetadata("Carpet", new FixedMetadataValue(p, carpet));
        }

        boolean shouldGlow() {
            if (!light) {
                return false;
            }
            if (light && !MagicCarpet.canLight(who)) {
            	lightOff();
            	who.sendMessage("The luminous stones in the carpet slowly fade away.");
            	MagicCarpet.getCarpets().update(who);
            	return false;
            }
            if (dx == 0 && dz == 0) {
                return true;
            }
            return false;
        }

        boolean shouldEnder() {
        	if (!tools) {
        		return false;
        	}
        	if (tools && !MagicCarpet.canTool(who)) {
        		toolsOff();
        		who.sendMessage("The magic tools suddenly disappeared.");
        		MagicCarpet.getCarpets().update(who);
            	return false;
            }
        	if (dx == 2 && dz == 0) {
        		return true;
        	}
        	return false;
        }
        
        boolean shouldWork() {
        	if (!tools) {
        		return false;
        	}
        	if (tools && !MagicCarpet.canTool(who)) {
        		toolsOff();
        		who.sendMessage("The magic tools suddenly disappeared.");
        		MagicCarpet.getCarpets().update(who);
            	return false;
            }
        	if (dx == -2 && dz == 0) {
        		return true;
        	}
        	return false;
        }

        void update() {
            if (block.getMetadata("Carpet").isEmpty()) {
                return;
            }
            block.removeMetadata("Carpet", p);
            block.update(true);
        }
    }
    private static MagicCarpet p;
    private Carpet carpet = this;

    public static Carpet create(Player player, MagicCarpet plugin) {
        p = plugin;
        int sz = MagicCarpet.getCarpets().getLastSize(player);
        boolean light = MagicCarpet.getCarpets().hasLight(player);
        Material thread = MagicCarpet.getCarpets().getMaterial(player);
        Material shine = MagicCarpet.getCarpets().getLightMaterial(player);
        boolean tools = MagicCarpet.getCarpets().hasTools(player);
        Carpet carpet = new Carpet(player, sz, light, thread, shine, tools);
        MagicCarpet.getCarpets().assign(player, carpet);
        return carpet;
    }
    private Block currentCentre;
    private int edge = 0, area = 0, rad = 0, radplsq = 0;
    private CarpetFibre[] fibres;
    private boolean hidden, light, tools;
    private Material thread, shine;
    private Player who;

    private Carpet(Player p, int s, boolean o, Material m, Material l, boolean t) {
        setSize(s);
        who = p;
        currentCentre = p.getLocation().getBlock();
        light = o;
        hidden = true;
        thread = m;
        shine = l;
        tools = t;
    }
    
    private void drawCarpet() {
    	if (!MagicCarpet.canFly(who)) {
            hide();
            who.sendMessage("You shout your command, but it falls on deaf ears. Nothing happens.");
            MagicCarpet.getCarpets().update(who);
            return;
        }
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
                if (fibre.shouldGlow()) {
                    fibre.set(bl, getShine());
                } else if (fibre.shouldEnder()) {
                	fibre.set(bl, Material.ENDER_CHEST);
                } else if (fibre.shouldWork()) {
                	fibre.set(bl, Material.WORKBENCH);
                } else {
                    fibre.set(bl, getThread());
                }
            }
        }
    }

    private boolean canReplace(Material type) {
        switch (type) {
            case AIR:
                return true;
            case WATER:
                return p.canChangeLiquids("water");
            case STATIONARY_WATER:
                return p.canChangeLiquids("water");
            case LAVA:
                return p.canChangeLiquids("lava");
            case STATIONARY_LAVA:
                return p.canChangeLiquids("lava");
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
            case VINE:
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

    public void hide() {
        if (!hidden) {
            removeCarpet();
            hidden = true;
        }
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
    
    public boolean hasLight() {
        return light;
    }

    public void lightOff() {
        removeCarpet();
        light = false;
        drawCarpet();
    }

    public void lightOn() {
        removeCarpet();
        light = true;
        drawCarpet();
    }

    public void moveTo(Location to) {
    	if (!p.canFlyHere(to)) {
            hide();
            who.sendMessage("Your carpet is forbidden in this area!");
            MagicCarpet.getCarpets().update(who);
            return;
        }
        removeCarpet();
        currentCentre = to.getBlock();
        drawCarpet();
    }

    public void setLight(Material material) {
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
    
    public boolean hasTools() {
        return tools;
    }
    
    public void toolsOff() {
        removeCarpet();
        tools = false;
        drawCarpet();
    }

    public void toolsOn() {
        removeCarpet();
        tools = true;
        drawCarpet();
    }
}
