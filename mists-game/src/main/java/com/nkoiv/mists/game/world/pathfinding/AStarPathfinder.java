/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.util.MinHeap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * AStarPathfinder with clearance maps for different sized objects.
 * @author nikok
 */
public class AStarPathfinder implements PathfinderAlgorithm {
    
    private PathFinder pathfinder; //Pathfinder keeps the collisionmap up to date, we just refer to it.
    
    private MinHeap<Node> closedNodes = new MinHeap();
    private MinHeap<Node> openNodes = new MinHeap();
    private Node[][] nodeMap;
    private int[][] nodeStatus;
    private static final int CLEAR = 0;
    private static final int OPEN = 1;
    private static final int CLOSED = 2;
    
    private int maxSearchDistance;
    private boolean allowDiagonalMovement;
   
    private HashMap<Integer, int[][]> clearanceMaps;
    
    public AStarPathfinder(PathFinder pathfinder, int maxSearchDistance, boolean allowDiagonalMovement) {
        this.pathfinder = pathfinder;
        this.maxSearchDistance = maxSearchDistance;
        this.allowDiagonalMovement = allowDiagonalMovement;
        this.clearanceMaps = new HashMap<>(); 
        this.clearanceMaps.put(0, pathfinder.getClearanceMap(0)); //Base clearanceMap for 0-type movement. 
    }
    
    
    /**
    * THE MAIN PATHFINDING ROUTINE
    * Based on A* tips from the book "Artificial Intelligence for games (2e)" by Millington and Funge
    * TODO: Implement some speed tips
    * 
     * @param map The map we're moving on
     * @param tileSize Size of the object we're finding a path for
     * @param crossableTerrain terrains this object can path through
     * @param startX starting position of the object
     * @param startY starting position of the object
     * @param goalX pathfinding goal of the object
     * @param goalY pathfinding goal of the object
    */
    @Override
    public Path findPath(CollisionMap map, int tileSize,List<Integer> crossableTerrain, int startX, int startY, int goalX, int goalY) {
        //Mists.logger.log(Level.INFO, "Finding path for size {0} unit from [{1},{2}] to [{3},{4}}", new Object[]{tileSize, startX, startY, goalX, goalY});
        Path path = new Path();
        //If the goal is blocked, return empty path - it's the AI:s problem to deal with that.
        if (map.isBlocked(crossableTerrain, goalX, goalY)) return path;
        Node start = initializePathfinding(map, crossableTerrain, startX, startY, goalX, goalY);
        Node goal = new Node(goalX, goalY);
        
        Node currentNode = start;
        //Mists.logger.log(Level.INFO,"Starting a new pathfinding: from {0},{1} to {2}, {3}",new Object[]{currentNode.getX(), currentNode.getY(), goal.getX(), goal.getY()});
        while (openNodes.size() >0) {  //Iterate the list until all open nodes have been dealt with
            currentNode = getFirstInOpen();
            //Mists.logger.log(Level.INFO, "Currently at: {0},{1} - Goal at: {2}, {3}", new Object[]{currentNode.getX(), currentNode.getY(), goal.getX(), goal.getY()});
            //Mists.logger.log(Level.INFO, "Number of open points: {0}. Number of closed points: {1}", new Object[]{openNodes.size(), closedNodes.size()});
            if (currentNode.getDepth() > this.maxSearchDistance) {
                //Mists.logger.info("Ran to max search distance ("+maxSearchDistance+")");
                //Stop searching and return empty path
                return new Path();
            } else { //not at goal yet, find open neighbours
                List<Node> neighbours = new ArrayList<>(); //add in all traversable neighbours
                neighbours.addAll(this.Neighbours(map, tileSize,crossableTerrain, currentNode.getX(), currentNode.getY()));
                if (allowDiagonalMovement) neighbours.addAll(this.DiagonalNeighbours(map, tileSize,crossableTerrain, currentNode.getX(), currentNode.getY()));
                //Mists.logger.log(Level.INFO, "{0} neighbouring tiles found for {1},{2}", new Object[]{neighbours.size(), currentNode.getX(), currentNode.getY()});
                // Check through all the neighbouring tiles
                for (Node n : neighbours) {
                    Node nn = new Node(n.getX(), n.getY());
                    //Estimate the total cost to get to end from this node
                    nn.setCostEstimate(currentNode.getCumulativeCost()+
                            this.getMovementCost(crossableTerrain, nn.getX(), nn.getY(), goal.getX(), goal.getY()));
                    nn.setPreviousNode(currentNode);
                    nn.setDepth(currentNode.getDepth()+1);
                    //TODO: Consider setting the cost to 1.41 nn.getMovementCost() when moving diagonally
                    //Because pythagoran
                    nn.setCumulativeCost(currentNode.getCumulativeCost()+nn.getMovementCost());
                    //Mists.logger.log(Level.INFO, "Checking neighbour at [{0},{1}]", new Object[]{n.getX(), n.getY()});
                    if (inClosedList(n)) {
                        Node cN = nodeMap[n.getX()][n.getY()];
                        //Mists.logger.info("Node ["+n.getX()+","+n.getY()+"] was found on the Closed list");
                        if (cN.getCostEstimate() <= nn.getCostEstimate()) { //We ran to this node again, and we havent found a shorter route to it
                            //Keep it in the closed list for now and nevermind
                            //continue;
                        } else { //We've got a new shorter route to this (closed) node
                            removeFromClosed(cN);
                        }
                    } else if (inOpenList(n)) { //This is already on the open lists
                        Node oN = nodeMap[n.getX()][n.getY()];
                        //Mists.logger.info("Node ["+n.getX()+","+n.getY()+"] was found on the Open list");
                        if (oN.getCostEstimate() <= nn.getCostEstimate()) {
                            //We ran to this node again, and we havent found a shorter route to it
                            //Keep it in the open list for now and nevermind
                            //continue;
                        } else { //We've got a new shorter route to this (open) node
                            oN.setCostEstimate(nn.getCostEstimate());
                            oN.setDepth(nn.getDepth());
                            oN.setCumulativeCost(currentNode.getCostEstimate()+oN.getMovementCost());
                        }
                    }  else { //We have a new node to visit. Add it to the open list 
                        //Mists.logger.log(Level.INFO, "Node [{0},{1}] was on neither list. Adding to open.", new Object[]{n.getX(), n.getY()});
                        addToOpen(nn);
                    }
                }
                removeFromOpen(currentNode); //Move the current node to closed
                addToClosed(currentNode);
                //path.addStep(currentNode);
                //Mists.logger.info(currentNode.getX()+","+currentNode.getY()+" set as current node");

            }
            //Mists.logger.info("Checked the neighbours. Open nodes size is now "+openNodes.size());
            if(currentNode.getX() == goal.getX() && currentNode.getY() == goal.getY()) { //we're at the goal
                //Mists.logger.info("Found goal!");
                openNodes.clear();
                break;
            }
            
        }
        if(currentNode.getX() != goal.getX() || currentNode.getY() != goal.getY()) {
            //We ran out of open nodes and didn't find our goal. Return empty path
            return new Path();
        }
        //Mists.logger.info("Building back the path from ["+currentNode.getX()+","+currentNode.getY()+"]...");
        while (currentNode.getPreviousNode()!=null) { //Build back the path
            path.prependNode(currentNode.getX(), currentNode.getY());
            currentNode = currentNode.getPreviousNode();
            //Mists.logger.info("Added ["+currentNode.getX()+","+currentNode.getY()+"] to the path");
        }
        return path;
    }
    
