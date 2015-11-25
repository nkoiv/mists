/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries;

import com.nkoiv.mists.game.Mists;
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
    
    
    public static void initializeStructLibrary(MobLibrary<Structure> lib) {
        Mists.logger.info("Loading up structure data");
        
        //TODO: Load the data from a file
        
        //Dungeon walls
        ImageView wallimages = new ImageView("/images/structures/dwall.png");
        Wall dungeonwall = new Wall("DungeonWall", new Image("/images/structures/blank.png"), 1, null, 0, 0, wallimages);
        lib.addTemplate(dungeonwall);
        //Dungeon stuff
        Structure dungeondoor = new Structure("DungeonDoor", new Image("/images/structures/ddoor.png"), null, 0, 0);
        lib.addTemplate(dungeondoor);
    }
}
