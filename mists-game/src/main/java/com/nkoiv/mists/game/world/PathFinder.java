/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * 
 * @author nkoiv
 */
public class PathFinder {

    Location location;
    int mapTileWidth;
    int mapTileHeight;
    Node[][] nodeMap;
    int maxSearchDepth;
    
    public PathFinder (Location l) {
        this.location = l;
        this.maxSearchDepth = 30;
        //First we'll convert map to tiles, even if it's BGMap
        mapTileWidth = (int)(l.getMap().getWidth() / Global.TILESIZE);
        mapTileHeight = (int)(l.getMap().getHeight() / Global.TILESIZE); 
        nodeMap = new Node[mapTileWidth][mapTileHeight];
        //Then populate a nodemap with empty (=passable) nodes
        for (int row = 0; row < this.mapTileHeight;row++) {
            for (int column = 0; column < this.mapTileWidth; column++) {
                this.nodeMap[column][row] = new Node(column, row, Global.TILESIZE);
            }
        }
        //Update the nodes to match Location mobs
        this.updateCollisionLevels();
    }
    
    public Direction pathTowards (List<Integer> crossableTerrain,double startX, double startY, double goalX, double goalY) {
        Node startNode = nodeMap[(int)startX/Global.TILESIZE][(int)startY/Global.TILESIZE];
        Node goalNode = nodeMap[(int)goalX/Global.TILESIZE][(int)goalY/Global.TILESIZE];
        List<PathPoint> pathToGoal = this.calculatePath(crossableTerrain, startNode, goalNode);
        if (pathToGoal.size()<1) {
            return Direction.STAY;
        } else {
            int xChange = pathToGoal.get(0).node.getX() - startNode.getX();
            int yChange = pathToGoal.get(0).node.getY() - startNode.getY();
            Mists.logger.log(Level.INFO, "Figuring direction from {0},{1} to {2},{3}", new Object[]{startNode.getX(), startNode.getY(), pathToGoal.get(0).node.getX(), pathToGoal.get(0).node.getY()});
            return getDirection(xChange, yChange);
        }
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
            if (yChange>0) return Direction.UPRIGHT;
        }
        return Direction.STAY; //last resort
    }
        
    private int ManhattanDistance(Node start, Node goal) {
        //Linear distance of nodes
        int manhattanDistance = Math.abs(start.getX() - goal.getX()) + 
                        Math.abs(start.getY() - goal.getY());
        return manhattanDistance;
    }
    
    private int DiagonalDistance(Node start, Node goal) {
        //Diagonal distanceassumes going diagonally costs the same as going cardinal
        int diagonalDistance = Math.max(Math.abs(start.getX() - goal.getX()),
                        Math.abs(start.getY() - goal.getY()));
        return diagonalDistance;
    }
    
    private double EuclideanDistance(Node start, Node goal) {
        /*With euclidean the diagonal movement is considered to be
        * slightly more expensive than cardinal movement
        * ((AC = sqrt(AB^2 + BC^2))), 
        * where AB = x2 - x1 and BC = y2 - y1 and AC will be [x3, y3]
        */
        double euclideanDistance = Math.sqrt(Math.pow(start.getX() - goal.getX(), 2)
                            + Math.pow(start.getY() - goal.getY(), 2));
        return euclideanDistance;
    }
    
    private boolean isCrossable(List<Integer> crossableTerrain ,int x, int y) {
        return ((nodeMap[x][y] != null) &&
		(crossableTerrain.contains(nodeMap[x][y].getCollisionLevel())));
    }
    
    private List<Node> Neighbours(List<Integer> crossableTerrain, int x, int y) {
        /*Neighbours only lists cardinal directions
        * because being surrounded cardinally blocks movement
        * TODO: Consider if this is good
        */
            int N = y - 1;
            int S = y + 1;
            int E = x + 1;
            int W = x - 1;
            boolean myN = (N > -1 && isCrossable(crossableTerrain, x, N));
            boolean myS = (S < this.mapTileHeight && isCrossable(crossableTerrain, x, S));
            boolean myE = (E < this.mapTileWidth && isCrossable(crossableTerrain, E, y));
            boolean myW = (W > -1 && isCrossable(crossableTerrain, W, y));
            ArrayList<Node> result = new ArrayList<>();
            if(myN) {
                result.add(this.nodeMap[x][N]);
            }
            if(myE) {
                result.add(this.nodeMap[E][y]);
            }
            if(myS) {
                result.add(this.nodeMap[x][S]);
            }
            if(myW) {
                result.add(this.nodeMap[W][y]);
            }
            return result;
    }
    
    private List<Node> DiagonalNeighbours(List<Integer> crossableTerrain, int x, int y) {
        //Return all NE, NW, SE and SW that are passable without squeezing through
        ArrayList<Node> result = new ArrayList<>();
        int N = y - 1;
        int S = y + 1;
        int E = x + 1;
        int W = x - 1;
        boolean myN = (N > -1 && isCrossable(crossableTerrain, x, N));
        boolean myS = (S < this.mapTileHeight && isCrossable(crossableTerrain, x, S));
        boolean myE = (E < this.mapTileWidth && isCrossable(crossableTerrain, E, y));
        boolean myW = (W > -1 && isCrossable(crossableTerrain, W, y));        
        if (myN) {
            if (myE && isCrossable(crossableTerrain, E, N)) result.add(nodeMap[E][N]);
            if (myW && isCrossable(crossableTerrain, W, N)) result.add(nodeMap[W][N]);
        }
        if (myS) {
            if (myE && isCrossable(crossableTerrain, E, S)) result.add(nodeMap[E][S]);
            if (myW && isCrossable(crossableTerrain, W, S)) result.add(nodeMap[W][S]);
        }      
        return result;
    }
      
    public void updateCollisionLevels() {
        //Go through all the nodes and check the location if it has something at them
        List<MapObject> mobs = this.location.getMOBList();
        for (MapObject mob : mobs) {
            int mobXNode = ((int)mob.getxPos() / Global.TILESIZE);
            int mobYNode = ((int)mob.getyPos() / Global.TILESIZE);
            int mobCL = mob.getFlag("collisionLevel");
            this.nodeMap[mobXNode][mobYNode].setCollisionLevel(mobCL);
        }
    }
    
    private List<PathPoint> calculatePath(List<Integer> crossableTerrain, Node start, Node goal) {
        ArrayList<PathPoint> path = new ArrayList<>();
        PathPoint startPoint = new PathPoint(start.getX(), start.getY());
        PathPoint goalPoint = new PathPoint (goal.getX(), goal.getY());
       
        ArrayList<PathPoint> openPoints = new ArrayList<>();
        ArrayList<Node> closedNodes = new ArrayList<>();
        
        openPoints.add(startPoint);
        PathPoint currentPoint = startPoint;
        path.add(startPoint);
        while (!openPoints.isEmpty()) {
            if(path.size() > this.maxSearchDepth) break; //stop if we've reached max depth 
            //Iterate the list until all open nodes have been dealt with
            //Mists.logger.log(Level.INFO, "Currently at: {0},{1} - Goal at: {2}, {3}", new Object[]{currentPoint.node.getX(), currentPoint.node.getY(), goalPoint.node.getX(), goalPoint.node.getY()});
            //Mists.logger.info("Path has "+path.size()+ " steps in it. Number of open points: "+openPoints.size());
            if(currentPoint.node == goalPoint.node) { //we're at the goal
                //Mists.logger.info("Found goal!");
                openPoints.clear();
            } else { //not at goal yet
                //find open neighbours
                List<Node> neighbours = new ArrayList<>(); //add in all traversable neighbours
                neighbours.addAll(this.Neighbours(crossableTerrain, currentPoint.node.getX(), currentPoint.node.getY()));
                neighbours.addAll(this.DiagonalNeighbours(crossableTerrain, currentPoint.node.getX(), currentPoint.node.getY()));
                neighbours.removeAll(closedNodes); //Close routes we know are bad
                //Mists.logger.log(Level.INFO, "{0} neighbouring tiles found for {1},{2}", new Object[]{neighbours.size(), currentPoint.node.getX(), currentPoint.node.getY()});
                if (neighbours.size()<=1) { //if we can only go backwards
                    closedNodes.add(currentPoint.node); //Close this zone from further testing
                    currentPoint = path.get(path.size()-1); //Move to last good node
                } else {
                    List<PathPoint> currentNeighbours = new ArrayList<>();
                    for (Node n : neighbours) { //Make pathpoints to all availale nodes and tag them open
                        if(!pathHasNode(path, n)) { //dont go to direction we came from
                            //Mists.logger.info("Adding a new pathpoit to openList");
                            PathPoint pp = new PathPoint(currentPoint, n.getX(), n.getY());
                            pp.distanceToStart = path.size();
                            pp.distanceToGoal = DiagonalDistance(pp.node, goal);
                            openPoints.add(pp); //remember the node in case we need to go back to it
                            currentNeighbours.add(pp);
                        }
                    }
                    currentPoint = currentNeighbours.get(0);
                    //Mists.logger.info("Current distance to goal: "+currentPoint.distanceToGoal);
                    for (PathPoint pp : currentNeighbours) {
                        //find the best open node
                        /*Mists.logger.info("Considering going from " +
                                currentPoint.node.getX() + ","+currentPoint.node.getY()+
                                " to " +pp.node.getX() + "," + pp.node.getY()
                                ); 
                        */
                        if(pp.compareTo(currentPoint) < 0) {
                            //Mists.logger.info("Found a better node to go to: "+pp.node.getX()+","+pp.node.getY());
                            currentPoint = pp;
                        }
                    }
                    path.add(currentPoint); //add the best point to path and move there
                    closedNodes.add(currentPoint.node); // this node doesnt need to be calculated again
                }
            }
            
            
        }
        path.remove(0); //remove start from path;
        return path;
    }
    
    private boolean pathHasNode(List<PathPoint> path, Node node) {
        for (PathPoint p : path) {
            if (p.node == node) return true;
        } 
        return false;
    }
    
    private class PathPoint {
        /*PathPoints node-like coordinates for the pathfinding
        * They know where they came from, and how far they
        * are from start and goal
        */
        Node node;        
        PathPoint parent;
        double distanceToStart;
        double distanceToGoal;
        
        public PathPoint(int x, int y) {
            this.node = nodeMap[x][y];
            this.parent = null;
            this.distanceToGoal = 0;
            this.distanceToStart = 0;
        }
        
        public PathPoint(PathPoint parent, int x, int y) {
            this.node = nodeMap[x][y];
            this.parent = parent;
            this.distanceToGoal = 0;
            this.distanceToStart = 0;           
        }
        
        public int compareTo(PathPoint pp) {
            return Double.compare(this.distanceToGoal,pp.distanceToGoal);
        }

    }
     
    private class Node {
        /* Nodes are individual map tiles
        *  Paths go through nodes. 
        *  New nodes are collisionLevel = 0
        */
        private final int xCoordinate;
        private final int yCoordinate;
        private final int size;
        private int collisionLevel;
        
        public Node(int xCoordinate, int yCoordinate, int size) {
            this.xCoordinate = xCoordinate;
            this.yCoordinate = yCoordinate;
            this.size = size;
            this.collisionLevel = 0;
        }
        
        public void setCollisionLevel(int cl) {
            this.collisionLevel = cl;
        }
        
        public int getCollisionLevel() {
            return this.collisionLevel;
        }
        
        public int getX() {
            return this.xCoordinate;
        }
        public int getY() {
            return this.yCoordinate;
        }
        public int getSize() {
            return this.size;
        }
        
    }
}
