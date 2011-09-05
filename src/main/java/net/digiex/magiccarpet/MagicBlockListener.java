package net.digiex.magiccarpet;

import java.util.Hashtable;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;

public class MagicBlockListener extends BlockListener {

    private MagicCarpet parent;
    MagicPlayerListener listener;
    Hashtable<String, Carpet> carpets;

    public MagicBlockListener(MagicCarpet parent, MagicPlayerListener play) {
        this.parent = parent;
        listener = play;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        carpets = listener.getCarpets();
        for (Carpet carpet : carpets.values()) {
            if (carpet == null) {
                return;
            }

            boolean test = carpet.checkBlock(event.getBlock());

            if (test) {
                event.setCancelled(true);
            }
        }
    }
    
    @Override
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        carpets = listener.getCarpets();
        for (Carpet carpet : carpets.values()) {
            if (carpet == null) {
                return;
            }

            boolean test = carpet.checkBlock(event.getRetractLocation().getBlock());

            if (test) {
                event.setCancelled(true);
            }
        }
    }
}
