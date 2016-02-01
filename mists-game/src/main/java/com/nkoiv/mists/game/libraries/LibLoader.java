/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.nkoiv.mists.game.AI.CompanionAI;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.libraries.LocationLibrary.LocationTemplate;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.world.BGMap;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Libloader stocks up the libraries with required preloaded data.
 * TODO: parse the library data from XML or something
 * @author nikok
 */
public class LibLoader {
    
    private static void initializeLibraryFromYAML(Object library, String libFile) {
        File libraryYAML = new File(libFile);
        try {
            Mists.logger.info("Attempting to read YAML from "+libraryYAML.getCanonicalPath());
            YamlReader reader = new YamlReader(new FileReader(libraryYAML));
            while (true) {
                Object object = reader.read();
                if (object == null) break;
                try {
                    Map libraryObjectData = (Map)object;
                    if (library instanceof MobLibrary) {
                        MapObject mob = ((MobLibrary)library).generateFromYAML(libraryObjectData);
                        Mists.logger.info("Got "+mob.getName()+ " from YAML parsing");
                        ((MobLibrary)library).addTemplate(mob);
                    }
                    if (library instanceof ItemLibrary) {
                        Item item = ((ItemLibrary)library).generateFromYAML(libraryObjectData);
                        Mists.logger.info("Got "+item.getName()+ " from YAML parsing");
                        ((ItemLibrary)library).addTemplate(item);
                    }
                } catch (Exception e) {
                    Mists.logger.warning("Failed parsing "+object.toString());
                    Mists.logger.warning(e.toString());
                }
                
            }

        } catch (Exception e) {
            Mists.logger.warning("Was unable to read structure data!");
            Mists.logger.warning(e.toString());
        }
        
    }
    
    public static void initializeStructureLibrary(MobLibrary<Structure> lib) {
        Mists.logger.info("Loading up structure data");
        //TODO: Load the data from a file
        initializeLibraryFromYAML(lib, "src/main/resources/libdata/structures.yml");
        //initializeStructureLibraryFromYAML(lib);
        
    }
    
    public static void initializeCreatureLibrary(MobLibrary<Creature> lib) {
        Mists.logger.info("Loading up creature data");
        //TODO: Load the data from a file
        
        int creatureID = 0;
        //Companions
        
        Creature himmu = new Creature("Himmu",new Image("/images/himmu.png"));
        himmu.setAnimation("downMovement", new ImageView("/images/himmu_walk_down.png"), 4, 0, 0, 0, 0, 64, 64 );
        himmu.setAnimation("upMovement", new ImageView("/images/himmu_walk_up.png"), 4, 0, 0, 0, 0, 64, 64 );       
        himmu.setAnimation("leftMovement", new ImageView("/images/himmu_walk_left.png"), 4, 0, 0, 0, 0, 64, 64 );
        himmu.setAnimation("rightMovement", new ImageView("/images/himmu_walk_right.png"), 4, 0, 0, 0, 0, 64, 64 );
        himmu.setAI(new CompanionAI(himmu));
        himmu.setMaxHealth(1000);
        himmu.setAttribute("Strength", 20);
        MeleeAttack himmumelee = new MeleeAttack();
        himmumelee.setFlag("cooldown", 2000);
        himmu.addAction(himmumelee);
        
        himmu.setTemplateID(creatureID);
        creatureID++;
        
        lib.addTemplate(himmu);
        
        //initializeCreatureLibraryFromYAML(lib);
        initializeLibraryFromYAML(lib, "src/main/resources/libdata/creatures.yml");
        
    }
    
    public static void initializeActionLibrary(ActionLibrary lib) {
        Mists.logger.info("Loading up action data");
        MeleeAttack melee = new MeleeAttack();
        lib.addTemplate(melee);
    }
    
    public static void initializeItemLibrary(ItemLibrary lib) {
        initializeLibraryFromYAML(lib, "src/main/resources/libdata/items.yml");
    }
    
