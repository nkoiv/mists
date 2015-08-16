/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.gameobject.Structure;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.scene.canvas.GraphicsContext;

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
    
    public TileMap (String filename) {
        this.loadMap(filename);
        this.tilesize = Global.TILESIZE;
        this.tileMap = new Tile[tileWidth][tileHeight];
    }
    
    @Override
    public void render(double xOffset, double yOffset, GraphicsContext gc) {
        /*Render all the tiles
        * TODO: Only the tiles on the visible area
        */
        for (int row=0;row<this.tileHeight;row++) {
            for (int column=0;column<this.tileWidth;column++) {
                if (this.tileMap[column][row]!=null)
                    this.tileMap[column][row].render(xOffset, yOffset, gc);
            }
        }
        
        
    }

    @Override
    public ArrayList<Structure> getStaticStructures() {
        //Generate structures and return them
        ArrayList<Structure> staticStructures = new ArrayList<>();
        return staticStructures;
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
