/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Original pathfinder based on http://www.cokeandcode.com/main/tutorials/path-finding/ (Kevin Glass)
 * Modified to use different heuristics for cost. Restructured to use same Nodes for map and Path.
 * Modified to use collision levels for movement. Created clearance metrics for large objects.
 * Added separate methods for checking neighbours.
 * @author nikok
 */
public class PathFinder {
	
	private ArrayList closedNodes = new ArrayList();
	private SortedList openNodes = new SortedList();
	private CollisionMap map; //The collision map derived from the Locations MOBs
        private HashMap<Integer, int[][]> clearanceMaps;
	private Node[][] nodes; //Nodemap used for pathfinding, filled with costs as we calculate them
	private int maxSearchDistance;
	
	private boolean allowDiagonalMovement;
	/** The the calculator we're applying to determine which nodes to search first */
	private MoveCostCalculator calc;

	public PathFinder(CollisionMap map, int maxSearchDistance, boolean allowDiagonalMovement) {
            this.map = map;
            /* TODO: Consider how often/when the clearance maps should be updated
            *  TODO: Should Creatures be on clearance maps, or just structures?
            */
            this.clearanceMaps = new HashMap<>(); 
            this.clearanceMaps.put(0, getClearanceMap(0, map)); //Base clearanceMap for 0-type movement.
            this.maxSearchDistance = maxSearchDistance;
            this.allowDiagonalMovement = allowDiagonalMovement;
            this.calc = new MoveCostCalculator(); //Defaults to Manhattan
            this.nodes = new Node[map.getMapTileWidth()][map.getMapTileHeight()];
            for (int row = 0; row < this.map.getMapTileHeight();row++) {
                for (int column = 0; column < this.map.getMapTileWidth(); column++) {
                    this.nodes[column][row] = new Node(column, row);
                }
            }
	}
        
        public Direction pathTowards (double unitSize, List<Integer> crossableTerrain,double startX, double startY, double goalX, double goalY) {
            int clearanceNeed = (int)(unitSize/this.map.getNodeSize());
            int sX = ((int) startX / this.map.getNodeSize());
            int sY = ((int) startY / this.map.getNodeSize());
            int gX = ((int) goalX / this.map.getNodeSize());
            int gY = ((int) goalY / this.map.getNodeSize());
            
            Path pathToGoal = this.findPath(clearanceNeed, crossableTerrain, sX, sY, gX, gY);
            
            if (pathToGoal == null) {
                //got an empty path - stay put!
                return Direction.STAY;
            }
            
            if (pathToGoal.getLength() < 2) {
                //Mists.logger.info("Next to object, staying put");
                return Direction.STAY;
            } else {
                int xChange = pathToGoal.getNode(1).getX() - sX;
                int yChange = pathToGoal.getNode(1).getY() - sY;
                //Mists.logger.log(Level.INFO, "Figuring direction from {0},{1} to {2},{3}", new Object[]{sX, sY, pathToGoal.getNode(1).getX(), pathToGoal.getNode(1).getY()});
                return getDirection(xChange, yChange);
            }
         }
        
        private int[] findNearestOpenNode (List<Integer> crossableTerrain, int goalX, int goalY)  {
            int[] openNode = new int[2];
            if (!map.isBlocked(crossableTerrain, goalX, goalY)) {
                openNode[1] = goalX;
                openNode[2] = goalY;
                return openNode;
            } else {
                List<Node> openNeighbours = this.Neighbours(crossableTerrain, goalY, goalY);
                if (openNeighbours.size() > 0) { //Just try to go to whichever open block
                    openNode[0] = openNeighbours.get(0).getX();
                    openNode[1] = openNeighbours.get(0).getY();
                    return openNode;
                } else {
                    return null; //There's not even open neighbours
                }
            }
        }
        
