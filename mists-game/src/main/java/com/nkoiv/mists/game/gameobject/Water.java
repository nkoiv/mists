/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.MovingGraphics;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Water is a type of terrain obstacle that needs
 * to be tiled to look nice. Not much unlike Walls.
 * @author nikok
 */
public class Water extends MapObject implements HasNeighbours{
    private boolean[] neighbours;
    private static Image[] shapeImages;
    private static Image[] shapeImages_alt;
    
    public Water(String name, Image[] shapeImages, Image[] shapeImages_alt) {
        super(name);
    }

    public Water(String name, ImageView shapeImages, ImageView shapeImages_alt) {
        super(name);
    }
    
    @Override
    public boolean[] checkNeighbours() {
        boolean[] newNeighbours = new boolean[8];
        if (this.location == null) return newNeighbours;
        double xCoor = this.getCenterXPos();
        double yCoor = this.getCenterYPos();
        MapObject mob;
        //UpLeft
        mob = this.location.getMobAtLocation(xCoor-this.getWidth(), yCoor-this.getHeight());
        if (mob instanceof Water) newNeighbours[0] = true;
        //Up
        mob = this.location.getMobAtLocation(xCoor, yCoor-this.getHeight());
        if (mob instanceof Water) newNeighbours[1] = true;
        //UpRight
        mob = this.location.getMobAtLocation(xCoor+this.getWidth(), yCoor-this.getHeight());
        if (mob instanceof Water) newNeighbours[2] = true;
        //Left
        mob = this.location.getMobAtLocation(xCoor-this.getWidth(), yCoor);
        if (mob instanceof Water) newNeighbours[3] = true;
        //Right
        mob = this.location.getMobAtLocation(xCoor+this.getWidth(), yCoor);
        if (mob instanceof Water) newNeighbours[4] = true;
        //DownLeft
        mob = this.location.getMobAtLocation(xCoor-this.getWidth(), yCoor+this.getHeight());
        if (mob instanceof Water) newNeighbours[5] = true;
        //Down
        mob = this.location.getMobAtLocation(xCoor, yCoor+this.getHeight());
        if (mob instanceof Water) newNeighbours[6] = true;
        //DownRight
        mob = this.location.getMobAtLocation(xCoor+this.getWidth(), yCoor+this.getHeight());
        if (mob instanceof Wall) newNeighbours[7] = true;
        
        return newNeighbours;
    }
    
    @Override
    public boolean[] getNeighbours() {
        return this.neighbours;
    }

    @Override
    public void setNeighbours(boolean[] neighbours) {
        this.neighbours = neighbours;
    }
    
    /**
     * Add a neighbouring wall to this wall
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * @param n The number of the neighbour added
     */
    @Override
    public void addNeighbour(int n) {
        this.neighbours[n] = true;
    }
    
     /**
     * Remove a neighbouring wall from this wall
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * 
     * In imagefile:
     * [6][1][3][4] Cardinal walls
     * [0][5][7][2] Optional diagonal walls
     * @param n The number of the neighbour to remove
     */
    @Override
    public void removeNeighbour(int n) {
        this.neighbours[n] = false;
    }

    @Override
    public void updateGraphicsBasedOnNeighbours() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
