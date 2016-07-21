/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.world.mapgen;

import java.util.HashSet;
import java.util.Set;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Structure;

/**
 * Rooms are the most basic components of a dungeon.
 * A room is a more or less stand-alone area that has a theme.
 * Rooms are meant to be used as easy-to-serialize templates 
 * in generation, and they need to be processed before they
 * can be utilized by the actual gameplay.
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
	 * Compile all the structures from the room to a single set 
	 * @return
	 */
	public Set<Structure> getStructures() {
		HashSet<Structure> ret = new HashSet<>();
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
	
	/**
	 * Get the wall-to-wall distance of two rooms
	 * @param anotherRoom Room to compare this room to
	 * @return Shortest wall-to-wall link from this room to anotherRoom. Return -1 if the rooms intersect. 
	 */
	public int distanceTo(DungeonRoom anotherRoom) {
		//calculate distance from the center of this room to the center of the other room
		int xSpace = this.tileWidth/2 + anotherRoom.tileWidth/2; //Any closer than this and we're intersecting
		int ySpace = this.tileHeight/2 + anotherRoom.tileHeight/2;
		int xDist = this.xPosition+(this.tileWidth/2) - anotherRoom.xPosition+(anotherRoom.tileWidth/2);
		int yDist = this.yPosition+(this.tileHeight/2) - anotherRoom.yPosition+(anotherRoom.tileHeight/2);
		//Calculate the space between the walls
		xDist = (Math.abs(xDist) - xSpace);
		yDist = (Math.abs(yDist) - ySpace);
		//Check if we're actually intersecting with the other room;
		if (xDist < 0 && yDist < 0) return -1;
		//If not, return the distance
		return (Math.max(xDist, yDist));
	}

	public boolean intersects(DungeonRoom anotherRoom) {
		return xPosition < anotherRoom.getXPos() + anotherRoom.getWidth() 
		&& xPosition + tileWidth > anotherRoom.xPosition && yPosition < anotherRoom.getYPos() + anotherRoom.getHeight() 
		&& yPosition + tileHeight > anotherRoom.getYPos();
	}

	public int getWidth() {
		return this.tileWidth;
	}
	
	public int getHeight() {
		return this.tileHeight;
	}
	
	public int getXPos() {
		return this.xPosition;
	}
	
	public int getYPos() {
		return this.yPosition;
	}
}
