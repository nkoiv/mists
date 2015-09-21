/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.pathfinding.util.ComparingNodeQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * PathFinder uses A* principles in finding the shortest working route between point A and pointB
 * Many of the supporting functions are used to give Creatures ability to figure their next course of action.
 * The original pathfinder was based on http://www.cokeandcode.com/main/tutorials/path-finding/ (Kevin Glass)
 * After that it was rewritten with the help of the book "Artificial Intelligence for Games" (2e) by Millington and Funge
 * Modified to use different heuristics for cost. Restructured to use same Nodes for map and Path.
 * Modified to use collision levels for movement. Created clearance metrics for large objects.
 * Added separate methods for checking neighbours.
 * @author nikok
 */
public class PathFinder {
	
    private PathfinderAlgorithm algo;
    //private Node[][] nodes; //Nodemap used for pathfinding, filled with costs as we calculate them
     private CollisionMap map; //The collision map derived from the Locations MOBs
    /** The the calculator we're applying to determine which nodes to search first */
    private MoveCostCalculator calc;

    public PathFinder(CollisionMap map, int maxSearchDistance, boolean allowDiagonalMovement) {
        this.map = map;
        this.algo = new AStarPathfinder(this, maxSearchDistance, allowDiagonalMovement);
        //double startTime = System.currentTimeMillis();
        /* TODO: Consider how often/when the clearance maps should be updated
        *  TODO: Should Creatures be on clearance maps, or just structures?
        */
        this.calc = new MoveCostCalculator(0); //Defaults to Manhattan, 0 for Manhattan, 1 for Diagonal, 2 for Euclidean
    }

    /**
    * PathTowards gives the direction of the next node on the Path
    * This method is the one units usually ask when they want to start heading towards a point on the map
    * @param unitSize Size of the unit for collisions
    * @param crossableTerrain List of terrain the unit can cross
    * @param startX X coordinate of the starting position
    * @param startY Y coordinate of the starting position
    * @param goalX X coordinate of the goal
    * @param goalY Y coordinate of the goal
    * @return Direction to go to get towards goal
    */
    public Direction directionTowards (double unitSize, List<Integer> crossableTerrain,double startX, double startY, double goalX, double goalY) {
        Node targetNode = nextTileOnPath(unitSize, crossableTerrain, startX, startY, goalX, goalY);
        //Get the direction towards the center of the next tile on the path
        double xChange = (targetNode.getX() * this.getTileSize())+(this.getTileSize()/2) - startX;
        double yChange = (targetNode.getY() * this.getTileSize())+(this.getTileSize()/2) - startY;
        //Mists.logger.log(Level.INFO, "Path from {0},{1} ({2},{3}) to {4},{5}({6},{7})", new Object[]{startX, startY, (int)startX/this.getTileSize(), (int)startY/this.getTileSize(),targetNode.getX(),targetNode.getY(), (targetNode.getX() * this.getTileSize())+(this.getTileSize()/2), (targetNode.getY() * this.getTileSize())+(this.getTileSize()/2)});
        return getDirection(xChange, yChange);
     }

    public double[] coordinatesTowards (double unitSize, List<Integer> crossableTerrain,double startX, double startY, double goalX, double goalY) {
        Node targetNode = nextTileOnPath(unitSize, crossableTerrain, startX, startY, goalX, goalY);
        //Return the (center) coordinates of the next tile on path
        double xCoord = (targetNode.getX()*this.getTileSize()) + (this.getTileSize()/2);
        double yCoord = (targetNode.getX()*this.getTileSize()) + (this.getTileSize()/2);
        //Mists.logger.log(Level.INFO, "Path from {0},{1} ({2},{3}) to {4},{5}", new Object[]{startX, startY, (int)startX/this.getTileSize(), (int)startY/this.getTileSize(), xCoord, yCoord});
        return new double[]{xCoord, yCoord};
    }

