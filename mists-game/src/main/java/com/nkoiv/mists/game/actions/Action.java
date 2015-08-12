/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * All actions are stored in allById -map
 * @author nkoiv
 */
public class Action implements Serializable {
    private static int nextId = 0;
    private String name;
    private int id;
    private MapObject owner;
    private ArrayList<Effect> effects;
    private HashMap<String, Integer> flags;    
    
    private static Map<Integer, Action> allById;
    
    public Action(String name) {
        this(name, nextId++);
    }
    
    public Action(String name, int id) {
        this.name = name;
        this.id = id;
        this.flags = new HashMap<>();
        getAllById().put(id, this);
    }
    
    private Map<Integer, Action> getAllById() {
        if(allById == null) {
            allById = new HashMap<>();
        }
        return allById;
    }
    
    public void setOwner(MapObject o) {
        this.owner = o;
    }
    
    public MapObject getOwner() {
        return this.owner;
    }
    
    public void use(Creature actor) {
        //Override this to do things
        Mists.logger.log(Level.INFO, "{0} used by {1}", new Object[]{this.toString(), actor.getName()});
    }
    
    public void setFlag(String flag, int value) {
        if (this.flags.containsKey(flag)) {
            this.flags.replace(flag, value);
        } else {
            this.flags.put(flag, value);
        }   
    }
    
    public int getFlag(String flag) {
        if (this.flags.containsKey(flag)) {
            return this.flags.get(flag);
        } else {
            return 0;
        }
    }
      
    public boolean isFlagged (String flag) {
        if (this.flags.containsKey(flag)) {
            return this.flags.get(flag) > 0;
        } else {
            return false;
        }
    }

    public void hitOn(ArrayList<MapObject> mobs) {
        
    }
    
     @Override
    public String toString() {
        return name;
    }
    
}
