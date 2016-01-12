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
import com.nkoiv.mists.game.world.util.Flags;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Action is something that someone triggers on a call.
 * It might be an attack from a creature, or it might be a trap on the floor.
 * TODO: Consider if the allById map gets bloated and if it should get cleaned
 * TODO: Maybe only unique action templates should be stored in allById
 * TODO: (?) All actions are stored in the (static) allById -map
 * @author nkoiv
 */
public class Action extends Flags implements Serializable {
    private static int nextId = 0;
    protected String name;
    protected int id;
    protected MapObject owner;
    protected ArrayList<Effect> effects;
    protected HashMap<String, Integer> flags;    
    
    
    public Action(String name) {
        this(name, nextId++);
    }
    
    public Action(String name, int id) {
        this.name = name;
        this.id = id;
        this.flags = new HashMap<>();
    }
    
    /*
    private static Map<Integer, Action> getAllById() {
        if(allById == null) {
            allById = new HashMap<>();
        }
        return allById;
    }
    */
    /**
    * Actions are owned by the user
    * setOwner gives this action to the MapObject
    * @param o MapObject to own the action
    */
    public void setOwner(MapObject o) {
        this.owner = o;
    }
    
    /**
    * Actions are owned by the user
    * getOwner returns the MapObject(creature?) using the action.
    * @return The owner of the action
    */
    public MapObject getOwner() {
        if (this.owner == null) return new MapObject("MISSING OWNER ON ACTION");
        return this.owner;
    }
    
    /**
    * Use -call is used when the action lands on a target
    * This should trigger the actual effects of the action (damage, slow, teleport...)
    * @param actor The target of the action
    */
    public void use(Creature actor) {
        //Override this to do things
        Mists.logger.log(Level.INFO, "{0} used by {1}", new Object[]{this.toString(), actor.getName()});
    }
    
    public void use(Creature actor, double xCoor, double yCoor) {
        //Override this to do things
        Mists.logger.log(Level.INFO, "{0} used by {1} towards {2}x{3}", new Object[]{this.toString(), actor.getName(), (int)xCoor, (int)yCoor});
    }
    

    public void hitOn(ArrayList<MapObject> mobs) {
        
    }
    
    public String getName() {
        return this.name;
    }

    public Action createFromTemplate() {
        Action a = new Action(this.name);
        for (String flag : this.flags.keySet()) {
            a.setFlag(flag, this.flags.get(flag));
        }
        return a;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
