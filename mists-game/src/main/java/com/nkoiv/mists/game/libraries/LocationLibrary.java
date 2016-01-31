/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapEntrance;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.GameMap;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import com.nkoiv.mists.game.world.mapgen.DungeonGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import javafx.scene.image.Image;

/**
 * LocationLibrary houses all the premade locations in the game.
 * While locations can be generated on the fly via MapGens, some
 * locations are static.
 * Locations are stored in this library as Templates, from which
 * the actual playable Locations are generated from. Some templates
 * are more static than others.
 * @author nikok
 */
public class LocationLibrary  {
    private final HashMap<String, LocationTemplate> libByName;
    private final HashMap<Integer, LocationTemplate> lib;
    
    public LocationLibrary() {
        this.lib = new HashMap<>();
        this.libByName = new HashMap<>();
    }
    
    public Location create(String locationName) {
        String lowercase = locationName.toLowerCase();
        if (this.libByName.keySet().contains(lowercase)) {
            LocationTemplate t = this.libByName.get(lowercase);
            return this.generateLocation(t);
        }
        else {
            return null;
        }
    }
    
    public Location create(int locationID) {
        if (this.lib.keySet().contains(locationID)) {
            Mists.logger.info("Creating location from LocationLibrary for ID "+locationID);
            LocationTemplate t = this.lib.get(locationID);
            if (t!=null) {
                Mists.logger.info("ID found, generating...");
                return this.generateLocation(t);
            }
        }
        Mists.logger.warning("Could not generate location");
        return null;
        
    }
    
    public void addTemplate(LocationTemplate e) {
        String lowercasename = e.name.toLowerCase();
        int locationID = e.baseID;
        this.libByName.put(lowercasename, e);
        this.lib.put(locationID, e);
        Mists.logger.log(Level.INFO, "{0} added into library", e.name);
    }
    

    private Location generateLocation(LocationTemplate template) {
        Location l;
        if (template.map == null) {
            //Need to generate a map first
            Mists.logger.info("LocationTemplate had a null Map - generating");
            int tileWidth = (int)template.width/Mists.TILESIZE;
            int tileHeight = (int)template.height/Mists.TILESIZE;
            DungeonGenerator dgen = new DungeonGenerator();
            if (template.randomSeed!=0) {
                DungeonGenerator.setRandomSeed(template.randomSeed);
            }
            TileMap tmap = DungeonGenerator.generateDungeon(dgen, tileWidth, tileHeight);
            Mists.logger.info("Random dungeonmap generated, constructing location...");
            l = new Location (template.name, tmap);
        } 
        else {
            Mists.logger.info("Template map was found: "+template.map.toString());
            l = new Location(template.name, template.map);
        }
        Mists.logger.info("Location map generated, mobing to template mobs");
        l.loading = true;
        for (MapObject mob : template.mobs) {
            l.addMapObject(mob);
            if (mob.getXPos() == 0 && mob.getYPos() == 0) l.setMobInRandomOpenSpot(mob);
        }
        Mists.logger.info("Template mobs added, generating stairs");
        MapEntrance stairs = (MapEntrance)Mists.structureLibrary.create("dungeonStairs");
        l.addMapObject(stairs);
        l.setMobInRandomOpenSpot(stairs);
        
        l.loading = false;
        return l;
    }
    
        
    //TODO: Templates shouldn't be needed outside this class(?)
    /*
    public LocationTemplate getTemplate(int locationID) {
        return this.lib.get(locationID);
    }
    
    public LocationTemplate getTemplate(String locationName) {
        String lowercase = locationName.toLowerCase();
        return this.libByName.get(lowercase);
    }
    */

    public static class LocationTemplate {
        public int baseID;
        public String name;
        public GameMap map;
        public long randomSeed;
        public double width;
        public double height;
        public ArrayList<MapObject> mobs;
        
        public LocationTemplate(int ID, String name, double width, double height) {
            this.baseID = ID; this.name = name;
            this.width = width; this.height = height;
            this.mobs = new ArrayList();
        }

        
    }

    
}
