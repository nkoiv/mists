/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.libraries.LibLoader;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class TileMap implements GameMap, KryoSerializable {

   
    protected int tilesize;
    protected int tileWidth;
    protected int tileHeight;
    
    protected int[][] intMap; //Contains both floor and structures
    protected int[][] floorMap; //Contains only floor, used if available
    protected int[][] structureMap; //Contains only structures, used if available
    
    protected Tile[][] tileMap;
    protected HashMap<Integer, Structure> structureCodes;
    protected HashMap<Integer, Image> floorCodes;
    
    protected static final int CLEAR = 0;
    protected static final int FLOOR = 46;
    protected static final int WALL = 35;
    protected static final int DOOR = 43;
    
    public TileMap() {
        
    }
    
    /**
     * Build the map from intmap (tile codes)
     * and initialize the tiles within for graphics usage.
     */
    public void buildMap() {
        if (this.intMap == null || (this.floorMap == null && this.structureMap == null)) return;
        Mists.logger.log(Level.INFO, "Got a {0}x{1} intMap for mapgeneration. Generating tiles...", new Object[]{intMap.length, intMap[0].length});
        this.tileMap = new Tile[tileWidth][tileHeight];
        initializeTileGraphics();
        Mists.logger.info("Tile graphics initialized");
        if (this.floorMap == null) this.generateTilesFromIntMap(this.floorMap);
        else this.generateTilesFromIntMap(this.intMap);
        Mists.logger.info("Tiles generated");
    }
    
    public TileMap (String mapFileName) {
        this.tilesize = Global.TILESIZE;
        this.loadMap(mapFileName); //Generate intmap from the mapfile
        this.tileMap = new Tile[tileWidth][tileHeight];
        this.generateTilesFromIntMap(this.intMap); //turn the intmap into a tilemap
    }
    
    public TileMap (int tileWidth, int tileHeight, int tilesize, int[][] intMap) {
        this.tilesize = tilesize;
        this.intMap = intMap;
        this.tileWidth=intMap.length;
        this.tileHeight=intMap[0].length;
        this.tileMap = new Tile[tileWidth][tileHeight];
        Mists.logger.log(Level.INFO, "Got a {0}x{1} intMap for mapgeneration. Generating tiles...", new Object[]{intMap.length, intMap[0].length});
        initializeTileGraphics();
        Mists.logger.info("Tile graphics initialized");
        this.generateTilesFromIntMap(this.intMap);
        Mists.logger.info("Tiles generated");
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        /*Render all the tiles
        * 
        */
        double screenWidth = gc.getCanvas().getWidth();
        double screenHeight = gc.getCanvas().getHeight();
        for (int row=(int)(-yOffset/this.tilesize);row<(screenHeight/this.tilesize)+(-yOffset/this.tilesize);row++) {
            for (int column=(int)(-xOffset/this.tilesize);column<(screenWidth/this.tilesize)+(-xOffset/this.tilesize);column++) {
                if (column<this.tileMap.length && row<this.tileMap[0].length && this.tileMap[column][row]!=null) {
                    this.tileMap[column][row].render(-xOffset, -yOffset, gc);
                //Print tile coordinates on top of tile
                //gc.strokeText(column+","+row, this.tileMap[column][row].getSprite().getXPos()-xOffset, this.tileMap[column][row].getSprite().getYPos()-yOffset);
                }
            }
        } 
    }
    
    private void initializeTileGraphics() {
        //TODO: Check the tilecodes to make sure we have all the images we need
        
        
    }
    
    private void loadDefaultFloorCodes() {
        try {
            this.floorCodes = LibLoader.loadLocationFloorCodes("libdata/defaultFloorCodes.yml");
        } catch (Exception e) {
            Mists.logger.warning(("Error loading floor codes!"));
            Mists.logger.warning(e.getMessage());

        }
        
        Mists.logger.info("Default floor codes loaded");
    }
    
    private void loadDefaultStructCodes() {
        try {
            this.structureCodes = LibLoader.loadLocationStructureCodes("libdata/defaultStructCodes.yml");
        } catch (Exception e) {
            Mists.logger.warning(("Error loading struct codes!"));
            Mists.logger.warning(e.getMessage());
        }
        
        Mists.logger.info("Default structure codes loaded");
    }
    
    /**
     * Supply the TileMap with pairs of character and structure,
     * used when generating actual MapObject Structures from a tilemap
     * @param structureCodes 
     */
    public void setStructureCodes(HashMap<Integer, Structure> structureCodes) {
        this.structureCodes = structureCodes;
    }
    
    /*
    * generateStructure takes a tileCode and generates the Structure from it
    * TODO: Fix the class to use a StructureSheet (repository for the structureCodes)
    * Note that generated maps should have separate arrays for structures and the tiles,
    * because structures should have "floor" under them
    */
    private Structure generateStructure(int tileCode, int xCoor, int yCoor) {
        int t = tileCode;
        if (this.structureCodes == null) this.loadDefaultStructCodes();
        if (t == CLEAR) t = WALL;
        if (xCoor < 0 || xCoor > tileWidth-1 || yCoor < 0 || yCoor > tileHeight-1) return null;
        if (this.structureCodes.keySet().contains(t)) {
            Structure s = this.structureCodes.get(t).createFromTemplate();
            s.setPosition(xCoor*this.tilesize, yCoor*this.tilesize);
            return s;
        }
        else return null;
    }

    public void clearTile(int tileX, int tileY) {
        if (tileX>=0 && tileX < this.tileWidth && tileY >=0 && tileY < this.tileHeight) {
            this.intMap[tileX][tileY] = '0';
            //TODO: Tilemap already has only Floor or Dark Floor so clearing it is pointless
        }
    }
    
    /**
     * Update structures fixes context dependant structures
     * For example walls might need to change their sprite
     * if adjacent wall is destroyed or built
     * @param tileX xCoordinate for the change in structures
     * @param tileY yCoordinate for the change in structures
     * @return boolean array of the walls surrounding the selected tile
     */
    public boolean[] getNeighbouringWalls(int tileX, int tileY) {
        //All the structures in 3x3 area (around the target) need to be checked and updated
        boolean[] neighbours = new boolean[8];
        if (tileX>0) {
            if (this.intMap[tileX-1][tileY]==WALL || this.intMap[tileX-1][tileY]==CLEAR) neighbours[3] = true; //Left
            if (tileY>0) {
                if (this.intMap[tileX-1][tileY-1]==WALL || this.intMap[tileX-1][tileY-1]==CLEAR) neighbours[0] = true; //UpLeft
            }
            if (tileY<this.tileHeight-1) {
                if (this.intMap[tileX-1][tileY+1]==WALL || this.intMap[tileX-1][tileY+1]==CLEAR) neighbours[5] = true; //DownLeft
            }
        }
        if (tileX<this.tileWidth-1) {
            if (this.intMap[tileX+1][tileY]==WALL || this.intMap[tileX+1][tileY]==CLEAR) neighbours[4] = true; //Right
            if (tileY>0) {
                if (this.intMap[tileX+1][tileY-1]==WALL || this.intMap[tileX+1][tileY-1]==CLEAR) neighbours[2] = true; //UpRight
            }
            if (tileY<this.tileHeight-1) {
                if (this.intMap[tileX+1][tileY+1]==WALL || this.intMap[tileX+1][tileY+1]==CLEAR) neighbours[7] = true; //DownRight
            }
        }
        if (tileY>0) {
            if (this.intMap[tileX][tileY-1]==WALL ||this.intMap[tileX][tileY-1]==CLEAR) neighbours[1] = true; //Up
        }
        if (tileY<this.tileHeight-1) {
            if (this.intMap[tileX][tileY+1]==WALL||this.intMap[tileX][tileY+1]==CLEAR) neighbours[6] = true; //Down
        }
        
        return neighbours;
    }
    
    //TODO: Structures should have their own map, and not just intMap
    @Override
    public ArrayList<Structure> getStaticStructures() {
        if (this.structureCodes == null) this.loadDefaultStructCodes();
        //Generate structures and return them
        Mists.logger.info("Generating structures");
        ArrayList<Structure> staticStructures = new ArrayList<>();
        for (int x=0; x<this.tileWidth; x++) {
            for (int y=0; y<this.tileHeight; y++) {
                //TODO: Check the intMap value against tilesheet
                //For now, everything is floor
                Structure newStructure;
                if (this.structureMap == null) newStructure = this.generateStructure(this.intMap[x][y], x, y);
                else newStructure = this.generateStructure(this.structureMap[x][y], x, y);
                if(newStructure != null)staticStructures.add(newStructure); 
            }
        }
        //update walls
        Mists.logger.info("Updating walls");
        for (Structure s : staticStructures) {
            if (s instanceof Wall) {
                boolean[] wallneighbours = getNeighbouringWalls((int)s.getCenterXPos()/this.tilesize, (int)s.getCenterYPos()/this.tilesize);
                Wall w = (Wall)s;
                w.setNeighbours(wallneighbours);
            }
        }
        for (Structure s: staticStructures) {
            if (s instanceof Wall) {
                ((Wall)s).updateGraphicsBasedOnNeighbours();
            }
        }
        
        Mists.logger.info("Done generating static structures");
        return staticStructures;
    }

    public int getTileSize() {
        return this.tilesize;
    }
    
    @Override
    public double getWidth() {
        return (this.tileWidth * this.tilesize);
    }

    @Override
    public double getHeight() {
        return (this.tileHeight * this.tilesize);
    }
    
    //Print the intmap for testing purposes
    public void printIntMapToConsole(){
        String row;
        for (int x=0; x<this.tileWidth; x++) {
            row = "";
            for (int y=0; y<this.tileHeight; y++) {
                    row = row + this.intMap[x][y]; 

            }
            System.out.println(row);
        }	
    }
    
    public void setIntMap(int[][] intMap) {
        this.intMap = intMap;
    }
    
    public void setTileMap(Tile[][] tileMap) {
    	this.tileMap = tileMap;
    }
    
    
    //use the intMap to generate the tiles
    protected void generateTilesFromIntMap(int[][] intMap) {
        if (this.floorCodes == null) this.loadDefaultFloorCodes();
        Mists.logger.info("Generating tiles");
        Mists.logger.info("IntMap: "+this.intMap.length+"x"+this.intMap[0].length);
        Mists.logger.info("TileMap: "+this.tileMap.length+"x"+this.tileMap[0].length);
        for (int x=0; x<this.tileWidth; x++) {
            for (int y=0; y<this.tileHeight; y++) {
                if (floorCodes.containsKey(intMap[x][y])) {
                    this.tileMap[x][y] = new Tile(intMap[x][y], "Floor", this.tilesize, 
                    new Sprite(floorCodes.get(intMap[x][y]),x*this.tilesize, y*this.tilesize));
                } else {
                    int avgTileCode = getFloorTileAverage(x, y, intMap);
                    if (floorCodes.containsKey(avgTileCode)) {
                        this.tileMap[x][y] = new Tile(avgTileCode, "Floor", this.tilesize, 
                            new Sprite(floorCodes.get(avgTileCode),x*this.tilesize, y*this.tilesize));
                    } else {
                        Mists.logger.log(Level.WARNING, "Was unable to generate floortile with the (average)code of {0}", avgTileCode);
                        this.tileMap[x][y] = new Tile(0, "Floor", this.tilesize, 
                            new Sprite(Mists.graphLibrary.getImage("floorDungeonLight"),x*this.tilesize, y*this.tilesize));
                    }
                    /*
                    this.tileMap[x][y] = new Tile(1, "Floor", this.tilesize, 
                    new Sprite(Mists.graphLibrary.getImage("floorDungeonLight"),
                    x*this.tilesize, y*this.tilesize)); 
                    */
                }
            }
        }    
    }
    
    
    /**
     * Check the area around the given maptile, and figure out
     * what's the most common floortile. This is utilized to
     * fill floor in unclear spots, so that the floor fits with
     * surroundings
     * @param x Tile X coordinate
     * @param y Tile y coordinate
     * @param intMap Tilemap to check
     * @return floorcode from the tilemap
     */
    private int getFloorTileAverage(int x, int y, int[][] intMap) {
        int ret = 0;
        TreeMap<Integer, Integer> counts = new TreeMap<>();
        //Row above the spot
        if (y > 0) {
            if (x>0) incrementTileCount(intMap[x-1][y-1], counts);
            incrementTileCount(intMap[x][y-1], counts);
            if (x<intMap.length-1) incrementTileCount(intMap[x+1][y-1], counts);
        }
        //Row at the spot
            if (x>0) incrementTileCount(intMap[x-1][y], counts);
            incrementTileCount(intMap[x][y], counts);
            if (x<intMap.length-1) incrementTileCount(intMap[x+1][y], counts);
        //Row below the spot
        if (y < intMap[0].length-1) {
            if (x>0) incrementTileCount(intMap[x-1][y+1], counts);
            incrementTileCount(intMap[x][y+1], counts);
            if (x<intMap.length-1) incrementTileCount(intMap[x+1][y+1], counts);
        }
        
        //Get the best match from floorCodes
        while (!counts.isEmpty()) {
            int value = counts.lastKey();
            if (floorCodes.containsKey(value)) {
                ret = value;
                break;
            } else {
                counts.remove(counts.lastKey());
            }
        }
        
        return ret;
    }
    
    private void incrementTileCount(int value, TreeMap<Integer, Integer> countMap) {
        if (countMap.containsKey(value)) countMap.put(value, countMap.get(value)+1);
        else countMap.put(value, 1);
    }
    
    /**
    * Maploader from files mainly for testing purposes
    * TODO: Make proper save/load for maps
    * @param filename path to the mapfile
    */
    private void loadMap(String filename) {
        
        try (Scanner scanner = new Scanner (Game.class.getResourceAsStream(filename))) {
            // First two lines are the width and height of the map
            String line = scanner.nextLine();
            this.tileWidth = Integer.parseInt(line);
            line = scanner.nextLine();
            this.tileHeight = Integer.parseInt(line);
            Mists.logger.info("Parsing map: "+this.tileWidth+"x"+this.tileHeight);
            // load map data
            intMap = new int[(int)this.tileWidth][(int)this.tileHeight];
            for (int y=0; y<this.tileHeight; y++) {
                line = scanner.nextLine();
                String lines = line.toString();
                for (int x=0; x<this.tileWidth; x++) {
                    int tilecode = lines.charAt(x);
                    if (tilecode == 32) tilecode = 0; //Hardcode empty (space) to be 0
                    this.intMap[x][y] = tilecode;
                }
            }
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
        Mists.logger.info("loadMap complete on "+filename);
        this.printIntMapToConsole();
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeInt(this.tilesize);
        output.writeInt(this.tileWidth);
        output.writeInt(this.tileHeight);
        for (int[] intColumn : this.intMap) {
            output.writeInts(intColumn);
        }
        //kryo.writeClassAndObject(output, this.structureCodes);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        this.tilesize = input.readInt();
        this.tileWidth = input.readInt();
        this.tileHeight = input.readInt();
        this.intMap = new int[tileWidth][tileHeight];
        for (int i = 0; i < this.tileWidth; i++) {
            this.intMap[i] = input.readInts(tileHeight);
        }
        this.loadDefaultStructCodes();
        this.loadDefaultFloorCodes();
        //this.structureCodes = (HashMap<Integer, Structure>)kryo.readClassAndObject(input);
    }

}