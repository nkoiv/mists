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
	
	private SortedNodeList closedNodes = new SortedNodeList();
	private SortedNodeList openNodes = new SortedNodeList();
	private CollisionMap map; //The collision map derived from the Locations MOBs
        private HashMap<Integer, int[][]> clearanceMaps;
	//private Node[][] nodes; //Nodemap used for pathfinding, filled with costs as we calculate them
	private int maxSearchDistance;
	
	private boolean allowDiagonalMovement;
	/** The the calculator we're applying to determine which nodes to search first */
	private MoveCostCalculator calc;

	public PathFinder(CollisionMap map, int maxSearchDistance, boolean allowDiagonalMovement) {
            this.map = map;
            double startTime = System.currentTimeMillis();
            /* TODO: Consider how often/when the clearance maps should be updated
            *  TODO: Should Creatures be on clearance maps, or just structures?
            */
            this.clearanceMaps = new HashMap<>(); 
            this.clearanceMaps.put(0, getClearanceMap(0, map)); //Base clearanceMap for 0-type movement.
            this.maxSearchDistance = maxSearchDistance;
            this.allowDiagonalMovement = allowDiagonalMovement;
            this.calc = new MoveCostCalculator(0); //Defaults to Manhattan, 0 for Manhattan, 1 for Diagonal, 2 for Euclidean
	}
        
        /*
        * PathTowards gives the direction of the next node on the Path
        * This method is the one units usually ask when they want to start heading towards a point on the map
        */
        public Direction directionTowards (double unitSize, List<Integer> crossableTerrain,double startX, double startY, double goalX, double goalY) {
            Node targetNode = nextTileOnPath(unitSize, crossableTerrain, startX, startY, goalX, goalY);
            //Get the direction towards the center of the next tile on the path
            double xChange = (targetNode.getX() * this.getTileSize())+(this.getTileSize()/2) - startX;
            double yChange = (targetNode.getY() * this.getTileSize())+(this.getTileSize()/2) - startY;
            Mists.logger.log(Level.INFO, "Path from {0},{1} ({2},{3}) to {4},{5}({6},{7})", new Object[]{startX, startY, (int)startX/this.getTileSize(), (int)startY/this.getTileSize(),targetNode.getX(),targetNode.getY(), (targetNode.getX() * this.getTileSize())+(this.getTileSize()/2), (targetNode.getY() * this.getTileSize())+(this.getTileSize()/2)});
            return getDirection(xChange, yChange);
         }
        
        public double[] coordinatesTowards (double unitSize, List<Integer> crossableTerrain,double startX, double startY, double goalX, double goalY) {
            Node targetNode = nextTileOnPath(unitSize, crossableTerrain, startX, startY, goalX, goalY);
            //Return the (center) coordinates of the next tile on path
            double xCoord = (targetNode.getX()*this.getTileSize()) + (this.getTileSize()/2);
            double yCoord = (targetNode.getX()*this.getTileSize()) + (this.getTileSize()/2);
            Mists.logger.log(Level.INFO, "Path from {0},{1} ({2},{3}) to {4},{5}", new Object[]{startX, startY, (int)startX/this.getTileSize(), (int)startY/this.getTileSize(), xCoord, yCoord});
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
            
            Path pathToGoal = this.findPath(clearanceNeed, crossableTerrain, sX, sY, gX, gY);
            
            if (pathToGoal == null || pathToGoal.getLength()==0) {
                //Got an empty path. Probably means no route was found.
                //Just head in the general direction of the target.
                Mists.logger.info("No path found, giving the Node of the target");
                return new Node(gX, gY);
            }
            Mists.logger.info("Goal was at ["+gX+","+gY+"] got the path:" + pathToGoal.toString());
            if (pathToGoal.getLength() < 2) {
                Mists.logger.info("Next to goal, returning node the goal is at ");
                return pathToGoal.getNode(pathToGoal.getLength()-1);
            } else {
                //Check if there's corners we might get stuck into:
                List<Node> corners = this.DiagonalNeighbours(clearanceNeed, crossableTerrain, pathToGoal.getNode(0).getX(), pathToGoal.getNode(0).getY());
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
        private List<Node> Neighbours(int clearanceNeed, List<Integer> crossableTerrain, int x, int y) {
        /*Neighbours only lists cardinal directions
        * because being surrounded cardinally blocks movement
        * TODO: Consider if this is good
        */
            int N = y - 1;
            int S = y + 1;
            int E = x + 1;
            int W = x - 1;
            boolean myN = (N > -1 && !this.map.isBlocked(crossableTerrain, x, N) 
                    && this.hasClearance(clearanceNeed, crossableTerrain, x, N));
            boolean myS = (S < this.map.getMapTileHeight() && !this.map.isBlocked(crossableTerrain, x, S)
                    && this.hasClearance(clearanceNeed, crossableTerrain, x, S));
            boolean myE = (E < this.map.getMapTileWidth() && !this.map.isBlocked(crossableTerrain, E, y)
                    && this.hasClearance(clearanceNeed, crossableTerrain, E, y));
            boolean myW = (W > -1 && !this.map.isBlocked(crossableTerrain, W, y)
                    && this.hasClearance(clearanceNeed, crossableTerrain, W, y));
            ArrayList<Node> result = new ArrayList<>();
            if(myN) {
                result.add(new Node(x,N));
            }
            if(myE) {
                result.add(new Node(E,y));
            }
            if(myS) {
                result.add(new Node(x,S));
            }
            if(myW) {
                result.add(new Node(W,y));
            }
            return result;
    }
    private List<Node> DiagonalNeighbours(List<Integer> crossableTerrain, int x, int y) {
        //if no size given, assume clearance need of 1
        return DiagonalNeighbours(1, crossableTerrain, x, y);
    }
    private List<Node> DiagonalNeighbours(int clearanceNeed, List<Integer> crossableTerrain, int x, int y) {
        //Return all NE, NW, SE and SW that are passable without squeezing through
        ArrayList<Node> result = new ArrayList<>();
        int N = y - 1;
        int S = y + 1;
        int E = x + 1;
        int W = x - 1;
        boolean myN = (N > -1 && !this.map.isBlocked(crossableTerrain, x, N)
                && this.hasClearance(clearanceNeed, crossableTerrain, x, N));
        boolean myS = (S < this.map.getMapTileHeight() && !this.map.isBlocked(crossableTerrain, x, S)
                && this.hasClearance(clearanceNeed, crossableTerrain, x, S));
        boolean myE = (E < this.map.getMapTileWidth() && !this.map.isBlocked(crossableTerrain, E, y)
                && this.hasClearance(clearanceNeed, crossableTerrain, E, y));
        boolean myW = (W > -1 && !this.map.isBlocked(crossableTerrain, W, y)
                && this.hasClearance(clearanceNeed, crossableTerrain, W, y));        
        if (myN) {
            if (myE && !this.map.isBlocked(crossableTerrain, E, N)
                    && this.hasClearance(clearanceNeed, crossableTerrain, E, N))
                    result.add(new Node(E,N));
            if (myW && !this.map.isBlocked(crossableTerrain, W, N)
                    && this.hasClearance(clearanceNeed, crossableTerrain, W, N)) 
                    result.add(new Node(W,N));
        }
        if (myS) {
            if (myE && !this.map.isBlocked(crossableTerrain, E, S)
                    && this.hasClearance(clearanceNeed, crossableTerrain, E, S))
                result.add(new Node(E,S));
            if (myW && !this.map.isBlocked(crossableTerrain, W, S)
                    && this.hasClearance(clearanceNeed, crossableTerrain, W, S)) 
                    result.add(new Node(W,S));
        }      
        return result;
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
        
        private double[] getTileCoordinates(int tileXCoor, int tileYCoor, int tileSize){
            /* When moving in tight spaces, it's imperative for
            *  the mover to be fully inside the tile Pathfinder thinks its in.
            *  Otherwise the mover might get stuck on corners
            *  This method returns the center of the given tile in "real coordinates"
            */
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
            return findPath(1,crossableTerrain, startX, startY, goalX, goalY);
        }
        
        /*
        * THE MAIN PATHFINDING ROUTINE
        * Based on A* tips from the book "Artificial Intelligence for games (2e)" by Millington and Funge
        * TODO: Implement some speed tips
        */
	private Path findPath(int tileSize,List<Integer> crossableTerrain, int startX, int startY, int goalX, int goalY) {
        //Mists.logger.log(Level.INFO, "Finding path for size {0} unit from [{1},{2}] to [{3},{4}}", new Object[]{tileSize, startX, startY, goalX, goalY});
        Path path = new Path();
        
        //Check we have all the clearanceMaps we need.
        for (Integer terrainType : crossableTerrain) {
            if (!this.clearanceMaps.containsKey(terrainType)) { //if we dont already have the given map, we need to generate it
                this.clearanceMaps.put(terrainType, getClearanceMap(terrainType, this.map));
            }
        }
        
        //Initialize the starting node
        Node start = new Node(startX, startY);
        start.setPreviousNode(null); //Start has no previous node
        start.setDepth(0); //When we're at start, we havent moved yet
        start.setCostEstimate(this.getMovementCost(crossableTerrain, start.getX(), start.getY(), goalX, goalY));
        
        Node goal = new Node(goalX, goalY);
        
        closedNodes.clear(); //Reset the closed nodes
        openNodes.clear(); //Reset the open nodes
        openNodes.add(start);
        Node currentNode = start;
        //Mists.logger.log(Level.INFO,"Starting a new pathfinding: from {0},{1} to {2}, {3}",new Object[]{currentNode.getX(), currentNode.getY(), goal.getX(), goal.getY()});
        //Iterate the list until all open nodes have been dealt with
        while (openNodes.size() > 0) {
            currentNode = (Node)openNodes.first();
            //Mists.logger.log(Level.INFO, "Currently at: {0},{1} - Goal at: {2}, {3}", new Object[]{currentNode.getX(), currentNode.getY(), goal.getX(), goal.getY()});
            //Mists.logger.log(Level.INFO, "Path has {0} steps in it. Number of open points: {1}. Number of closed points: {2}", new Object[]{path.getLength(), openNodes.size(), closedNodes.size()});
            if (path.getLength() > this.maxSearchDistance) {
                //Mists.logger.info("Ran to max search distance ("+maxSearchDistance+")");
                break;
            }
            if(currentNode.getX() == goal.getX() && currentNode.getY() == goal.getY()) { //we're at the goal
                //Mists.logger.info("Found goal!");
                openNodes.clear();
                break;
            } else if (currentNode.isNextTo(goalX, goalY) && map.isBlocked(crossableTerrain, goalX, goalY)) { //We're next to Goal, but it's unreachable
                //Mists.logger.info("Next to goal but cant go there");
                openNodes.clear();
                break;
            } else { //not at goal yet
                //find open neighbours
                List<Node> neighbours = new ArrayList<>(); //add in all traversable neighbours
                neighbours.addAll(this.Neighbours(tileSize,crossableTerrain, currentNode.getX(), currentNode.getY()));
                neighbours.addAll(this.DiagonalNeighbours(tileSize,crossableTerrain, currentNode.getX(), currentNode.getY()));
                //Mists.logger.log(Level.INFO, "{0} neighbouring tiles found for {1},{2}", new Object[]{neighbours.size(), currentNode.getX(), currentNode.getY()});
                List<Node> currentNeighbours = new ArrayList<>();
                /*
                * Check through all the neighbouring tiles
                */
                for (Node n : neighbours) {
                    Node nn = new Node(n.getX(), n.getY());
                    //Estimate the total cost to get to end from this node
                    nn.setCostEstimate(currentNode.getDepth()+
                            this.getMovementCost(crossableTerrain, nn.getX(), nn.getY(), goal.getX(), goal.getY()));
                    nn.setPreviousNode(currentNode);
                    nn.setDepth(currentNode.getDepth()+1);
                    //Mists.logger.info("Checking neihgbour at ["+n.getX()+","+n.getY()+"]");
                    if (closedNodes.contains(n.getX(), n.getY())) {
                        Node cN = closedNodes.get(n.getX(), n.getY());
                        //Mists.logger.info("Node ["+n.getX()+","+n.getY()+"] was found on the Closed list");
                        if (cN.getCostEstimate() <= nn.getCostEstimate()) {
                            //We ran to this node again, and we havent found a shorter route to it
                            //Keep it in the closed list for now and nevermind
                            //continue;
                        } else {
                            //We've got a new shorter route to this (closed) node
                            //Remove the node from closed list
                            closedNodes.remove(cN);
                        }
                        
                        
                    } else if (openNodes.contains(n.getX(), n.getY())) {
                        //This is already
                        Node oN = openNodes.get(n.getX(), n.getY());
                        //Mists.logger.info("Node ["+n.getX()+","+n.getY()+"] was found on the Open list");
                        if (oN.getCostEstimate() <= nn.getCostEstimate()) {
                            //We ran to this node again, and we havent found a shorter route to it
                            //Keep it in the open list for now and nevermind
                            //continue;
                        } else {
                            //We've got a new shorter route to this (open) node
                            //Update the cost to match that
                            oN.setCostEstimate(nn.getCostEstimate());
                            oN.setDepth(nn.getDepth());
                        }
                        
                    }  else { 
                        //We have a new node to visit. Add it to the open list
                        //Mists.logger.info("Node ["+n.getX()+","+n.getY()+"] was on neither list.");
                        openNodes.add(nn);
                        //Mists.logger.info("Adding a new node to openList");
                    }
                    
                    //currentNeighbours.add(nn); // <- What was the point of this line again?
                }
                openNodes.remove(currentNode);
                closedNodes.add(currentNode);
                //path.addStep(currentNode);
                //Mists.logger.info(currentNode.getX()+","+currentNode.getY()+" set as current node");
                
            }
            //Mists.logger.info("Checked the neighbours. Open nodes size is now "+openNodes.size());
        }
        //Build back the path
        //Mists.logger.info("Building back the path from ["+currentNode.getX()+","+currentNode.getY()+"]...");
        while (currentNode.getPreviousNode()!=null) {
            path.prependNode(currentNode.getX(), currentNode.getY());
            currentNode = currentNode.getPreviousNode();
            //Mists.logger.info("Added ["+currentNode.getX()+","+currentNode.getY()+"] to the path");
        }
        return path;
    }

    private Node getFirstInOpen() {
            return openNodes.first();
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

    /**
     * A simple sorted list for Nodes
     * based on the http://www.cokeandcode.com/main/tutorials/path-finding/ (Kevin Glass)
     * @author nkoiv
     */
    private class SortedNodeList {
        /** The list of elements */
        private ArrayList<Node> list = new ArrayList();
        /**
         * Retrieve the first element from the list
         *  
         * @return The first element from the list
         */
        public Node first() {
                return list.get(0);
        }
        
        /*
        * Return the first node on the list that matches given X and Y
        */
        public Node get(int x, int y) {
            for (Node n : this.list) {
                if (n.getX() == x && n.getY() == y) {
                    return n;
                }
            }
            return null;
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
        public void add(Node n) {
                list.add(n);
                Collections.sort(list);
        }

        /**
         * Remove an element from the list
         * 
         * @param o The element to remove
         */
        public void remove(Node n) {
                list.remove(n);
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
        public boolean contains(Node n) {
                return list.contains(n);
        }
        
        /*
        * Check if the list contains a node with the given coordinates
        */
        public boolean contains(int x, int y) {
            for (Node n : this.list) {
                if (n.getX()==x && n.getY() == y) return true;
            } 
            return false;   
        }
    }
	

}