/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.util.Toolkit;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class WorldMap {
    private Image backgroundImage;
    private ArrayList<MapLocation> locationsOnMap;
    private ArrayList<MapObject> mobsOnMap;
    private double xOffset;
    private double yOffset;
    
    public WorldMap(Image backgroundImage) {
        this.locationsOnMap = new ArrayList<>();
    }
    
     public void render(GraphicsContext gc) {
        gc.drawImage(backgroundImage, xOffset, yOffset);
        for (MapLocation ml : this.locationsOnMap) {
            ml.render(gc, xOffset, yOffset);
        }
        for (MapObject mob : this.mobsOnMap) {
            mob.render(xOffset, yOffset, gc);
        }
    }
    
    public MapObject mobAtCoordinates(double xCoor, double yCoor) {
        for (MapObject mob : this.mobsOnMap) {
            boolean b = true;
            if (xCoor < mob.getXPos() && xCoor > (mob.getXPos()+mob.getWidth())) b = false;
            if (yCoor < mob.getYPos() && yCoor > (mob.getYPos()+mob.getHeight())) b = false;
            
            if (b) return mob;
        }
        return null;
    }
    
    public MapLocation locationAtCoordinates (double xCoor, double yCoor) {
        for (MapLocation ml : this.locationsOnMap) {
            boolean b = true;
            if (xCoor < ml.getXPos() && xCoor > (ml.getXPos()+ml.getImage().getWidth())) b = false;
            if (yCoor < ml.getYPos() && yCoor > (ml.getYPos()+ml.getImage().getHeight())) b = false;
            
            if (b) return ml;
        }
        return null;
    }
    
    public class MapNode {
        protected String name;
        protected Image imageOnMap;
        protected double xPos;
        protected double yPos;
        ArrayList<MapNode> neighbours;
        
        public MapNode(String name) {
            this.name = name;
        }
        
        public MapNode getNeighbour(Direction d) {
            double dir[] = Toolkit.getDirectionXY(d);
            
            
            return null;
        }
        
        public ArrayList<MapNode> getNeighbours() {
            return this.neighbours;
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
            gc.drawImage(imageOnMap, xOffset, yOffset);
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
    
    
    public class MapLocation extends MapNode {
        
        private Location location; //if already created
        private int locationSeed; //if random generated
        
        
        public MapLocation(String name) {
            super(name);
        }
        
        
    }
    
   
}
