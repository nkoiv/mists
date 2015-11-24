/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * A wall section has its appearance based on whats around it
 * Since every walltile can have up to 8 other walls surrounding it:
 * [x][x][x]
 * [x][*][x]
 * [x][x][x]
 * 
 * Wall is based on which sides around it do NOT have more walls.
 * 
 * [ ][x][ ]    [ ][x][ ]   [ ][ ][ ]   [x][x][x]
 * [ ][*][ ]    [ ][*][ ]   [ ][*][ ]   [x][x][x]
 * [ ][x][ ]    [ ][ ][ ]   [ ][x][ ]   [x][x][x]
 * 
 * Corners
 * [x][x][ ]    [ ][x][x]   [ ][ ][ ]   [ ][ ][ ]
 * [x][*][ ]    [ ][*][x]   [x][*][ ]   [ ][*][x]
 * [ ][ ][ ]    [ ][ ][ ]   [x][x][ ]   [ ][x][x]
 * 
 * 
 * 
 * There's a total of 47 various arrangements how these can be made.
 * 
 * @author nikok
 */
public class Wall extends Structure {
    private ImageView wallparts;
    
    private boolean[] neighbours = new boolean[8];
    /* Neighbours is the list of walls that affect the looks of this wall
     [0][1][2]
     [3]   [4]   
     [5][6][7]
    */
    
    public Wall(String name, Image image, int collisionLevel, Location location, int xCoor, int yCoor, ImageView wallparts) {
        super(name, image, location, xCoor, yCoor);
        this.setCollisionLevel(collisionLevel);
        this.wallparts = wallparts;
    }
 
    public void updateNeighbours() {
        this.removeExtras();
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        if (neighbours[1] ==false) {
            wallparts.setViewport(new Rectangle2D(32,0,this.sprite.getWidth(),this.sprite.getHeight()));
            WritableImage upWall = wallparts.snapshot(parameters, snapshot);
            Sprite s = new Sprite(upWall);
            this.addExtra(s, 0, 0);
        }
        if (neighbours[3] ==false) {
            wallparts.setViewport(new Rectangle2D(96,0,this.sprite.getWidth(),this.sprite.getHeight()));
            WritableImage leftWall = wallparts.snapshot(parameters, snapshot);
            Sprite s = new Sprite(leftWall);
            this.addExtra(s, 0, 0);
        }
        if (neighbours[4] ==false) {
            wallparts.setViewport(new Rectangle2D(64,0,this.sprite.getWidth(),this.sprite.getHeight()));
            WritableImage rightWall = wallparts.snapshot(parameters, snapshot);
            Sprite s = new Sprite(rightWall);
            this.addExtra(s, 0, 0);
        }
        if (neighbours[6] ==false) {
            wallparts.setViewport(new Rectangle2D(0,0,this.sprite.getWidth(),this.sprite.getHeight()));
            WritableImage downWall = wallparts.snapshot(parameters, snapshot);
            Sprite s = new Sprite(downWall);
            this.addExtra(s, 0, 0);
        }
        
    }
    
    public boolean[] getNeighbours() {
        return this.neighbours;
    }

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
    public void addNeighbour(int n) {
        this.neighbours[n] = true;
    }
    
     /**
     * Remove a neighbouring wall from this wall
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * @param n The number of the neighbour to remove
     */
    public void removeNeighbour(int n) {
        this.neighbours[n] = false;
    }
    
}
