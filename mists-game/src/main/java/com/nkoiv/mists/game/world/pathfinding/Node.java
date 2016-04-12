/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

import com.nkoiv.mists.game.Global;

/**
 * Node is a single map point in a gridmap.
 * Used for CollisionMap and PathFinder
 * @author nikok
 */
	public class Node implements Comparable {
		private int xCoor;
		private int yCoor;
                private int collisionLevel; //this tells us if there's something blocking the node
                private double[] movementCosts; //how "fast" is it to move in this node. if 0, it takes normal unit of speed (1) to pass this node
                private int size; //Node size is the size of tiles. TODO: Not really relevant for node nor needed. Remove?
                private double estimatedCost; //Node cost for when used by PathFinder (from here to goal)
                private double cumulativeCost; //Cost to get from start to this node
                private int depth; //Depth in the path for when used by PathFinder - used to see when we've gone "too deep"
                private Node previousNode; //for PathFinder
		
		public Node(int x, int y) {
			this.xCoor = x;
			this.yCoor = y;
                        this.size = Global.TILESIZE;
                        this.collisionLevel = 0;
                        this.cumulativeCost = 0;
                        this.estimatedCost = 0;
                        this.movementCosts = new double[10];
		}
                
                public Node(int x, int y, int size, int collisionLevel) {
                    this.xCoor = x;
                    this.yCoor = y;
                    this.size = size;
                    this.collisionLevel = collisionLevel;
                }
	
                //Returns true if this node is next to target numbers
                public boolean isNextTo(int xCoor, int yCoor) {
                    return (Math.abs(this.xCoor - xCoor) <= 1) && (Math.abs(this.yCoor - yCoor) <= 1);
                }
                
		public int getX() {
			return xCoor;
		}

		public int getY() {
			return yCoor;
		}
                
                public int getSize() {
                    return this.size;
                }
                
                public int getCollisionLevel() {
                    return this.collisionLevel;
                }
                
                public void setCollisionLevel(int cl) {
                    this.collisionLevel = cl;
                }
                
                public int setPreviousNode (Node n) {
                        if (this.previousNode == null) {
                            this.depth = 1;
                        } else {
                            this.depth = this.getPreviousNode().getDepth() + 1;
                        }
                        
			this.previousNode = n;
			return depth;
                    
                }
   
                public void setCostEstimate(double estimatedCost) {
                    this.estimatedCost = estimatedCost;
                }
                
                public void setCumulativeCost(double cumulativeCost) {
                    this.cumulativeCost = cumulativeCost;
                }
                
                public void setDepth(int depth) {
                    this.depth = depth;
                }
                
                /*
                * Return the movementCost for the default movement type (type 0)
                */
                public double getMovementCost() {
                    if (this.movementCosts[0] == 0) return 1;
                    return this.movementCosts[0];
                }
                
                /**
                 * Return the movement cost for traversing this node
                 * with the given movement type. Defaults to 99 if given
                 * movement type isn't on the list of the node.
                 * @param movementType The type of movement the traveller has
                 * @return movement cost for crossing the node
                 */
                
                public double getMovementCost(int movementType) {
                    if (movementType >= 0 && movementType < this.movementCosts.length) {
                        if (this.movementCosts[movementType] != 0) return this.movementCosts[movementType];
                        else return 1;
                    }
                    return 99;
                }
                
                /**
                 * Return the fastest speed unit can move through the node
                 * with the given movementTypes.
                 * Returns 99 if unit doesn't have any ability to cross the zone
                 * @param movementTypes list of movement abilities the unit has
                 * @return fastest (smallest) speed it can cross the node
                 */
                public double getMovementCost(boolean[] movementTypes) {
                    double cost = 99;
                    for (int i = 0; i < this.movementCosts.length; i++) {
                        if (movementTypes[i] && getMovementCost(i) < cost) cost = getMovementCost(i);
                    }
                    return cost;
                }
                
                             
                public void setMovementCost(int costType, double cost) {
                    if (costType >=0 && costType < this.movementCosts.length)
                        this.movementCosts[costType] = cost;
                }
                
                
                public void setMovementCosts(double[] costs) {
                    if (costs.length == 10)
                        System.arraycopy(costs, 0, this.movementCosts, 0, costs.length);
                }
                
                
                public double getCumulativeCost() {
                    return this.cumulativeCost;
                }
                
                public double getCostEstimate() {
                    return this.estimatedCost;
                }
                
                public int getDepth() {
                    return this.depth;
                }
                
                public Node getPreviousNode() {
                    return this.previousNode;
                }
                
                @Override
                public String toString() {
                    return "Node: "+this.getX()+","+this.getY()+" estimated cost "+this.getCostEstimate();
                } 
                        
                
                @Override
                public int hashCode() {
                    int hash = 5;
                    hash = 29 * hash + this.xCoor;
                    hash = 29 * hash + this.yCoor;
                    return hash;
                }

                @Override
		public boolean equals(Object other) {
			if (other instanceof Node) {
				Node o = (Node) other;
				return (o.xCoor == xCoor) && (o.yCoor == yCoor);
			}		
			return false;
                }
                
                @Override
                public int compareTo(Object other) {
			Node o = (Node) other;
			
			double d = estimatedCost;
			double od =  o.estimatedCost;
			
			if (d < od) {
				return -1;
			} else if (d > od) {
				return 1;
			} else {
				return 0;
			}
		}
        }
