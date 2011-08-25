package net.digiex.magiccarpet;

import java.util.Hashtable;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class MagicBlockListener extends BlockListener {

    MagicPlayerListener listener;
    Hashtable<String, Carpet> carpets;

    public MagicBlockListener(MagicPlayerListener play) {
        listener = play;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        carpets = listener.getCarpets();
        for (Carpet carpet : carpets.values()) {
            if (carpet == null) {
                return;
            }

            boolean test = carpet.checkGlowstone(event.getBlock());

            if (test) {
                event.getBlock().setTypeId(0);
            }
        }
    }
}
