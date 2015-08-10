/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.io.Serializable;
import java.util.*;

/**
 * A library is a collection of map objects templates.
 * Monsters are stored in a monlibrary, structures in a struclibrary, etc
 * 
 * The concept was inspired (read: stolen) from Mikeras' Tyrant (github.com/mikera/tyrant/)
 * 
 * TODO: Saving and loading of libraries
 * 
 * @author nkoiv
 */
public class Library extends Object implements Serializable, Cloneable {
/*    
    // all library objects, indexed by Name
    private HashMap<String, Object> library = new HashMap<>();
    private HashMap<String, String> lowerCaseNames=new HashMap<>();
	
    private List<MapObject> all = new ArrayList<>();
    private transient Map<String, Map<Integer, List<MapObject>>> types;
    private static Library instance;
    private int createCount=0;
    
    public Library() {
        clearTypes();
    }
    
    
    public void clearTypes() {
    	types = new HashMap<>();
    }
    
    public HashMap<String, Object> getLib() {
        return library;
    }
    
    public List<MapObject> getAll() {
        return all;
    }
    
    public Map<String, Map<Integer, List<MapObject>>> getTypes() {
        return types;
    }
    
    public Object intern(Object o) {
        if (o instanceof String) {
            return ((String)o).intern();
        }
        return o;
    }
    
    public static void add(MapObject mob) {
        Library library = instance();
        if (library == null)
            throw new Error("Game.hero.lib not available!");
        String name = (String) mob.getName();
        if (name == null)
            throw new Error("Trying to add unnamed object to Library!");

        prepareAdd(mob);
        
        if (library.library.get(name)!=null) {
            Mists.logger.warning("Trying to add duplicate object ["+name+"] to library!");      
        }
        library.library.put(name, mob);
        library.lowerCaseNames.put(name.toLowerCase(),name);
        library.all.add(mob);
        library.addThingToTypeArray(mob);

    }   
    
    //Pre-prosessing ensures no broken mobs can get into a library
    public static void prepareAdd (MapObject mob) {
        //TODO: Pre-processing
    }    
    
    public static MapObject get(String name) {
        return (MapObject) Library.instance().library.get(name);
    }
    
    private static Object libLock=new Object();
    
    public static Library instance() {
        synchronized (libLock) {
        	if(instance == null) {
	            instance = new Library();
	            Library.init();
	        }
	    }
        return instance;
    }
    
    public static void init() {
        // set up base classes
        initBase();

        Monsters.init();
        
        Mists.logger.info("Libraries initialized");
        }
    }

    private static void initBase() {
        MapObject mob;
        
        mob = new MapObject("base mob", new Image());
        Library.add(mob);
    }
 */   
}
