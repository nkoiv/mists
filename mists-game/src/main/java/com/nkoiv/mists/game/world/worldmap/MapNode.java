/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.worldmap;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class MapNode {
        protected String name;
        protected Image imageOnMap;
        protected boolean bigNode;
        protected double xPos;
        protected double yPos;
        MapNode[] neighboursByDirection; //0 is left empty, as the direction would be Direction.STAY
        /* Neighbours by direction
        * [8][1][2]
        * [7]   [3]
        * [6][5][4]
        */
        
        public MapNode(String name, Image image) {
            this.name = name;
            this.neighboursByDirection = new MapNode[9];
            if (image != null) {
                this.imageOnMap = image;
                if (image.getWidth() > 32) bigNode = true;
            }
        }
        
        public MapNode getNeighbour(Direction d) {
            return neighboursByDirection[Toolkit.getDirectionNumber(d)];
        }
        
        public void setNeighbour(MapNode neighbour, Direction d) {
            this.neighboursByDirection[Toolkit.getDirectionNumber(d)] = neighbour;
        }
        
        public ArrayList<MapNode> getNeighboursAsAList() {
            ArrayList<MapNode> n = new ArrayList<>();
            for (int i = 1; i < neighboursByDirection.length; i++) {
                if (neighboursByDirection[i] != null) n.add(neighboursByDirection[i]);
            }
            return n;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setImage(Image image) {
            this.imageOnMap = image;
        }
        
        public Image getImage() {
            return this.imageOnMap;
        }
        
        public void render(GraphicsContext gc, double xOffset, double yOffset) {
            if (bigNode) gc.drawImage(Mists.graphLibrary.getImage("circle64"), xPos - xOffset, yPos - yOffset);
            if (!bigNode) gc.drawImage(Mists.graphLibrary.getImage("circle32"), xPos - xOffset, yPos - yOffset);
            if (imageOnMap!=null) gc.drawImage(imageOnMap, xPos - xOffset, yPos - yOffset);
        }
        
        public double getXPos() {
            return this.xPos;
        }
        
        public double getYPos() {
            return this.yPos;
        }
        
        public void setXPos(double xPos) {
            this.xPos = xPos;
        }
        
        public void setYPos(double yPos) {
            this.yPos = yPos;
        }
        
        public void setPosition(double xPos, double yPos) {
            this.xPos = xPos;
            this.yPos = yPos;
        }
        
        
    }
