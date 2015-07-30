/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

/**
 * The Global interface works as a dummy global config -file
 * @author nkoiv
 */
public interface Global {
    
    //Screen and tile size
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    public static final int SCALE = 1;
    public static final int TILESIZE = 32;
    public static final int TILES_DRAWN_X = WIDTH/TILESIZE;
    public static final int TILES_DRAWN_Y = HEIGHT/TILESIZE;
    //public static final float MAXIMUM_STEP = 0.5f;
	
    //Config mode
    public static final boolean debug = true;
    
}
