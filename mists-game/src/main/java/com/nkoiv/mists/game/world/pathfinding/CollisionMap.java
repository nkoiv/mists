/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

import com.nkoiv.mists.game.Mists;
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
    private boolean structuresOnly;
            
    public CollisionMap(Location l, int nodeSize) {
        this.location = l;
        this.nodeSize = nodeSize; //size of nodes in map pixels - usually same as tilesize
        Mists.logger.info("Generating collisionmap for "+l.getName());
        double startTime = System.currentTimeMillis();
        //First we'll convert map to tiles, even if it's BGMap
        this.mapTileWidth = (int)(l.getMap().getWidth() / nodeSize)+1;
        this.mapTileHeight = (int)(l.getMap().getHeight() / nodeSize)+1; 
        nodeMap = new Node[mapTileWidth][mapTileHeight];
        visited = new Boolean[mapTileWidth][mapTileHeight];
        //Then populate a nodemap with empty (=collisionLevel 0) nodes
        for (int row = 0; row < this.mapTileHeight;row++) {
            for (int column = 0; column < this.mapTileWidth; column++) {
                this.nodeMap[column][row] = new Node(column, row, nodeSize, 0);
            }
        }        
        Mists.logger.info("Collisionmap generated in "+(System.currentTimeMillis()-startTime)+"ms");
    }
    
    public void updateCollisionLevels() {
        //Mists.logger.info("Updating collisionmap for "+this.location.getName());
        double startTime = System.currentTimeMillis();
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
            //Mists.logger.info("Doing collisionmapstuff for "+mob.getName());
            //Mob blocks nodes from its top left corner...
            int mobXNodeStart = ((int)mob.getXPos() / nodeSize);
            int mobYNodeStart = ((int)mob.getYPos() / nodeSize);
            //... to its bottom right corner
            int mobXNodeEnd = ((int)(mob.getXPos()+mob.getSprite().getWidth()-1)/ nodeSize); 
            int mobYNodeEnd = ((int)(mob.getYPos()+mob.getSprite().getHeight()-1)/ nodeSize); 
            int mobCL = mob.getFlag("collisionLevel");
            
            //Structures mark all blocked nodes with collisionLevel
            if (mob instanceof Structure) {
                //Mists.logger.info("This is a structure at "+mobYNodeStart+","+mobXNodeStart);
                for (int row = mobYNodeStart; row <= mobYNodeEnd;row++ ) {
                    for (int column = mobXNodeStart; column <= mobXNodeEnd;column++) {
                        //Mists.logger.info("Should be setting some CL at "+column+","+row);
                        if(this.isOnMap(column, row))
                            this.nodeMap[column][row].setCollisionLevel(mobCL);
                            //Mists.logger.info("Added CL "+mobCL+" at "+column+","+row);
                    }
                }
            } else if (!this.structuresOnly) { //Creatures block only their original spot
                //Mists.logger.info("This is NOT structure");
                if(this.isOnMap(mobXNodeStart, mobYNodeStart))
                    this.nodeMap[mobXNodeStart][mobYNodeStart].setCollisionLevel(mobCL);
            }
            
            
            //Mists.logger.info("["+mobXNode+","+mobYNode+"] has a "+mob.getName()+ ": set to CL "+mobCL);
        }
        //Mists.logger.info("Collisionmap updated in "+(System.currentTimeMillis()-startTime)+"ms");
    }
    
    
    /* isBlocked checks if the given unit can pass through the given node 
     * returns False if tile is blocked, true if not. Every creature should be able to cross CL 0
    */
    public boolean isBlocked(List<Integer> crossableTerrain ,int x, int y) {
        if (x>this.mapTileWidth-1 || y>this.mapTileHeight-1) return true;
        return ((nodeMap[x][y] == null) ||
		(!crossableTerrain.contains(nodeMap[x][y].getCollisionLevel())));
    }
    
    public boolean isBlocked(int crossableTerrain, int x, int y) {
        if (x>this.mapTileWidth-1 || y>this.mapTileHeight-1) return true;
        return ((nodeMap[x][y] == null) ||
		!(crossableTerrain == nodeMap[x][y].getCollisionLevel()));
    }

    public void pfVisit(int xCoor, int yCoor) {
        this.visited[xCoor][yCoor] = true;
    }
    
    public boolean isVisited (int xCoor, int yCoor) {
        if (this.visited[xCoor][yCoor] == null) return false;
        return this.visited[xCoor][yCoor];
    }
    
    public void setStructuresOnly (boolean onlyStructures) {
        this.structuresOnly = onlyStructures;
    }
    
    public boolean isStructuresOnly() {
        return this.structuresOnly;
    }
    
    private boolean isOnMap (int xCoor, int yCoor) {
        if ((xCoor >= 0) && (xCoor <= this.mapTileWidth) && (yCoor >= 0) && (yCoor <= this.mapTileHeight))  {
            return true;
        } else {
            return false;
        }
    }
    
    public int getMapTileWidth() {
        return this.mapTileWidth;
    }
    
    public int getMapTileHeight() {
        return this.mapTileHeight;
    }
    
    public Location getLocation() {
        return this.location;
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