    public static void initializeGraphLibrary(GraphLibrary lib) {
        //---Base elements---
        lib.addImage("blank", new Image("/images/blank.png"));
        lib.addImage("black", new Image("/images/black.png"));
        
        //---UI elements----
        //Panels
        lib.addImageSet("panelBeige", new ImageView("/images/ui/panel_beige.png"), 4, 4, 25, 25);
        lib.addImageSet("panelBrown", new ImageView("/images/ui/panel_brown.png"), 4, 4, 25, 25);
        lib.addImageSet("panelBeigeLight", new ImageView("/images/ui/panel_beigeLight.png"), 4, 4, 25, 25);
        lib.addImageSet("panelBlue", new ImageView("/images/ui/panel_blue.png"), 4, 4, 25, 25);
        
        lib.addImageSet("panelInsetBeige", new ImageView("/images/ui/panelInset_beige.png"), 4, 4, 25, 25);
        lib.addImageSet("panelInsetBrown", new ImageView("/images/ui/panelInset_brown.png"), 4, 4, 25, 25);
        lib.addImageSet("panelInsetBeigeLight", new ImageView("/images/ui/panelInset_beigeLight.png"), 4, 4, 25, 25);
        lib.addImageSet("panelInsetBlue", new ImageView("/images/ui/panelInset_blue.png"), 4, 4, 25, 25);
        
        //Bars
        lib.addImage("barGreenHorizontalLeft", new Image("/images/ui/barGreen_horizontalLeft.png"));
        lib.addImage("barGreenHorizontalMid", new Image("/images/ui/barGreen_horizontalMid.png"));
        lib.addImage("barGreenHorizontalRight", new Image("/images/ui/barGreen_horizontalRight.png"));
        lib.addImage("barRedHorizontalLeft", new Image("/images/ui/barRed_horizontalLeft.png"));
        lib.addImage("barRedHorizontalMid", new Image("/images/ui/barRed_horizontalMid.png"));
        lib.addImage("barRedHorizontalRight", new Image("/images/ui/barRed_horizontalRight.png"));
        lib.addImage("barBackHorizontalLeft", new Image("/images/ui/barBack_horizontalLeft.png"));
        lib.addImage("barBackHorizontalMid", new Image("/images/ui/barBack_horizontalMid.png"));
        lib.addImage("barBackHorizontalRight", new Image("/images/ui/barBack_horizontalRight.png"));
        
        //Cursors
        lib.addImage("cursorHandblue", new Image("/images/ui/cursorHand_blue.png"));
        lib.addImage("cursorHandbeige", new Image("/images/ui/cursorHand_beige.png"));
        lib.addImage("cursorHandgrey", new Image("/images/ui/cursorHand_grey.png"));
        
        lib.addImage("cursorGauntletblue", new Image("/images/ui/cursorGauntlet_blue.png"));
        lib.addImage("cursorGauntletbronze", new Image("/images/ui/cursorGauntlet_bronze.png"));
        lib.addImage("cursorGauntletgrey", new Image("/images/ui/cursorGauntlet_grey.png"));
        lib.addImage("cursorGauntletSmallbronze", new Image("/images/ui/cursorGauntletSmall_bronze.png"));
        
        lib.addImage("cursorSwordgold", new Image("/images/ui/cursorSword_gold.png"));
        lib.addImage("cursorSwordsilver", new Image("/images/ui/cursorSword_silver.png"));
        lib.addImage("cursorSwordbronze", new Image("/images/ui/cursorSword_bronze.png"));
        
        //Buttons
        lib.addImage("buttonSquareBeige", new Image("/images/ui/buttonSquare_beige.png"));
        lib.addImage("buttonSquareBeigePressed", new Image("/images/ui/buttonSquare_beige_pressed.png"));
        
        lib.addImage("iconCheckBeige", new Image("/images/ui/iconCheck_beige.png"));
        lib.addImage("iconCheckBlue", new Image("/images/ui/iconCheck_blue.png"));
        lib.addImage("iconCheckBronze", new Image("/images/ui/iconCheck_bronze.png"));
        
        lib.addImage("iconCrossBeige", new Image("/images/ui/iconCross_beige.png"));
        lib.addImage("iconCrossBlue", new Image("/images/ui/iconCross_blue.png"));
        lib.addImage("iconCrossBronze", new Image("/images/ui/iconCross_bronze.png"));
        
        lib.addImage("iconCircleBeige", new Image("/images/ui/iconCircle_beige.png"));
        lib.addImage("iconCircleBlue", new Image("/images/ui/iconCircle_blue.png"));
        lib.addImage("iconCircleronze", new Image("/images/ui/iconCircle_bronze.png"));
        
        //Icons
        lib.addImage("musicOnIcon", new Image("/images/ui/musicOn.png"));
        lib.addImage("musicOffIcon", new Image("/images/ui/musicOff.png"));
        
        lib.addImage("buttonSelectbeige", new Image("/images/ui/buttonSquareSelect_beige.png"));
        lib.addImage("buttonSelectSmallbeige", new Image("/images/ui/buttonSquareSelectSmall_beige.png"));
        
        //Icons
        lib.addImage("circle64", new Image("/images/circle_64.png"));
        lib.addImage("circle32", new Image("/images/circle_32.png"));
    }
    
