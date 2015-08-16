/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.world.Location;
import java.util.List;

/**
 *
 * @author daedra
 */
public class CollisionMap {
    /* Location that this CollisionMap is based on */
    private Location location;
    /* Nodes that make up the map */
    private Node[][] nodeMap; 
    /* Visited is used for pathfinding to determine which nodes have already been visited */
    private Boolean[][] visited;
    private int mapTileWidth;
    private int mapTileHeight;
    private int nodeSize;
            
    public CollisionMap(Location l, int nodeSize) {
        this.location = l;
        this.nodeSize = nodeSize; //size of nodes in map pixels - usually same as tilesize
        
        //First we'll convert map to tiles, even if it's BGMap
        this. mapTileWidth = (int)(l.getMap().getWidth() / nodeSize);
        this. mapTileHeight = (int)(l.getMap().getHeight() / nodeSize); 
        nodeMap = new Node[mapTileWidth][mapTileHeight];
        visited = new Boolean[mapTileWidth][mapTileHeight];
        //Then populate a nodemap with empty (=collisionLevel 0) nodes
        for (int row = 0; row < this.mapTileHeight;row++) {
            for (int column = 0; column < this.mapTileWidth; column++) {
                this.nodeMap[column][row] = new Node(column, row, nodeSize, 0);
            }
        }
    }
    
        public void updateCollisionLevels() {
        //Clear the old map
        this.nodeMap = new Node[mapTileWidth][mapTileHeight];
        //Then populate a nodemap with empty (=passable) nodes
        for (int row = 0; row < this.mapTileHeight;row++) {
            for (int column = 0; column < this.mapTileWidth; column++) {
                this.nodeMap[column][row] = new Node(column, row, nodeSize, 0);
            }
        }
        //Go through all the nodes and check the location if it has something at them
        List<MapObject> mobs = this.location.getMOBList();
        //Mists.logger.info("Moblist has " +mobs.size()+" objects");
        for (MapObject mob : mobs) {
            //Mob blocks nodes from its top left corner...
            int mobXNodeStart = ((int)mob.getxPos() / nodeSize);
            int mobYNodeStart = ((int)mob.getyPos() / nodeSize);
            //... to its bottom right corner
            int mobXNodeEnd = ((int)(mob.getxPos()+mob.getSprite().getWidth())/ nodeSize); 
            int mobYNodeEnd = ((int)(mob.getyPos()+mob.getSprite().getHeight())/ nodeSize); 
            int mobCL = mob.getFlag("collisionLevel");
            
            //Structures mark all blocked nodes with collisionLevel
            if (mob instanceof Structure) {
                for (int row = mobYNodeStart; row < mobYNodeEnd;row++ ) {
                    for (int column = mobXNodeStart; column < mobXNodeEnd;column++) {
                        this.nodeMap[column][row].setCollisionLevel(mobCL);
                    }
                }
            } else { //Creatures block only their original spot
                this.nodeMap[mobXNodeStart][mobYNodeStart].setCollisionLevel(mobCL);
            }
            
            
            //Mists.logger.info("["+mobXNode+","+mobYNode+"] has a "+mob.getName()+ ": set to CL "+mobCL);
        }
    }
    
    
    /* isBlocked checks if the given unit can pass through the given node 
     * returns False if tile is blocked, true if not. Every creature should be able to cross CL 0
    */
    public boolean isBlocked(List<Integer> crossableTerrain ,int x, int y) {
        return ((nodeMap[x][y] == null) ||
		(!crossableTerrain.contains(nodeMap[x][y].getCollisionLevel())));
    }

    public void pfVisit(int xCoor, int yCoor) {
        this.visited[xCoor][yCoor] = true;
    }
    
    public boolean isVisited (int xCoor, int yCoor) {
        if (this.visited[xCoor][yCoor] == null) return false;
        return this.visited[xCoor][yCoor];
    }
    
    public int getMapTileWidth() {
        return this.mapTileWidth;
    }
    
    public int getMapTileHeight() {
        return this.mapTileHeight;
    }
    
    public int getNodeSize() {
        return this.nodeSize;
    }
    
    public Node getNode( int x, int y) {
        return this.nodeMap[x][y];
    }
    
    //TODO: Make it so that different terrain can slow or speed up movers
    public float getMovementCost(List<Integer> movementModifiers, int startX, int startY, int targetX, int targetY) {
		return 1; // For now all cost 1;
	}
    
}
