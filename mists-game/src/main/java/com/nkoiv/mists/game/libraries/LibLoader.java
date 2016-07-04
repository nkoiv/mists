/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.libraries;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.nkoiv.mists.game.AI.CompanionAI;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.actions.MeleeWeaponAttack;
import com.nkoiv.mists.game.actions.ProjectileSpell;
import com.nkoiv.mists.game.actions.ProjectileWeaponAttack;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.gameobject.CircuitTile;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.LocationDoorway;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PuzzleTile;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.items.Item;
import com.nkoiv.mists.game.libraries.LocationLibrary.LocationTemplate;
import com.nkoiv.mists.game.libraries.premade.Beach;
import com.nkoiv.mists.game.libraries.premade.StarterDungeon;
import com.nkoiv.mists.game.libraries.premade.Village;
import com.nkoiv.mists.game.puzzle.CircuitPuzzle;
import com.nkoiv.mists.game.puzzle.LightsOutPuzzle;
import com.nkoiv.mists.game.puzzle.Puzzle;
import com.nkoiv.mists.game.puzzle.TileLitRequirement;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.sprites.SpriteAnimation;
import com.nkoiv.mists.game.triggers.InsertMobTrigger;
import com.nkoiv.mists.game.world.BGMap;
import com.nkoiv.mists.game.world.TileMap;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Libloader stocks up the libraries with required preloaded data.
 * TODO: parse the library data from XML or something
 * @author nikok
 */
public class LibLoader {
    
