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
import com.nkoiv.mists.game.gameobject.Door;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class TileMap implements GameMap, KryoSerializable {

   
    private int tilesize;
    private int tileWidth;
    private int tileHeight;
    
    private int[][] intMap;
    
    private Tile[][] tileMap;
    private HashMap<Integer, String> tilecodes;
    
    private static final int CLEAR = 0;
    private static final int FLOOR = 1;
    private static final int WALL = 2;
    private static final int DOOR = 4;
    
    public TileMap() {
        
    }
    
    public void buildMap() {
        if (this.intMap == null) return;
        Mists.logger.log(Level.INFO, "Got a {0}x{1} intMap for mapgeneration. Generating tiles...", new Object[]{intMap.length, intMap[0].length});
        this.tileMap = new Tile[tileWidth][tileHeight];
        initializeTileGraphics();
        Mists.logger.info("Tile graphics initialized");
        this.generateTilesFromIntMap();
        Mists.logger.info("Tiles generated");
    }
    
    public TileMap (String filename) {
        this.tilesize = Global.TILESIZE;
        this.loadMap(filename);
        this.tileMap = new Tile[tileWidth][tileHeight];
        this.generateTilesFromIntMap();
    }
    
    public TileMap (int tileWidth, int tileHeight, int[][] intMap) {
        this.tilesize = Global.TILESIZE;
        this.intMap = intMap;
        this.tileWidth=intMap.length;
        this.tileHeight=intMap[0].length;
        this.tileMap = new Tile[tileWidth][tileHeight];
        Mists.logger.log(Level.INFO, "Got a {0}x{1} intMap for mapgeneration. Generating tiles...", new Object[]{intMap.length, intMap[0].length});
        initializeTileGraphics();
        Mists.logger.info("Tile graphics initialized");
        this.generateTilesFromIntMap();
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
                if (column<this.tileMap.length && row<this.tileMap[0].length) {
                if (this.tileMap[column][row]!=null)
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
    
    /*
    * generateStructure takes a tileCode and generates the Structure from it
    * TODO: Fix the class to use a StructureSheet (repository for the structureCodes)
    * Note that generated maps should have separate arrays for structures and the tiles,
    * because structures should have "floor" under them
    */
    private Structure generateStructure(int tileCode, Location l, int xCoor, int yCoor) {
        if (xCoor < 0 || xCoor > tileWidth-1 || yCoor < 0 || yCoor > tileHeight-1) return null;
        //TODO Should also take in a "HashMap<Integer,String> structureSheet"
        //if (tileCode == CLEAR) return null;
        //Lets make Clear into Wall, for testing
        if (tileCode == CLEAR || tileCode == WALL) {
            Wall dungeonwall = (Wall)Mists.structureLibrary.create("dungeonwall", l, xCoor*tilesize, yCoor*tilesize);
            return dungeonwall;
        }
        if (tileCode == FLOOR) return null;
        if (tileCode == DOOR) {
            Door dungeondoor = (Door)Mists.structureLibrary.create("dungeondoor", l, xCoor*tilesize, yCoor*tilesize);
            return dungeondoor;
        }
        return null;
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
    public ArrayList<Structure> getStaticStructures(Location l) {
        //Generate structures and return them
        Mists.logger.info("Generating structures");
        ArrayList<Structure> staticStructures = new ArrayList<>();
        for (int x=0; x<this.tileWidth; x++) {
            for (int y=0; y<this.tileHeight; y++) {
                //TODO: Check the intMap value against tilesheet
                //For now, everything is floor
                Structure newStructure = this.generateStructure(this.intMap[x][y], l, x, y);
                if(newStructure != null)staticStructures.add(newStructure); 
            }
        }
        //update walls
        Mists.logger.info("Updating walls");
        for (Structure s : staticStructures) {
            if (s instanceof Wall) {
                boolean[] wallneighbours = getNeighbouringWalls((int)s.getXPos()/this.tilesize, (int)s.getYPos()/this.tilesize);
                Wall w = (Wall)s;
                w.setNeighbours(wallneighbours);
                w.updateNeighbours();
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
    
    private void setIntMap(int[][] intMap) {
        this.intMap = intMap;
    }
    
    //use the intMap to generate the tiles
    private void generateTilesFromIntMap() {
        Mists.logger.info("Generating tiles");
        Mists.logger.info("IntMap: "+this.intMap.length+"x"+this.intMap[0].length);
        Mists.logger.info("TileMap: "+this.tileMap.length+"x"+this.tileMap[0].length);
        for (int x=0; x<this.tileWidth; x++) {
            for (int y=0; y<this.tileHeight; y++) {
               //TODO: Check the intMap value against tilesheet
               //For now, everything is floor
               if (this.intMap[x][y]==1) {
                   this.tileMap[x][y] = new Tile(1, "Floor", this.tilesize, 
                    new Sprite(new Image("/images/dungeonfloor.png"),
                    x*this.tilesize, y*this.tilesize)); 
               } else {
                   this.tileMap[x][y] = new Tile(0, "DarkFloor", this.tilesize, 
                    new Sprite(new Image("/images/dungeondarkfloor.png"),
                    x*this.tilesize, y*this.tilesize)); 
               }
               
            }
        }    
    }
    
    /*
    * Maploader from files mainly for testing purposes
    * TODO: Make proper save/load for maps
    */
    private void loadMap(String filename) {
        
        try (Scanner scanner = new Scanner (Game.class.getResourceAsStream(filename))) {
            // First two lines are the width and height of the map
            String line = scanner.nextLine();
            this.tileWidth = Integer.parseInt(line);
            line = scanner.nextLine();
            this.tileHeight = Integer.parseInt(line);

            // load map data
            intMap = new int[(int)this.tileWidth][(int)this.tileHeight];
            for (int y=0; y<this.tileHeight; y++) {
                line = scanner.nextLine();
                for (int x=0; x<this.tileWidth; x++) {
                    char tilecode = line.charAt(x);
                    this.intMap[x][y] = tilecode;
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeInt(this.tilesize);
        output.writeInt(this.tileWidth);
        output.writeInt(this.tileHeight);
        for (int[] intColumn : this.intMap) {
            output.writeInts(intColumn);
        }
        kryo.writeClassAndObject(output, this.tilecodes);
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
        this.tilecodes = (HashMap<Integer, String>)kryo.readClassAndObject(input);
    }

}