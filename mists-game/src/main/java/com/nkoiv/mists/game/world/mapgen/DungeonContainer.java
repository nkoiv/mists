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
		nextRoomID = 1;
	}
	
	/**
	 * Add a room to the DungeonContainer at the specified position
	 * @param room Room to add to the container
	 * @param xPos X Positioning (upper left corner) of the room
	 * @param yPos Y Positioning (upper left corner) of the room
	 * @return True if room was added succesfully
	 */
	public boolean addRoom(DungeonRoom room, int xPosition, int yPosition) {
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
	 * @param tileID
	 * @param emptyID
	 */
	public void fill(int tileID, int emptyID) {
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
	
	public void setRoomID(int x, int y, int roomID) {
		this.roomMap[x][y] = roomID;
	}
	
	
	public int getRoomID(int x, int y) {
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

}