    /**
    * Initialize the pathfinding by clearing all the
    * lists and making sure we have required clearance maps.
    * @param map CollisionMap for the pathfinding
    * @param crossableTerrain the terrainwalking of the unit we're finding path for
    * @param startX Where we're coming from
    * @param startY Where we're coming from
    * @param goalX Where we're going to
    * @param goalY Where we're going to
    * @return starting node
    */
    private Node initializePathfinding(CollisionMap map, List<Integer> crossableTerrain, int startX, int startY, int goalX, int goalY) {
        //Mists.logger.log(Level.INFO, "Initializing the path");
        //Check we have all the clearanceMaps we need.
        for (Integer terrainType : crossableTerrain) {
            //Mists.logger.info("Checking we have clearance for terrain : "+terrainType);
            if (!this.clearanceMaps.containsKey(terrainType)) { //if we dont already have the given map, we need to generate it
                //Mists.logger.info("Clearance generated");
                this.clearanceMaps.put(terrainType, pathfinder.getClearanceMap(terrainType));
            } else {
                //Mists.logger.info("Clearance found");
            }
        }

        //Initialize the starting node
        Node start = new Node(startX, startY);
        start.setPreviousNode(null); //Start has no previous node
        start.setDepth(0); //When we're at start, we havent moved yet
        start.setCostEstimate(this.getMovementCost(crossableTerrain, start.getX(), start.getY(), goalX, goalY));

        //Clear the nodemap and nodelists
        nodeMap = new Node[map.getMapTileWidth()][map.getMapTileHeight()]; //Reset nodemap
        nodeStatus = new int[map.getMapTileWidth()][map.getMapTileHeight()]; //Reset node statuses
        closedNodes.clear(); //Reset the closed nodes
        openNodes.clear(); //Reset the open nodes
        addToOpen(start);
        
        return start;
    }
    
