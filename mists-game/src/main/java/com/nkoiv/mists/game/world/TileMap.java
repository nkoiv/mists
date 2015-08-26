/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Global;
import static com.nkoiv.mists.game.Global.TILESIZE;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public class TileMap implements GameMap {

   
    int tilesize;
    int tileWidth;
    int tileHeight;
    
    private int[][] intMap;
    
    private Tile[][] tileMap;
    
    private static final int CLEAR = 0;
    private static final int FLOOR = 1;
    private static final int WALL = 2;
    private static final int DOOR = 4;
    
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
        Mists.logger.info("Got a "+intMap.length+"x"+intMap[0].length+" intMap for mapgeneration. Generating tiles...");
        this.generateTilesFromIntMap();
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
                if (this.tileMap[column][row]!=null)
                    this.tileMap[column][row].render(-xOffset, -yOffset, gc);
                //Print tile coordinates on top of tile
                //gc.strokeText(column+","+row, this.tileMap[column][row].getSprite().getXPos()-xOffset, this.tileMap[column][row].getSprite().getYPos()-yOffset);
        
            }
        }
        
        
    }
    
    /*
    * generateStructure takes a tileCode and generates the Structure from it
    * TODO: Fix the class to use a StructureSheet (repository for the structureCodes)
    * Note that generated maps should have separate arrays for structures and the tiles,
    * because structures should have "floor" under them
    */
    private Structure generateStructure(int tileCode, Location l, int xCoor, int yCoor) {
        //TODO Should also take in a "HashMap<Integer,Structure> structureSheet"
        if (tileCode == CLEAR) return null;
        if (tileCode == FLOOR) return null;
        if (tileCode == WALL) return new Structure("Wall", new Image("/images/dungeonwall.png"), l, xCoor*this.tilesize, yCoor*this.tilesize);
        if (tileCode == DOOR) return new Structure("Door", new Image("/images/dungeondoor.png"), l, xCoor*this.tilesize, yCoor*this.tilesize);
        return null;
    }

    //TODO: Structures should have their own map, and not just intMap
    @Override
    public ArrayList<Structure> getStaticStructures(Location l) {
        //Generate structures and return them
        ArrayList<Structure> staticStructures = new ArrayList<>();
        for (int x=0; x<this.tileWidth; x++) {
            for (int y=0; y<this.tileHeight; y++) {
                //TODO: Check the intMap value against tilesheet
                //For now, everything is floor
                Structure newStructure = this.generateStructure(this.intMap[x][y], l, x, y);
                if(newStructure != null)staticStructures.add(newStructure); 
            }
        }  
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
                    int tilecode = Integer.parseInt(line.charAt(x)+"");
                    this.intMap[x][y] = tilecode;
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

}