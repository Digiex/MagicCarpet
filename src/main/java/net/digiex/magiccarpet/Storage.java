package net.digiex.magiccarpet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import net.digiex.magiccarpet.plugins.Plugins;
import net.digiex.magiccarpet.plugins.Vault;

import org.bukkit.Material;
import org.bukkit.entity.Player;

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
public class Storage implements Serializable {

    private class CarpetEntry implements Serializable {

        public transient Carpet carpet;

        public boolean hasCarpet = false;
        public boolean given = false;
        public boolean tools = false;
        public boolean autoRenew = false;
        public boolean oneTimeFee = false;
        public String autoPackage = null;

        public boolean crouch = Config.getCrouch();
        public int lastSize = Config.getCarpSize();
        public Material light = Config.getLightMaterial();
        public boolean lightsOn = Config.getGlowing();
        public Material thread = Config.getCarpetMaterial();
        public long time = Config.getChargeTime();
        public byte data = Config.getCarpetData();
    }

    private final HashMap<UUID, CarpetEntry> carpets = new HashMap<UUID, CarpetEntry>();

    private CarpetEntry getEntry(final Player player) {
        if (carpets.containsKey(player.getUniqueId()))
            return carpets.get(player.getUniqueId());
        return null;
    }

    public Iterable<Carpet> all() {
        return new Iterable<Carpet>() {
            @Override
            public Iterator<Carpet> iterator() {
                return new Iterator<Carpet>() {
                    private final Iterator<CarpetEntry> iter = carpets.values().iterator();
                    private CarpetEntry toRemove = null;

                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public Carpet next() {
                        toRemove = iter.next();
                        return toRemove.carpet;
                    }

                    @Override
                    public void remove() {
                        if (toRemove == null)
                            throw new IllegalStateException();
                        if (toRemove.carpet != null)
                            toRemove.carpet.removeCarpet();
                        toRemove.carpet = null;
                    }
                };
            }
        };
    }

    public void assign(final Player player, final Carpet carpet) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            entry = new CarpetEntry();
            carpets.put(player.getUniqueId(), entry);
        }
        if (entry.carpet != null)
            entry.carpet.removeCarpet();
        entry.carpet = carpet;
    }

    public Carpet getCarpet(final Player player) {
        if (carpets.containsKey(player.getUniqueId()))
            return carpets.get(player.getUniqueId()).carpet;
        return null;
    }

    public void remove(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        if (entry.carpet != null) {
            entry.carpet.removeCarpet();
            entry.carpet = null;
        }
    }

    public void clear() {
        for (final CarpetEntry entry : carpets.values()) {
            if (entry.carpet == null || !entry.carpet.isVisible())
                continue;
            entry.carpet.removeCarpet();
        }
        carpets.clear();
    }

    public void update(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        if (entry.carpet == null) {
            entry.hasCarpet = false;
            return;
        }
        entry.lastSize = entry.carpet.getSize();
        entry.hasCarpet = entry.carpet.isVisible();
        entry.lightsOn = entry.carpet.hasLight();
        entry.thread = entry.carpet.getThread();
        entry.light = entry.carpet.getShine();
        entry.tools = entry.carpet.hasTools();
        entry.data = entry.carpet.getData();
    }

    public void checkCarpets() {
        for (final CarpetEntry entry : carpets.values()) {
            if (!MagicCarpet.getAcceptableCarpetMaterial().contains(entry.thread))
                entry.thread = Config.getCarpetMaterial();
            if (!MagicCarpet.getAcceptableLightMaterial().contains(entry.light))
                entry.light = Config.getLightMaterial();
            if (entry.lastSize > Config.getMaxCarpetSize())
                entry.lastSize = Config.getCarpSize();
            if (entry.thread != Config.getCarpetMaterial() && !Config.getCustomCarpets())
                entry.thread = Config.getCarpetMaterial();
            if (entry.light != Config.getLightMaterial() && !Config.getCustomLights())
                entry.light = Config.getLightMaterial();
            if (entry.lightsOn && !Config.getLights())
                entry.lightsOn = false;
            if (entry.tools && !Config.getTools())
                entry.tools = false;
            if (Plugins.isVaultEnabled())
                if (Config.getChargeTimeBased()) {
                    if (entry.hasCarpet && entry.time <= 0L && !entry.given)
                        entry.hasCarpet = false;
                    if (Vault.getPackage(entry.autoPackage) == null) {
                        entry.autoPackage = null;
                        entry.autoRenew = false;
                    }
                } else if (entry.hasCarpet && !entry.oneTimeFee && !entry.given)
                    entry.hasCarpet = false;
        }
    }

    public boolean crouches(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return Config.getCrouch();
        return entry.crouch;
    }

    public int getLastSize(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return Config.getCarpSize();
        return entry.lastSize;
    }

    public Material getMaterial(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return Config.getCarpetMaterial();
        return entry.thread;
    }

    public Material getLightMaterial(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return Config.getLightMaterial();
        return entry.light;
    }

    public boolean has(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return false;
        return entry.hasCarpet;
    }

    public boolean hasLight(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return (Config.getLights()) ? Config.getGlowing() : false;
        return entry.lightsOn;
    }

    public void toggleCrouch(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        entry.crouch = !entry.crouch;
    }

    public boolean wasGiven(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return false;
        return entry.given;
    }

    public void setGiven(final Player player, final Boolean given) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            entry = new CarpetEntry();
            carpets.put(player.getUniqueId(), entry);
        }
        entry.given = given;
    }

    public boolean hasTools(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return Config.getTools();
        return entry.tools;
    }

    public void setTime(final Player player, final Long time) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        entry.time = time;
    }

    public long getTime(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return Config.getChargeTime();
        return entry.time;
    }

    public boolean canAutoRenew(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return false;
        return entry.autoRenew;
    }

    public void setAutoRenew(final Player player, final Boolean renew) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        entry.autoRenew = renew;
    }

    public String getAutoPackage(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return null;
        return entry.autoPackage;
    }

    public void setAutoPackage(final Player player, final String auto) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        entry.autoPackage = auto;
    }

    public boolean hasPaidFee(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return false;
        return entry.oneTimeFee;
    }

    public void setPaidFee(final Player player, final Boolean paid) {
        CarpetEntry entry = getEntry(player);
        if (entry == null) {
            entry = new CarpetEntry();
            carpets.put(player.getUniqueId(), entry);
        }
        entry.oneTimeFee = paid;
    }

    public byte getData(final Player player) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return Config.getCarpetData();
        return entry.data;
    }

    public void setData(final Player player, final byte data) {
        final CarpetEntry entry = getEntry(player);
        if (entry == null)
            return;
        entry.data = data;
    }
}