    private Node getFirstInOpen() {
        return openNodes.first();
    }

    private void addToOpen(Node node) {
            this.openNodes.add(node);
            this.nodeMap[node.getX()][node.getY()] = node;
            this.nodeStatus[node.getX()][node.getY()] = OPEN;
    }

    private boolean inOpenList(Node node) {
        return this.nodeStatus[node.getX()][node.getY()] == OPEN;
    }


    private void removeFromOpen(Node node) {
        Node n = this.nodeMap[node.getX()][node.getY()];
        this.openNodes.remove(n);
        this.nodeMap[node.getX()][node.getY()] = null;
        this.nodeStatus[node.getX()][node.getY()] = CLEAR;
    }

    private void addToClosed(Node node) {
        this.closedNodes.add(node);
        this.nodeMap[node.getX()][node.getY()] = node;
        this.nodeStatus[node.getX()][node.getY()] = CLOSED;
    }

    private boolean inClosedList(Node node) {
        return this.nodeStatus[node.getX()][node.getY()] == CLOSED;
    }

    private void removeFromClosed(Node node) {
        Node n = this.nodeMap[node.getX()][node.getY()];
        this.closedNodes.remove(n);
        this.nodeMap[node.getX()][node.getY()] = null;
        this.nodeStatus[node.getX()][node.getY()] = CLEAR;
    }
    
    /**
     * Neighbours with the default clearance need of 1
     * @param crossableTerrain Tilewalking ability
     * @param x Target nodes X coordinate
     * @param y Target nodes Y coordinate
     * @return List of available neighbours
     */
    public List<Node> Neighbours(CollisionMap map, List<Integer> crossableTerrain, int x, int y) {
        //if no size given, assume clearance need of 1
        return Neighbours(map, 1,crossableTerrain, x, y);
    } 

    /**
     * Neighbours returns all the cardinal direction (Up, Down, Left, Right)
     * neighbours of a given X,Y tile that can be crossed with the given parameters.
     * In other words, it returns available paths.
     * @param clearanceNeed Size of the unit doing the moving
     * @param crossableTerrain Tilewalking ability
     * @param x Target nodes X coordinate
     * @param y Target nodes Y coordinate
     * @return List of available neighbours
     */

