/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.pathfinding;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Path is a supporting class for PathFinder.
 * It has a beginning and an end, and
 * every step on the path can be backtraced to the previous one.
 * 
 * TODO: Consider removing Path and just using linked nodes
 * Nodes themselves can be naturally linked (Node.previousNode()),
 * and in a way the Path is redundant.
 * The Path however is easier and more convenient to access (first, last)
 * by creatures than digging through the chain of nodes every time(?)

 * 
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
            return this.getNode(nodeNumber).getX();
	}

	public int getY(int nodeNumber) {
            return this.getNode(nodeNumber).getY();
	}
	
	//Adds a node to the end of the path
        public void addStep(Node node) {
            this.nodes.add(node);
        }
        
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
        
        /**
         * Draw the path on a given graphics context
         * Mainly used for testing and evaluating the pathfinder
         * algorithm in use
         * @param gc GraphicContext from the game
         * @param tilesize Size of tiles the game is rendered on, to position the path
         * @param xOffset Offset for displayed screen position on the map
         * @param yOffset Offset for displayed screen position on the map
         */
        public void drawPath (GraphicsContext gc, int tilesize, double xOffset, double yOffset) {
            //Mists.logger.info("Trying to draw a path!");
            gc.save();
            //First tile to go to
            if (!this.nodes.isEmpty()) {
                Node startNode = this.nodes.get(0);
                double xTile = ((startNode.getX()*tilesize)+tilesize/4) - xOffset;
                double yTile = ((startNode.getY()*tilesize)+tilesize/4) - yOffset;
                
                gc.setStroke(Color.ORANGERED);
                gc.strokeOval(xTile, yTile, tilesize/4, tilesize/4);
                
            }
            //Lines to sequencal tiles
            if (this.nodes.size()>1) {
                for (int i = 1; i < this.nodes.size()-1; i++) {
                    Node node = this.nodes.get(i);
                    double xTile = ((node.getX()*tilesize)+tilesize/4) - xOffset;
                    double yTile = ((node.getY()*tilesize)+tilesize/4) - yOffset;
                    gc.setStroke(Color.ORANGERED);
                    gc.strokeOval(xTile, yTile, tilesize/4, tilesize/4);
                }
                
            }
            
            //Goaltile
            if (this.nodes.size()>1) {
                Node endNode = this.nodes.get(this.nodes.size()-1);
                double xTile = ((endNode.getX()*tilesize)+tilesize/4) - xOffset;
                double yTile = ((endNode.getY()*tilesize)+tilesize/4) - yOffset;
                gc.setStroke(Color.TEAL);
                gc.strokeOval(xTile, yTile, tilesize/4, tilesize/4);
                
            }
            gc.restore();
        }
        
        @Override
        public String toString() {
            String pathString = "";
            for (Node n : this.nodes) {
                pathString = pathString+("["+n.getX()+","+n.getY()+"] ");
            }
            
            return pathString;
        }
	
}
