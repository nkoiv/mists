/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.world.mapgen;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.Tile;

/**
 * DungeonContainer hosts the dungeon during a generation phase.
 * It's main use is to easily shuffle room positions etc and
 * then export the generated dungeon into either game-ready Map
 * or ascii int[][].
 * @author nkoiv
 *
 */
public class DungeonContainer {
	private int tileWidth;
	private int tileHeight;
	private int[][] roomMap;
	private HashMap<Integer, DungeonRoom> rooms;	
	private int nextRoomID;
	
	public DungeonContainer() {
		this.rooms = new HashMap<>();
		nextRoomID = 1;
	}
	
	public DungeonContainer(int width, int height) {
		this();
		this.tileWidth = width;
		this.tileHeight = height;
		this.roomMap = new int[width][height];
		//initializeRoomMap();
	}
	
	private void initializeRoomMap(int floorID) {
		for (int y = 0; y < tileHeight; y++) {
			for (int x = 0; x < tileWidth; x++) {
				roomMap[x][y] = floorID;
			}
		}
	}
	
	/**
	 * Add a room to the DungeonContainer at the specified position.
	 * Any existing structure on the roomMap will be overwritten.
	 * Can result in overlay with different rooms, but rooms will
	 * never be placed outside container bounds.
	 * @param room Room to add to the container
	 * @param xPos X Positioning (upper left corner) of the room
	 * @param yPos Y Positioning (upper left corner) of the room
	 * @return True if room was added succesfully
	 */
	public boolean addRoom(DungeonRoom room, int xPosition, int yPosition) {
		if (room.getXPos() < 0 || room.getYPos() < 0 || room.getXPos()+room.getWidth() > this.tileWidth || room.getYPos()+room.getHeight() > this.tileHeight) return false;
		int roomID = nextRoomID;
		nextRoomID++;
		room.setPosition(xPosition, yPosition);
		rooms.put(roomID, room);
		for (int x = xPosition; x < (xPosition + room.getWidth()); x++) {
			for (int y = yPosition; y < (yPosition + room.getHeight()); y++ ) {
				roomMap[x][y] = roomID;
			}
		}
		return true;
	}
	
	/**
	 * Shorthand for adding room to the coordinates it
	 * already has (room.getXPos(), room.getYPos())
	 * @param room Room to add to the container
	 * @return True if room was added successfully
	 */
	public boolean addRoom(DungeonRoom room) {
		return addRoom(room, room.getXPos(), room.getYPos());
	}
	
	/**
	 * Fill every empty tile in the container with the given tile
	 * @param emptyID
	 * @param tileID
	 */
	public void fill(int emptyID, int tileID) {
		for (int x = 0; x < tileWidth; x++) {
			for (int y = 0; y < tileHeight; y++) {
				if (roomMap[x][y] == emptyID) roomMap[x][y] = tileID;
			}
		}
	}
	
	/**
	 * Four way floodfill the container from the given position, replacing every empty
	 * tile with the given tileID.
	 * Does nothing if the start tile is not of the empty type.
	 * @param tileID Tile to fill with
	 * @param emptyID Tile to flood over
	 * @param xPos X start for Flood Fill
	 * @param yPos Y start for Flood Fill
	 */
	public void floodFill(int tileID, int emptyID, int xPos, int yPos) {
		if (roomMap[xPos][yPos] != emptyID) return;
		Stack<int[]> points = new Stack<>();
		points.push(new int[]{xPos, yPos});
		while (!points.isEmpty()) {
			int[] current = points.pop();
			//Add neighbours
			if (current[1] > 0 && roomMap[current[0]][current[1]-1] == emptyID) points.push(new int[]{current[0], current[1]-1}); 
			if (current[0] < tileWidth-1 && roomMap[current[0]+1][current[1]] == emptyID) points.push(new int[]{current[0]+1, current[1]});
			if (current[1] < tileHeight-1 && roomMap[current[0]][current[1]+1] == emptyID) points.push(new int[]{current[0], current[1]+1});
			if (current[0] > 0 && roomMap[current[0]-1][current[1]] == emptyID) points.push(new int[]{current[0]-1, current[1]});
			//Fill the tile
			roomMap[current[0]][current[1]] = tileID;
		}
	}
	
	/**
	 * Find a point containing given tileID on the map and return it.
	 * Scanning is done from top to bottom, from left to right.
	 * @return First given tileID position found. Null if there are no open points.
	 */
	public int[] findRoomID(int roomID) {
		for (int x = 0; x < this.tileWidth; x++) {
			for (int y = 0; y < this.tileHeight; y++) {
				if (this.roomMap[x][y] == roomID) return new int[]{x,y};
			}
		}
		
		return null;
	}
	
