/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.mapgen;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.GameMap;
import com.nkoiv.mists.game.world.TileMap;

/**
 * MapGenerator makes maps for Locations.
 * It uses procedural generation, splitting the given
 * area in smaller and smaller chunks randomly, until 
 * its satisfied with what it has.
 * After the area has been split into segments, its
 * populated by rooms that are joined by corridors.
 * 
 * TODO: Refactor this dirty code into reworkable modules
 * and expand from there (initially with room population)
 * 
 * @author nikok
 */
public class BSPDungeonGenerator implements DungeonGenerator {
	
	private boolean allowUnevenRooms = true;
	private int minRoomSize = 5;
	private static float unevenRoom = 1.25f;
	
	
	public BSPDungeonGenerator() {
		
	}
	
	@Override
	public TileMap generateDungeon(int xSize, int ySize) {
		BSPLeaf root = new BSPLeaf(0, 0, xSize, ySize);
		generateBSPAreas(root, this.minRoomSize);
		populateBSPAreasWithrooms(root);
		DungeonContainer dc = new DungeonContainer(xSize, ySize);
		setRoomsIntoContainer(dc, root);
		dc.printMap();
		return new TileMap(xSize, ySize, 32, dc.getIntMap());
	}
	
	private void generateBSPAreas(BSPLeaf root, int minLeafSize) {
		Stack<BSPLeaf> leaves = new Stack<>();
		leaves.push(root);
		while (!leaves.isEmpty()){
			System.out.println("splitting");
			BSPLeaf current = leaves.pop();
			boolean split = current.split(minLeafSize);
			if (split) {
				leaves.push(current.leftChild);
				leaves.push(current.rightChild);
			}
		}
	}
	
	private void populateBSPAreasWithrooms(BSPLeaf root) {
		Stack<BSPLeaf> leaves = new Stack<>();
		leaves.push(root);
		while (!leaves.isEmpty()) {
			System.out.println("populating");
			BSPLeaf current = leaves.pop();
			if (current == null) continue;
			if (current.hasChildren()) {
				leaves.push(current.leftChild);
				leaves.push(current.rightChild);
			} else {
				System.out.println("Populating room: "+minRoomSize+" "+current.x+" "+current.y+" "+current.width+" "+current.height);
				boolean roomAdded = current.setRoom(DungeonGenerator.generateRandomRoom(minRoomSize, current.width, minRoomSize , current.height));
				if (!roomAdded) System.out.println("Room setting failed in BSP!");
			}
		}
	}
	
	private void setRoomsIntoContainer(DungeonContainer dc, BSPLeaf root) {
		Stack<BSPLeaf> leaves = new Stack<>();
		leaves.push(root);
		while (!leaves.isEmpty()) {
			System.out.println("creating rooms");
			BSPLeaf current = leaves.pop();
			if (current.hasChildren()) {
				leaves.push(current.leftChild);
				leaves.push(current.rightChild);
			} else {
				DungeonRoom newRoom = new DungeonRoom(current.room.getWidth(), current.room.getHeight());
				newRoom.setPosition(current.x + current.room.getXPos() , current.y + current.room.getYPos());
				dc.addRoom(newRoom);
			}
		}
	}
	
	private class BSPLeaf {
		private int x, y, width, height;
		private BSPLeaf leftChild;
		private BSPLeaf rightChild;
		private DungeonRoom room;
		 
		
		public BSPLeaf(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public boolean hasChildren() {
			return (!(this.leftChild == null && this.rightChild == null));
		}
		
		public DungeonRoom getRoom() {
			return this.room;
		}
		
		public boolean setRoom(DungeonRoom room) {			
			if (room == null || room.getWidth()>this.width || room.getHeight() > this.height) return false;
			int xPos;
			int yPos;
			if (width  == room.getWidth()) xPos = 0;
			else xPos = DungeonGenerator.RND.nextInt(this.width - room.getWidth());
			if (height  == room.getHeight()) yPos = 0;
			else yPos = DungeonGenerator.RND.nextInt(this.height - room.getHeight());
			room.setPosition(xPos, yPos);
			this.room = room;
			return true;
		}
		
		/**
		 * Split this BSPleaf into two smaller leaves
		 * @param minLeafSize minimum size of leaf. if current size is smaller or equal to minsize times two, no splitting can be done
		 * @return True if room was split, false if room couldn't be split (room was already split)
		 */
		public boolean split(int minLeafSize) {
			if (leftChild != null || rightChild != null) return false;
			if (this.width <= 0 || this.height <= 0) return false;
			boolean splitHorizontal = true; 
			float splitDirection = 0f;
			
			//If very uneven rooms are allowed, it's up to random to see how the split happens
			if (allowUnevenRooms) {
				splitDirection = DungeonGenerator.RND.nextFloat();
			} else { //If we're disallowing uneven rooms, we force split direction in case of uneven areas
				if (width > height && (width / height) > unevenRoom) splitDirection =  1f;
				else if (height > width && (height / width) > unevenRoom)  splitDirection = 0f;  
				else splitDirection = DungeonGenerator.RND.nextFloat(); 
			}
			if (splitDirection <= 0.5f) splitHorizontal = false;
			
			
			
			int splitPosition;
			
			//Generate the children based on the split
			if (splitHorizontal) {
				if (this.height <= minLeafSize*2) return false;
				splitPosition = DungeonGenerator.RND.nextInt(this.height - (minLeafSize*2))+minLeafSize;
				this.leftChild = new BSPLeaf(this.x, this.y, this.width, splitPosition);
				this.rightChild = new BSPLeaf(this.x, this.y+splitPosition, this.width, this.height - splitPosition);
			} else {
				if (this.width <= minLeafSize*2) return false;
				splitPosition = DungeonGenerator.RND.nextInt(this.width - (minLeafSize*2))+minLeafSize;
				this.leftChild = new BSPLeaf(this.x, this.y, splitPosition, this.height);
				this.rightChild = new BSPLeaf(this.x + splitPosition, this.y, this.width - splitPosition, this.height);
			}
			System.out.println("Generated left child: x:"+leftChild.x+", y:"+leftChild.y+" w:"+leftChild.width+" h:"+leftChild.height);
			System.out.println("Generated right child: x:"+rightChild.x+", y:"+rightChild.y+" w:"+rightChild.width+" h:"+rightChild.height);
			return true;
		}
		
	}
	
    



}

