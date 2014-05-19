package net.digiex.magiccarpet.nms.v1_7_R1;

import static org.bukkit.Material.BEDROCK;
import static org.bukkit.Material.BOOKSHELF;
import static org.bukkit.Material.BRICK;
import static org.bukkit.Material.CLAY;
import static org.bukkit.Material.COAL_BLOCK;
import static org.bukkit.Material.COAL_ORE;
import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.DIAMOND_BLOCK;
import static org.bukkit.Material.DIAMOND_ORE;
import static org.bukkit.Material.DIRT;
import static org.bukkit.Material.DOUBLE_STEP;
import static org.bukkit.Material.EMERALD_BLOCK;
import static org.bukkit.Material.ENDER_STONE;
import static org.bukkit.Material.GLASS;
import static org.bukkit.Material.GLOWSTONE;
import static org.bukkit.Material.GOLD_BLOCK;
import static org.bukkit.Material.GOLD_ORE;
import static org.bukkit.Material.GRASS;
import static org.bukkit.Material.HARD_CLAY;
import static org.bukkit.Material.HUGE_MUSHROOM_1;
import static org.bukkit.Material.HUGE_MUSHROOM_2;
import static org.bukkit.Material.IRON_BLOCK;
import static org.bukkit.Material.IRON_ORE;
import static org.bukkit.Material.JACK_O_LANTERN;
import static org.bukkit.Material.LAPIS_BLOCK;
import static org.bukkit.Material.LAPIS_ORE;
import static org.bukkit.Material.LEAVES;
import static org.bukkit.Material.LOG;
import static org.bukkit.Material.MELON_BLOCK;
import static org.bukkit.Material.MOSSY_COBBLESTONE;
import static org.bukkit.Material.MYCEL;
import static org.bukkit.Material.NETHERRACK;
import static org.bukkit.Material.NETHER_BRICK;
import static org.bukkit.Material.OBSIDIAN;
import static org.bukkit.Material.PUMPKIN;
import static org.bukkit.Material.QUARTZ_BLOCK;
import static org.bukkit.Material.SANDSTONE;
import static org.bukkit.Material.SNOW_BLOCK;
import static org.bukkit.Material.SPONGE;
import static org.bukkit.Material.STAINED_CLAY;
import static org.bukkit.Material.STAINED_GLASS;
import static org.bukkit.Material.STONE;
import static org.bukkit.Material.WOOD;
import static org.bukkit.Material.WOOL;

import java.util.EnumSet;

import net.digiex.magiccarpet.nms.api.Abstraction;
import net.minecraft.server.v1_7_R1.Block;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

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

public class Handler implements Abstraction {

    private static EnumSet<Material> acceptableCarpet = EnumSet.of(STONE, GRASS, DIRT, COBBLESTONE, WOOD, BEDROCK, GOLD_ORE, IRON_ORE, COAL_ORE, LOG, LEAVES, SPONGE, GLASS, LAPIS_ORE, LAPIS_BLOCK, SANDSTONE, WOOL, GOLD_BLOCK, IRON_BLOCK, DOUBLE_STEP, BRICK, BOOKSHELF, MOSSY_COBBLESTONE, OBSIDIAN, DIAMOND_ORE, DIAMOND_BLOCK, SNOW_BLOCK, CLAY, PUMPKIN, NETHERRACK, MYCEL, NETHER_BRICK, ENDER_STONE, HUGE_MUSHROOM_1, HUGE_MUSHROOM_2, MELON_BLOCK, COAL_BLOCK, EMERALD_BLOCK, HARD_CLAY, QUARTZ_BLOCK, STAINED_GLASS, STAINED_CLAY);
    private static EnumSet<Material> acceptableLight = EnumSet.of(GLOWSTONE, JACK_O_LANTERN);

    @SuppressWarnings("deprecation")
    @Override
    public boolean setBlockFast(final World world, final int x, final int y, final int z, final Material material, final byte data) {
        return ((CraftWorld) world).getHandle().getChunkAt(x >> 4, z >> 4).a(x & 0x0f, y, z & 0x0f, Block.e(material.getId()), data);
    }

    @Override
    public void playFirework(final Location loc, final FireworkEffect effect) {
        final World world = loc.getWorld();
        final Firework fw = world.spawn(loc, Firework.class);
        final FireworkMeta fm = fw.getFireworkMeta();
        fm.clearEffects();
        fm.setPower(1);
        fm.addEffect(effect);
        fw.setFireworkMeta(fm);
        ((CraftWorld) world).getHandle().broadcastEntityEffect(((CraftEntity) fw).getHandle(), (byte) 17);
        fw.detonate();
        fw.remove();
    }

    @Override
    public EnumSet<Material> getAcceptableCarpetMaterial() {
        return acceptableCarpet;
    }

    @Override
    public EnumSet<Material> getAcceptableLightMaterial() {
        return acceptableLight;
    }
}
