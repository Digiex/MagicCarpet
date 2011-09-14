package net.digiex.magiccarpet;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Magic Carpet 2.0
 * Copyright (C) 2011 Android, Celtic Minstrel, xzKinGzxBuRnzx
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
public class MagicCarpetLogging {

    private static final Logger logger = Logger.getLogger("Minecraft");

    public void info(String s) {
        logger.log(Level.INFO, "[MagicCarpet] " + s);
    }

    public void severe(String s) {
        logger.log(Level.SEVERE, "[MagicCarpet] " + s);
    }

    public void warning(String s) {
        logger.log(Level.WARNING, "[MagicCarpet] " + s);
    }
}