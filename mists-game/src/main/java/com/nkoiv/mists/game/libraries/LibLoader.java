/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.AI.CompanionAI;
import com.nkoiv.mists.game.AI.MonsterAI;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.Door;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Libloader stocks up the libraries with
 * required preloaded data.
 * @author nikok
 */
public class LibLoader {
    
    
    public static void initializeStructureLibrary(MobLibrary<Structure> lib) {
        Mists.logger.info("Loading up structure data");
        //TODO: Load the data from a file
        
        //Dungeon walls
        ImageView wallimages = new ImageView("/images/structures/dwall.png");
        Wall dungeonwall = new Wall("DungeonWall", new Image("/images/structures/blank.png"), 1, wallimages);
        lib.addTemplate(dungeonwall);
        //Dungeon stuff
        Door dungeondoor = new Door("DungeonDoor", new Image("/images/structures/ddoor.png"), new Image("/images/structures/ddoor_open.png"), 1);
        lib.addTemplate(dungeondoor);
        
        //Outdoor stuff
        Structure tree = new Structure("Tree", new Image("/images/tree_stump.png"), 1);
        tree.addExtra(new Image("/images/tree.png"), -35, -96);
        lib.addTemplate(tree);
        Structure rock = new Structure("Rock", new Image("/images/block.png"), 1);
        lib.addTemplate(rock);
        
    }
    
    public static void initializeCreatureLibrary(MobLibrary<Creature> lib) {
        Mists.logger.info("Loading up creature data");
        //TODO: Load the data from a file
        
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
        lib.addTemplate(himmu);
        
        //Monsters
        Creature worm = new Creature("Worm", new ImageView("/images/monster_small.png"), 3, 0, 0, 4, 0, 36, 32);
        MonsterAI wormAI = new MonsterAI(worm);
        worm.setAI(wormAI);
        worm.addAction(new MeleeAttack());
        lib.addTemplate(worm);
        
        Creature rabbit = new Creature("Rabbit", new ImageView("/images/monster_small.png"), 3, 0, 4, 4, 0, 36, 32);
        MonsterAI rabbitAI = new MonsterAI(rabbit);
        rabbit.setAI(rabbitAI);
        rabbit.addAction(new MeleeAttack());
        lib.addTemplate(rabbit);
        
        Creature eggy = new Creature("Eggy", new ImageView("/images/monster_small.png"), 3, 3, 0, 4, 0, 36, 32);
        MonsterAI eggyAI = new MonsterAI(eggy);
        eggy.setAI(eggyAI);
        eggy.addAction(new MeleeAttack());
        lib.addTemplate(eggy);
        
        Creature swampy = new Creature("Swampy", new ImageView("/images/monster_small.png"), 3, 3, 4, 4, 0, 36, 32);
        MonsterAI swampyAI = new MonsterAI(eggy);
        swampy.setAI(swampyAI);
        swampy.addAction(new MeleeAttack());
        lib.addTemplate(swampy);
        
        
    }
    
    public static void initializeActionLibrary(ActionLibrary lib) {
        Mists.logger.info("Loading up action data");
        MeleeAttack melee = new MeleeAttack();
        lib.addTemplate(melee);
    }
    
    public static void initializeGraphLibrary(GraphLibrary lib) {
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
        
        lib.addImage("buttonSelectbeige", new Image("/images/ui/buttonSquareSelect_beige.png"));
        lib.addImage("buttonSelectSmallbeige", new Image("/images/ui/buttonSquareSelectSmall_beige.png"));
        
        //Icons
        
        
        
    }
}