    public List<Node> Neighbours(CollisionMap map, int clearanceNeed, List<Integer> crossableTerrain, int x, int y) {
    /**Neighbours only lists cardinal directions
    * because being surrounded cardinally blocks movement
    * TODO: Consider if this is good
    */
        int N = y - 1;
        int S = y + 1;
        int E = x + 1;
        int W = x - 1;
        boolean myN = (N > -1 && !map.isBlocked(crossableTerrain, x, N) 
                && this.hasClearance(clearanceNeed, crossableTerrain, x, N));
        boolean myS = (S < map.getMapTileHeight() && !map.isBlocked(crossableTerrain, x, S)
                && this.hasClearance(clearanceNeed, crossableTerrain, x, S));
        boolean myE = (E < map.getMapTileWidth() && !map.isBlocked(crossableTerrain, E, y)
                && this.hasClearance(clearanceNeed, crossableTerrain, E, y));
        boolean myW = (W > -1 && !map.isBlocked(crossableTerrain, W, y)
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
        
        
    /**
     * DiagonalNeighbours with default clearance need of 1.
     * @param crossableTerrain Tilewalking ability
     * @param x Target nodes X coordinate
     * @param y Target nodes Y coordinate
     * @return List of available diagonally neighbouring spots
     */    
    public List<Node> DiagonalNeighbours(CollisionMap map, List<Integer> crossableTerrain, int x, int y) {
        //if no size given, assume clearance need of 1
        return DiagonalNeighbours(map, 1, crossableTerrain, x, y);
    }
    /** DiagonalNeighbours returns all the diagonal direction (UpRight, RightDown, UpLeft, LeftDown)
    * neighbours of a given X,Y tile that can be crossed with the given parameters.
    * In other words, it returns available paths.
    * @param clearanceNeed Size of the unit doing the moving
    * @param crossableTerrain Tilewalking ability
    * @param x Target nodes X coordinate
    * @param y Target nodes Y coordinate
    * @return List of available diagonally neighbouring spots
    */  
    public List<Node> DiagonalNeighbours(CollisionMap map, int clearanceNeed, List<Integer> crossableTerrain, int x, int y) {
        //Return all NE, NW, SE and SW that are passable without squeezing through
        ArrayList<Node> result = new ArrayList<>();
        int N = y - 1;
        int S = y + 1;
        int E = x + 1;
        int W = x - 1;
        boolean myN = (N > -1 && !map.isBlocked(crossableTerrain, x, N)
                && this.hasClearance(clearanceNeed, crossableTerrain, x, N));
        boolean myS = (S < map.getMapTileHeight() && !map.isBlocked(crossableTerrain, x, S)
                && this.hasClearance(clearanceNeed, crossableTerrain, x, S));
        boolean myE = (E < map.getMapTileWidth() && !map.isBlocked(crossableTerrain, E, y)
                && this.hasClearance(clearanceNeed, crossableTerrain, E, y));
        boolean myW = (W > -1 && !map.isBlocked(crossableTerrain, W, y)
                && this.hasClearance(clearanceNeed, crossableTerrain, W, y));        
        if (myN) {
            if (myE && !map.isBlocked(crossableTerrain, E, N)
                    && this.hasClearance(clearanceNeed, crossableTerrain, E, N))
                    result.add(new Node(E,N));
            if (myW && !map.isBlocked(crossableTerrain, W, N)
                    && this.hasClearance(clearanceNeed, crossableTerrain, W, N)) 
                    result.add(new Node(W,N));
        }
        if (myS) {
            if (myE && !map.isBlocked(crossableTerrain, E, S)
                    && this.hasClearance(clearanceNeed, crossableTerrain, E, S))
                result.add(new Node(E,S));
            if (myW && !map.isBlocked(crossableTerrain, W, S)
                    && this.hasClearance(clearanceNeed, crossableTerrain, W, S)) 
                    result.add(new Node(W,S));
        }      
        return result;
    }

    
    /**
    * Check if unit of given size can fit in the given tile with given movement type
    * @param unitSize the size of the unit doing the moving
    * @param terrainNumber the type of terrain we're moving on
    * @param x X coordinate of the node
    * @param y Y coordinate of the node
    * @return true if the unit can fit in the node
    */
    private boolean hasClearance (int unitSize, int terrainNumber, int x, int y) {
        if (!this.clearanceMaps.containsKey(terrainNumber) || this.pathfinder.mapIsOutOfDate()) { //If we dont have the map for this type of terrain, generate it
            Mists.logger.log(Level.INFO, "Tried to check clearance but no clearance map - generating a new one ({0})", terrainNumber);
            this.clearanceMaps.put(terrainNumber, pathfinder.getClearanceMap(terrainNumber));
            this.pathfinder.setMapOutOfDate(false); //TODO: Consider giving maps timestamps, and comparing different clearancelevel-maps to those
        }
        //Check if the unit can fit in the square
        //Mists.logger.log(Level.INFO, "unitsize {0} going to CL {1}", new Object[]{unitSize, this.clearanceMaps.get(terrainNumber)[x][y]});
        return this.clearanceMaps.get(terrainNumber)[x][y] >= unitSize;
    }

    /**
    * Checking unit with multiple terrain movements fitting in a mapnode
    * @param unitSize size of the unit doing the moving
    * @param crossableTerrain the types of terrainwalking ability the unit has
    * @param x X coordinate of the node
    * @param y Y coordinate of the node
    * @return true if the unit can fit in the node with at least one of its terrainwalkings
    */
    private boolean hasClearance (int unitSize, List<Integer> crossableTerrain, int x, int y) {
        for (Integer terrainType : crossableTerrain) {
                if (this.hasClearance(unitSize, terrainType,x, y)) {
                    return true;
                }
            }
        return false;
    }
    
    public HashMap<Integer, int[][]> getClearanceMaps() {
        return this.clearanceMaps;
    }
    
    public double getMovementCost(List<Integer> movementAbilities, int currentX, int currentY, int goalX, int goalY) {
            return pathfinder.getMovementCost(movementAbilities, currentX, currentY, goalX, goalY);
    }
    
}
