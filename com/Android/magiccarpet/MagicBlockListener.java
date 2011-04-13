package com.Android.magiccarpet;

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
    //When a player joins the game, if they had a carpet when the logged out it puts it back.
    public void onBlockBreak(BlockBreakEvent event) {
    	carpets = listener.getCarpets();
    	for(Carpet carpet : carpets.values()){
    		if (carpet == null)
    			return;
    	
    		boolean test = carpet.checkGlowstone(event.getBlock());
    	
    		if (test)
    			event.getBlock().setTypeId(0);
    	}
	}

}
