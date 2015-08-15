/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import java.util.ArrayList;

/**
 * Path is a supporting class for PathFinder.
 * It has a beginning and an end, and
 * every step on the path can be backtraced to the previous one.
 * @author nkoiv
 */
public class Path {
	private ArrayList<Node> nodes;
	
	public Path() {
            this.nodes  = new ArrayList<>();
	}

	public int getLength() {
            return this.nodes.size();
	}
	
	public Node getNode(int nodeNumber) {
            return this.nodes.get(nodeNumber);
	}
	
	public int getX(int nodeNumber) {
            return this.getNode(nodeNumber).x;
	}

	public int getY(int nodeNumber) {
            return this.getNode(nodeNumber).y;
	}
	
	//Adds a node to the end of the path
	public void addStep(int xCoor, int yCoor) {
            this.nodes.add(new Node(xCoor,yCoor));
	}

        //Adds a node to the start of the path
	public void prependNode(int xCoor, int yCoor) {
            this.nodes.add(0, new Node(xCoor, yCoor));
	}
	
	public boolean containsNode(int xCoor, int yCoor) {
            return this.nodes.contains(new Node(xCoor,yCoor));
	}
	
	public class Node {
		private int x;
		private int y;
		
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
	
		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

                @Override
                public int hashCode() {
                    int hash = 5;
                    hash = 29 * hash + this.x;
                    hash = 29 * hash + this.y;
                    return hash;
                }

                @Override
		public boolean equals(Object other) {
			if (other instanceof Node) {
				Node o = (Node) other;
				return (o.x == x) && (o.y == y);
			}		
			return false;
                }
        }
}
