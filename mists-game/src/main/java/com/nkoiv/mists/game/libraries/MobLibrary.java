/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.libraries;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Effect;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.util.Flags;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * A library is a collection of map objects templates.
 * Monsters are stored in a monlibrary, structures in a struclibrary, etc
 * The concept was inspired (read: stolen) from Mikeras' Tyrant (github.com/mikera/tyrant/)
 * 
 * The generic MobLibrary also houses generic static classes for parsing flags, sprites, etc
 * common elements from YAML.
 * 
 * @author nkoiv
 * @param <E> The type of MapObject stored in this MobLibrary
 */
public class MobLibrary <E extends MapObject> implements Serializable, Cloneable {
    
    //ArrayList for storing all the individual libraries
    //private static final ArrayList<AssetLibrary> libraries = new ArrayList<>();
    
    private final HashMap<String, E> libByName;
    private final HashMap<Integer, E> lib;
    private int nextFreeID = 1;
    
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
    
    private int getNextFreeID() {
        while (this.lib.containsKey(nextFreeID)) {
            nextFreeID++;
        }
        return nextFreeID;
    }
    
    public void setTemplateID(Map mobData, MapObject mob) {
        if (mobData.containsKey("templateID")) {
            int templateID = Integer.parseInt((String)mobData.get("templateID"));
            mob.setTemplateID(templateID);
        } else {
            mob.setTemplateID(getNextFreeID());
        }
    }
   
    
    public static MapObject generateFromYAML(Map mobData) {
        String mobtype = (String)mobData.get("type");
        switch (mobtype) {
            case "Creature": Mists.logger.info("Generating CREATURE");
                return CreatureLibrary.generateCreatureFromYAML(mobData);
            case "GenericStructure": Mists.logger.info("Generating GENERIC STRUCTURE");
                return StructureLibrary.generateStructureFromYAML(mobData);
            case "Wall": Mists.logger.info("Generating WALL");
                return StructureLibrary.generateWallFromYAML(mobData);
            case "Water": Mists.logger.info("Generating WATER");
                return StructureLibrary.generateWaterFromYAML(mobData);
            case "ItemContainer": Mists.logger.info("Generating ITEM CONTAINER");
                return StructureLibrary.generateItemContainerFromYAML(mobData);
            case "MapEntrance": Mists.logger.info("Generating MAP ENTRANCE");
                return StructureLibrary.generateMapEntranceFromYAML(mobData);
            case "Door": Mists.logger.info("Generating DOOR");
                return StructureLibrary.generateDoorFromYAML(mobData);
            case "PuzzleTile": Mists.logger.info("Generating PUZZLE TILE");
                return StructureLibrary.generatePuzzleTileFromYAML(mobData);
            case "CircuitTile": Mists.logger.info("Generating CIRCUIT TILE");
                return StructureLibrary.generateCircuitTileFromYAML(mobData);
            case "AnimatedFrill": Mists.logger.info("Generating ANIMATED FRILL");
                return StructureLibrary.generateAnimatedFrillFromYAML(mobData);
            default: return null;
        }        
    }
    
    
    protected static void addFlagsFromYAML(Map mobData, MapObject mob) {
        if (mobData.containsKey("flags")) {
            Map flags = (Map)mobData.get("flags");
            for (Object f : flags.keySet()) {
                String flagName = (String)f;
                int flagValue = Integer.parseInt((String)flags.get(flagName));
                if ("collisionLevel".equals(flagName)) mob.setCollisionLevel(flagValue);
                else mob.setFlag(flagName, flagValue);
            }
        }
    }
    
    protected static void addAttributesFromYAML(Map mobData, Creature mob) {
        if (mobData.containsKey("attributes")) {
            Map attributes = (Map)mobData.get("attributes");
            for (Object a : attributes.keySet()) {
                String attributeName = (String)a;
                int attributeValue = Integer.parseInt((String)attributes.get(attributeName));
                mob.setAttribute(attributeName, attributeValue);
            }
        }
    }
    
   
    
    protected static Sprite getSpriteFromSpriteSheet(String spritesheetLocation, List<String> spriteSheetParameters) {
        //Mists.logger.info("Generating sprite from spritesheet");
        Sprite sp;
        int framecount = Integer.parseInt(spriteSheetParameters.get(0));
        if (framecount == 1) {
            //Mists.logger.info("Framecount set to 1");
            Image structureImage = clipImageFromSpriteSheet(spritesheetLocation, spriteSheetParameters);
            sp = new Sprite(structureImage);
        } else {
            //Mists.logger.info("Framecount set to more than 1");
            SpriteAnimation sa = generateSpriteAnimation(spritesheetLocation, spriteSheetParameters);
            sp = new Sprite(Mists.graphLibrary.getImage("blank"));
            sp.setAnimation(sa);
        }
        return sp;
    }
    
    protected static Image clipImageFromSpriteSheet(String spritesheet, List<String> spriteSheetParameters) {
        //Mists.logger.info("Clipping image from spritesheet...");
        ImageView imageView = new ImageView(spritesheet);
        //int framecount = Integer.parseInt(spriteSheetParameters.get(0));
        int xPos = Integer.parseInt(spriteSheetParameters.get(1));
        int yPos = Integer.parseInt(spriteSheetParameters.get(2));
        //int xOffset = Integer.parseInt(spriteSheetParameters.get(3));
        //int yOffset = Integer.parseInt(spriteSheetParameters.get(4));
        int width = Integer.parseInt(spriteSheetParameters.get(5));
        int height = Integer.parseInt(spriteSheetParameters.get(6));
        //Mists.logger.info("Rectangle setting to: "+xPos+"x"+yPos+" - size: "+width+"x"+height);
        Rectangle2D r = new Rectangle2D(xPos, yPos, width, height);
        imageView.setViewport(r);
        WritableImage snapshot = null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        //Mists.logger.info("Returning snapshot");
        return imageView.snapshot(parameters, snapshot);
    }
    
    protected static SpriteAnimation generateSpriteAnimation(String spritesheet, List<String> spriteSheetParameters) {
        ImageView imageView = new ImageView(spritesheet);
        SpriteAnimation sa = new SpriteAnimation(spritesheet+"_animation", imageView, Integer.parseInt(spriteSheetParameters.get(0)),
                    Integer.parseInt(spriteSheetParameters.get(1)),
                    Integer.parseInt(spriteSheetParameters.get(2)),
                    Integer.parseInt(spriteSheetParameters.get(3)),
                    Integer.parseInt(spriteSheetParameters.get(4)),
                    Integer.parseInt(spriteSheetParameters.get(5)),
                    Integer.parseInt(spriteSheetParameters.get(6)));
        return sa;
    }
    
    protected static void setCollisionArea(Map structureData, Structure structure) {
        if (!structureData.containsKey("collisionArea")) return;
        List<String> collisionParameters = (List)structureData.get("collisionArea");
        try {
            int width = Integer.parseInt(collisionParameters.get(0));
            int height = Integer.parseInt(collisionParameters.get(1));
            int xOffset = Integer.parseInt(collisionParameters.get(2));
            int yOffset = Integer.parseInt(collisionParameters.get(3));
            structure.getSprite().setCollisionBox(width, height, xOffset, yOffset);
        } catch (Exception e) {
            Mists.logger.log(Level.WARNING, "Tried to parse collisionArea data for {0} but ran into error with the list: {1}", new String[]{structure.getName(), e.toString()});
        }
    }
    
    protected static void addExtras(Map structureData, Structure structure) {
        Mists.logger.info("Generating extras for "+structure.getName());
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
    protected static void prepareAdd(MapObject e) {
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
