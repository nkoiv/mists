/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.world.mapgen;

import java.util.Random;

import com.nkoiv.mists.game.world.GameMap;

public interface DungeonGenerator {
	//Static ints are given for ASCII codes used in default dungeon formations
	//TODO: Consider loading these from external source, though probably best to keep these bare DEFAULTS as it is.
    public static final int CLEAR = 0;
    public static final int FLOOR = 46;
    public static final int WALL = 35;
    public static final int DOOR = 43;
    public static final Random RND = new Random();
	
    public static void setRandomSeed(long seed) {
        RND.setSeed(seed);
    }
    
    /**
     * This is the core of a DungeonGenerator,
     * to generate a full Map for Locations to use
     * @param xSize xSize of the map desired
     * @param ySize ySize of the map desired
     * @return a Map for Locations
     */
    public GameMap generateDungeon(int xSize, int ySize);
    
    public static GameMap generateDungeon(DungeonGenerator dg, int xSize, int ySize) {
    	return dg.generateDungeon(xSize, ySize);
    }
    
	/**
	 * Try to add room to a random position.
	 * @param dc DungeonContainer to place the room in
	 * @param room Room to place in the container
	 * @param minDistance minimum required distance to be left between rooms
	 * @return room True if room fit, False if it couldn't be added
	 */
	public static boolean roomFits(DungeonContainer dc, DungeonRoom room, int minDistance) {
		boolean fits = true;
		int xPos = DungeonGenerator.RND.nextInt(dc.getWidth());
		int yPos = DungeonGenerator.RND.nextInt(dc.getHeight());
		room.setPosition(xPos, yPos);
		for (Integer id : dc.getRoomIDs()) {
			if (room.intersects(dc.getRoom(id))) {
				fits = false;
				break;
			}
		}		
		return fits;
	}
	
	/**
	 * Check if it's clear to go to the given location
	 * Clear samples (x means checked block, . is position x/y)
	 * dir 0(up)   dir 2(down) dir 3(left) 
	 * [ ][x][ ]   [ ][ ][ ]   [ ][x][ ]
	 * [x][.][x]   [x][.][x]   [x][.][ ]
	 * [ ][ ][ ]   [ ][x][ ]   [ ][x][ ]
	 *  
	 * @param dc DungeonContainer to query for the position
	 * @param xPos X coordinate of the checked position
	 * @param yPos Y coordinate of the checked position
	 * @param direction direction route is heading
	 * @param clearID id of what constitutes as clear
	 * @return True if all three checked blocks are marked as clear
	 */
	public static boolean isClearRoute(DungeonContainer dc, int xPos, int yPos, int direction, int clearID) {
		if (clearID != -1) {
			switch(direction) {
			case 0: {
				if (dc.getRoomID(xPos, yPos-1) != clearID) return false;
				if (dc.getRoomID(xPos+1, yPos) != clearID) return false;
				if (dc.getRoomID(xPos-1, yPos) != clearID) return false;
				break;
			}
			case 1: {
				if (dc.getRoomID(xPos, yPos-1) != clearID) return false;
				if (dc.getRoomID(xPos+1, yPos) != clearID) return false;
				if (dc.getRoomID(xPos, yPos+1) != clearID) return false;
				break;
			}
			case 2: {
				if (dc.getRoomID(xPos, yPos+1) != clearID) return false;
				if (dc.getRoomID(xPos+1, yPos) != clearID) return false;
				if (dc.getRoomID(xPos-1, yPos) != clearID) return false;
				break;
			}
			case 3: {
				if (dc.getRoomID(xPos, yPos-1) != clearID) return false;
				if (dc.getRoomID(xPos-1, yPos) != clearID) return false;
				if (dc.getRoomID(xPos, yPos+1) != clearID) return false;
				break;
			}
			default: return false;
			}
		}
		
		return true;
	}
	

	/**
	 * Draw a corridor towards given location.
	 * Walls will be carved behind the corridor, where it came from. 
	 * @param xStart xPosition to draw at
	 * @param yStart yPosition to draw at
	 * @param direction the direction corridor is heading towards go towards, 0: up, 1: right, 2: down, 3: left. At -1, no walls will be carved
	 * @param wallID tileID of walls to leave behind
	 * @param clearID clearID for overwriting with walls. -1 for overwriting anything
	 * @return
	 */
	public static boolean carveCorridor(DungeonContainer dc, int xPosition, int yPosition, int direction, int corridorID, int wallID, int clearID) {
		if (dc.getRoomID(xPosition, yPosition) != clearID) return false;
		dc.setRoomID(xPosition, yPosition, corridorID);
		/*
		if (direction != -1) {
			switch(direction) {
			case 0: {
				dc.carveIfClear(xPosition-1, yPosition+1, wallID, clearID);
				dc.carveIfClear(xPosition+1, yPosition+1, wallID, clearID);
				break;
			}
			case 1: {
				dc.carveIfClear(xPosition-1, yPosition+1, wallID, clearID);
				dc.carveIfClear(xPosition-1, yPosition-1, wallID, clearID);
				break;
			}
			case 2: {
				dc.carveIfClear(xPosition+1, yPosition-1, wallID, clearID);
				dc.carveIfClear(xPosition-1, yPosition-1, wallID, clearID);
				break;
			}
			case 3: {
				dc.carveIfClear(xPosition+1, yPosition+1, wallID, clearID);
				dc.carveIfClear(xPosition+1, yPosition-1, wallID, clearID);
				break;
			}
			default: break;
			}
		}
		*/
		return true;
	}
	
	/**
	 * Return a cell that's next from the current one, going by
	 * the cardinal directions
	 * 
	 * @param direction Direction of the desired neighbour: 0: up, 1: right, 2: down, 3: left.
	 * @param cell Cell to get the neighbour from ([0] = xCoor, [1] = yCoor)
	 * @return Neighbouring cell coordinates([0] = xCoor, [1] = yCoor, [2] = direction)
	 */
	public static int[] neighbouringCell(int direction, int[] cell) {
		switch(direction) {
			case 0: return new int[]{cell[0], cell[1]-1, 0};
			case 1: return new int[]{cell[0]+1, cell[1], 1};
			case 2: return new int[]{cell[0], cell[1]+1, 2};
			case 3: return new int[]{cell[0]-1, cell[1], 3};
		default: return cell;
		}
	}
	
	/**
	 * Generate a random sized room within the given parameters
	 * @return A room from given range. Null if given minSize is larger than given maxSize.
	 */
	public static DungeonRoom generateRandomRoom(int minXsize, int maxXsize, int minYsize, int maxYsize) {
		if (minXsize > maxXsize || minYsize > maxYsize || minXsize <= 0 || maxXsize <= 0 || minYsize <= 0 || maxYsize <= 0) return null;
		int width;
		int height;
		if (minXsize==maxXsize) width = minXsize;
		else width = DungeonGenerator.RND.nextInt(maxXsize-minXsize)+minXsize; 
		if (minYsize==maxYsize) height = minYsize;
		else height = DungeonGenerator.RND.nextInt(maxYsize-minYsize)+minYsize;
		return new DungeonRoom(width, height);
	}
    
}
