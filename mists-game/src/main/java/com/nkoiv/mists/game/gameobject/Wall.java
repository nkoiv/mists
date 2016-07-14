/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

import java.util.Arrays;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.util.Toolkit;

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
public class Wall extends Structure implements HasNeighbours {
    //GeneratedWallImages key: templateID+"-"+Arrays.toString(neighbours)
    private static final HashMap<String, Image> GENERATED_COMPOSITE_IMAGES = new HashMap<>();
    
    private ImageView wallparts;
    private Image[] wallimages;
    private boolean useExtrasForWalls;
    private double topWallAdjustX;
    private double topWallAdjustY;
    
    private boolean[] neighbours;
    /* Neighbours is the list of similiar objects that affect the looks of this map object
     [0][1][2]
     [3]   [4]   
     [5][6][7]
    */
    
    public Wall() {
        super();
    }
    
    public Wall(String name, Image image, int collisionLevel, ImageView wallparts) {
        super(name, image, collisionLevel);
        this.wallparts = wallparts;
        this.useExtrasForWalls = false;
    }
    
    public Wall(String name, Image image, int collisionLevel,  Image[] wallimages) {
        super(name, image, collisionLevel);
        this.wallimages = wallimages;
        this.useExtrasForWalls = false;
    }
    
    /**
     * TopWallAdjust is the X and Y by which the top wall 
     * component adjusted when layered on the composite wall-image.
     * This exists because sometimes the wall is needed
     * to be a bit "higher" than it actually blocks, so that
     * it can be walked "behind".
     * @param topWallAdjustX xCoordinate for the adjustment
     * @param topWallAdjustY yCoordinate for the adjustment
     */
    public void setTopWallAdjust(double topWallAdjustX, double topWallAdjustY) {
        this.topWallAdjustX = topWallAdjustX;
        this.topWallAdjustY = topWallAdjustY;
    }
    /**
     * Check the current location of this object
     * and get the neighbouring walls as an array
     * [0][1][2]
     * [3]   [4]   
     * [5][6][7]
     * @return Array of neighbouring walls
     */
    @Override
    public boolean[] checkNeighbours() {
        boolean[] newNeighbours = new boolean[8];
        if (this.location == null) return newNeighbours;
        double xCoor = this.getCenterXPos();
        double yCoor = this.getCenterYPos();
        MapObject mob;
        //UpLeft
        mob = this.location.getStructureAtLocation(xCoor-this.getWidth(), yCoor-this.getHeight());
        if (mob instanceof Wall) newNeighbours[0] = true;
        //Up
        mob = this.location.getStructureAtLocation(xCoor, yCoor-this.getHeight());
        if (mob instanceof Wall) newNeighbours[1] = true;
        //UpRight
        mob = this.location.getStructureAtLocation(xCoor+this.getWidth(), yCoor-this.getHeight());
        if (mob instanceof Wall) newNeighbours[2] = true;
        //Left
        mob = this.location.getStructureAtLocation(xCoor-this.getWidth(), yCoor);
        if (mob instanceof Wall) newNeighbours[3] = true;
        //Right
        mob = this.location.getStructureAtLocation(xCoor+this.getWidth(), yCoor);
        if (mob instanceof Wall) newNeighbours[4] = true;
        //DownLeft
        mob = this.location.getStructureAtLocation(xCoor-this.getWidth(), yCoor+this.getHeight());
        if (mob instanceof Wall) newNeighbours[5] = true;
        //Down
        mob = this.location.getStructureAtLocation(xCoor, yCoor+this.getHeight());
        if (mob instanceof Wall) newNeighbours[6] = true;
        //DownRight
        mob = this.location.getStructureAtLocation(xCoor+this.getWidth(), yCoor+this.getHeight());
        if (mob instanceof Wall) newNeighbours[7] = true;
        
        return newNeighbours;
    }
    
