package com.nkoiv.mists.game.libraries;

import java.util.List;
import java.util.Map;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.CircuitTile;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Door;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PuzzleTile;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import com.nkoiv.mists.game.gameobject.Water;
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * StructureLibrary extends the generic MapObject library (MobLibrary)
 * by providing services related directly to Structures in particular.
 * This includes but isn't limited to YAML-parsing and object verification.
 * @author daedra
 */
public class StructureLibrary<E extends Structure> extends MobLibrary<Structure> {
	
	public StructureLibrary() {
		super();
	}

	public static Structure generateStructureFromYAML(Map structureData) {
        String mobname = (String)structureData.get("name");
        int collisionLevel;
        if (structureData.containsKey("collisionLevel")) {
            collisionLevel = Integer.parseInt((String)structureData.get("collisionLevel"));
        } else {
            collisionLevel = 1;
        }
        Structure struct;
        if (structureData.containsKey("spritesheet")) {
            Mists.logger.info("Generating structure sprite from spritesheet");
            List<String> spritesheetParameters = (List<String>)structureData.get("spritesheetParameters");
            String spritesheetLocation = (String)structureData.get("spritesheet");
            Sprite sp = getSpriteFromSpriteSheet(spritesheetLocation, spritesheetParameters);
            struct = new Structure(mobname, sp, collisionLevel);
        } else {
            Image image = new Image((String)structureData.get("image"));
            struct = new Structure(mobname, image, collisionLevel); 
        }
        if (structureData.containsKey("lightLevel")) struct.setLightSize(Double.parseDouble((String)structureData.get("lightLevel")));
        addExtras(structureData, struct);
        setCollisionArea(structureData, struct);
        return struct;
    }
    
    
	public static ItemContainer generateItemContainerFromYAML(Map structureData) {
        String mobname = (String)structureData.get("name");
        ItemContainer ic;
        if (structureData.containsKey("spritesheet")) {
            List<String> spritesheetParameters = (List<String>)structureData.get("spritesheetParameters");
            String spritesheetLocation = (String)structureData.get("spritesheet");
            Sprite sp = getSpriteFromSpriteSheet(spritesheetLocation, spritesheetParameters);
            ic = new ItemContainer(mobname, sp);
        } else {
            Image image = new Image((String)structureData.get("image"));
            ic = new ItemContainer(mobname, new Sprite(image)); 
        }
        if (structureData.containsKey("collisionLevel")) ic.setCollisionLevel(Integer.parseInt((String)structureData.get("collisionLevel")));
        addExtras(structureData, ic);
        addFlagsFromYAML(structureData, ic);
        return ic;
    }
    
	public static Structure generateAnimatedFrillFromYAML(Map frillData) {
        String frillname = (String)frillData.get("name");
        SpriteAnimation sa = generateSpriteAnimation((String)frillData.get("spritesheet"), 
                (List<String>)frillData.get("spritesheetParameters"));
        Sprite sp = new Sprite(Mists.graphLibrary.getImage("blank"));
        sp.setAnimation(sa);
        Structure struct = new Structure(frillname, sp, 0);
        addExtras(frillData, struct);
        return struct;
    }
    
	public static Wall generateWallFromYAML(Map wallData) {
        String mobname = (String)wallData.get("name");
        String collisionLevel = (String)wallData.get("collisionLevel");
        ImageView wallImages = new ImageView((String)wallData.get("image"));
        Wall wall = new Wall(mobname, new Image("/images/structures/blank.png"), Integer.parseInt(collisionLevel), wallImages);
        wall.generateWallImages(wallImages);
        addFlagsFromYAML(wallData, wall);
        return wall;
    }
    
	public static Water generateWaterFromYAML(Map waterData) {
        String mobname = (String)waterData.get("name");
        int collisionLevel = Integer.parseInt((String)waterData.get("collisionLevel"));
        ImageView waterImages = new ImageView((String)waterData.get("image_anim1"));
        ImageView waterImages_alt = new ImageView((String)waterData.get("image_anim2"));
        Water water = new Water(mobname, waterImages, waterImages_alt);
        water.setCollisionLevel(collisionLevel);
        water.generateWaterTilesFromImageView();
        addFlagsFromYAML(waterData, water);
        return water;
    }
    
	public static WorldMapEntrance generateMapEntranceFromYAML(Map entranceData) {
        String mobname = (String)entranceData.get("name");
        int collisionLevel = Integer.parseInt((String)entranceData.get("collisionLevel"));
        Image image = new Image((String)entranceData.get("image"));
        WorldMapEntrance entrance = new WorldMapEntrance(mobname, new Sprite(image), collisionLevel, null); 
        addExtras(entranceData, entrance);
        addFlagsFromYAML(entranceData, entrance);
        return entrance;
        //MapEntrance stairs = new MapEntrance("dungeonStairs", new Sprite(new Image("/images/structures/stairs.png")), 0, null);
    }
    
	public static PuzzleTile generatePuzzleTileFromYAML(Map tileData) {
        String mobname = (String)tileData.get("name");
        Image imageOpen = new Image((String)tileData.get("imageLit"));
        Image imageClosed = new Image((String)tileData.get("imageUnlit"));
        PuzzleTile puzzletile = new PuzzleTile(mobname, imageOpen, imageClosed);
        addExtras(tileData, puzzletile);
        addFlagsFromYAML(tileData, puzzletile);
        return puzzletile;
    }
    
	public static CircuitTile generateCircuitTileFromYAML(Map tileData) {
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
            default: Mists.logger.warning("Unrecognized puzzle tile shape in YAML"); break;
        }
        Image imageOpen = new Image((String)tileData.get("imageLit"));
        Image imageClosed = new Image((String)tileData.get("imageUnlit"));
        CircuitTile circuittile = new CircuitTile(mobname, openPaths, imageOpen, imageClosed);
        addExtras(tileData, circuittile);
        addFlagsFromYAML(tileData, circuittile);
        return circuittile;
    }
    
	public static Door generateDoorFromYAML(Map doorData) {
        String mobname = (String)doorData.get("name");
        int collisionLevel = Integer.parseInt((String)doorData.get("collisionLevel"));
        Image imageOpen = new Image((String)doorData.get("imageOpen"));
        Image imageClosed = new Image((String)doorData.get("imageClosed"));
        Door door = new Door(mobname, imageOpen, imageClosed, collisionLevel); 
        Map extras = (Map)doorData.get("extras");
        addExtras(doorData, door);
        addFlagsFromYAML(doorData, door);
        return door;
    }
	
}
