package net.digiex.magiccarpet.plugins;

import java.util.HashMap;

import net.digiex.magiccarpet.Carpet;
import net.digiex.magiccarpet.MagicCarpet;
import net.digiex.magiccarpet.Permissions;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

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
public class Vault {

	public class TimePackage {
		private final String name;
		private final long time;
		private final double amount;

		public TimePackage(String name, Long time, Double amount) {
			this.name = name;
			this.time = time;
			this.amount = amount;
		}

		public String getName() {
			return name;
		}

		public Long getTime() {
			return time;
		}

		public Double getAmount() {
			return amount;
		}
	}

	private final MagicCarpet plugin;
	private Economy vaultPlugin;
	private HashMap<String, TimePackage> packages = new HashMap<String, TimePackage>();

	public Vault(MagicCarpet plugin) {
		this.plugin = plugin;
		loadPackages();
		loadVault();
	}

	private void loadVault() {
		Plugin p = plugin.getServer().getPluginManager().getPlugin("Vault");
		if (p == null || !(p instanceof net.milkbowl.vault.Vault)) {
			return;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return;
		}
		vaultPlugin = rsp.getProvider();
		startCharge();
	}

	private void startCharge() {
		plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				if (!isEnabled()
						|| !MagicCarpet.getMagicConfig().getChargeTimeBased()) {
					return;
				}
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
					if (carpet == null || !carpet.isVisible()) {
						continue;
					}
					if (Permissions.canNotPay(player)
							|| MagicCarpet.getCarpets().wasGiven(player)) {
						continue;
					}
					if (get(player) == 300) {
						if (!MagicCarpet.getCarpets().canAutoRenew(player)) {
							player.sendMessage("You are running low on time to use the Magic Carpet. If you wish to continue using it please purchase more time using /mcb.");
							substractTime(carpet, 1L);
							continue;
						} else {
							TimePackage pack = getPackage(MagicCarpet
									.getCarpets().getAutoPackage(player));
							if (addTime(player, pack.getTime(),
									pack.getAmount())) {
								player.sendMessage("Your Magic Carpet has auto renewed for "
										+ getTime(pack.getTime())
										+ " and you was charged "
										+ format(pack.getAmount()) + ".");
							}
						}
					}
					substractTime(carpet, 1L);
				}
			}
		}, 20L, 20L);
	}

	public boolean isEnabled() {
		return (vaultPlugin != null && MagicCarpet.getMagicConfig().getCharge()) ? true
				: false;
	}

	public void loadPackages() {
		try {
			for (Object o : MagicCarpet.getMagicConfig().getChargePackages()) {
				String[] s = o.toString().split(":");
				String name = s[0];
				long time = Long.valueOf(s[1]);
				double amount = Double.valueOf(s[2]);
				addPackage(name, time, amount);
			}
		} catch (NumberFormatException e) {
			plugin.getLogger().severe(
					"Unable to read charge-packages; defaulting.");
			addPackage("alpha", 3600L, 5.0);
			addPackage("beta", 7200L, 10.0);
		}
	}

	public boolean add(String player, double amount) {
		return vaultPlugin.depositPlayer(player, amount).transactionSuccess();
	}

	public boolean subtract(String player, double amount) {
		return vaultPlugin.withdrawPlayer(player, amount).transactionSuccess();
	}

	public boolean hasEnough(String player, double amount) {
		return vaultPlugin.has(player, amount);
	}

	public double balance(String player) {
		return vaultPlugin.getBalance(player);
	}

	public String format(double amount) {
		return vaultPlugin.format(amount);
	}

	public String getPluginName() {
		if (vaultPlugin == null) {
			return "";
		} else {
			return vaultPlugin.getName();
		}
	}

	public String getCurrencyName() {
		return vaultPlugin.currencyNameSingular();
	}

	public String getCurrencyNamePlural() {
		return vaultPlugin.currencyNamePlural();
	}

	public long get(Player player) {
		return MagicCarpet.getCarpets().getTime(player);
	}

	public String getTime(Player player) {
		return getTime(get(player));
	}

	public String getTime(Long time) {
		long days = time / 86400L;
		long remainder = time % 86400L;
		long hours = remainder / 3600L;
		remainder %= 3600L;
		long minutes = remainder / 60L;
		long seconds = remainder % 60L;
		String s = "";
		if (days > 0) {
			s = s + days + "D ";
		}
		if (hours > 0) {
			s = s + hours + "H ";
		}
		if (minutes > 0) {
			s = s + minutes + "M ";
		}
		if (seconds > 0) {
			s = s + seconds + "S";
		}
		if (s.equals(" ") || s.isEmpty()) {
			return "zero seconds";
		}
		return s;
	}

	public void substractTime(Carpet carpet, long time) {
		Player player = carpet.getPlayer();
		long rTime = get(player) - time;
		if (rTime <= 0L) {
			carpet.hide();
			player.sendMessage("You've ran out of time to use the Magic Carpet. Please refill using /mcb");
		}
		MagicCarpet.getCarpets().setTime(player, rTime);
	}

	public boolean addTime(Player player, Long time, Double amount) {
		long rTime = get(player) + time;
		if (hasEnough(player.getName(), amount)) {
			subtract(player.getName(), amount);
			MagicCarpet.getCarpets().setTime(player, rTime);
			return true;
		} else {
			player.sendMessage("You don't have enough "
					+ getCurrencyNamePlural() + ".");
		}
		return false;
	}

	public void addTime(Player player, Long time) {
		long rTime = get(player) + time;
		MagicCarpet.getCarpets().setTime(player, rTime);
		player.sendMessage("Console has given you " + getTime(time)
				+ " of time to use Magic Carpet");
	}

	public HashMap<String, TimePackage> getPackages() {
		return packages;
	}

	public TimePackage getPackage(String name) {
		return getPackages().get(name);
	}

	public void addPackage(String name, Long time, Double amount) {
		packages.put(name, new TimePackage(name, time, amount));
	}
}