/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.Location;
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
    private Image[] wallimages;
    
    private boolean[] neighbours = new boolean[8];
    /* Neighbours is the list of walls that affect the looks of this wall
     [0][1][2]
     [3]   [4]   
     [5][6][7]
    */
    
    public Wall(String name, Image image, int collisionLevel, Location location, int xCoor, int yCoor, ImageView wallparts) {
        this(name, image, collisionLevel, location, xCoor, yCoor);
        this.wallparts = wallparts;
    }
    
    public Wall(String name, Image image, int collisionLevel, Location location, int xCoor, int yCoor, Image[] wallparts) {
        this(name, image, collisionLevel, location, xCoor, yCoor);
        this.wallimages = wallparts;
    }
    
    private Wall(String name, Image image, int collisionLevel, Location location, int xCoor, int yCoor) {
        super(name, image, location, xCoor, yCoor);
        this.setCollisionLevel(collisionLevel);
        this.wallimages = new Image[8];
    }
    
 
    public void updateNeighbours() {
        this.removeExtras();
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        //Cardinals
        if (neighbours[1] ==false) {
            Sprite s;
            if (this.wallimages[1]!=null) {
                s = new Sprite(this.wallimages[1]);
            } else {
                wallparts.setViewport(new Rectangle2D(this.sprite.getWidth(),0,this.sprite.getWidth(),this.sprite.getHeight()));
                WritableImage upWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(upWall);
            }
            this.addExtra(s, 0, -5);
        }
        if (neighbours[3] ==false) {
            Sprite s;
            if (this.wallimages[1]!=null) {
                s = new Sprite(this.wallimages[3]);
            } else {
                wallparts.setViewport(new Rectangle2D(this.sprite.getWidth()*3,0,this.sprite.getWidth(),this.sprite.getHeight()));
                WritableImage leftWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(leftWall);
            }
            this.addExtra(s, 0, -5);
        }
        if (neighbours[4] ==false) {
            Sprite s;
            if (this.wallimages[1]!=null) {
                s = new Sprite(this.wallimages[4]);
            } else {
                wallparts.setViewport(new Rectangle2D(this.sprite.getWidth()*2,0,this.sprite.getWidth(),this.sprite.getHeight()));
                WritableImage rightWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(rightWall);
            }
            this.addExtra(s, 0, -5);
        }
        if (neighbours[6] ==false) {
            Sprite s;
            if (this.wallimages[1]!=null) {
                s = new Sprite(this.wallimages[6]);
            } else {
                wallparts.setViewport(new Rectangle2D(0,0,this.sprite.getWidth(),this.sprite.getHeight()));
                WritableImage downWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(downWall);
            }
            //this.addExtra(s, 0, 0);
            this.sprite.setImage(s.getImage());
        }
        //Diagonals
        /*
        if (neighbours[0] ==false) {
            Sprite s;
            if (this.wallimages[0]!=null) {
                s = new Sprite(this.wallimages[0]);
            } else {
                wallparts.setViewport(new Rectangle2D(0,this.sprite.getHeight(),this.sprite.getWidth(),this.sprite.getHeight()));
                WritableImage upWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(upWall);
            }
            this.addExtra(s, 0, -8);
        }
        if (neighbours[5] ==false) {
            Sprite s;
            if (this.wallimages[5]!=null) {
                s = new Sprite(this.wallimages[5]);
            } else {
                wallparts.setViewport(new Rectangle2D(this.sprite.getWidth(),this.sprite.getHeight(),this.sprite.getWidth(),this.sprite.getHeight()));
                WritableImage leftWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(leftWall);
            }
            this.addExtra(s, 0, 0);
        }
        if (neighbours[7] ==false) {
            Sprite s;
            if (this.wallimages[7]!=null) {
                s = new Sprite(this.wallimages[7]);
            } else {
                wallparts.setViewport(new Rectangle2D(this.sprite.getWidth()*2,this.sprite.getHeight(),this.sprite.getWidth(),this.sprite.getHeight()));
                WritableImage rightWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(rightWall);
            }
            this.addExtra(s, 0, 0);
        }
        if (neighbours[2] ==false) {
            Sprite s;
            if (this.wallimages[2]!=null) {
                s = new Sprite(this.wallimages[2]);
            } else {
                wallparts.setViewport(new Rectangle2D(this.sprite.getWidth()*3,this.sprite.getHeight(),this.sprite.getWidth(),this.sprite.getHeight()));
                WritableImage downWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(downWall);
            }
            this.addExtra(s, 0, -8);
        }
        */
    }
    
    public void setWallImages(Image[] wallimages) {
        this.wallimages = wallimages;
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
     * 
     * In imagefile:
     * [6][1][3][4] Cardinal walls
     * [0][5][7][2] Optional diagonal walls
     * @param n The number of the neighbour to remove
     */
    public void removeNeighbour(int n) {
        this.neighbours[n] = false;
    }
    
    public void generateWallImages(ImageView wallparts) {
        
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
       
        //Cardinal
        wallparts.setViewport(new Rectangle2D(0,0,this.sprite.getWidth(),this.sprite.getHeight()));
        WritableImage downWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(32,0,this.sprite.getWidth(),this.sprite.getHeight()));
        WritableImage upWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(64,0,this.sprite.getWidth(),this.sprite.getHeight()));
        WritableImage rightWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(96,0,this.sprite.getWidth(),this.sprite.getHeight()));
        WritableImage leftWall = wallparts.snapshot(parameters, snapshot);
        //Diagonal
        wallparts.setViewport(new Rectangle2D(0,32,this.sprite.getWidth(),this.sprite.getHeight()));
        WritableImage upleftWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(32,32,this.sprite.getWidth(),this.sprite.getHeight()));
        WritableImage downleftWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(64,32,this.sprite.getWidth(),this.sprite.getHeight()));
        WritableImage downrightWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(96,32,this.sprite.getWidth(),this.sprite.getHeight()));
        WritableImage uprightWall = wallparts.snapshot(parameters, snapshot);
        
        this.wallimages = new Image[]{upleftWall, upWall, uprightWall, leftWall, rightWall, downleftWall, downWall, downrightWall};
        
    }
    
    @Override
    public Wall createFromTemplate() {
        if (this.wallimages == null) this.generateWallImages(this.wallparts);
        Wall newWall = new Wall(this.name, this.getSprite().getImage(), this.getCollisionLevel(), null, 0, 0, this.wallparts);
        return newWall;
    }
}