        private List<Node> Neighbours(List<Integer> crossableTerrain, int x, int y) {
            //if no size given, assume clearance need of 1
            return Neighbours(1,crossableTerrain, x, y);
        } 
        private List<Node> Neighbours(int unitSize, List<Integer> crossableTerrain, int x, int y) {
        /*Neighbours only lists cardinal directions
        * because being surrounded cardinally blocks movement
        * TODO: Consider if this is good
        */
            int N = y - 1;
            int S = y + 1;
            int E = x + 1;
            int W = x - 1;
            boolean myN = (N > -1 && !this.map.isBlocked(crossableTerrain, x, N) 
                    && this.hasClearance(unitSize, crossableTerrain, x, N));
            boolean myS = (S < this.map.getMapTileHeight() && !this.map.isBlocked(crossableTerrain, x, S)
                    && this.hasClearance(unitSize, crossableTerrain, x, S));
            boolean myE = (E < this.map.getMapTileWidth() && !this.map.isBlocked(crossableTerrain, E, y)
                    && this.hasClearance(unitSize, crossableTerrain, E, y));
            boolean myW = (W > -1 && !this.map.isBlocked(crossableTerrain, W, y)
                    && this.hasClearance(unitSize, crossableTerrain, W, y));
            ArrayList<Node> result = new ArrayList<>();
            if(myN) {
                result.add(this.nodes[x][N]);
            }
            if(myE) {
                result.add(this.nodes[E][y]);
            }
            if(myS) {
                result.add(this.nodes[x][S]);
            }
            if(myW) {
                result.add(this.nodes[W][y]);
            }
            return result;
    }
    private List<Node> DiagonalNeighbours(List<Integer> crossableTerrain, int x, int y) {
        //if no size given, assume clearance need of 1
        return DiagonalNeighbours(1, crossableTerrain, x, y);
    }
    private List<Node> DiagonalNeighbours(int unitSize, List<Integer> crossableTerrain, int x, int y) {
        //Return all NE, NW, SE and SW that are passable without squeezing through
        ArrayList<Node> result = new ArrayList<>();
        int N = y - 1;
        int S = y + 1;
        int E = x + 1;
        int W = x - 1;
        boolean myN = (N > -1 && !this.map.isBlocked(crossableTerrain, x, N)
                && this.hasClearance(unitSize, crossableTerrain, x, N));
        boolean myS = (S < this.map.getMapTileHeight() && !this.map.isBlocked(crossableTerrain, x, S)
                && this.hasClearance(unitSize, crossableTerrain, x, S));
        boolean myE = (E < this.map.getMapTileWidth() && !this.map.isBlocked(crossableTerrain, E, y)
                && this.hasClearance(unitSize, crossableTerrain, E, y));
        boolean myW = (W > -1 && !this.map.isBlocked(crossableTerrain, W, y)
                && this.hasClearance(unitSize, crossableTerrain, W, y));        
        if (myN) {
            if (myE && !this.map.isBlocked(crossableTerrain, E, N)
                    && this.hasClearance(unitSize, crossableTerrain, E, N))
                    result.add(nodes[E][N]);
            if (myW && !this.map.isBlocked(crossableTerrain, W, N)
                    && this.hasClearance(unitSize, crossableTerrain, W, N)) 
                    result.add(nodes[W][N]);
        }
        if (myS) {
            if (myE && !this.map.isBlocked(crossableTerrain, E, S)
                    && this.hasClearance(unitSize, crossableTerrain, E, S))
                result.add(nodes[E][S]);
            if (myW && !this.map.isBlocked(crossableTerrain, W, S)
                    && this.hasClearance(unitSize, crossableTerrain, W, S)) 
                    result.add(nodes[W][S]);
        }      
        return result;
    }

