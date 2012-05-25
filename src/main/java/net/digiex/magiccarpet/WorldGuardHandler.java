package net.digiex.magiccarpet;

import java.util.Iterator;
import java.util.Set;
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
public class WorldGuardHandler {

    private final MagicCarpet plugin;
    private final com.sk89q.worldguard.bukkit.WorldGuardPlugin worldGuard;

    public WorldGuardHandler(MagicCarpet plugin, com.sk89q.worldguard.bukkit.WorldGuardPlugin worldGuard) {
        this.plugin = plugin;
        this.worldGuard = worldGuard;
    }

    public boolean canFlyHere(Player player) {
        com.sk89q.worldguard.protection.managers.RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
        com.sk89q.worldguard.LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
        com.sk89q.worldedit.Vector location = localPlayer.getPosition();
        com.sk89q.worldguard.protection.ApplicableRegionSet set = regionManager.getApplicableRegions(location);
        if (set == null) {
            return true;
        }
        Set<String> flag = set.getFlag(com.sk89q.worldguard.protection.flags.DefaultFlag.BLOCKED_CMDS);
        if (flag == null) {
            return true;
        }
        for (Iterator<String> it = flag.iterator(); it.hasNext();) {
            String blocked = it.next();
            if (blocked == null) {
                continue;
            }
            if (blocked.contains("/mc") || blocked.contains("/magiccarpet")) {
                return false;
            }
        }
        return true;
    }
}