    public static void initializeLocationLibrary(LocationLibrary lib) {
        //--TestDungeon--
        LocationTemplate testDungeon = new LocationTemplate(1, "TestDungeon", 60*Mists.TILESIZE, 40*Mists.TILESIZE);
        for (int i = 0; i<10;i++) {
            //Make a bunch of trees
            Structure tree = Mists.structureLibrary.create("Tree");
            testDungeon.mobs.add(tree);
        }
        
        for (int i = 0; i<10;i++) {
            //Make a bunch of itempiles
            ItemContainer pile = new ItemContainer("ItemPile", new Sprite(Mists.graphLibrary.getImage("blank")));
            pile.setRenderContent(true);
            pile.addItem(Mists.itemLibrary.create("sword"));
            pile.addItem(Mists.itemLibrary.create("himmutoy"));
            testDungeon.mobs.add(pile);
        }
        
        for (int i = 0; i < 20; i++) {
            Random rnd = new Random();
            int randomMob = rnd.nextInt(4);
            Creature monster;
            switch (randomMob) {
                case 0: monster = Mists.creatureLibrary.create("worm"); break;
                case 1: monster = Mists.creatureLibrary.create("swampy"); break;
                case 2: monster = Mists.creatureLibrary.create("eggy"); break;
                case 3: monster = Mists.creatureLibrary.create("rabbit"); break;
                default: monster = Mists.creatureLibrary.create("worm");
            }
            testDungeon.mobs.add(monster);
        }
        
        lib.addTemplate(testDungeon);
        
        //--TestVillage--
        Image villageBackground = new Image("/images/pocmap.png");
        LocationTemplate testVillage = new LocationTemplate(2, "TestVillage", villageBackground.getWidth(), villageBackground.getHeight());
        testVillage.map = new BGMap(villageBackground);
        for (int i = 0; i<10;i++) {
            //Make a bunch of trees
            Structure tree = Mists.structureLibrary.create("Tree");
            testVillage.mobs.add(tree);
        }
        testVillage.lightlevel=0.5;
        lib.addTemplate(testVillage);
        
         //--Woods--
        Image woodsBackground = new Image("/images/pocmap.png");
        LocationTemplate woods = new LocationTemplate(3, "Woods", woodsBackground.getWidth(), woodsBackground.getHeight());
        woods.map = new BGMap(woodsBackground);
        for (int i = 0; i<25;i++) {
            //Make a bunch of trees
            Structure tree = Mists.structureLibrary.create("Tree");
            woods.mobs.add(tree);
        }
        for (int i = 0; i<10;i++) {
            //Make a bunch of trees
            Structure rock = Mists.structureLibrary.create("Rock");
            woods.mobs.add(rock);
        }
        woods.lightlevel = 0.3;
        lib.addTemplate(woods);
        
    }
}
