/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

/**
 * The Global interface works as a dummy global config -file
 * TODO: load this from some external file
 * @author nkoiv
 */
public interface Global {
    
    //TileSize
    public static final int SCALE = 1;
    public static final int TILESIZE = 32;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int TILES_DRAWN_X = (WIDTH/TILESIZE)+1;
    public static final int TILES_DRAWN_Y = (HEIGHT/TILESIZE)+1;
    
    //FPS limit
    public static final float MAXIMUM_STEP = 0.5f;
	
    //Config mode
    public static final boolean debug = true;
    public static final boolean DRAW_COLLISIONS = true;
    public static final boolean DRAW_GRID = true;
    
}