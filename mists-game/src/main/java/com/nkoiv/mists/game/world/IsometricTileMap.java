/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.Sprite;

/**
 *
 * @author nikok
 */
public class IsometricTileMap extends TileMap {
    
    
     public IsometricTileMap (int tileWidth, int tileHeight, int tilesize, int[][] intMap) {
         super(tileWidth, tileHeight, tilesize, intMap);
     }
    
    /*use the intMap to generate the tiles
    * since isometric map is drawn in 45deg angle, every second row is offsetted by
    * half the tile width. On the other hand, moving down a row only moves down the
    * Y by half a tile width.
    * (x*this.tilesize)+(x%2)*this.tilesize/2, (y*this.tilesize/2))
    */
    @Override
    protected void generateTilesFromIntMap(int[][] intMap) {
        Mists.logger.info("Generating tiles");
        Mists.logger.info("IntMap: "+intMap.length+"x"+intMap[0].length);
        Mists.logger.info("TileMap: "+this.tileMap.length+"x"+this.tileMap[0].length);
        for (int x=0; x<this.tileWidth; x++) {
            for (int y=0; y<this.tileHeight; y++) {
               //TODO: Check the intMap value against tilesheet
               //For now, everything is floor
               double xCoor = x*Mists.graphLibrary.getImage("isoDungeonFloor").getWidth();
               if (y%2 == 1) xCoor+=Mists.graphLibrary.getImage("isoDungeonFloor").getWidth()/2;
               double yCoor = y*this.tilesize/2;
               if (intMap[x][y]==1) {
                   this.tileMap[x][y] = new Tile(1, "Floor", this.tilesize, 
                    new Sprite(Mists.graphLibrary.getImage("isoDungeonFloor"),
                    xCoor, yCoor)); 
               } else {
                   this.tileMap[x][y] = new Tile(0, "DarkFloor", this.tilesize, 
                    new Sprite(Mists.graphLibrary.getImage("isoDungeonDarkFloor"),
                    xCoor, yCoor)); 
               }
               
            }
        }    
    }

}
