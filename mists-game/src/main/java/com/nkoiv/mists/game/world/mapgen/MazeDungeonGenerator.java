/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.world.mapgen;

import com.nkoiv.mists.game.world.TileMap;

public class MazeDungeonGenerator implements DungeonGenerator {

	
	/**
	 * A dungeon generator that uses Growing Tree algorithm to connect the rooms with maze-like structure
	 * Idea taken from Bob's blog at http://journal.stuffwithstuff.com/2014/12/21/rooms-and-mazes/
	 */
	@Override
	public TileMap generateDungeon(int xSize, int ySize) {
		
		
		
		return null;
	}
	
	/**
	 * TODO: Proper dungeon room generator!
	 * this is just creating blank rooms.
	 * @param dc DungeonContainer to place rooms in
	 * @param minDistance minimum (manhattan) distance between rooms in container 
	 * @param maxTries max count of placements (higher number for more density on rooms)
	 * @param maxRooms max count on rooms in container. 0 for no maximum.
	 */
	public static void addRooms(DungeonContainer dc, int minDistance, int maxTries, int maxRooms) {
		int roomsPlaced = 0;
		for (int i = 0; i < maxTries; i++) {
			DungeonRoom room = DungeonGenerator.generateRandomRoom(4, 10, 4, 10);
			if (DungeonGenerator.tryAddingRoom(dc, room, minDistance)) roomsPlaced++;
			if(maxRooms != 0 && roomsPlaced >= maxRooms) break;
		}
	}
	

	
}
