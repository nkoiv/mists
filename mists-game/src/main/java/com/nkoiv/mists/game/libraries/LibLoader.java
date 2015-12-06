/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.AI.CompanionAI;
import com.nkoiv.mists.game.AI.MonsterAI;
import static com.nkoiv.mists.game.Global.TILESIZE;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.PlayerCharacter;
import com.nkoiv.mists.game.gameobject.Structure;
import com.nkoiv.mists.game.gameobject.Wall;
import java.util.HashMap;
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
        Wall dungeonwall = new Wall("DungeonWall", new Image("/images/structures/blank.png"), 1, null, 0, 0, wallimages);
        lib.addTemplate(dungeonwall);
        //Dungeon stuff
        Structure dungeondoor = new Structure("DungeonDoor", new Image("/images/structures/ddoor.png"), null, 0, 0);
        lib.addTemplate(dungeondoor);
        
        //Outdoor stuff
        Structure tree = new Structure("Tree", new Image("/images/tree_stump.png"), null, 0, 0);
        tree.addExtra(new Image("/images/tree.png"), -35, -96);
        lib.addTemplate(tree);
        Structure rock = new Structure("Rock", new Image("/images/block.png"), null, 0, 0);
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
        MeleeAttack himmumelee = new MeleeAttack();
        himmumelee.setFlag("cooldown", 2000);
        himmumelee.setFlag("damage", 35);
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
        worm.setAI(rabbitAI);
        worm.addAction(new MeleeAttack());
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
}