    /**
     * Scan the surrounding walls for neighbouring walls,
     * and update this walls list of neighbours accordingly.
     */
    @Override
    public void updateGraphicsBasedOnNeighbours() {
        if (this.wallimages == null) this.generateWallImages(this.wallparts);
        if (!this.useExtrasForWalls) {        	
            //----NEW stuff, using one Extra for graphics always
            Image base = Mists.graphLibrary.getImage("black");
            Image walls = this.composeImage();
            this.getSprite().setImage(base);
            this.addExtra(this.composeImage(), 0, walls.getHeight() - base.getHeight());
            //----OLD stuff ----
            //this.getSprite().setImage(this.composeImage());
            //this.removeExtras();
            //Still always use the extra for [1], aka top wall
            //so creatures can move behind it!
            //Top Extra is always needed, so creatures can get "behind" the wall
            /*
            WritableImage snapshot = null;
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            if (!neighbours[1]) {
                Sprite s;
                if (this.wallimages[1]!=null) {
                    s = new Sprite(this.wallimages[1]);
                } else {
                    wallparts.setViewport(new Rectangle2D(this.getWidth(),0,this.getWidth(),this.getHeight()));
                    WritableImage upWall = wallparts.snapshot(parameters, snapshot);
                    s = new Sprite(upWall);
                }
                this.addExtra(s, topWallAdjustX, topWallAdjustY);
            }
            */
        }else {
            updateSpriteExtras();
        }
    }
    
    private void updateSpriteExtras() {
        boolean noNeedForLowerDiagonals = false; //Flag if [6] is set, in which case [5] and [7] are not needed
        this.removeExtras();
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        //Cardinals
        if (!neighbours[1]) {
            Sprite s;
            if (this.wallimages[1]!=null) {
                s = new Sprite(this.wallimages[1]);
            } else {
                wallparts.setViewport(new Rectangle2D(this.getWidth(),0,this.getWidth(),this.getHeight()));
                WritableImage upWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(upWall);
            }
            this.addExtra(s, topWallAdjustX, topWallAdjustY);
        }
        if (!neighbours[3]) {
            Sprite s;
            if (this.wallimages[3]!=null) {
                s = new Sprite(this.wallimages[3]);
            } else {
                wallparts.setViewport(new Rectangle2D(this.getWidth()*3,0,this.getWidth(),this.getHeight()));
                WritableImage leftWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(leftWall);
            }
            this.addExtra(s, 0, -4);
        }
        if (!neighbours[4]) {
            Sprite s;
            if (this.wallimages[4]!=null) {
                s = new Sprite(this.wallimages[4]);
            } else {
                wallparts.setViewport(new Rectangle2D(this.graphics.getWidth()*2,0,this.graphics.getWidth(),this.graphics.getHeight()));
                WritableImage rightWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(rightWall);
            }
            this.addExtra(s, 0, -4);
        }
        if (!neighbours[6]) {
            Sprite s;
            if (this.wallimages[6]!=null) {
                s = new Sprite(this.wallimages[6]);
            } else {
                wallparts.setViewport(new Rectangle2D(0,0,this.graphics.getWidth(),this.graphics.getHeight()));
                WritableImage downWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(downWall);
            }
            //this.addExtra(s, 0, 0);
            this.getSprite().setImage(s.getImage());
            noNeedForLowerDiagonals = true;
        }
        //Diagonals

        if (!neighbours[0]) {
            Sprite s;
            if (this.wallimages[0]!=null) {
                s = new Sprite(this.wallimages[0]);
            } else {
                wallparts.setViewport(new Rectangle2D(0,graphics.getHeight(),graphics.getWidth(),graphics.getHeight()));
                WritableImage upWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(upWall);
            }
            this.addExtra(s, 0, -8);
        }
        if (!neighbours[5]) {
            Sprite s;
            if (this.wallimages[5]!=null) {
                s = new Sprite(this.wallimages[5]);
            } else {
                wallparts.setViewport(new Rectangle2D(graphics.getWidth(),graphics.getHeight(),graphics.getWidth(),graphics.getHeight()));
                WritableImage leftWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(leftWall);
            }
            if (!noNeedForLowerDiagonals) this.addExtra(s, 0, 0);
        }
        if (!neighbours[7]) {
            Sprite s;
            if (this.wallimages[7]!=null) {
                s = new Sprite(this.wallimages[7]);
            } else {
                wallparts.setViewport(new Rectangle2D(graphics.getWidth()*2,graphics.getHeight(),graphics.getWidth(),graphics.getHeight()));
                WritableImage rightWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(rightWall);
            }
            if (!noNeedForLowerDiagonals) this.addExtra(s, 0, 0);
        }
        if (!neighbours[2]) {
            Sprite s;
            if (this.wallimages[2]!=null) {
                s = new Sprite(this.wallimages[2]);
            } else {
                wallparts.setViewport(new Rectangle2D(graphics.getWidth()*3,graphics.getHeight(),graphics.getWidth(),graphics.getHeight()));
                WritableImage downWall = wallparts.snapshot(parameters, snapshot);
                s = new Sprite(downWall);
            }
            this.addExtra(s, 0, -8);
        }

    }
    
