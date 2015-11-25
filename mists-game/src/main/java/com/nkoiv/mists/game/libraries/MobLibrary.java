/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.Structure;
import java.io.Serializable;
import java.util.*;

/**
 * A library is a collection of map objects templates.
 * Monsters are stored in a monlibrary, structures in a struclibrary, etc
 * The concept was inspired (read: stolen) from Mikeras' Tyrant (github.com/mikera/tyrant/)
 * 
 * TODO: Saving and loading of libraries
 * 
 * @author nkoiv
 * @param <E> The type of MapObject stored in this MobLibrary
 */
public class MobLibrary <E extends MapObject> implements Serializable, Cloneable {
    
    //ArrayList for storing all the individual libraries
    //private static final ArrayList<AssetLibrary> libraries = new ArrayList<>();
    
    private final HashMap<String, E> lib;
    
    
    public MobLibrary() {
        lib = (HashMap<String, E>) new HashMap();
    }
    
    public E create(String name) {
        E template = getTemplate(name);
        return (E)template.createFromTemplate();
    }
    
    public void addTemplate(E e) {
        prepareAdd(e);
        String lowercasename = e.getName().toLowerCase();
        this.lib.put(lowercasename, e);
    }
    
    /**
     * PrepareAdd makes sure no broken stuff gets in the library
     * Also cleans up unneeded values from them. 
     * 
     * @param e 
     */
    private static void prepareAdd(MapObject e) {
        if (e instanceof Creature) {
            prepareCreature((Creature)e);
        }
        if (e instanceof Structure) {
            prepareStructure((Structure)e);
        }
        if (e instanceof Effect) {
            prepareEffect((Effect)e);
        }
        
    }
    
    private static void prepareCreature(Creature e) {
        
    }
    
    private static void prepareStructure(Structure e) {
        
    }
    
    private static void prepareEffect(Effect e) {
        
    }
    
    /**
     * Get the desired template from the library.
     * Note: This is the template, not a new object.
     * @param name Will be converted to lowercase for retrieval
     * @return Selected MapObject
     */
    public E getTemplate(String name) {
        String lowercasename = name.toLowerCase();
        return this.lib.get(lowercasename);
    }
    
    
    
}
