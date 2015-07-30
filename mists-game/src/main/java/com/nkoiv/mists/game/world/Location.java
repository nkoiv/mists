/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.sprites.Sprite;
import java.util.ArrayList;
import java.util.Iterator;
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
    private String name;
    private Map map;
    
    private MapObject screenFocus;
    private PlayerCharacter player;
    
    /*
    * Constructer for demofield
    */
    public Location() {
        this.name = "POCmap";
        this.mapObjects = new ArrayList<>();
        this.map = new BGMap(new Image("/images/pocmap.png"));
        
        PlayerCharacter himmu = new PlayerCharacter("Himmu", new Image("/images/himmu.png"));
        himmu.getSprite().setPosition(300, 200);
        himmu.setLocation(this);
        this.setPlayer(himmu);
        this.mapObjects.add(himmu);
        this.screenFocus = himmu;
        
        Structure block = new Structure("CollisionTest", new Image("/images/block.png"), this, 450, 350);
        this.mapObjects.add(block);
    }
    
    public void addStructure(String name, Image image, int xPos, int yPos) {
        MapObject newMob = new MapObject(name, image);
        newMob.setLocation(this);
        newMob.getSprite().setPosition(xPos, yPos);
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
        double xOffset = xPos - (WIDTH) / 2;
        //Prevent leaving the screen
        if (xOffset < 0) {
            xOffset = 0;
        } else if (xOffset > map.getWidth() -(WIDTH)) {
            xOffset = map.getWidth() - (WIDTH);
        }
        
        return xOffset;
	}
	
    public double getyOffset(double yPos){
	//Calculate Offset to ensure Player is centered on the screen
        double yOffset = yPos - (HEIGHT) / 2;
        //Prevent leaving the screen
        if (yOffset < 0) {
            yOffset = 0;
        } else if (yOffset > map.getHeight() -(HEIGHT)) {
            yOffset = map.getHeight() - (HEIGHT);
        }
        
        return yOffset;
	}
    
    public void update (double time) {
        /*
        * Update is the main "tick" of the Location.
        * Movement, combat and triggers should all be handled here
        */
        if (!this.mapObjects.isEmpty()) {
            for (MapObject mob : this.mapObjects) {
                mob.update(time);
            }    
        }
    }
    
    public MapObject checkCollisions (MapObject o) {
        /*
        * TODO: Maybe only check collisions from nearby objects?
        */
        Iterator<MapObject> mapObjectsIter = mapObjects.iterator();
        while ( mapObjectsIter.hasNext() )
        {
            MapObject collidingObject = mapObjectsIter.next();
            if (!collidingObject.equals(o)) { // Colliding with yourself is not really a collision
                if ( o.instersects(collidingObject) ) 
                 {
                    return collidingObject;
                }
            }
        }
        return null;
    }
    
    public void render (GraphicsContext gc) {
        
        /*
        * Update Offsets first to know which parts of the location are drawn
        */
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
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        String s;
        s = this.name+", a "+this.map.getWidth()+"x"+this.map.getHeight()+" area with "+this.mapObjects.size()+" mobs";
        return s;
    }
    
}
