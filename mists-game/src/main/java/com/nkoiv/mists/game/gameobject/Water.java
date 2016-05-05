/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import java.util.Arrays;
import java.util.Random;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Water is a type of terrain obstacle that needs
 * to be tiled to look nice. Not much unlike Walls.
 * @author nikok
 */
public class Water extends MapObject implements HasNeighbours{
    private boolean[] neighbours;
    private ImageView waterImages;
    private ImageView waterImages_alt;
    private SpriteAnimation[] animatedTiles;
    
    public Water(String name, ImageView shapeImages, ImageView shapeImages_alt) {
        super(name);
        this.waterImages = shapeImages;
        this.waterImages_alt = shapeImages_alt;
        this.graphics = new Sprite(this.getImageFromImageView(shapeImages, 0, 0));
        this.neighbours = new boolean[8];
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
        if (mob instanceof Water) newNeighbours[7] = true;
        
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
     * Add a neighbouring watertile to this watertile
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
     * Remove a neighbouring watertile from this watertile
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * @param n The number of the neighbour to remove
     */
    @Override
    public void removeNeighbour(int n) {
        this.neighbours[n] = false;
    }

    private SpriteAnimation generateSpriteAnimationFromImageViews(int tileID) {
        switch (tileID) {
            case 0: return generateSpriteAnimationFromImageViews(0,0);
            case 1: return generateSpriteAnimationFromImageViews(1,0);
            case 2: return generateSpriteAnimationFromImageViews(2,0);
            case 3: return generateSpriteAnimationFromImageViews(0,1);
            case 4: return generateSpriteAnimationFromImageViews(1,1);
            case 5: return generateSpriteAnimationFromImageViews(2,1);
            case 6: return generateSpriteAnimationFromImageViews(0,2);
            case 7: return generateSpriteAnimationFromImageViews(1,2);
            case 8: return generateSpriteAnimationFromImageViews(2,2);
            case 9: return generateSpriteAnimationFromImageViews(0,3);
            case 10: return generateSpriteAnimationFromImageViews(1,3);
            case 11: return generateSpriteAnimationFromImageViews(2,3);
            case 12: return generateSpriteAnimationFromImageViews(0,4);
            case 13: return generateSpriteAnimationFromImageViews(1,4);
            case 14: return generateSpriteAnimationFromImageViews(2,4);
            default: return generateSpriteAnimationFromImageViews(0,0);
        }
    }
    
    private SpriteAnimation generateSpriteAnimationFromImageViews(int x, int y) {
        //Frame1
        Image frame1 = getImageFromImageView(this.waterImages, (int)(x*this.getWidth()), (int)(y*this.getHeight()));
        //Frame2
        Image frame2 = getImageFromImageView(this.waterImages_alt, (int)(x*this.getWidth()), (int)(y*this.getHeight()));
        
        return new SpriteAnimation(new Image[]{frame1, frame2});
    }
    
    private Image getImageFromImageView(ImageView imageview, int x, int y) {
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        
        imageview.setViewport(new Rectangle2D(x,y, imageview.getImage().getWidth()/3,imageview.getImage().getHeight()/5));
        return imageview.snapshot(parameters, snapshot);
    }
    
    private int randomIDFromArray(int[] array) {
        Random rnd = new Random();
        int r = rnd.nextInt(array.length);
        return array[r];
    }
    
    private int getWaterGraphicsIDbyNeighbours() {        
        /*  +----0----+----1----+----2----+
            |[ ][ ][ ]|[x][x][x]|[x][x][x]|
            |[ ][0][ ]|[x][0][x]|[x][0][x]|
            |[ ][ ][ ]|[x][x][ ]|[ ][x][x]|
            +---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{false, false, false, false, false, false, false, false})) return randomIDFromArray(new int[]{0,3});
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, true, true, false})) return 1;
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, false, true, true})) return 2;
        /*  +----3----+----4----+----5----+
            |[ ][ ][ ]|[x][x][ ]|[ ][x][x]|
            |[ ][0][ ]|[x][0][x]|[x][0][x]|
            |[ ][ ][ ]|[x][x][x]|[x][x][x]|
            +---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{false, false, false, false, false, false, false, false})) return randomIDFromArray(new int[]{0,3});
        if (Arrays.equals(neighbours, new boolean[]{true, true, false, true, true, true, true, true})) return 4;
        if (Arrays.equals(neighbours, new boolean[]{false, true, true, true, true, true, true, true})) return 5;
        /*  +----6----+----7----+----8----+
            |[ ][ ][ ]|[ ][ ][ ]|[ ][ ][ ]|
            |[ ][0][x]|[x][0][x]|[x][0][ ]|
            |[ ][x][x]|[x][x][x]|[x][x][ ]|
            +---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{false, false, false, false, true, false, true, true})) return 6;
        if (Arrays.equals(neighbours, new boolean[]{false, false, false, true, true, true, true, true})) return 7;
        if (Arrays.equals(neighbours, new boolean[]{false, false, false, true, false, true, true, false})) return 8;
        /*  +----9----+----10---+----11---+
            |[ ][x][x]|[x][x][x]|[x][x][ ]|
            |[ ][0][x]|[x][0][x]|[x][0][ ]|
            |[ ][ ][ ]|[ ][ ][ ]|[ ][ ][ ]|
            +---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{false, true, true, false, true, false, false, false})) return 9;
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, false, false, false})) return 10;
        if (Arrays.equals(neighbours, new boolean[]{true, true, false, true, false, false, false, false})) return 11;
        /*  +----12---+----13---+----14---+
            |[x][x][x]|[x][x][x]|[x][x][x]|
            |[x][0][x]|[x][0][x]|[x][0][x]|
            |[x][x][x]|[x][x][x]|[x][x][x]|
            +---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, true, true, true})) return randomIDFromArray(new int[]{12,13, 14});
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, true, true, true})) return randomIDFromArray(new int[]{12,13, 14});
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, true, true, true})) return randomIDFromArray(new int[]{12,13, 14});
        
        return -1;
    }
    
    @Override
    public void updateGraphicsBasedOnNeighbours() {
        int id = getWaterGraphicsIDbyNeighbours();
        if (this.animatedTiles[id] == null) this.animatedTiles[id] = generateSpriteAnimationFromImageViews(id);
        ((Sprite)this.graphics).setAnimation(this.animatedTiles[getWaterGraphicsIDbyNeighbours()]);
    }
    
    @Override
    public String[] getInfoText() {
        String[] s = new String[]{
            this.name,
            "ID "+this.IDinLocation+" @ "+this.location.getName(),
            "X:"+((int)this.getXPos())+" Y:"+((int)this.getYPos()),
            "Neighbours: ",
            Arrays.toString(neighbours),
            "WIK: "+this.templateID+"-"+Arrays.toString(this.neighbours),
        };
        return s;
    }
    
    @Override
    public Water createFromTemplate() {
        Water newWater = new Water(name, waterImages, waterImages_alt);
        newWater.animatedTiles = this.animatedTiles;
        newWater.setTemplateID(this.templateID);
        
        return newWater;
    }
    
}