    private Node nextTileOnPath(double unitSize, List<Integer> crossableTerrain,double startX, double startY, double goalX, double goalY) {
        int clearanceNeed = (int)(unitSize/this.map.getNodeSize());
        //Mists.logger.info("Clearance needed: "+clearanceNeed+" (unit size"+unitSize+", nodesize "+this.map.getNodeSize());

        /*
        * The units are in freely moving double -type coordinates on the game map
        * First we need to convert these to rigid int -types that the PathFinder uses
        */
        int sX = ((int) startX / this.map.getNodeSize());
        int sY = ((int) startY / this.map.getNodeSize());
        int gX = ((int) goalX / this.map.getNodeSize());
        int gY = ((int) goalY / this.map.getNodeSize());

        Path pathToGoal = algo.findPath(map, clearanceNeed, crossableTerrain, sX, sY, gX, gY);

        if (pathToGoal == null || pathToGoal.getLength()==0) {
            //Got an empty path. Probably means no route was found.
            //Just head in the general direction of the target.
            //Mists.logger.info("No path found, giving the Node of the target");
            return new Node(gX, gY);
        }
        //Mists.logger.info("Goal was at ["+gX+","+gY+"] got the path:" + pathToGoal.toString());
        if (pathToGoal.getLength() < 2) {
            //Mists.logger.info("Next to goal, returning node the goal is at ");
            return pathToGoal.getNode(pathToGoal.getLength()-1);
        } else {
            //Check if there's corners we might get stuck into:
            List<Node> corners = algo.DiagonalNeighbours(map, clearanceNeed, crossableTerrain, pathToGoal.getNode(0).getX(), pathToGoal.getNode(0).getY());
            if (corners.size() == 4) {
                //All corners clear, move on
                return pathToGoal.getNode(1);
            } else {
                //Something might be obstructing movement
                //We need to be at least past our current node center before we head towards new node.
                double xCoord = (pathToGoal.getNode(1).getX()*this.getTileSize()) + (this.getTileSize()/2);
                double yCoord = (pathToGoal.getNode(1).getX()*this.getTileSize()) + (this.getTileSize()/2);
                Direction currentDirection = getDirection(startX-xCoord, startY-yCoord);
                switch(currentDirection) {
                //Cardinal directions are fine
                case UP: return pathToGoal.getNode(1);
                case DOWN: return pathToGoal.getNode(1);
                case LEFT: return pathToGoal.getNode(1);
                case RIGHT: return pathToGoal.getNode(1);
                //Problems arise if we're moving diagonally without pathfinder knowing it
                case UPRIGHT: return pathToGoal.getNode(0);
                case UPLEFT: return pathToGoal.getNode(0);
                case DOWNRIGHT: return pathToGoal.getNode(0);
                case DOWNLEFT: return pathToGoal.getNode(0);
                default: return pathToGoal.getNode(0);
                }
            }
        }
    }

    /**
     * findNearesOpenNode tries to find an open node adjacent to the target node.
     * This is useful when trying to move as close to as possible on a blocked
     * target (a structure etc)
     * @param crossableTerrain Terrainwalking ability of the mover
     * @param goalX Node to search around
     * @param goalY Node to search around
     * @return 
     */
    private int[] findNearestOpenNode (List<Integer> crossableTerrain, int goalX, int goalY)  {
        int[] openNode = new int[2];
        if (!map.isBlocked(crossableTerrain, goalX, goalY)) {
            openNode[1] = goalX;
            openNode[2] = goalY;
            return openNode;
        } else {
            List<Node> openNeighbours = algo.Neighbours(map, crossableTerrain, goalY, goalY);
            if (openNeighbours.size() > 0) { //Just try to go to whichever open block
                openNode[0] = openNeighbours.get(0).getX();
                openNode[1] = openNeighbours.get(0).getY();
                return openNode;
            } else {
                return null; //There's not even open neighbours
            }
        }
    }

    
    public static Direction getDirection(double xChange, double yChange) {
        if (xChange==0) { //Not moving left or right
            if (yChange==0) return Direction.STAY;
            if (yChange<0) return Direction.UP;
            if (yChange>0) return Direction.DOWN;
        }
        if (xChange>0) { //Moving right
            if (yChange==0) return Direction.RIGHT;
            if (yChange<0) return Direction.UPRIGHT;
            if (yChange>0) return Direction.DOWNRIGHT;
        }
        if (xChange<0) { // Moving left
            if (yChange==0) return Direction.LEFT;
            if (yChange<0) return Direction.UPLEFT;
            if (yChange>0) return Direction.DOWNLEFT;
        }
        return Direction.STAY; //last resort
    }
        
    /** When moving in tight spaces, it's imperative for
        *  the mover to be fully inside the tile Pathfinder thinks its in.
        *  Otherwise the mover might get stuck on corners
        *  This method returns the center of the given tile in "real coordinates"
        */
    private double[] getTileCoordinates(int tileXCoor, int tileYCoor, int tileSize){    
        double[] realCoordinates = new double[2];
        realCoordinates[0] = (tileXCoor * tileSize)+(tileSize/2);
        realCoordinates[1] = (tileYCoor * tileSize)+(tileSize/2);
        return realCoordinates;
    }

    public void printCollisionMapIntoConsole() { // for testing
        System.out.println("-------");
        for (int row = 0; row < this.map.getMapTileHeight();row++) {
            System.out.println("");
            for (int column = 0; column < this.map.getMapTileWidth(); column++) {
                System.out.print("["+this.map.getNode(column, row).getCollisionLevel()+"]");
            }
        }
         //System.out.println("-------");
     }
        
        
    //If trying to find a path for an object of unspecified size, assume it's tilesize 1
    public Path findPath (List<Integer> crossableTerrain, int startX, int startY, int goalX, int goalY) {
        return this.algo.findPath(map, 1,crossableTerrain, startX, startY, goalX, goalY);
    }
        
        

    
    private boolean isValidLocation(List<Integer> crossableTerrain, int currentX, int currentY, int goalX, int goalY) {
            boolean invalid = (goalX < 0) || (goalY < 0) || (goalX >= map.getMapTileWidth()) || (goalY >= map.getMapTileWidth());

            if ((!invalid) && ((currentX != goalX) || (currentY != goalX))) {
                    invalid = map.isBlocked(crossableTerrain, goalX, goalY);
            }

            return !invalid;
    }

