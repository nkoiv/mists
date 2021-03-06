/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.libraries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.puzzle.Puzzle;
import com.nkoiv.mists.game.sprites.Roof;
import com.nkoiv.mists.game.world.GameMap;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import com.nkoiv.mists.game.world.mapgen.BSPDungeonGenerator;
import com.nkoiv.mists.game.world.mapgen.DungeonGenerator;

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
    

    /**
     * Generate a location out of a template
     * TODO: Currently the TEMPLATE resources are directly
     * accessed, resulting in zero reusability for the same template
     * Start using .createFromTemplate() on things?
     * @param template
     * @return 
     */
    private Location generateLocation(LocationTemplate template) {
        Location l;
        if (template.map == null) {
            //Need to generate a map first
            Mists.logger.info("LocationTemplate had a null Map - generating");
            int tileWidth = (int)template.width/Mists.TILESIZE;
            int tileHeight = (int)template.height/Mists.TILESIZE;
            if (template.randomSeed!=0) {
                DungeonGenerator.setRandomSeed(template.randomSeed);
            }
            TileMap tmap = BSPDungeonGenerator.generateBSPDungeon(tileWidth, tileHeight);
            Mists.logger.info("Random dungeonmap generated, constructing location...");
            l = new Location (template.name, tmap);
        } 
        else {
            Mists.logger.info("Template map was found: "+template.map.toString());
            l = new Location(template.name, template.map);
        }
        Mists.logger.info("Location map generated, adding template mobs to location");
        l.loading = true;
        for (MapObject mob : template.mobs) {
            l.addMapObject(mob);
            if (mob.getXPos() == 0 && mob.getYPos() == 0) l.setMobInRandomOpenSpot(mob);
        }
        Mists.logger.info("Linking dialogue to map objects");
        addDialoguesToManager(template.dialogues);
        Mists.logger.info("Initializing puzzles for the location");
        for (Puzzle p : template.puzzles) {
            l.getPuzzleManager().addPuzzle(p);
        }
        Mists.logger.info("Creating roofs on structures");
        for (Roof r : template.roofs) {
            l.addRoof(r);
        }
        Mists.logger.info("Roofs done, generating stairs");
        boolean entranceFound = false;
        for (MapObject mob : template.mobs) {
            if (mob instanceof WorldMapEntrance) {
                entranceFound = true;
            }
        }
        Mists.logger.info("Entrance generated");
        //If no MapEntrance waws found, generate one
        /*
        if (!entranceFound) {
            MapEntrance stairs = (MapEntrance)Mists.structureLibrary.create("dungeonStairs");
            l.addMapObject(stairs);
            l.setMobInRandomOpenSpot(stairs);
        }
        */
        //l.setMinLightLevel(template.lightlevel);
        if (template.lightlevel > 0) l.getEnvironment().setLightlevel(template.lightlevel);
        l.getEnvironment().setDefaultMusic(template.music);
        l.updateAllVariableGraphicStructures();
        l.setBaseID(template.baseID);
        l.loading = false;
        return l;
    }
    
    private void addDialoguesToManager(HashMap<String, Integer> dialogues) {
    	if (dialogues.isEmpty()) return;
    	Mists.logger.info("Linking dialogues: "+dialogues.toString());
    	for (String mobname : dialogues.keySet()) {
    		int dialogueID = dialogues.get(mobname);
    		Mists.logger.info("Adding " + mobname + " with dialogueID " + dialogueID);
    		Mists.MistsGame.dialogueManager.setDialogue(mobname, dialogueID);	
    	}
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
    
    public static Structure generateStructureFromYAML(Map structureDataMap) {
        Structure s = null;
        try {
            String templateName = (String)structureDataMap.get("structure");
            s = Mists.structureLibrary.getTemplate(templateName);
        } catch (Exception e) {
            Mists.logger.log(Level.WARNING, "Error generating structure from StructureDataMap: {0}", structureDataMap.toString());
        }
        return s;
    }

    public static class LocationTemplate {
        public int baseID;
        public String name;
        public GameMap map;
        public long randomSeed;
        public double width;
        public double height;
        public ArrayList<MapObject> mobs;
        public ArrayList<Puzzle> puzzles;
        public ArrayList<Roof> roofs;
        public HashMap<String, Integer> dialogues;
        public double lightlevel;
        public String music;
        
        public LocationTemplate(int ID, String name, double width, double height) {
            this.baseID = ID; this.name = name;
            this.width = width; this.height = height;
            this.mobs = new ArrayList();
            this.puzzles = new ArrayList();
            this.roofs = new ArrayList();
            this.dialogues = new HashMap<>();
            this.music = "none";
        }

        
    }

    
}
