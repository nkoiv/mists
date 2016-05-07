/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.libraries.premade;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.TriggerPlate;
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.libraries.LocationLibrary.LocationTemplate;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.DialogueTrigger;
import com.nkoiv.mists.game.world.TileMap;
import javafx.scene.image.Image;

/**
 * Starting beach, where the player lands in originally
 * @author nikok
 */
public abstract class Beach {
    
    public static LocationTemplate getBeach() {
        Mists.logger.info("Generating template for the beach");
        LocationTemplate beach = new LocationTemplate(5, "Beach", 40*Mists.TILESIZE, 30*Mists.TILESIZE);
        beach.map = new TileMap("/mapdata/beach.map");
        generateStaticStructures(beach);
        generateNPCs(beach);
        return beach;
    }
     
   private static void generateStaticStructures(LocationTemplate beachTemplate) {
        
        Image signpostImage = Mists.structureLibrary.getTemplate("SignpostSmall").getSnapshot();
        WorldMapEntrance entrance = new WorldMapEntrance("To Worldmap", new Sprite(signpostImage), 0, null);
        entrance.setPosition(31*Mists.TILESIZE, 2*Mists.TILESIZE);
        beachTemplate.mobs.add(entrance);
        
        
    }
    
   private static void generateNPCs(LocationTemplate beachTemplate) {
       Creature gamemaster = new Creature("The GM", Mists.graphLibrary.getImage("blank"));
       TriggerPlate tp = new TriggerPlate("Triggerplate", 32, 32, 1000, gamemaster);
       tp.setTrigger(new DialogueTrigger(gamemaster, Mists.dialogueLibrary.getDialogue(1)));
       tp.setPosition(14*Mists.TILESIZE, 8*Mists.TILESIZE);
       tp.setCooldown(10000);
       beachTemplate.mobs.add(gamemaster);
       beachTemplate.mobs.add(tp);
       
   }
   
}
