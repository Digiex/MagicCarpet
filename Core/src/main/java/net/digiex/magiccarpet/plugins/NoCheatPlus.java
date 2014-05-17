package net.digiex.magiccarpet.plugins;

import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

public class NoCheatPlus {
    
    public static void exempt(final Player player) {
    	NCPExemptionManager.exemptPermanently(player, CheckType.MOVING_SURVIVALFLY);
    }
    
    public static void unexempt(final Player player) {
    	NCPExemptionManager.unexempt(player, CheckType.MOVING_SURVIVALFLY);
    }
}