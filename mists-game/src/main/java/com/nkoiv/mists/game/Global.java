/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game;

/**
 * The Global interface works as a dummy global config -file
 * TODO: load this from some external file
 * @author nkoiv
 */
public interface Global {
    
    //TileSize
    public static final int TILESIZE = 32;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    //TILES_DRAWN is obsolete and should not be used.
    //It should be derived dynamically from Mists.WIDTH / Mists.HEIGHT
    //public static final int TILES_DRAWN_X = (WIDTH/TILESIZE)+1; 
    //public static final int TILES_DRAWN_Y = (HEIGHT/TILESIZE)+1;
    
    //FPS limit
    public static final float MAXIMUM_STEP = 0.5f;
	
    //Config mode
    public static boolean debug = false;
    public static boolean DRAW_COLLISIONS = false;
    public static boolean DRAW_GRID = false;
    
    public static String serverAddress = "localhost";
    
}