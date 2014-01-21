package net.digiex.magiccarpet.plugins;

import net.digiex.magiccarpet.MagicCarpet;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class Plugins {
	
	private final MagicCarpet plugin;
	private static WorldGuard worldGuard;
	private static Vault vault;
	
	public Plugins(MagicCarpet plugin) {
		this.plugin = plugin;
		getWorldGuard();
		getVault();
	}
	
	private void getVault() {
		Plugin p = plugin.getServer().getPluginManager().getPlugin("Vault");
		if (p == null || !(p instanceof net.milkbowl.vault.Vault)) {
			return;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return;
		}
		vault = new Vault(plugin, rsp.getProvider());
	}
	
	private void getWorldGuard() {
		Plugin p = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		if (p == null
				|| !(p instanceof com.sk89q.worldguard.bukkit.WorldGuardPlugin)) {
			return;
		}
		worldGuard = new WorldGuard((com.sk89q.worldguard.bukkit.WorldGuardPlugin) p);
	}
	
	public static boolean isVaultEnabled() {
		return (vault != null) ? true : false;
	}

	public static boolean isWorldGuardEnabled() {
		return (worldGuard != null) ? true : false;
	}
}