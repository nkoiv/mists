package com.nkoiv.mists.game.controls;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.world.worldmap.MapNode;
import com.nkoiv.mists.game.world.worldmap.WorldMap;

public class WorldMapControls {
	
	public static boolean moveToDirecition(WorldMap worldmap, Direction dir) {
		
		MapNode mn = worldmap.getNode(worldmap.getPlayerNode().getNeighbour(dir));
        if (mn != null) {
        	worldmap.getPlayerNode().exitNode();
        	worldmap.setPlayerNode(mn);
        	worldmap.getPlayerNode().enterNode();
        	return true;
        }
		
		return false;
	}
	
	public static boolean moveToNode(WorldMap worldmap, MapNode node) {
		if (node!=null) {
			worldmap.getPlayerNode().exitNode();
			worldmap.setPlayerNode(node);
			worldmap.getPlayerNode().enterNode();
		}
		
		return false;
	}

}
