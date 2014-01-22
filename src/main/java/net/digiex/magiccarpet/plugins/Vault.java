package net.digiex.magiccarpet.plugins;

import java.util.HashMap;

import net.digiex.magiccarpet.Carpet;
import net.digiex.magiccarpet.Config;
import net.digiex.magiccarpet.MagicCarpet;
import net.digiex.magiccarpet.Permissions;
import net.milkbowl.vault.economy.Economy;

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
public class Vault {

    public static class TimePackage {
        private final String name;
        private final long time;
        private final double amount;

        public TimePackage(final String name, final Long time, final Double amount) {
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
    private static Economy economy;
    private static HashMap<String, TimePackage> packages = new HashMap<String, TimePackage>();

    public Vault(final MagicCarpet plugin, final Economy eco) {
        this.plugin = plugin;
        economy = eco;
        loadPackages();
        startCharge();
    }

    private void startCharge() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (!Config.getCharge() || !Config.getChargeTimeBased())
                    return;
                for (final Player player : plugin.getServer().getOnlinePlayers()) {
                    final Carpet carpet = MagicCarpet.getCarpets().getCarpet(player);
                    if (carpet == null || !carpet.isVisible())
                        continue;
                    if (Permissions.canNotPay(player) || MagicCarpet.getCarpets().wasGiven(player))
                        continue;
                    if (get(player) == 300)
                        if (!MagicCarpet.getCarpets().canAutoRenew(player)) {
                            player.sendMessage("You are running low on time to use the Magic Carpet. If you wish to continue using it please purchase more time using /mcb.");
                            substractTime(carpet, 1L);
                            continue;
                        } else {
                            final TimePackage pack = getPackage(MagicCarpet.getCarpets().getAutoPackage(player));
                            if (addTime(player, pack.getTime(), pack.getAmount()))
                                player.sendMessage("Your Magic Carpet has auto renewed for " + getTime(pack.getTime()) + " and you was charged " + format(pack.getAmount()) + ".");
                        }
                    substractTime(carpet, 1L);
                }
            }
        }, 20L, 20L);
    }

    public static void loadPackages() {
        try {
            for (final Object o : Config.getChargePackages()) {
                final String[] s = o.toString().split(":");
                final String name = s[0];
                final long time = Long.valueOf(s[1]);
                final double amount = Double.valueOf(s[2]);
                addPackage(name, time, amount);
            }
        } catch (final NumberFormatException e) {
            MagicCarpet.log().severe("Unable to read charge-packages; defaulting.");
            addPackage("alpha", 3600L, 5.0);
            addPackage("beta", 7200L, 10.0);
        }
    }

    public static boolean add(final String player, final double amount) {
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public static boolean subtract(final String player, final double amount) {
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public static boolean hasEnough(final String player, final double amount) {
        return economy.has(player, amount);
    }

    public static double balance(final String player) {
        return economy.getBalance(player);
    }

    public static String format(final double amount) {
        return economy.format(amount);
    }

    public static String getPluginName() {
        if (economy == null)
            return "";
        else
            return economy.getName();
    }

    public static String getCurrencyName() {
        return economy.currencyNameSingular();
    }

    public static String getCurrencyNamePlural() {
        return economy.currencyNamePlural();
    }

    public static long get(final Player player) {
        return MagicCarpet.getCarpets().getTime(player);
    }

    public static String getTime(final Player player) {
        return getTime(get(player));
    }

    public static String getTime(final Long time) {
        final long days = time / 86400L;
        long remainder = time % 86400L;
        final long hours = remainder / 3600L;
        remainder %= 3600L;
        final long minutes = remainder / 60L;
        final long seconds = remainder % 60L;
        String s = "";
        if (days > 0)
            s = s + days + "D ";
        if (hours > 0)
            s = s + hours + "H ";
        if (minutes > 0)
            s = s + minutes + "M ";
        if (seconds > 0)
            s = s + seconds + "S";
        if (s.equals(" ") || s.isEmpty())
            return "zero seconds";
        return s.trim();
    }

    public static void substractTime(final Carpet carpet, final long time) {
        final Player player = carpet.getPlayer();
        final long rTime = get(player) - time;
        if (rTime <= 0L) {
            carpet.hide();
            player.sendMessage("You've ran out of time to use the Magic Carpet. Please refill using /mcb");
        }
        MagicCarpet.getCarpets().setTime(player, rTime);
    }

    public static boolean addTime(final Player player, final Long time, final Double amount) {
        final long rTime = get(player) + time;
        if (hasEnough(player.getName(), amount)) {
            subtract(player.getName(), amount);
            MagicCarpet.getCarpets().setTime(player, rTime);
            return true;
        } else
            player.sendMessage("You don't have enough " + getCurrencyNamePlural() + ".");
        return false;
    }

    public static void addTime(final Player player, final Long time) {
        final long rTime = get(player) + time;
        MagicCarpet.getCarpets().setTime(player, rTime);
        player.sendMessage("Console has given you " + getTime(time) + " of time to use Magic Carpet");
    }

    public static HashMap<String, TimePackage> getPackages() {
        return packages;
    }

    public static TimePackage getPackage(final String name) {
        return getPackages().get(name);
    }

    public static void addPackage(final String name, final Long time, final Double amount) {
        packages.put(name, new TimePackage(name, time, amount));
    }
}
