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
    
	/**
	 * Try to add room to a random position.
	 * @param dc DungeonContainer to place the room in
	 * @param room Room to place in the container
	 * @param minDistance minimum required distance to be left between rooms
	 * @return room True if room fit, False if it couldn't be added
	 */
	public static boolean tryAddingRoom(DungeonContainer dc, DungeonRoom room, int minDistance) {
		boolean fits = true;
		int xPos = DungeonGenerator.RND.nextInt(dc.getWidth());
		int yPos = DungeonGenerator.RND.nextInt(dc.getHeight());
		room.setPosition(xPos, yPos);
		for (Integer id : dc.getRoomIDs()) {
			if (room.distanceTo(dc.getRoom(id)) < minDistance) {
				fits = false;
				break;
			}
		}
		if (fits) dc.addRoom(room);
		return fits;
	}

	/**
	 * Generate a random sized room within the given parameters
	 * @return A room from given range. Null if given minSize is larger than given maxSize.
	 */
	public static DungeonRoom generateRandomRoom(int minXsize, int maxXsize, int minYsize, int maxYsize) {
		if (minXsize > maxXsize || minYsize > maxYsize) return null;
		int width;
		int height;
		width = DungeonGenerator.RND.nextInt(maxXsize-minXsize)+minXsize;
		height = DungeonGenerator.RND.nextInt(maxYsize-minYsize)+minYsize;
		return new DungeonRoom(width, height);
	}
    
}
