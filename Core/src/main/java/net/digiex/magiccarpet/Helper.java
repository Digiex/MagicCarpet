package net.digiex.magiccarpet;

import java.lang.reflect.InvocationTargetException;

import net.digiex.magiccarpet.nms.api.Abstraction;

import org.bukkit.plugin.Plugin;

/*
 * Magic Carpet 2.3 Copyright (C) 2012 Android, Celtic Minstrel, xzKinGzxBuRnzx
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

public class Helper {

	private static Abstraction nms;

	public Helper(MagicCarpet plugin) {
		try {
			Helper.nms = init(plugin);
		} catch (Exception e) {
			Helper.nms = null;
		}
	}

	private Abstraction init(Plugin plugin) throws ClassNotFoundException,
			IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		String serverPackageName = plugin.getServer().getClass().getPackage()
				.getName();
		String pluginPackageName = plugin.getClass().getPackage().getName();

		String version = serverPackageName.substring(serverPackageName
				.lastIndexOf('.') + 1);
		if (version.equals("craftbukkit")) {
			version = "pre";
		}

		final Class<?> clazz = Class.forName(pluginPackageName + ".nms."
				+ version + ".Handler");

		if (Abstraction.class.isAssignableFrom(clazz)) {
			nms = (Abstraction) clazz.getConstructor().newInstance();
		} else {
			throw new IllegalStateException("Class " + clazz.getName()
					+ " does not implement NMSAbstraction");
		}

		return nms;
	}

	public static Abstraction getNMS() {
		return nms;
	}

	public static boolean isEnabled() {
		return (nms != null) ? true : false;
	}
}