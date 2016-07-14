/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.gameobject;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;

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
public class Water extends Structure implements HasNeighbours{
    private boolean[] neighbours;
    private ImageView waterImages;
    private ImageView waterImages_alt;
    private SpriteAnimation[] animatedTiles;
    
    public Water() {
        super();
    }
    
    public Water(String name, ImageView shapeImages, ImageView shapeImages_alt) {
        super(name, new Sprite(), 10);
        this.waterImages = shapeImages;
        this.waterImages_alt = shapeImages_alt;
        this.graphics = new Sprite(this.getImageFromImageView(shapeImages, 0, 0));
        this.neighbours = new boolean[8];
        this.animatedTiles = new SpriteAnimation[18];
    }
    
    @Override
    public boolean[] checkNeighbours() {
        boolean[] newNeighbours = new boolean[8];
        if (this.location == null) return newNeighbours;
        double xCoor = this.getCenterXPos();
        double yCoor = this.getCenterYPos();
        MapObject mob;
        //UpLeft
        mob = this.location.getStructureAtLocation(xCoor-this.getWidth(), yCoor-this.getHeight());
        if (mob instanceof Water) newNeighbours[0] = true;
        //Up
        mob = this.location.getStructureAtLocation(xCoor, yCoor-this.getHeight());
        if (mob instanceof Water) newNeighbours[1] = true;
        //UpRight
        mob = this.location.getStructureAtLocation(xCoor+this.getWidth(), yCoor-this.getHeight());
        if (mob instanceof Water) newNeighbours[2] = true;
        //Left
        mob = this.location.getStructureAtLocation(xCoor-this.getWidth(), yCoor);
        if (mob instanceof Water) newNeighbours[3] = true;
        //Right
        mob = this.location.getStructureAtLocation(xCoor+this.getWidth(), yCoor);
        if (mob instanceof Water) newNeighbours[4] = true;
        //DownLeft
        mob = this.location.getStructureAtLocation(xCoor-this.getWidth(), yCoor+this.getHeight());
        if (mob instanceof Water) newNeighbours[5] = true;
        //Down
        mob = this.location.getStructureAtLocation(xCoor, yCoor+this.getHeight());
        if (mob instanceof Water) newNeighbours[6] = true;
        //DownRight
        mob = this.location.getStructureAtLocation(xCoor+this.getWidth(), yCoor+this.getHeight());
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
    
    /**
     * Preload the imageviews in this watertile
     * into SpriteAnimations, so they can be accessed
     * (and copied) faster later.
     */
    public void generateWaterTilesFromImageView() {
        for (int i = 0; i < this.animatedTiles.length; i++) {
            animatedTiles[i] = generateSpriteAnimationFromImageViews(i);
        }
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
            case 15: return generateSpriteAnimationFromImageViews(0,5);
            case 16: return generateSpriteAnimationFromImageViews(1,5);
            case 17: return generateSpriteAnimationFromImageViews(2,5);
            default: return generateSpriteAnimationFromImageViews(0,0);
        }
    }
    
    private SpriteAnimation generateSpriteAnimationFromImageViews(int x, int y) {
        //Frame1
        Image frame1 = getImageFromImageView(this.waterImages, (int)(x*this.getWidth()), (int)(y*this.getHeight()));
        //Frame2
        Image frame2 = getImageFromImageView(this.waterImages_alt, (int)(x*this.getWidth()), (int)(y*this.getHeight()));
        
        return new SpriteAnimation(this.name+"_animation", new Image[]{frame1, frame2});
    }
    
