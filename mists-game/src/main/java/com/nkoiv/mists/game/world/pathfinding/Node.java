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
                private int size;
                private double estimatedCost; //Node cost for when used by PathFinder (from here to goal)
                private int depth; //Depth in the path for when used by PathFinder
                private Node previousNode; //for PathFinder
		
		public Node(int x, int y) {
			this.xCoor = x;
			this.yCoor = y;
                        this.size = Global.TILESIZE;
                        this.collisionLevel = 0;
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
                
                public void setDepth(int depth) {
                    this.depth = depth;
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