    private Image composeImage() {
        String wallImagesKey = (this.templateID+"-"+Arrays.toString(this.neighbours));
        if (!GENERATED_COMPOSITE_IMAGES.containsKey(wallImagesKey)) {
            //Mists.logger.log(Level.INFO, "Composite image for {0} was not found, creating one", wallImagesKey);
            Image[] extraImages = new Image[9];
            extraImages[0] = Mists.graphLibrary.getImage("black");
            int n = 1;
            boolean noNeedForLowerCardinals = true; //neighbours[6] //TODO: Do we EVER need the lower corners?
            for (int i = 0; i < this.neighbours.length; i++) {
                if (!this.neighbours[i]) {
                    if ((i == 5 || i == 7) && noNeedForLowerCardinals) {
                        //No need to add lower right or lower left corner if lower center is present
                    } else {
                        extraImages[n] = this.wallimages[i];
                        n++;
                    }
                }
            }
            Image imageToStore;
            if (n == 0) imageToStore = Mists.graphLibrary.getImage("blank");
            else { 
                Image[] trimmedExtraImages = new Image[n];
                System.arraycopy(extraImages, 0, trimmedExtraImages, 0, n);
                if (trimmedExtraImages.length == 1) imageToStore = trimmedExtraImages[0];
                else imageToStore =  Toolkit.mergeImage(false, trimmedExtraImages);
            }
            GENERATED_COMPOSITE_IMAGES.put(wallImagesKey, imageToStore);
            //Mists.logger.info(wallImagesKey+" key placed in GENERATED_COMPOSITE_IMAGES");
        } 
        return GENERATED_COMPOSITE_IMAGES.get(wallImagesKey);
    }
    
    public void setWallImages(Image[] wallimages) {
        this.wallimages = wallimages;
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
    
    
    public void generateWallImages(ImageView wallparts) {
        this.wallimages = generateNewWallImageArray(wallparts);
    }
    
    private Image[] generateNewWallImageArray(ImageView wallparts) {
        Mists.logger.info("Generating wallimages from wallparts");
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        wallparts.setViewport(new Rectangle2D(0,0,graphics.getWidth(),graphics.getHeight()));            
        WritableImage downWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(Mists.TILESIZE,0,graphics.getWidth(),graphics.getHeight()));
        WritableImage upWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(Mists.TILESIZE*2,0,graphics.getWidth(),graphics.getHeight()));
        WritableImage rightWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(Mists.TILESIZE*3,0,graphics.getWidth(),graphics.getHeight()));
        WritableImage leftWall = wallparts.snapshot(parameters, snapshot);
        //Diagonal
        wallparts.setViewport(new Rectangle2D(0,Mists.TILESIZE,graphics.getWidth(),graphics.getHeight()));
        WritableImage upleftWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(Mists.TILESIZE,Mists.TILESIZE,graphics.getWidth(),graphics.getHeight()));
        WritableImage downleftWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(Mists.TILESIZE*2,Mists.TILESIZE,graphics.getWidth(),graphics.getHeight()));
        WritableImage downrightWall = wallparts.snapshot(parameters, snapshot);
        wallparts.setViewport(new Rectangle2D(Mists.TILESIZE*3,Mists.TILESIZE,graphics.getWidth(),graphics.getHeight()));
        WritableImage uprightWall = wallparts.snapshot(parameters, snapshot);
        wallimages =  new Image[]{upleftWall, upWall, uprightWall, leftWall, rightWall, downleftWall, downWall, downrightWall};
        //Cardinal
        
        Mists.logger.info("Done with wallimages");
        return wallimages;
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
    public Wall createFromTemplate() {
        if (this.wallimages == null) this.generateWallImages(this.wallparts);
        Wall newWall = new Wall(this.name, this.getSprite().getImage(), this.getCollisionLevel(), this.wallimages);
        newWall.setTemplateID(this.templateID);
        newWall.setTopWallAdjust(topWallAdjustX, topWallAdjustY);
        return newWall;
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
			if (dummy instanceof Wall) {
				this.wallparts = ((Wall)dummy).wallparts;
				this.wallimages = ((Wall)dummy).wallimages;
			}
		} else this.graphics = new Sprite();
		this.setPosition(xCoor, yCoor);
	}
	
}
