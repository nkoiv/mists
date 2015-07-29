/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nkoiv
 */
public class Location implements Global {
    
    /*
    * TODO: Lists for various types of MapObjects, from creatures to frills.
    */
    private final List<MapObject> mapObjects;
    
    private Map map;
    
    private MapObject screenFocus;
    private PlayerCharacter player;
    
    /*
    * Constructer for demofield
    */
    public Location() {
        this.mapObjects = new ArrayList<>();
        this.map = new BGMap(new Image("/images/pocmap.png"));
        
        PlayerCharacter himmu;
        himmu = new PlayerCharacter("Himmu", new Image("/images/himmu.png"));
        himmu.getSprite().setPosition(300, 200);
        this.setPlayer(himmu);
        this.mapObjects.add(himmu);
        this.screenFocus = himmu;
        
  
    }
    
    public void setMap(Map m) {
        this.map = m;
    }
    
    public Map getMap() {
        return this.map;
    }
    
    public void setPlayer(PlayerCharacter p) {
        this.player = p;
    }
    
    public PlayerCharacter getPlayer() {
        return this.player;
    }
    
    public double getxOffset(double xPos){
	//Calculate Offset to ensure Player is centered on the screen
        double xOffset = xPos - (TILES_DRAWN_X*TILESIZE) / 2;
        //Prevent leaving the screen
        if (xOffset < 0) {
            xOffset = 0;
        } else if (xOffset > map.getWidth() -(TILES_DRAWN_X*TILESIZE)) {
            xOffset = map.getWidth() - (TILES_DRAWN_X*TILESIZE);
        }
        
        return xOffset;
	}
	
    public double getyOffset(double yPos){
	//Calculate Offset to ensure Player is centered on the screen
        double yOffset = yPos - (TILES_DRAWN_Y*TILESIZE) / 2;
        //Prevent leaving the screen
        if (yOffset < 0) {
            yOffset = 0;
        } else if (yOffset > map.getHeight() -(TILES_DRAWN_Y*TILESIZE)) {
            yOffset = map.getHeight() - (TILES_DRAWN_Y*TILESIZE);
        }
        
        return yOffset;
	}
    
    public void update (double time) {
        if (!this.mapObjects.isEmpty()) {
            for (MapObject mob : this.mapObjects) {
                mob.getSprite().update(time);
            }    
        }
    }
    
    public void render (GraphicsContext gc) {
        
        double xOffset = getxOffset(screenFocus.getSprite().getXPos());
        double yOffset = getyOffset(screenFocus.getSprite().getYPos());
        
        this.map.render(-xOffset, -yOffset, gc);
        /*
        * TODO: Consider rendering mobs in order so that those closer to bottom of the screen overlap those higher up.
        */
        if (!this.mapObjects.isEmpty()) {
            for (MapObject mob : this.mapObjects) {
                mob.render(xOffset, yOffset, gc);
            }    
        }
        
        
    }
    
}
