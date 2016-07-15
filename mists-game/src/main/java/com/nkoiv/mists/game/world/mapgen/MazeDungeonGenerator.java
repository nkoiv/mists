/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.world.mapgen;

import java.util.ArrayList;
import java.util.Stack;

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
	
	/**
	 * 
	 * @param dc DungeonContainer to grow maze in
	 * @param xStart X starting point of the maze
	 * @param yStart Y starting point of the maze
	 * @param mazeID TileID to carve the maze with
	 * @param clearID TileID to create maze over (everything else is blocked)
	 * @return
	 */
	public static boolean growMaze(DungeonContainer dc, int xStart, int yStart, int mazeID, int clearID) {
		if (dc.getRoomID(xStart, yStart) != clearID) return false;
		Stack<int[]> cells = new Stack<>(); 
		int[] current = new int[]{xStart, yStart};
		dc.setRoomID(current[0], current[1], mazeID);
		cells.add(current);
		while (!cells.isEmpty()) {
			current = cells.pop();
			if (hasClearNeighbour(dc, current[0], current[1], clearID)) cells.push(current);
			current = randomClearNeighbour(dc, current[0], current[1], clearID);
			if (current != null) dc.setRoomID(current[0], current[1], mazeID);
		}
		return true;
	}
	
	private int[] randomPathDirection(DungeonContainer dc, int xPos, int yPos, int clearID, int lastDirection, int straightCorridorPreference) {
		return null
	}
	
	private static int[] randomClearNeighbour(DungeonContainer dc, int xPos, int yPos, int clearID) {
		int randomDirection = DungeonGenerator.RND.nextInt(4);
		int tries = 0;
		while(tries < 4) {
			switch(randomDirection) {
			case 0: if (dc.getRoomID(xPos, yPos-1) == clearID) return new int[]{xPos, yPos-1}; break;
			case 1: if (dc.getRoomID(xPos+1, yPos) == clearID) return new int[]{xPos+1, yPos}; break;
			case 2: if (dc.getRoomID(xPos, yPos+1) == clearID) return new int[]{xPos, yPos+1}; break;
			case 3: if (dc.getRoomID(xPos-1, yPos) == clearID) return new int[]{xPos-1, yPos}; break;
			default: break;
			}
			tries++;
			randomDirection++;
			if (randomDirection > 3) randomDirection = 0;
		}
		return null;
	}
	
	/**
	 * Check if there's any clear tiles next to the given one
	 * @param dc DungeonContainer to check
	 * @param xPos X Position to check around
	 * @param yPos Y Position to check around
	 * @param clearID TileID of what constitutes as "clear"
	 * @return true if at least one of the four surrounding cardinal directions was clear
	 */
	private static boolean hasClearNeighbour(DungeonContainer dc, int xPos, int yPos, int clearID) {
		if (xPos > 0 && dc.getRoomID(xPos-1, yPos) == clearID) return true;
		if (yPos > 0 && dc.getRoomID(xPos, yPos-1) == clearID) return true;
		if (xPos < dc.getWidth()-1 && dc.getRoomID(xPos+1, yPos) == clearID) return true;
		if (yPos < dc.getHeight()-1  && dc.getRoomID(xPos, yPos+1) == clearID) return true;
		return false;
	}
	
}