    public double getMovementCost(List<Integer> movementAbilities, int currentX, int currentY, int goalX, int goalY) {
            return this.calc.getCost(this.map, movementAbilities, currentX, currentY, goalX, goalY);
    }
    
    /**
     * Generate a clearance map with the map this pathfinder is tied to
     * @param crossableTerrain
     * @return clearance map
     */
    public int[][] getClearanceMap (int crossableTerrain) {
        return getClearanceMap(crossableTerrain, this.map);
    }
    /**
    * ClearanceMap tells how large objects can fit in the given tile
    * For example 2 means that a 2x2 tile sized object could fit here
    * ClearanceMaps are generated per CrossableTerrain.
    * @param crossableTerrain terrainwalking ability to generate the clearance map with
    * @param collisionMap the collision map containing blocking objects
    * @return generated clearance map
    */ 
    public static int[][] getClearanceMap (int crossableTerrain, CollisionMap collisionMap) {
        int[][] clearanceMap = new int[collisionMap.getMapTileWidth()][collisionMap.getMapTileHeight()];        
        int level;
       // Mists.logger.log(Level.INFO, "Clearance map generation started. Size of map: [{0},{1}]", new Object[]{collisionMap.getMapTileWidth(), collisionMap.getMapTileHeight()});
        //Check through all the tiles on the collisionMap and calculate their clearanceLevels
        for (int y=0;y<collisionMap.getMapTileHeight();y++) {  //collisionMap.getMapTileHeight()
            for (int x=0;x<collisionMap.getMapTileWidth();x++) { // collisionMap.getMapTileWidth()
                //Mists.logger.info("Starting work at ["+x+","+y+"]");
                if (collisionMap.isBlocked(crossableTerrain, x, y)) { 
                    clearanceMap[x][y] = 0; // Blocked tiles are given value of 0. Only sizeless things can past through.
                } else { //Coordinates were not blocked, so we can start iterating clearance levels
                    /* Edges-list:
                    *  X00 -> 0X0 -> 00X
                    *  000    XX0    00X
                    *  000    000    XXX
                    */
                    ArrayList<int[]> edges = new ArrayList<>();    
                    int[] start = new int[2]; //create node to start with
                    start[0] = x; start[1] = y; //designate it to be the starting point
                    boolean blocked = false;
                    int lap = 0;
                    while (!blocked) { //keep building the are until we hit an obstacle
                        lap++;
                        //Mists.logger.info("Doing lap "+lap+" at ["+x+","+y+"]");
                        //update the Clearance levels on current box
                        for (int row=0;row<lap;row++) {
                            for (int column=0;column<lap;column++) {
                               if (clearanceMap[x+column][y+row] < lap) {
                                   if(clearanceMap[x+column][y+row]<(lap-row) && clearanceMap[x+column][y+row]<(lap-column))
                                       clearanceMap[x+column][y+row]++; //increase the level (up to lap)
                               }
                            }
                        }
                        //add all the edges of this tile to the edges list
                        edges.clear();
                        for (int row=0;row<lap+1;row++) {
                            if(row < lap) { //add a right side edge
                                int[] edge = new int[]{start[0]+lap, start[1]+row};
                                edges.add(edge);
                                //System.out.println("Added right side edge");
                            } else { //add the bottom edge
                                for (int column=0;column<lap+1;column++) {
                                    int[] edge = new int[]{start[0]+column, start[1]+lap};
                                    edges.add(edge);
                                    //System.out.println("Added bottom edge");
                                }
                            }
                        }
                        //Mists.logger.info(edges.size()+" edges to check in total");
                        //Go through the current edges and see if we can expand
                        while (edges.size() > 0 && !blocked) {
                            //Mists.logger.info("Edges left to do: " +edges.size());
                            //Check if this tile on the edge is blocked
                            if (collisionMap.isBlocked(crossableTerrain, edges.get(0)[0], edges.get(0)[1])) {
                                blocked = true;
                            }
                            //Remove it from the list 
                            edges.remove(0); 
                        }
                        //if(blocked) Mists.logger.info("Hit a block with ["+x+","+y+"] on round "+lap);
                        //System.out.println("Doing ["+x+","+y+"], lap "+lap);
                        //this.printArrayMap(clearanceMap);
                        //System.out.println("");
                    }
                }
            }
            
        }
        return clearanceMap;
    }
    
    public int getTileSize() {
        return this.map.getNodeSize();
    }
    
    private void printArrayMap (int[][] arrayMap) {
        System.out.println("-------");
            for (int row = 0; row < this.map.getMapTileHeight();row++) {
                System.out.println("");
                for (int column = 0; column < this.map.getMapTileWidth(); column++) {
                    System.out.print("["+arrayMap[column][row]+"]");
                }
            }
    }
    
    //TODO: For testing purposes
    public void printClearanceMapIntoConsole (int crossableTerrain) { 
        System.out.println("Getting a new clearance map for crossing ["+crossableTerrain+"]");
        int[][] clearanceMap = this.getClearanceMap(crossableTerrain, this.map);
        this.printArrayMap(clearanceMap);
             //System.out.println("-------");
    }

}
