/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author nikok
 */
public class Config {
    
     //TileSize
    public int scale = 1;
    public int tilesize = 32;
    public int width = 800;
    public int height = 600;
    
    //TILES_DRAWN is obsolete and should not be used.
    //It should be derived dynamically from Mists.WIDTH / Mists.HEIGHT
    //public static final int TILES_DRAWN_X = (WIDTH/TILESIZE)+1; 
    //public static final int TILES_DRAWN_Y = (HEIGHT/TILESIZE)+1;
    
    //FPS limit
    public float maximumStep = 0.5f;
	
    //Config mode
    public boolean debug = true;
    public boolean drawCollisions = false;
    public boolean drawGrid = false;
    
    public Config() {
        
    }
    
    
    
    private void setAttribute (String attribute, String value) {
        switch (attribute) {
            case "debug": setDebugMode(value); break;
            case "drawCollisions": setDrawCollisions(value); break;
            case "drawGrid": setDrawGrid(value); break;
        }
        
        
    }
    
    private void setDebugMode(String value) {
        if (value.equals("true")) this.debug = true;
        if (value.equals("false")) this.debug = false;
    }
    
    private void setDrawCollisions(String value) {
        if (value.equals("true")) this.drawCollisions = true;
        if (value.equals("false")) this.drawCollisions = false;
    }
    
    private void setDrawGrid(String value) {
        if (value.equals("true")) this.drawGrid = true;
        if (value.equals("false")) this.drawGrid = false;
    }
    
    /**
     * Load config from external config file
     * (parse stuff)
     * @param path The location of the config file to use
     * @throws java.io.IOException
     */
    public void loadConfig(String path) throws IOException {
        try 
            (BufferedReader br = new BufferedReader(new FileReader (path))) {
            Mists.logger.log(Level.INFO, "Loading config from file: {0}", path);
            String line = br.readLine();
            String section = null;
            while(line != null) {
                if (!line.contains("/")) {
                    String[] command = line.split("=");
                    if (command.length>1) this.setAttribute(command[0], command[1]);
                }
                line = br.readLine();
            }
            Mists.logger.info("Config loaded succesfully.");
        } catch (Exception e) {
            
        }
    }
    
    @Override
    public String toString() {
        String output =
        "Mists Config:"
        +"\n ---------------"
        +"\n Tilesize: "+this.tilesize
        +"\n FPS steplimit: "+this.maximumStep
        +"\n DebugMode: "+this.debug
        +"\n DrawCollisions: "+this.drawCollisions
        +" DrawGrid"+this.drawGrid
        ;
        return output;
    }
    
}
