/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author nkoiv
 */
public class Location {
    
    /*
    * TODO: Lists for various types of MapObjects, from creatures to frills.
    */
    private List<MapObject> mapObjects;
    
    private Map map;
    
    /*
    * Constructer for demofield
    */
    public Location() {
        this.map = new BGMap(new Image("/src/main/resources/images/pocmap.png"));
  
    }
    
    public void render (GraphicsContext gc) {
        
        
        for (MapObject mob : this.mapObjects) {
            mob.render(gc);
        }
        
    }
    
}
