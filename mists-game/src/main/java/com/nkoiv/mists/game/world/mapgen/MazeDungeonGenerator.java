/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.world.mapgen;

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
			DungeonRoom room = DungeonGenerator.generateRandomRoom(4, 10, 4, 10); //TODO: Replace with proper room generation
			if (DungeonGenerator.roomFits(dc, room, minDistance)) {
				roomsPlaced++;
				dc.addRoom(room);
			}
			if(maxRooms != 0 && roomsPlaced >= maxRooms) break;
		}
	}
	
	/**
	 * 
	 * @param dc DungeonContainer to grow maze in
	 * @param mazeLength number of tiles to carve. -1 for no restriction
	 * @param xStart X starting point of the maze
	 * @param yStart Y starting point of the maze
	 * @param mazeID TileID to carve the maze with
	 * @param clearID TileID to create maze over (everything else is blocked)
	 * @param corridorFocus the chance (0 to 1) to try and go down straight path 
	 * @return True if dungeon was carved, False if not (in case starting tile was not clear)
	 */
	public static boolean growMaze(DungeonContainer dc, int mazeLength, int xStart, int yStart, int mazeID, int wallID, int clearID, float corridorFocus) {
		if (dc.getRoomID(xStart, yStart) != clearID) {
			System.out.println("starting room was not clear (was '"+dc.getRoomID(xStart, yStart) +"', aborting");
			return false;
		}
		Stack<int[]> cells = new Stack<>(); 
		int[] current = new int[]{xStart, yStart, -1};
		dc.setRoomID(current[0], current[1], mazeID);
		cells.add(current);
		int count = 0;
		while (!cells.isEmpty()) {
			if (count> mazeLength && mazeLength != -1) break;
			current = cells.pop();
			int[] next = null;
			if (DungeonGenerator.RND.nextFloat() < corridorFocus && current[2] != -1) {
				next = DungeonGenerator.neighbouringCell(current[2], current);
			}
			else next = randomClearNeighbour(dc, current[0], current[1], clearID);
			
			if (next != null && DungeonGenerator.isClearRoute(dc, next[0], next[1], next[2], clearID)) {
				DungeonGenerator.carveCorridor(dc, next[0], next[1], next[2], mazeID, wallID, clearID);
				if (current != null && hasClearNeighbour(dc, current[0], current[1], clearID)) cells.push(current); 
				if (next != null && hasClearNeighbour(dc, next[0], next[1], clearID)) cells.push(next);
			}
			
			count++;
			dc.printMap();
			System.out.println("cells:" + cells.size());
			
		}
		return true;
	}
	
	private static int[] randomClearNeighbour(DungeonContainer dc, int xPos, int yPos, int clearID) {
		int randomDirection = DungeonGenerator.RND.nextInt(4);
		int tries = 0;
		while(tries < 4) {
			int[] newPath = DungeonGenerator.neighbouringCell(randomDirection, new int[]{xPos, yPos, randomDirection});
			if (dc.getRoomID(newPath[0], newPath[1]) == clearID && DungeonGenerator.isClearRoute(dc, newPath[0], newPath[1], newPath[2], clearID)) return newPath;
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
		if (DungeonGenerator.isClearRoute(dc, xPos, yPos-1, 0, clearID)) return true;
		if (DungeonGenerator.isClearRoute(dc, xPos+1, yPos, 1, clearID)) return true;
		if (DungeonGenerator.isClearRoute(dc, xPos, yPos+1, 2, clearID)) return true;
		if (DungeonGenerator.isClearRoute(dc, xPos-1, yPos, 3, clearID)) return true;
		return false;
	}
	
}