    private Image getImageFromImageView(ImageView imageview, int x, int y) {
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        
        imageview.setViewport(new Rectangle2D(x,y, imageview.getImage().getWidth()/3,imageview.getImage().getHeight()/6));
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
        /*  +----9----+----10---+---11----+
            |[ ][x][x]|[x][x][x]|[x][x][ ]|
            |[ ][x][x]|[x][x][x]|[x][x][ ]|
            |[ ][x][x]|[x][x][x]|[x][x][ ]|
            +---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{false, true, true, false, true, false, true, true})) return 9;
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, true, true, true})) return randomIDFromArray(new int[]{10, 15,16, 17});
        if (Arrays.equals(neighbours, new boolean[]{true, true, false, true, false, true, true, false})) return 11;
        /*  +----12---+----13---+----14---+
            |[ ][x][x]|[x][x][x]|[x][x][ ]|
            |[ ][0][x]|[x][0][x]|[x][0][ ]|
            |[ ][ ][ ]|[ ][ ][ ]|[ ][ ][ ]|
            +---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{false, true, true, false, true, false, false, false})) return 12;
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, false, false, false})) return 13;
        if (Arrays.equals(neighbours, new boolean[]{true, true, false, true, false, false, false, false})) return 14;
        /*  +----15---+----16---+----17---+
            |[x][x][x]|[x][x][x]|[x][x][x]|
            |[x][0][x]|[x][0][x]|[x][0][x]|
            |[x][x][x]|[x][x][x]|[x][x][x]|
            +---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, true, true, true})) return randomIDFromArray(new int[]{10, 15,16, 17});
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, true, true, true})) return randomIDFromArray(new int[]{10, 15,16, 17});
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, true, true, true})) return randomIDFromArray(new int[]{10, 15,16, 17});
        
        
        //Corner variations
        /*  +----9----+-----9---+---11----+---11----+
            |[x][x][x]|[ ][x][x]|[x][x][x]|[x][x][ ]|
            |[ ][0][x]|[ ][0][x]|[x][0][ ]|[x][0][ ]|
            |[ ][x][x]|[x][x][x]|[x][x][ ]|[x][x][x]|
            +---------+---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, false, true, false, true, true})) return 9;
        if (Arrays.equals(neighbours, new boolean[]{false, true, true, false, true, true, true, true})) return 9;
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, false, true, true, false})) return 11;
        if (Arrays.equals(neighbours, new boolean[]{true, true, false, true, false, true, true, true})) return 11;
        /*  +----7----+-----7---+---13----+---13----+
            |[x][ ][ ]|[ ][ ][x]|[x][x][x]|[x][x][x]|
            |[x][0][x]|[x][0][x]|[x][0][x]|[x][0][x]|
            |[x][x][x]|[x][x][x]|[x][ ][ ]|[ ][ ][x]|
            +---------+---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{true, false, false, true, true, true, true, true})) return 7;
        if (Arrays.equals(neighbours, new boolean[]{false, false, true, true, true, true, true, true})) return 7;
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, true, false, false})) return 13;
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, true, false, false, true})) return 13;
        /*  +----6----+----8----+----12---+----14---+
            |[ ][ ][ ]|[ ][ ][ ]|[x][x][x]|[x][x][x]|
            |[ ][0][x]|[x][0][ ]|[ ][0][x]|[x][0][ ]|
            |[x][x][x]|[x][x][x]|[ ][ ][ ]|[ ][ ][ ]|
            +---------+---------+---------+ */
        if (Arrays.equals(neighbours, new boolean[]{false, false, false, false, true, true, true, true})) return 6;
        if (Arrays.equals(neighbours, new boolean[]{false, false, false, true, false, true, true, true})) return 8;
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, false, true, false, false, false})) return 12;
        if (Arrays.equals(neighbours, new boolean[]{true, true, true, true, false, false, false, false})) return 14;
        
        
        //Reaching here means we have a weird neighbours-array
        Mists.logger.log(Level.WARNING, "WARNING: {0} at {1}x{2} has neighbours: {3}", new Object[]{this.getName(), this.getXPos(), this.getYPos(), Arrays.toString(this.neighbours)});
        return 0;
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
    
    @Override
    public void write(Kryo kryo, Output output) {
    	super.write(kryo, output);
    	for (int i = 0; i < 8;  i++) {
    		output.writeBoolean(this.neighbours[i]);
    	}
    }

	@Override
	public void read(Kryo kryo, Input input) {
		super.read(kryo, input);
		this.neighbours = new boolean[8];
		for (int i = 0; i < 8; i++) {
			this.neighbours[i] = input.readBoolean();
		}
	}
	
	protected void readGraphicsFromLibrary(int templateID, double xCoor, double yCoor) {
		if (Mists.structureLibrary != null) {
			Structure dummy = Mists.structureLibrary.create(templateID);
			if (dummy == null) return;
			this.graphics = dummy.graphics;
			this.extraSprites = dummy.extraSprites;
			if (dummy instanceof Water) {
				this.waterImages = ((Water)dummy).waterImages;
				this.waterImages_alt = ((Water)dummy).waterImages_alt;
				this.animatedTiles = ((Water)dummy).animatedTiles;
			}
		} else this.graphics = new Sprite();
		this.setPosition(xCoor, yCoor);
	}
    
}