    /**
     * Initializing a library from YAML is done pretty by supplying the
     * YAMLs Map Objects to the corresponding Library for parsing.
     * Resulting library objects are then placed in the supplied library.
     * @param library Library to store the objects in. Parsing style dictated by type of library supplied.
     * @param libFile filename of the library file to load
     */
    public static void initializeLibraryFromYAML(Object library, String libFile) {
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
                        MapObject mob = MobLibrary.generateFromYAML(libraryObjectData);
                        //Mists.logger.info("Got "+mob.getName()+ " from YAML parsing");
                        ((MobLibrary)library).setTemplateID(libraryObjectData, mob);
                        ((MobLibrary)library).addTemplate(mob);
                    }
                    if (library instanceof ItemLibrary) {
                        Item item = ItemLibrary.generateFromYAML(libraryObjectData);
                        //Mists.logger.info("Got "+item.getName()+ " from YAML parsing");
                        ((ItemLibrary)library).addTemplate(item);
                    }
                    if (library instanceof DialogueLibrary) {
                        Dialogue dialogue = DialogueLibrary.generateDialogueFromYAML(libraryObjectData);
                        int dialogueID = Integer.parseInt((String)libraryObjectData.get("dialogueID"));
                        ((DialogueLibrary)library).addTemplate(dialogue, dialogueID);
                    }
                } catch (Exception e) {
                    Mists.logger.warning("Failed parsing "+object.toString()+" Error message: "+e.toString());
                }
                
            }

        } catch (Exception e) {
            Mists.logger.warning("Was unable to read YAML data!");
            Mists.logger.warning(e.toString());
        }
        
    }
    
    /**
     * Structure codes are Character-representations for structures in
     * a map. Each ASCII character can be linked to a different structure,
     * and by doing so a map for a location can be drawn with same
     * characters, but with different structures.
     * @param codeFile Path to the file with the structure codes
     * @return HashMap with the ASCII value of a character mapped to a Structure
     * @throws java.lang.Exception Throws exception when file is not found
     */
    public static HashMap<Integer,Structure> loadLocationStructureCodes(String codeFile) throws Exception {
        HashMap<Integer, Structure> structureMap = new HashMap<>();
        File codeYAML = new File(codeFile);
        try {
            //Mists.logger.info("Attempting to read YAML from "+codeYAML.getCanonicalPath());
            YamlReader reader = new YamlReader(new FileReader(codeYAML));
            while (true) {
                Object object = reader.read();
                if (object == null) break;
                try {
                    Map structureDataMap = (Map)object;
                    int tileCode = (int)((String)structureDataMap.get("symbol")).charAt(0);
                    Structure structure = LocationLibrary.generateStructureFromYAML(structureDataMap);
                    if (structure !=null) {
                        structureMap.put(tileCode, structure);
                        //Mists.logger.info("Added "+structure.getName()+" on tileCode "+tileCode);
                    }
                } catch (Exception e) {
                    Mists.logger.warning("Failed parsing Structure Code for "+object.toString());
                    Mists.logger.warning(e.toString());
                }
                
            }

        } catch (Exception e) {
            Mists.logger.warning("Was unable to read structure code data!");
            Mists.logger.warning(e.toString());
        }
        return structureMap;
    }
    
    public static HashMap<Integer, Image> loadLocationFloorCodes(String codeFile) {
        HashMap<Integer, Image> floorMap = new HashMap<>();
        File codeYAML = new File(codeFile);
        try {
            Mists.logger.info("Attempting to read YAML from "+codeYAML.getCanonicalPath());
            YamlReader reader = new YamlReader(new FileReader(codeYAML));
            while (true) {
                Object object = reader.read();
                if (object == null) break;
                try {
                    Map floorDataMap = (Map)object;
                    int tileCode = (int)((String)floorDataMap.get("symbol")).charAt(0);
                    if (tileCode == 32) tileCode = 0; //Space (empty) defaults to code 0, which is used for unclear cases
                    String graphicsPath = (String)floorDataMap.get("graphics");
                    Image floorGraphics = new Image(graphicsPath);
                    floorMap.put(tileCode, floorGraphics);
                    Mists.logger.log(Level.INFO, "Added {0} on tileCode {1}", new Object[]{(String)floorDataMap.get("graphic"), tileCode});
                } catch (Exception e) {
                    Mists.logger.log(Level.WARNING, "Failed parsing {0}", object.toString());
                    Mists.logger.warning(e.toString());
                }
                
            }

        } catch (Exception e) {
            Mists.logger.warning("Was unable to read structure code data!");
            Mists.logger.warning(e.toString());
        }
        return floorMap;
    }
    
    public static void initializeStructureLibrary(MobLibrary<Structure> lib) {
        Mists.logger.info("Loading up structure data");
        try {
            initializeLibraryFromYAML(lib, "libdata/structures.yml");
        } catch (Exception e) {
            Mists.logger.warning(e.getMessage());
        }
        
        
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
        himmu.setCurrentDialogue(Mists.dialogueLibrary.getDialogue(2));
        himmu.setTemplateID(creatureID);
        creatureID++;
        
        lib.addTemplate(himmu);
        
        //initializeCreatureLibraryFromYAML(lib);
        try {
            initializeLibraryFromYAML(lib, "libdata/creatures.yml");
        } catch (Exception e) {
            Mists.logger.warning(e.getMessage());
        }
        
        
    }
    
    public static void initializeDialogueLibrary(DialogueLibrary lib) {
        Mists.logger.info("Loading up dialogue data");
        try {
            initializeLibraryFromYAML(lib, "libdata/dialogueTest.yml");
        } catch (Exception e) {
            Mists.logger.warning(e.getMessage());
        }
        
    }
    
    public static void initializeActionLibrary(ActionLibrary lib) {
        Mists.logger.info("Loading up action data");
        MeleeAttack melee = new MeleeAttack();
        lib.addTemplate(melee);
        MeleeWeaponAttack weaponattack = new MeleeWeaponAttack();
        lib.addTemplate(weaponattack);
        ProjectileWeaponAttack shoot = new ProjectileWeaponAttack();
        lib.addTemplate(shoot);
        ProjectileSpell firebolt = new ProjectileSpell();
        lib.addTemplate(firebolt);
        
    }
    
    public static void initializeItemLibrary(ItemLibrary lib) {
        Mists.logger.info("Loading up item data");
        try {
            initializeLibraryFromYAML(lib, "libdata/items.yml");
        } catch (Exception e) {
            Mists.logger.warning(e.getMessage());
        }
        
    }
    
    public static void loadAnimationSheets(GraphLibrary lib) {
    	Image[] torchFlameAnimation = SpriteAnimation.buildFramesFromImageview(new ImageView("/images/environment/torch_flame.png"), 4, 0, 0, 0, 0, 32, 32);
    	lib.addImageSet("torchFlameAnimation", torchFlameAnimation);
        Image[] explosion4Animation = SpriteAnimation.buildFramesFromImageview(new ImageView("/images/effects/explosion-4.png"), 12, 0, 0, 0, 0, 128, 128);
        lib.addImageSet("explosion4Animation", explosion4Animation);
        Image[] clawAttackAnimation = SpriteAnimation.buildFramesFromImageview(new ImageView("/images/effects/attackAnimations.png"), 4, 32, 0, 0, 0, 32, 32);
        lib.addImageSet("clawAttackAnimation", clawAttackAnimation);
        Image[] arrowAnimation = SpriteAnimation.buildFramesFromImageview(new ImageView("/images/effects/arrow.png"), 3, 0, 0, 0, 0, 20, 6);
        lib.addImageSet("arrowAnimation", arrowAnimation);
    }
    
    public static void initializeGraphLibrary(GraphLibrary lib) {
    	//TODO: Load smart! From external (YAML?) file?
    	
    	loadAnimationSheets(lib);

    	//---Base elements---
        lib.addImage("blank", new Image("/images/blank.png"));
        lib.addImage("black", new Image("/images/black.png"));
        
        lib.addImage("lightspot", new Image("/images/light.png"));
        
        //---Roofs---
        lib.addImage("roof_thatch_10x8", new Image("/images/roof_thatch_10x8.png"));
        lib.addImage("roof_brick_11x8", new Image("/images/roof_brick_11x8.png"));
        
        //---Floor tiles---

        lib.addImage("floorDungeonLight", new Image("/images/environment/floor_dungeon_light.png"));
        lib.addImage("floorDungeonDark", new Image("/images/environment/floor_dungeon_dark.png"));
        lib.addImage("floorWoodDark", new Image("/images/environment/floor_wood_dark.png"));
        lib.addImage("floorGrass", new Image("/images/environment/floor_grass.png"));
        
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
        
        //Button bars
        lib.addImageSet("buttonLongBeige", new ImageView("/images/ui/buttonLong_beige.png"), 4, 1, 48, 49);
        lib.addImageSet("buttonLongBrown", new ImageView("/images/ui/buttonLong_brown.png"), 4, 1, 48, 49);
        lib.addImageSet("buttonLongGrey", new ImageView("/images/ui/buttonLong_grey.png"), 4, 1, 48, 49);
        lib.addImageSet("buttonLongBlue", new ImageView("/images/ui/buttonLong_blue.png"), 4, 1, 48, 49);
        
        lib.addImageSet("buttonLongBeigePressed", new ImageView("/images/ui/buttonLong_beige_pressed.png"), 4, 1, 48, 45);
        lib.addImageSet("buttonLongBrownPressed", new ImageView("/images/ui/buttonLong_brown_pressed.png"), 4, 1, 48, 45);
        lib.addImageSet("buttonLongGreyPressed", new ImageView("/images/ui/buttonLong_grey_pressed.png"), 4, 1, 48, 45);
        lib.addImageSet("buttonLongBluePressed", new ImageView("/images/ui/buttonLong_blue_pressed.png"), 4, 1, 48, 45);
        
        
        //Icons
        lib.addImage("musicOnIcon", new Image("/images/ui/musicOn.png"));
        lib.addImage("musicOffIcon", new Image("/images/ui/musicOff.png"));
        
        lib.addImage("charsheetIcon", new Image("/images/ui/charsheetIcon.png"));
        lib.addImage("inventoryIcon", new Image("/images/ui/inventoryIcon.png"));
        lib.addImage("locationmenuIcon", new Image("/images/ui/locationmenuIcon.png"));
        lib.addImage("questlogIcon", new Image("/images/ui/questlogIcon.png"));
        
        lib.addImage("buttonSelectbeige", new Image("/images/ui/buttonSquareSelect_beige.png"));
        lib.addImage("buttonSelectSmallbeige", new Image("/images/ui/buttonSquareSelectSmall_beige.png"));
        
        //Icons
        lib.addImage("circle64", new Image("/images/circle_64.png"));
        lib.addImage("circle32", new Image("/images/circle_32.png"));
    }
    
    public static void initializeLocationLibrary(LocationLibrary lib) {
    	//--Dummy backup Location--
    	LocationTemplate dummy = new LocationTemplate(-1, "Dummy", 60*Mists.TILESIZE, 50*Mists.TILESIZE);
        dummy.map = new TileMap("/mapdata/pathfinder_test.map");
    	
        lib.addTemplate(dummy);
        
        //--TestDungeon--
        LocationTemplate testDungeon = new LocationTemplate(1, "TestDungeon", 60*Mists.TILESIZE, 40*Mists.TILESIZE);
        
        for (int i = 0; i<10;i++) {
            //Make a bunch of itempiles
            ItemContainer pile = new ItemContainer("ItemPile", new Sprite(Mists.graphLibrary.getImage("blank")));
            pile.setPermanency(true);
            pile.setRenderContent(true);
            pile.addItem(Mists.itemLibrary.create("sword"));
            pile.addItem(Mists.itemLibrary.create("himmutoy"));
            testDungeon.mobs.add(pile);
        }
        for (int i = 0; i<10;i++) {
            //Make a bunch of torches
            Structure torch = Mists.structureLibrary.create("TorchFloorDungeon");
            testDungeon.mobs.add(torch);
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
                default: monster = Mists.creatureLibrary.create("worm"); break;
            }
            testDungeon.mobs.add(monster);
        }
        testDungeon.lightlevel = 1;
        Image stairsDownImage = Mists.structureLibrary.getTemplate("DungeonStairsDown").getSprite().getImage();
        LocationDoorway ld = new LocationDoorway("To Puzzles", new Sprite(stairsDownImage), 0, 4, 500, 500);
        ld.setTargetLocation(4, 500, 500);
        testDungeon.mobs.add(ld);
        lib.addTemplate(testDungeon);
        
        //--TestDungeon--
        //Level1
        lib.addTemplate(StarterDungeon.getDungeonSkeletonLevel());
        //Level2
        lib.addTemplate(StarterDungeon.getDungeonCaveLevel());
        
        //--TestVillage--
        lib.addTemplate(Village.getVillage());
        
         //--Woods--
        Image woodsBackground = new Image("/images/pocmap.png");
        LocationTemplate woods = new LocationTemplate(3, "Woods", woodsBackground.getWidth(), woodsBackground.getHeight());
        woods.map = new BGMap(woodsBackground);
        Image signpostImage = Mists.structureLibrary.getTemplate("SignpostSmall").getSnapshot();
        WorldMapEntrance entrance = new WorldMapEntrance("To Worldmap", new Sprite(signpostImage), 0, null);
        entrance.setPosition(15*Mists.TILESIZE, 22*Mists.TILESIZE);
        woods.mobs.add(entrance);
        for (int i = 0; i<10;i++) {
            //Make a bunch of trees
            Structure tree = Mists.structureLibrary.create("Tree1");
            woods.mobs.add(tree);
        }
        for (int i = 0; i<5;i++) {
            //Make a bunch of trees
            Structure tree = Mists.structureLibrary.create("Tree2");
            woods.mobs.add(tree);
        }
        for (int i = 0; i<5;i++) {
            //Make a bunch of trees
            Structure tree = Mists.structureLibrary.create("Tree3a");
            woods.mobs.add(tree);
        }
        for (int i = 0; i<5;i++) {
            //Make a bunch of trees
            Structure tree = Mists.structureLibrary.create("Tree3b");
            woods.mobs.add(tree);
        }
        for (int i = 0; i<10;i++) {
            //Make a bunch of trees
            Structure rock = Mists.structureLibrary.create("Rock");
            woods.mobs.add(rock);
        }
        woods.lightlevel = 2;
        lib.addTemplate(woods);
        
        //PuzzleArea
        Mists.logger.info("Generating template for puzzlemap");
        LocationTemplate puzzlearea = new LocationTemplate(4, "Puzzle Area", 60*Mists.TILESIZE, 50*Mists.TILESIZE);
        puzzlearea.map = new TileMap("/mapdata/puzzlearea.map");
        
        WorldMapEntrance puzzleEntrance = new WorldMapEntrance("To Worldmap", new Sprite(signpostImage), 0, null);
        puzzleEntrance.setPosition(15*Mists.TILESIZE, 22*Mists.TILESIZE);
        puzzlearea.mobs.add(puzzleEntrance);
        
        //LightsOut Puzzle 1
        
        MapObject[] puzzle1 = LightsOutPuzzle.generateLightsOutPuzzle((PuzzleTile)Mists.structureLibrary.create("PuzzleRune"), 4, Mists.TILESIZE, 20*Mists.TILESIZE, 30*Mists.TILESIZE);
        puzzlearea.mobs.addAll(Arrays.asList(puzzle1));
        Puzzle p1 = LightsOutPuzzle.generatePuzzleFromTiles(puzzle1, true, true);
        puzzlearea.puzzles.add(p1);
        
        //LightsOut Puzzle 2
        /*
        MapObject[] puzzle2 = LightsOutPuzzle.generateLightsOutPuzzle((PuzzleTile)Mists.structureLibrary.create("PuzzleRune"), 3, Mists.TILESIZE, 45*Mists.TILESIZE, 25*Mists.TILESIZE);
        puzzlearea.mobs.addAll(Arrays.asList(puzzle2));
        Puzzle p2 = LightsOutPuzzle.generatePuzzleFromTiles(puzzle2, true, true);
        puzzlearea.puzzles.add(p2);
        */
        
        //Circuit Puzzle 1
        CircuitTile[] circuitPuzzle = CircuitPuzzle.generateTestPuzzle(10*Mists.TILESIZE, 40*Mists.TILESIZE);
        CircuitTile targetTile = circuitPuzzle[10];
        Puzzle p2 = new Puzzle();
        p2.addRequirement(new TileLitRequirement(targetTile));
        puzzlearea.mobs.addAll(Arrays.asList(circuitPuzzle));
        p2.addTrigger(new InsertMobTrigger(Mists.creatureLibrary.create("Blob"), 20*Mists.TILESIZE, 20*Mists.TILESIZE));
        lib.addTemplate(puzzlearea);
        Mists.logger.info("Puzzlemap template added");
        
        //Beach
        LocationTemplate beach = Beach.getBeach();
        lib.addTemplate(beach);
        
    }
}
