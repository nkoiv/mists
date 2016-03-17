/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.mapgen;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PuzzleTile;
import com.nkoiv.mists.game.gameobject.PuzzleTile.PuzzleTrigger;
import com.nkoiv.mists.game.gameobject.TriggerPlate;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.Arrays;
import java.util.Stack;

/**
 * Generate LightsOut style puzzles on set areas
 * @author nikok
 */
public abstract class LightsOutPuzzle {
    private static int defaultTriggerPlateCooldown = 500;
    /**
     * Generate a LightsOutPuzzle
     * @param tileToClone The template used for the tile (graphics etc)
     * @param rowsize width of the puzzle, in tiles
     * @param margin coordinates margin between tiles
     * @param xPosition xPosition of the top left tile
     * @param yPosition yPosition of the top left tile
     * @return An array of MapObjects for the puzzle, first half containing the tiles, second half the triggers
     */
    public static MapObject[] generateLightsOutPuzzle(PuzzleTile tileToClone, int rowsize, double margin, double xPosition, double yPosition) {
        Mists.logger.info("Generating a new Lights Out puzzle");
        MapObject[] ret = new MapObject[(int)(Math.pow(rowsize, 2))*2];
        PuzzleTile[] tiles = generatePuzzleTiles(tileToClone, rowsize);
        for (int i = 0; i < tiles.length; i++) {
            tiles[i].setPosition((i%rowsize)*(tileToClone.getWidth()), (i/rowsize)*tileToClone.getHeight());
            if ((i%rowsize)>0) tiles[i].setPosition(tiles[i].getXPos()+margin*(i%rowsize), tiles[i].getYPos());
            if ((i/rowsize)>0) tiles[i].setPosition(tiles[i].getXPos(), tiles[i].getYPos()+margin*(i/rowsize));
            tiles[i].setPosition(tiles[i].getXPos()+xPosition, tiles[i].getYPos()+yPosition);
        }
        for (int i = 0; i < tiles.length; i++) {
            ret[i] = tiles[i];
        }
        TriggerPlate[] triggers = generateTriggerPlates(tiles, tileToClone.getWidth(), tileToClone.getHeight());
        for (int i = 0; i < triggers.length; i++) {
            triggers[i].setPosition((i%rowsize)*tileToClone.getWidth(), (i/rowsize)*tileToClone.getHeight());
            if ((i%rowsize)>0) triggers[i].setPosition(triggers[i].getXPos()+margin*(i%rowsize), triggers[i].getYPos());
            if ((i/rowsize)>0) triggers[i].setPosition(triggers[i].getXPos(), triggers[i].getYPos()+margin*(i/rowsize));
            triggers[i].setPosition(triggers[i].getXPos()+xPosition, triggers[i].getYPos()+yPosition);
        }
        for (int i = tiles.length; i < tiles.length*2; i++) {
            ret[i] = triggers[i-tiles.length];
        }
        /*
        Mists.logger.info("Lights out Puzzle generated, tiles:");
        Mists.logger.info(Arrays.toString(ret));
        */
        return ret;
    }
    
    /**
     * Generated array of puzzletiles is formed from
     * left to right, from top to bottom
     * [1][2][3]
     * [4][5][6]
     * [7][8][9]
     * @param tileToClone Tile to clone into the puzzle
     * @param rowlength length of a single row (and height of puzzle) in tiles 
     * @return 
     */
    private static PuzzleTile[] generatePuzzleTiles(PuzzleTile tileToClone, int rowlength) {
        int tileCount = (int)Math.pow(rowlength, 2);
        PuzzleTile[] tiles = new PuzzleTile[tileCount];
        for (int i = 0; i < tileCount; i++) {
            tiles[i] = tileToClone.createFromTemplate();
        }
        return tiles;
    }
    
    /**
     * Generate the triggerplates on top of the tiles
     * @param tiles Tiles to layer with triggerplates
     * @param tileWidth width of a tile
     * @param tileHeight height of a tile
     * @return Array of triggerplates, one for each tile
     */
    private static TriggerPlate[] generateTriggerPlates(PuzzleTile[] tiles, double tileWidth, double tileHeight) {
        TriggerPlate[] triggers = new TriggerPlate[tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            TriggerPlate tp = generateTriggerPlate(tiles, i, tileWidth, tileHeight);
            triggers[i] = tp;
        }
        return triggers;
    }
    
    /**
     * Generate a single triggerplate on top of a given tile
     * @param tiles Tiles in the puzzle
     * @param tile Number of the tile to build trigger on
     * @param tileWidth width of a tile
     * @param tileHeight height of a tile
     * @return 
     */
    private static TriggerPlate generateTriggerPlate(PuzzleTile[] tiles, int tile, double tileWidth, double tileHeight) {
        int rowlength = (int)(Math.sqrt(tiles.length));
        int xPos = tile%rowlength;
        int yPos = tile/rowlength;
        TriggerPlate tp = new TriggerPlate("PuzzleTrigger", tileWidth, tileHeight, defaultTriggerPlateCooldown);
        tp.setRequireReEntry(true);
        int triggertileID;
        Stack<Integer> neighbours = new Stack<>();
        //UpLeft
        triggertileID = getTilePosition(rowlength, xPos-1, yPos-1);
        if (triggertileID >= 0) neighbours.push(triggertileID);
        //Up
        triggertileID = getTilePosition(rowlength, xPos, yPos-1);
        if (triggertileID >= 0) neighbours.push(triggertileID);
        //UpRight
        triggertileID = getTilePosition(rowlength, xPos+1, yPos-1);
        if (triggertileID >= 0) neighbours.push(triggertileID);
        //Left
        triggertileID = getTilePosition(rowlength, xPos-1, yPos);
        if (triggertileID >= 0) neighbours.push(triggertileID);
        //Center
        triggertileID = getTilePosition(rowlength, xPos, yPos);
        if (triggertileID >= 0) neighbours.push(triggertileID);
        //Right
        triggertileID = getTilePosition(rowlength, xPos+1, yPos);
        if (triggertileID >= 0) neighbours.push(triggertileID);
        //DownLeft
        triggertileID = getTilePosition(rowlength, xPos-1, yPos+1);
        if (triggertileID >= 0) neighbours.push(triggertileID);
        //Down
        triggertileID = getTilePosition(rowlength, xPos, yPos+1);
        if (triggertileID >= 0) neighbours.push(triggertileID);
        //DownRight
        triggertileID = getTilePosition(rowlength, xPos+1, yPos+1);
        if (triggertileID >= 0) neighbours.push(triggertileID);
        //Generate PuzzleTriggers
        while (!neighbours.isEmpty()) {
            triggertileID = neighbours.pop();
            PuzzleTrigger pt = tiles[tile].new PuzzleTrigger(tiles[triggertileID]);
            tp.addTouchTrigger(pt);
        }
        
        return tp;
    }
    
    /**
     * Get the position of a tile within the tilearray
     * @param rowlength length of a row in the tilearray
     * @param xPos xPosition of the tile
     * @param yPos yPosition of the tile
     * @return -1 if tile is outside array, otherwise return the position.
     */
    private static int getTilePosition(int rowlength, int xPos, int yPos) {
        if (xPos < 0 || xPos > rowlength-1 || yPos < 0 || yPos > rowlength-1) return -1;
        else return ((rowlength * yPos)+xPos);
    }
}