        private Direction getDirection(int xChange, int yChange) {
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
        private Path findPath (List<Integer> crossableTerrain, int startX, int startY, int goalX, int goalY) {
            return findPath(1,crossableTerrain, startX, startY, goalX, goalY);
        }
        
        /*
        * THE MAIN PATHFINDING ROUTINE
        * Check http://www.policyalmanac.org/games/aStarTutorial.htm 
        * TODO: Implement some speed tips
        */
	private Path findPath(int tileSize,List<Integer> crossableTerrain, int startX, int startY, int goalX, int goalY) {
        Path path = new Path();
        Node start = nodes[startX][startY];
        Node goal = nodes[goalX][goalY];
        start.setCost(this.getMovementCost(crossableTerrain, start.getX(), start.getY(), goal.getX(), goal.getY()));
       
        //Check we have all the clearanceMaps we need.
        for (Integer terrainType : crossableTerrain) {
            if (!this.clearanceMaps.containsKey(terrainType)) { //if we dont already have the given map, we need to generate it
                this.clearanceMaps.put(terrainType, getClearanceMap(terrainType, this.map));
            }
        }
        closedNodes.clear(); //Reset the closed nodes
        openNodes.clear(); //Reset the open nodes
        
        openNodes.add(start);
        Node currentNode = start;
        //path.addStep(start.getX(), start.getY());
        while (openNodes.size() > 0 && path.getLength() < this.maxSearchDistance) {
            if(path.getLength() > this.maxSearchDistance) break; //stop if we've reached max depth 
            //Iterate the list until all open nodes have been dealt with
            //Mists.logger.log(Level.INFO, "Currently at: {0},{1} - Goal at: {2}, {3}", new Object[]{currentNode.getX(), currentNode.getY(), goal.getX(), goal.getY()});
            //Mists.logger.info("Path has "+path.getLength()+ " steps in it. Number of open points: "+openNodes.size() + ". Number of closed points: "+closedNodes.size());
            if(currentNode.getX() == goal.getX() && currentNode.getY() == goal.getY()) { //we're at the goal
                //Mists.logger.info("Found goal!");
                openNodes.clear();
                break;
            } else if (currentNode.isNextTo(goalX, goalY) && map.isBlocked(crossableTerrain, goalX, goalY)) { //We're next to Goal, but it's unreachable
                Mists.logger.info("Next to goal but cant go there");
                openNodes.clear();
                break;
            } else { //not at goal yet
                currentNode = (Node)openNodes.first(); //First on the list has the lowest cost
                openNodes.remove(currentNode);
                closedNodes.add(currentNode); // this node doesnt need to be calculated again
                //find open neighbours
                List<Node> neighbours = new ArrayList<>(); //add in all traversable neighbours
                neighbours.addAll(this.Neighbours(tileSize,crossableTerrain, currentNode.getX(), currentNode.getY()));
                neighbours.addAll(this.DiagonalNeighbours(tileSize,crossableTerrain, currentNode.getX(), currentNode.getY()));
                //Mists.logger.info("Neigbours before Closed trimming:" +neighbours.size());
                neighbours.removeAll(closedNodes); //Close routes we know are bad
                //Mists.logger.info("Neigbours after Closed trimming:" +neighbours.size());
                //Mists.logger.log(Level.INFO, "{0} neighbouring tiles found for {1},{2}", new Object[]{neighbours.size(), currentNode.getX(), currentNode.getY()});
                if (neighbours.size()<=1) { //if we can only go backwards
                    currentNode = path.getNode((path.getLength()-1)); //Move to last good node
                } else {
                    List<Node> currentNeighbours = new ArrayList<>();
                    for (Node n : neighbours) { //Make pathpoints to all availale nodes and tag them open
                        if(!path.containsNode(n.getX(), n.getY())) { //dont go to direction we came from
                            //Mists.logger.info("Adding a new node to openList");
                            Node nn = nodes[n.getX()][n.getY()];
                            nn.setPreviousNode(currentNode);
                            nn.setDepth(path.getLength());
                            nn.setCost(this.getMovementCost(crossableTerrain, nn.getX(), nn.getY(), goal.getX(), goal.getY()));
                            if (!openNodes.contains(nn)) {
                                openNodes.add(nn); //remember the node in case we need to go back to it
                            }  
                            //currentNeighbours.add(nn); // <- What was the point of this line again?
                        }
                    }

                    path.addStep(currentNode);
                    //Mists.logger.info(currentNode.getX()+","+currentNode.getY()+" set as current node");
                }
            }    
        }
        //Mists.logger.info("Goal was at ["+goalX+","+goalY+"] Returning path:" + path.toString());
        return path;
    }

    private Node getFirstInOpen() {
            return (Node) openNodes.first();
    }

    private void addToOpen(Node node) {
            this.openNodes.add(node);
    }

    private boolean inOpenList(Node node) {
            return this.openNodes.contains(node);
    }


    private void removeFromOpen(Node node) {
            this.openNodes.remove(node);
    }

    private void addToClosed(Node node) {
            this.closedNodes.add(node);
    }

    private boolean inClosedList(Node node) {
            return this.closedNodes.contains(node);
    }

    private void removeFromClosed(Node node) {
            this.closedNodes.remove(node);
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
    

    /*
    * Checking unit with multiple terrain movements fitting in a mapnode
    */
    private boolean hasClearance (int unitSize, List<Integer> crossableTerrain, int x, int y) {
        for (Integer terrainType : crossableTerrain) {
                if (this.hasClearance(unitSize, terrainType,x, y)) {
                    return true;
                }
            }
        return false;
    }
    
    /*
    * Check if unit of given size can fit in the given tile with given movement type
    */
    private boolean hasClearance (int unitSize, int terrainNumber, int x, int y) {
        if (!this.clearanceMaps.containsKey(terrainNumber)) { //If we dont have the map for this type of terrain, generate it
            Mists.logger.info("Tried to check clearance but no clearance map - generating a new one ("+terrainNumber+")");
            this.clearanceMaps.put(terrainNumber, getClearanceMap(terrainNumber, this.map));
        }
        //Check if the unit can fit in the square
        return this.clearanceMaps.get(terrainNumber)[x][y] >= unitSize;
    }
    
    /*
    * ClearanceMap tells how large objects can fit in the given tile
    * For example 2 means that a 2x2 tile sized object could fit here
    * ClearanceMaps are generated per CrossableTerrain.
    */   
    public static int[][] getClearanceMap (int crossableTerrain, CollisionMap collisionMap) {
        int[][] clearanceMap = new int[collisionMap.getMapTileWidth()][collisionMap.getMapTileHeight()];        

        int level;
        Mists.logger.log(Level.INFO, "Clearance map generation started. Size of map: [{0},{1}]", new Object[]{collisionMap.getMapTileWidth(), collisionMap.getMapTileHeight()});
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

    /**
     * A simple sorted list
     * http://www.cokeandcode.com/main/tutorials/path-finding/ (Kevin Glass)
     * @author kevin
     */
    private class SortedList {
        /** The list of elements */
        private ArrayList list = new ArrayList();

        /**
         * Retrieve the first element from the list
         *  
         * @return The first element from the list
         */
        public Object first() {
                return list.get(0);
        }

        /**
         * Empty the list
         */
        public void clear() {
                list.clear();
        }

        /**
         * Add an element to the list - causes sorting
         * 
         * @param o The element to add
         */
        public void add(Object o) {
                list.add(o);
                Collections.sort(list);
        }

        /**
         * Remove an element from the list
         * 
         * @param o The element to remove
         */
        public void remove(Object o) {
                list.remove(o);
        }

        /**
         * Get the number of elements in the list
         * 
         * @return The number of element in the list
         */
        public int size() {
                return list.size();
        }

        /**
         * Check if an element is in the list
         * 
         * @param o The element to search for
         * @return True if the element is in the list
         */
        public boolean contains(Object o) {
                return list.contains(o);
        }
    }
	

}
