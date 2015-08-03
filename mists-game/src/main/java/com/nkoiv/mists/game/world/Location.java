/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

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
    * Constructor for demofield
    * TODO: load this from some XML or somesuch
    */
    
    
    public Location(String name) {
        this.mapObjects = new ArrayList<>();
    }
    
    public Location() {
        this.name = "POCmap";
        this.mapObjects = new ArrayList<>();
        this.map = new BGMap(new Image("/images/pocmap.png"));
        
        PlayerCharacter himmu = new PlayerCharacter();
        himmu.getSprite().setPosition(300, 200);
        himmu.getSprite().setCollisionAreaShape(2);
        himmu.setLocation(this);
        this.setPlayer(himmu);
        this.mapObjects.add(himmu);
        this.screenFocus = himmu;
        
        Structure rock = new Structure("Rock", new Image("/images/block.png"), this, 450, 350);
        this.mapObjects.add(rock);
        
        Structure tree1 = new Structure("Tree", new Image("/images/tree_stump.png"), this, 400, 240);
        tree1.addExtra(new Image("/images/tree.png"), -35, -96);
        this.mapObjects.add(tree1);
        
        Structure tree2 = new Structure("Tree", new Image("/images/tree_stump.png"), this, 230, 340);
        tree2.addExtra(new Image("/images/tree.png"), -35, -96);
        this.mapObjects.add(tree2);
        
    }
    
    public void addStructure(Structure s, int xPos, int yPos) {
        this.mapObjects.add(s);
        s.setLocation(this);
        s.getSprite().setPosition(xPos, yPos);
    }
    
    public void addCreature(Creature c) {
        this.mapObjects.add(c);
        c.setLocation(this);
    }
    
    public void addPlayerCharacter(PlayerCharacter p) {
        this.mapObjects.add(p);
        p.setLocation(this);
        
    }
    
    public void setMap(Map m) {
        this.map = m;
    }
    
    public void removeMapObject (MapObject o) {
        this.mapObjects.remove(o);
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
    
    public void setScreenFocus(MapObject focus) {
        this.screenFocus = focus;
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
        
        this.map.render(-xOffset, -yOffset, gc); //First we draw the underlying map
        /*
        * TODO: Consider rendering mobs in order so that those closer to bottom of the screen overlap those higher up.
        */
        if (!this.mapObjects.isEmpty()) {
            for (MapObject mob : this.mapObjects) {
                mob.render(xOffset, yOffset, gc); //Draw objects on the ground
                if (DRAW_COLLISIONS) { // Draw collision boxes for debugging purposes, if the Global variable is set
                    gc.setStroke(Color.RED);
                    if (mob.getSprite().getCollisionAreaType() == 1) {
                        gc.strokeRect(mob.getSprite().getXPos()-xOffset, mob.getSprite().getYPos()-yOffset,
                        mob.getSprite().getWidth(), mob.getSprite().getHeight());
                    } else if (mob.getSprite().getCollisionAreaType() == 2) {
                        gc.strokeOval(mob.getSprite().getXPos()-xOffset, mob.getSprite().getYPos()-yOffset,
                        mob.getSprite().getWidth(), mob.getSprite().getHeight());
                    }
                    
                }
                
            }
        }
        
        if (!this.mapObjects.isEmpty()) {
            for (MapObject struct : this.mapObjects) {
                if (struct instanceof Structure) {
                    //struct.renderExtras(xOffset, yOffset, gc); //Draw extra frill (leaves on trees etc)
                }
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