	public int getNextFreeRoomID() {
		int id = this.nextRoomID;
		nextRoomID++;
		return id;
	}
	
	/**
	 * Carve the selected tile with give roomID,
	 * but only if it's of the clear ID.
	 * Checks are done for out of bounds.
	 * @param x X position of the tile to carve
	 * @param y Y position of the tile to carve
	 * @param roomID ID to carve on the container
	 * @param clearID ID to carve over. -1 means any ID is okay to carve over.
	 * @return
	 */
	public boolean carveIfClear(int x, int y, int roomID, int clearID) {
		int id = getRoomID(x,y);
		if (id != -1 && (id == clearID || -1 == clearID)) {
			setRoomID(x, y, roomID);
			return true;
		} else {
			return false;
		}	
	}

	/**
	* Check to see if given point on the room map (and raidius around it)
	* is marked as clear
	* @param x Center of the checked area
	* @param y Center of the checked area
	* @param radius Radius of the checked area (diagonal distance counts as 1, so effectively box is checked every time)
	* @param clearID identifier of what counts as "clear"
	* @return True if designated area contained only clear tiles, false otherwise
	*/
	public boolean isClearArea(int x, int y, int radius, int clearID) {
		for (int row = y-radius; row < (y+radius); row++) {
			for (int column = x-radius; column < (x+radius); column++) {
				if (column < 0 || column > tileWidth-1 || row < 0 || row > tileHeight-1) return false;
				else if (roomMap[column][row] != clearID) return false;
			}
		}
		return true;
	}
	
	public void setRoomID(int x, int y, int roomID) {
		if (x < 0 || x > tileWidth-1 || y < 0 || y > tileHeight-1) return;
		this.roomMap[x][y] = roomID;
	}
        
        /**
         * Give the amount of neighbours the given tile has with the
         * [ ][C][ ]
         * [C][X][C]
         * [ ][C][ ]
         * given tileID
         * @param x tile X
         * @param y tile Y
         * @param tileID surrounding ID to count
         * @return number of surrounding tiles with given ID
         */
        public int countCardialNeighbours(int x, int y, int tileID) {
            int idNeighbours = 0;
            if (x > 0 && roomMap[x-1][y] == tileID) idNeighbours++;
            if (x < tileWidth-1 && roomMap[x+1][y] == tileID) idNeighbours++;
            if (y > 0 && roomMap[x][y-1] == tileID) idNeighbours++;
            if (y < tileHeight-1 && roomMap[x][y+1] == tileID) idNeighbours++;
            return idNeighbours;
        }
	
	/**
	 * Get the room/tileID of the given coordinates
	 * @param x xCoordinate
	 * @param y yCoordinate
	 * @return return roomID at the given coordinates, or -1 if accessing out of bounds area.
	 */
	public int getRoomID(int x, int y) {
		if (x < 0 || x > tileWidth-1 || y < 0 || y > tileHeight-1) return -1;
		return this.roomMap[x][y];
	}
	
	public Set<Integer> getRoomIDs() {
		return this.rooms.keySet();
	}
	
	public DungeonRoom getRoom(int id) {
		return this.rooms.get(id);
	}
	
	public int getWidth() {
		return this.tileWidth;
	}
	
	public int getHeight() {
		return this.tileHeight;
	}
	
	public Set<Structure> getStructures() {
		HashSet<Structure> allStructures = new HashSet<>();
		for (int key : rooms.keySet()) {
			allStructures.addAll(rooms.get(key).getStructures());
		}
		//TODO: also get corridors!
		return allStructures;
	}
	
	public Tile[][] getTileMap() {
		return new Tile[tileWidth][tileHeight];
	}
	
	public int[][] getIntMap() {
		return new int[tileWidth][tileHeight];
	}
	
	/**
	 * Print the map in char representations into
	 * standard system.out
	 */
	public void printMap() {
		System.out.println("--------MAP--------");
		for (int y = 0; y < this.tileHeight; y++) {
			System.out.println();
			for (int x = 0; x < this.tileWidth; x++) {
				if (roomMap[x][y] < 10) System.out.print((char)(48+roomMap[x][y])); 
				else System.out.print((char)roomMap[x][y]);
			}
		}
		System.out.println();
		System.out.println("-------------------");
	}

}
