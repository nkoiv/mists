/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Global;

/**
 * 
 * @author nkoiv
 */
public class PathFinder {

    Location location;
    
    public PathFinder (Location l) {
        this.location = l;
        
        int maxCrossableTerrain = 0;
        
        //First we'll convert map to tiles, even if it's BGMap
        double mapWidth = (l.getMap().getWidth() / Global.TILESIZE);
        double mapHeight = (l.getMap().getHeight() / Global.TILESIZE);
        
        
        
        }
    
}
