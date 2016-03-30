/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.AI.MonsterAI;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.CircuitTile;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Door;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapEntrance;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PuzzleTile;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.world.Location;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    
    private final HashMap<String, E> libByName;
    private final HashMap<Integer, E> lib;
    
    
    public MobLibrary() {
        lib = (HashMap<Integer, E>) new HashMap();
        libByName = (HashMap<String, E>) new HashMap();
    }
    
    public E create(int baseID) {
        E template = getTemplate(baseID);
        if (template == null) return null;
        return (E)template.createFromTemplate();
    }
    
    public E create(String name) {
        String lowercasename = name.toLowerCase();
        E template = getTemplate(lowercasename);
        if (template == null) {
            //TODO: Consider creating dummy placeholders for stuff not found
            return null;
        }
        return (E)template.createFromTemplate();
    }
    
    public E create(String name, Location l, int xCoor, int yCoor) {
        E template = create(name);
        E thing = (E)template.createFromTemplate();
        if (l!=null) {
            //l.addMapObject(thing);
            thing.setPosition(xCoor, yCoor);
        }
        return thing;
    }
    
    public void addTemplate(E e) {
        prepareAdd(e);
        String lowercasename = e.getName().toLowerCase();
        this.libByName.put(lowercasename, e);
        this.lib.put(e.getTemplateID(), e);
        Mists.logger.log(Level.INFO, "{0}:{1} added to library", new Object[]{e.getTemplateID(), lowercasename});
    }
    
    public static MapObject generateFromYAML(Map mobData) {
        String mobtype = (String)mobData.get("type");
        switch (mobtype) {
            case "Creature": Mists.logger.info("Generating CREATURE");
                return generateCreatureFromYAML(mobData);
            case "GenericStructure": Mists.logger.info("Generating GENERIC STRUCTURE");
                return generateStructureFromYAML(mobData);
            case "Wall": Mists.logger.info("Generating WALL");
                return generateWallFromYAML(mobData);
            case "ItemContainer": Mists.logger.info("Generating ITEM CONTAINER");
                return generateItemContainerFromYAML(mobData);
            case "MapEntrance": Mists.logger.info("Generating MAP ENTRANCE");
                return generateMapEntranceFromYAML(mobData);
            case "Door": Mists.logger.info("Generating DOOR");
                return generateDoorFromYAML(mobData);
            case "PuzzleTile": Mists.logger.info("Generating PUZZLE TILE");
                return generatePuzzleTileFromYAML(mobData);
            case "CircuitTile": Mists.logger.info("Generating CIRCUIT TILE");
                return generateCircuitTileFromYAML(mobData);
            case "AnimatedFrill": Mists.logger.info("Generating ANIMATED FRILL");
                return generateAnimatedFrillFromYAML(mobData);
        }        
        return null;
    }
    
    private static Creature generateCreatureFromYAML(Map creatureData) {
        Creature creep;
        int monsterID = Integer.parseInt((String)creatureData.get("monsterID"));
        String mobname = (String)creatureData.get("name");
        String spriteType = (String)creatureData.get("spriteType");
        if (spriteType.equals("static")) {
            Image monsterImage = new Image((String)creatureData.get("image"));
            creep = new Creature(mobname, monsterImage);
        } else {
            ImageView monsterSprites = new ImageView((String)creatureData.get("spritesheet"));
            Mists.logger.info("Spritesheet loaded: "+monsterSprites.getImage().getWidth()+"x"+monsterSprites.getImage().getHeight());
            List<String> p = (List<String>)creatureData.get("spritesheetParameters");
            Mists.logger.info("Parameters loaded: "+p.toString());
            creep = new Creature(mobname, monsterSprites,
                    Integer.parseInt(p.get(0)),
                    Integer.parseInt(p.get(1)),
                    Integer.parseInt(p.get(2)),
                    Integer.parseInt(p.get(3)),
                    Integer.parseInt(p.get(4)),
                    Integer.parseInt(p.get(5)),
                    Integer.parseInt(p.get(6)));
        }
        Mists.logger.info("Creature base generated, adding attributes and flags");
        
        if (creatureData.containsKey("attributes")) {
            Map attributes = (Map)creatureData.get("attributes");
            for (Object a : attributes.keySet()) {
                String attributeName = (String)a;
                int attributeValue = Integer.parseInt((String)attributes.get(attributeName));
                creep.setAttribute(attributeName, attributeValue);
            }
        }
        
        if (creatureData.containsKey("flags")) {
            Map flags = (Map)creatureData.get("flags");
            for (Object f : flags.keySet()) {
                String flagName = (String)f;
                int flagValue = Integer.parseInt((String)flags.get(flagName));
                creep.setAttribute(flagName, flagValue);
            }
        }
        
        if (creatureData.containsKey("aiType")) {
            String aiType = (String)creatureData.get("aiType");
            switch (aiType){
                case "monster": creep.setAI(new MonsterAI(creep));
                    break;
            }
        }
        
        
        //TODO: Add actions to YAML
        creep.addAction(new MeleeAttack());
        
        creep.setTemplateID(monsterID);
        return creep;
    }
    
    private static Structure generateStructureFromYAML(Map structureData) {
        String mobname = (String)structureData.get("name");
        int collisionLevel = Integer.parseInt((String)structureData.get("collisionLevel"));
        Structure struct;
        if (structureData.containsKey("spriteType")) {
            String spriteType = (String)structureData.get("spriteType");
            if (spriteType.equals("static")) {
                Image image = new Image((String)structureData.get("image"));
                struct = new Structure(mobname, image, collisionLevel); 
            } else { //if (spriteType.equals("spritesheet")){
                SpriteAnimation sa = generateSpriteAnimation((String)structureData.get("spritesheet"), 
                (List<String>)structureData.get("spritesheetParameters"));
                Sprite sp = new Sprite(Mists.graphLibrary.getImage("blank"));
                sp.setAnimation(sa);
                struct = new Structure(mobname, sp, collisionLevel);
            }
        } else {
            Image image = new Image((String)structureData.get("image"));
            struct = new Structure(mobname, image, collisionLevel); 
        }
        addExtras(structureData, struct);
        return struct;
    }
    
    
    private static ItemContainer generateItemContainerFromYAML(Map structureData) {
        String mobname = (String)structureData.get("name");
        ItemContainer ic;
        if (structureData.containsKey("spriteType")) {
            String spriteType = (String)structureData.get("spriteType");
            if (spriteType.equals("static")) {
                Image image = new Image((String)structureData.get("image"));
                ic = new ItemContainer(mobname, new Sprite(image)); 
            } else { //if (spriteType.equals("spritesheet")){
                SpriteAnimation sa = generateSpriteAnimation((String)structureData.get("spritesheet"), 
                (List<String>)structureData.get("spritesheetParameters"));
                Sprite sp = new Sprite(Mists.graphLibrary.getImage("blank"));
                sp.setAnimation(sa);
                ic = new ItemContainer(mobname, sp);
            }
        } else {
            Image image = new Image((String)structureData.get("image"));
            ic = new ItemContainer(mobname, new Sprite(image)); 
        }
        if (structureData.containsKey("collisionLevel")) ic.setCollisionLevel(Integer.parseInt((String)structureData.get("collisionLevel")));
        addExtras(structureData, ic);
        return ic;
    }
    
    private static SpriteAnimation generateSpriteAnimation(String spritesheet, List<String> spriteSheetParameters) {
        ImageView imageView = new ImageView(spritesheet);
        SpriteAnimation sa = new SpriteAnimation(imageView, Integer.parseInt(spriteSheetParameters.get(0)),
                    Integer.parseInt(spriteSheetParameters.get(1)),
                    Integer.parseInt(spriteSheetParameters.get(2)),
                    Integer.parseInt(spriteSheetParameters.get(3)),
                    Integer.parseInt(spriteSheetParameters.get(4)),
                    Integer.parseInt(spriteSheetParameters.get(5)),
                    Integer.parseInt(spriteSheetParameters.get(6)));
        return sa;
    }
    
    private static Structure generateAnimatedFrillFromYAML(Map frillData) {
        String frillname = (String)frillData.get("name");
        SpriteAnimation sa = generateSpriteAnimation((String)frillData.get("spritesheet"), 
                (List<String>)frillData.get("spritesheetParameters"));
        Sprite sp = new Sprite(Mists.graphLibrary.getImage("blank"));
        sp.setAnimation(sa);
        Structure struct = new Structure(frillname, sp, 0);
        addExtras(frillData, struct);
        return struct;
    }
    
    private static Wall generateWallFromYAML(Map wallData) {
        String mobname = (String)wallData.get("name");
        String collisionLevel = (String)wallData.get("collisionLevel");
        ImageView wallImages = new ImageView((String)wallData.get("image"));
        Wall wall = new Wall(mobname, new Image("/images/structures/blank.png"), Integer.parseInt(collisionLevel), wallImages);
        wall.generateWallImages(wallImages);
        return wall;
    }
    
    
    private static MapEntrance generateMapEntranceFromYAML(Map entranceData) {
        String mobname = (String)entranceData.get("name");
        int collisionLevel = Integer.parseInt((String)entranceData.get("collisionLevel"));
        Image image = new Image((String)entranceData.get("image"));
        MapEntrance entrance = new MapEntrance(mobname, new Sprite(image), collisionLevel, null); 
        addExtras(entranceData, entrance);
        return entrance;
        //MapEntrance stairs = new MapEntrance("dungeonStairs", new Sprite(new Image("/images/structures/stairs.png")), 0, null);
    }
    
    private static PuzzleTile generatePuzzleTileFromYAML(Map tileData) {
        String mobname = (String)tileData.get("name");
        Image imageOpen = new Image((String)tileData.get("imageLit"));
        Image imageClosed = new Image((String)tileData.get("imageUnlit"));
        PuzzleTile puzzletile = new PuzzleTile(mobname, imageOpen, imageClosed);
        addExtras(tileData, puzzletile);
        return puzzletile;
    }
    
    private static CircuitTile generateCircuitTileFromYAML(Map tileData) {
        String mobname = (String)tileData.get("name");
        String pathsID = (String)tileData.get("circuitPaths");
        boolean[] openPaths = new boolean[4];
        switch (pathsID) {
            case "I": openPaths = new boolean[]{true, false, true, false}; break;
            case "L": openPaths = new boolean[]{false, true, true, false}; break;
            case "X": openPaths = new boolean[]{true, true, true, true}; break;
            case "T": openPaths = new boolean[]{true, true, true, false}; break;
            case "S": openPaths = new boolean[]{false, false, true, false}; break;
            case "O": openPaths = new boolean[]{false, false, true, false}; break;
        }
        Image imageOpen = new Image((String)tileData.get("imageLit"));
        Image imageClosed = new Image((String)tileData.get("imageUnlit"));
        CircuitTile circuittile = new CircuitTile(mobname, openPaths, imageOpen, imageClosed);
        addExtras(tileData, circuittile);
        return circuittile;
    }
    
    private static Door generateDoorFromYAML(Map doorData) {
        String mobname = (String)doorData.get("name");
        int collisionLevel = Integer.parseInt((String)doorData.get("collisionLevel"));
        Image imageOpen = new Image((String)doorData.get("imageOpen"));
        Image imageClosed = new Image((String)doorData.get("imageClosed"));
        Door door = new Door(mobname, imageOpen, imageClosed, collisionLevel); 
        Map extras = (Map)doorData.get("extras");
        addExtras(doorData, door);
        return door;
    }
    
    
    private static void addExtras(Map structureData, Structure structure) {
        Map extras = (Map)structureData.get("extras");
        if (extras != null) {
            for (Object key : extras.keySet()) {
                Map extraValues = (Map)extras.get(key);
                if (extraValues.keySet().contains("spritesheet")) {
                    SpriteAnimation sa = generateSpriteAnimation((String)extraValues.get("spritesheet"), 
                        (List<String>)extraValues.get("spritesheetParameters"));
                    Sprite sp = new Sprite(Mists.graphLibrary.getImage("blank"));
                    sp.setAnimation(sa);
                    int xOffset = Integer.parseInt(((String)extraValues.get("xOffset")));
                    int yOffset = Integer.parseInt(((String)extraValues.get("yOffset")));
                    structure.addExtra(sp, xOffset, yOffset);
                } else {
                    Image extraImage = new Image((String)extraValues.get("image"));
                    int xOffset = Integer.parseInt(((String)extraValues.get("xOffset")));
                    int yOffset = Integer.parseInt(((String)extraValues.get("yOffset")));
                    structure.addExtra(extraImage, xOffset, yOffset);
                }
            }
        }
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
        return this.libByName.get(lowercasename);
    }
    
    public E getTemplate(int baseID) {
        return this.lib.get(baseID);
    }
    
    @Override
    public String toString() {
        String s = "MobLibrary containing:\n";
        s = s + this.lib.keySet().toString();
        return s;
    }
    
}
