/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.libraries.premade;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.LocationDoorway;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.libraries.LocationLibrary.LocationTemplate;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.TileMap;
import javafx.scene.image.Image;

/**
 *
 * @author nikok
 */
public abstract class StarterDungeon {
    
    public static LocationTemplate getDungeonLevelOne() {
        Mists.logger.info("Generating template for the starter dungeon level 1");
        LocationTemplate dungeon1 = new LocationTemplate(11, "StarterDungeonOne", 60*Mists.TILESIZE, 40*Mists.TILESIZE);
        dungeon1.map = new TileMap("/mapdata/dungeon_1.map");
        
        generateDungeonOneMobs(dungeon1);
        generateDungeonOneStaticStructures(dungeon1);
        
        return dungeon1;
    }
    
    
    public static LocationTemplate getDungeonLevelTwo() {
        Mists.logger.info("Generating template for the starter dungeon level 2");
        LocationTemplate dungeon2 = new LocationTemplate(12, "StarterDungeonTwo", 60*Mists.TILESIZE, 40*Mists.TILESIZE);
        dungeon2.map = new TileMap("/mapdata/dungeon_2.map");
        
        generateDungeonTwoMobs(dungeon2);
        generateDungeonTwoStaticStructures(dungeon2);
        
        return dungeon2;
    }
    
    private static void generateDungeonOneMobs(LocationTemplate dungeon1) {
        Creature skeleton = Mists.creatureLibrary.create("Skeleton");
        Item himmutoy = Mists.itemLibrary.create(3);
        skeleton.getInventory().addItem(himmutoy);
        skeleton.setPosition(24*Mists.TILESIZE, 11*Mists.TILESIZE);
        dungeon1.mobs.add(skeleton);
    }
    
    private static void generateDungeonOneStaticStructures(LocationTemplate dungeon1) {
        //Exit outside
        Image stairsUpImage = Mists.structureLibrary.getTemplate("DungeonStairsUp").getSprite().getImage();
        WorldMapEntrance entrance = new WorldMapEntrance("Exit", new Sprite(stairsUpImage), 0, null);
        entrance.setPosition(56*Mists.TILESIZE, 19*Mists.TILESIZE);
        dungeon1.mobs.add(entrance);
        
        //Deeper
        Image stairsDownImage = Mists.structureLibrary.getTemplate("DungeonStairsDown").getSprite().getImage();
        LocationDoorway stairsDown = new LocationDoorway("To Level 2", new Sprite(stairsDownImage), 0, 0, 0, 0);
        stairsDown.setTargetLocation(12, 25*Mists.TILESIZE, 5*Mists.TILESIZE);
        stairsDown.setPosition(40*Mists.TILESIZE, 28*Mists.TILESIZE);
        dungeon1.mobs.add(stairsDown);
        
        addStructureFrillsToDungeonOne(dungeon1);
        
    }
    
    private static void addStructureFrillsToDungeonOne(LocationTemplate dungeon1) {
        //Skulls and bones
        Structure skullpile1 = Mists.structureLibrary.create("Skullpile");
        skullpile1.setPosition(20*Mists.TILESIZE, 9*Mists.TILESIZE);
        dungeon1.mobs.add(skullpile1);
        Structure skullpile2 = Mists.structureLibrary.create("Skullpile");
        skullpile2.setPosition(30*Mists.TILESIZE, 11*Mists.TILESIZE);
        dungeon1.mobs.add(skullpile2);
        Structure skullpile3 = Mists.structureLibrary.create("Skullpile");
        skullpile3.setPosition(18*Mists.TILESIZE, 31*Mists.TILESIZE);
        dungeon1.mobs.add(skullpile3);
        
        Structure skull1 = Mists.structureLibrary.create("Skull");
        skull1.setPosition(14*Mists.TILESIZE, 29*Mists.TILESIZE);
        dungeon1.mobs.add(skull1);
        Structure skull2 = Mists.structureLibrary.create("Skull");
        skull2.setPosition(35*Mists.TILESIZE, 29*Mists.TILESIZE);
        dungeon1.mobs.add(skull2);
        Structure skull3 = Mists.structureLibrary.create("Skull");
        skull3.setPosition(30*Mists.TILESIZE, 8*Mists.TILESIZE);
        dungeon1.mobs.add(skull3);
        
        Structure bones1 = Mists.structureLibrary.create("BonesFrill");
        bones1.setPosition(25*Mists.TILESIZE, 12*Mists.TILESIZE);
        dungeon1.mobs.add(bones1);
        for (int i = 0; i < 15; i++) {
            Structure randomBones = Mists.structureLibrary.create("BonesFrill");
            dungeon1.mobs.add(randomBones);
        }
        
        //Ooze
        for (int i = 0; i < 10; i++) {
            Structure randomOoze = Mists.structureLibrary.create("OozeFrill");
            dungeon1.mobs.add(randomOoze);
        }
        
        //Muck
        for (int i = 0; i < 7; i++) {
            Structure randomOoze = Mists.structureLibrary.create("MuckFrill");
            dungeon1.mobs.add(randomOoze);
        }
        
        //Furniture
        Structure longTable = Mists.structureLibrary.create("TableWoodTall");
        longTable.setPosition(40*Mists.TILESIZE, 13*Mists.TILESIZE);
        dungeon1.mobs.add(longTable);
        
    }
    
    private static void generateDungeonTwoMobs(LocationTemplate dungeon2) {
        
    }
    
    private static void generateDungeonTwoStaticStructures(LocationTemplate dungeon2) {
        //Back up
        Image stairsUpImage = Mists.structureLibrary.getTemplate("DungeonStairsUp").getSprite().getImage();
        LocationDoorway stairsUp = new LocationDoorway("To Level 1", new Sprite(stairsUpImage), 0, 0, 0, 0);
        stairsUp.setTargetLocation(11, 40*Mists.TILESIZE, 28*Mists.TILESIZE);
        stairsUp.setPosition(25*Mists.TILESIZE, 5*Mists.TILESIZE);
        dungeon2.mobs.add(stairsUp);
        
    }
    
}
