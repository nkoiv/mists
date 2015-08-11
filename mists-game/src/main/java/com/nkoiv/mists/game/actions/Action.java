/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.actions;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import java.io.Serializable;
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
    
    private static Map<Integer, Action> allById;
    
    public Action(String name) {
        this(name, nextId++);
    }
    
    public Action(String name, int id) {
        this.name = name;
        this.id = id;
        getAllById().put(id, this);
    }
    
    private Map<Integer, Action> getAllById() {
        if(allById == null) {
            allById = new HashMap<>();
        }
        return allById;
    }
    
    public void use(Creature actor) {
        //Override this to do things
        Mists.logger.log(Level.INFO, "{0} used by {1}", new Object[]{this.toString(), actor.getName()});
    }

    @Override
    public String toString() {
        return name;
    }
    
}
