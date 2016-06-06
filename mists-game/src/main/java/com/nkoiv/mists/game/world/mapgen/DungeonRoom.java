package com.nkoiv.mists.game.world.mapgen;

import java.util.Stack;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Structure;

/**
 * Rooms are the most basic components of a dungeon.
 * A room is a more or less stand-alone area that has a theme.
 * @author nkoiv
 *
 */
public class DungeonRoom {
	private int tileWidth;
	private int tileHeight;
	private int[][] floorMap;
	private int[][] structureMap;
	private int xPosition;
	private int yPosition;
	
	public DungeonRoom(int width, int height) {
		this.tileWidth = width;
		this.tileHeight = height;
		this.floorMap = new int[width][height];
		this.structureMap = new int[width][height];
	}
	
	/**
	 * Compile all the structures from the room to a single Stack 
	 * @return
	 */
	public Stack<Structure> getStructures() {
		Stack<Structure> ret = new Stack<>();
		for (int x = 0; x < structureMap.length; x++) {
			for (int y = 0; y < structureMap[0].length; y++) {
				if (structureMap[x][y] > 0) {
					Structure s = Mists.structureLibrary.create(structureMap[x][y]);
					if (s instanceof Structure) {
						s.setPosition((xPosition+x)*Mists.TILESIZE, (yPosition+y)*Mists.TILESIZE);
						ret.add(s);
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Set the room to the given coordinates.
	 * These coordinates are used as offset for
	 * all the room object when they're referred
	 * to from outside the room.
	 * @param xPosition X position of the room (tiles)
	 * @param yPosition Y position of the room (tiles)
	 */
	public void setPosition(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

}
