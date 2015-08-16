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
import java.util.List;

/**
 * Based on http://www.cokeandcode.com/main/tutorials/path-finding/ (Kevin Glass)
 * Modified to use different heuristics for cost. Restructured to use same Nodes for map and Path.
 * Modified to use collision levels for movement.
 * @author nikok
 */
public class PathFinder {
	
	private ArrayList closedNodes = new ArrayList();
	private SortedList openNodes = new SortedList();
	private CollisionMap map; //The collision map derived from the Locations MOBs
	private Node[][] nodes; //Nodemap used for pathfinding, filled with costs as we calculate them
	private int maxSearchDistance;
	
	private boolean allowDiagonalMovement;
	/** The the calculator we're applying to determine which nodes to search first */
	private MoveCostCalculator calc;

	public PathFinder(CollisionMap map, int maxSearchDistance, boolean allowDiagonalMovement) {
            this.map = map;
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
        
        public Direction pathTowards (List<Integer> crossableTerrain,double startX, double startY, double goalX, double goalY) {
            int sX = ((int) startX / this.map.getNodeSize());
            int sY = ((int) startY / this.map.getNodeSize());
            int gX = ((int) goalX / this.map.getNodeSize());
            int gY = ((int) goalY / this.map.getNodeSize());
            
            Path pathToGoal = this.findPath(crossableTerrain, sX, sY, gX, gY);
            
            if (pathToGoal == null) {
                //got an empty path - stay put!
                return Direction.STAY;
            }
            
            if (pathToGoal.getLength() < 1) {
                //Mists.logger.info("Next to object, staying put");
                return Direction.STAY;
            } else {
                int xChange = pathToGoal.getNode(0).getX() - sX;
                int yChange = pathToGoal.getNode(0).getY() - sY;
                //Mists.logger.log(Level.INFO, "Figuring direction from {0},{1} to {2},{3}", new Object[]{startNode.getX(), startNode.getY(), pathToGoal.get(0).node.getX(), pathToGoal.get(0).node.getY()});
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
        /*Neighbours only lists cardinal directions
        * because being surrounded cardinally blocks movement
        * TODO: Consider if this is good
        */
            int N = y - 1;
            int S = y + 1;
            int E = x + 1;
            int W = x - 1;
            boolean myN = (N > -1 && !this.map.isBlocked(crossableTerrain, x, N));
            boolean myS = (S < this.map.getMapTileHeight() && !this.map.isBlocked(crossableTerrain, x, S));
            boolean myE = (E < this.map.getMapTileWidth() && !this.map.isBlocked(crossableTerrain, E, y));
            boolean myW = (W > -1 && !this.map.isBlocked(crossableTerrain, W, y));
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
        //Return all NE, NW, SE and SW that are passable without squeezing through
        ArrayList<Node> result = new ArrayList<>();
        int N = y - 1;
        int S = y + 1;
        int E = x + 1;
        int W = x - 1;
        boolean myN = (N > -1 && !this.map.isBlocked(crossableTerrain, x, N));
        boolean myS = (S < this.map.getMapTileHeight() && !this.map.isBlocked(crossableTerrain, x, S));
        boolean myE = (E < this.map.getMapTileWidth() && !this.map.isBlocked(crossableTerrain, E, y));
        boolean myW = (W > -1 && !this.map.isBlocked(crossableTerrain, W, y));        
        if (myN) {
            if (myE && !this.map.isBlocked(crossableTerrain, E, N)) result.add(nodes[E][N]);
            if (myW && !this.map.isBlocked(crossableTerrain, W, N)) result.add(nodes[W][N]);
        }
        if (myS) {
            if (myE && !this.map.isBlocked(crossableTerrain, E, S)) result.add(nodes[E][S]);
            if (myW && !this.map.isBlocked(crossableTerrain, W, S)) result.add(nodes[W][S]);
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
                if (yChange>0) return Direction.UPRIGHT;
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
        
	public Path findPath(List<Integer> crossableTerrain, int startX, int startY, int gX, int gY) {
		
            /*If target tile is blocked (we're going to a creature fex
            * we should try to find a tile that's next to it
            */
            int goalX = gX;
            int goalY = gY;
            
            boolean getToAdjacent = false;
            if (map.isBlocked(crossableTerrain, goalX, goalY)) {
                Mists.logger.info("Target is blocked, settling on adjacent node");
                getToAdjacent = true;
                //If there's not even open neighbours, just give up
                if (this.findNearestOpenNode(crossableTerrain, goalX, goalY) == null) return null;
                int[] adjacentGoal = this.findNearestOpenNode(crossableTerrain, goalX, goalY);
                goalX = adjacentGoal[0];
                goalY = adjacentGoal[1];
            }

            nodes[startX][startY].setCost(0);
            nodes[startX][startY].setDepth(0);
            closedNodes.clear(); //Reset the closed nodes
            openNodes.clear(); //Reset the open nodes
            openNodes.add(nodes[startX][startY]);
            
            //nodes[goalX][goalY].setPreviousNode(null); //Goal has no previous step
            int currentSearchDistance = 0;
            
            
            // while we haven't exceeded our max search depth and we still got nodes to look at
            while ((currentSearchDistance < maxSearchDistance) && (openNodes.size() != 0)) {
                //Because openNodes is a sorted list, the first node there has the lowest cost (nearest to end)
                Node current = getFirstInOpen();
                if (current == nodes[goalX][goalY]) {
                    break; //End if we're at the goal
                }
                if (getToAdjacent && current.isNextTo(goalX, goalY)) {
                     break; //or close enough
                }
               
                removeFromOpen(current);
                addToClosed(current); //This Node can be designated as "searched"

                /*Check all neigbouring tiles
                * XXX
                * X X
                * XXX
                */
                for (int x=-1;x<2;x++) {
                    for (int y=-1;y<2;y++) {
                        if ((x == 0) && (y == 0)) { //Current tile needs not be checked
                            continue;
                        }
                        if (!allowDiagonalMovement) {
                            if ((x != 0) && (y != 0)) {
                                continue;
                            }
                        }

                        // determine the location of the neighbour and evaluate it
                        int xp = x + current.getX();
                        int yp = y + current.getY();

                        if (isValidLocation(crossableTerrain,startX,startY,xp,yp)) {

                            double nextStepCost = current.getCost() + getMovementCost(crossableTerrain, current.getX(), current.getY(), xp, yp);
                            Node neighbour = nodes[xp][yp];
                            map.pfVisit(xp, yp);

                            // if the new cost we've determined for this node is lower than 
                            // it has been previously makes sure the node hasn't
                            // determined that there might have been a better path to get to
                            // this node so it needs to be re-evaluated

                            if (nextStepCost < neighbour.getCost()) {
                                    if (inOpenList(neighbour)) {
                                            removeFromOpen(neighbour);
                                    }
                                    if (inClosedList(neighbour)) {
                                            removeFromClosed(neighbour);
                                    }
                            }

                            // if the node hasn't already been processed and discarded then
                            // reset it's cost to our current cost and add it as a next possible
                            // step (i.e. to the open list)
                            if (!inOpenList(neighbour) && !(inClosedList(neighbour))) {
                                    neighbour.setCost(nextStepCost);
                                    currentSearchDistance = Math.max(currentSearchDistance, neighbour.setPreviousNode(current));
                                    addToOpen(neighbour);
                            }
                        }
                    }
                }
                
                //Mists.logger.info("Got to: "+current.getX()+","+current.getY());
            }

            // since we'e've run out of search 
            // there was no path. Just return null

            if (nodes[goalX][goalY].getPreviousNode() == null) {
                Mists.logger.info("Ran out of search!");
                    //return null;
            }

            // At this point we've definitely found a path so we can uses the parent

            // references of the nodes to find out way from the target location back

            // to the start recording the nodes on the way.

            Path path = new Path();
            Node target = nodes[goalX][goalY];
            
            while (target != nodes[startX][startX]) {
                if(target == null) System.out.println("Target at ["+goalX+","+goalY+"] is null!");
                    path.prependNode(target.getX(), target.getY());
                    target = target.getPreviousNode();
            }
            path.prependNode(startX,startY);

            // thats it, we have our path 
            Mists.logger.info(path.toString());
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
	
	/**
	 * A simple sorted list
	 *
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
