/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.libraries.premade;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.libraries.LocationLibrary.LocationTemplate;
import com.nkoiv.mists.game.sprites.Roof;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import javafx.scene.image.Image;

/**
 * Village is a premade village for the POC
 * version of the game. Done before the actual
 * tools for handcrafting the locations are
 * developed
 * @author nikok
 */
public class Village {
    
    /**
     * Generate and return the static premade test village template
     * @return Template for the test village, ready for LocationLibrary
     */
    public static LocationTemplate getVillage() {
        //--TestVillage--
        Mists.logger.info("Generating template for test village");
        LocationTemplate villageTemplate =new LocationTemplate(2, "TestVillage", 60*Mists.TILESIZE, 50*Mists.TILESIZE);
        villageTemplate.map = new TileMap("/mapdata/villagetest.map");
        
        addRoofs(villageTemplate);
        
        return villageTemplate;
    }
    
    /**
     * Add the roofs of the houses to the village
     * @param villageTemplate LocationTemplate to add the roofs on
     */
    private static void addRoofs(LocationTemplate villageTemplate) {
        //A 10x8 thatched house roof
        Roof roof1 = new Roof(new Image("/images/roof_thatch_10x8.png"));
        roof1.setPosition(10*Mists.TILESIZE, 18*Mists.TILESIZE);
        roof1.setHiddenArea(12*Mists.TILESIZE, 21*Mists.TILESIZE, 8*Mists.TILESIZE, 5*Mists.TILESIZE);
        villageTemplate.roofs.add(roof1);
        
        //A 11x7 brick roof
        Roof roof2 = new Roof(new Image("/images/roof_brick_11x8.png"));
        roof2.setPosition(38*Mists.TILESIZE, 12*Mists.TILESIZE);
        roof2.setHiddenArea(40*Mists.TILESIZE, 16*Mists.TILESIZE, 9*Mists.TILESIZE, 4*Mists.TILESIZE);
        villageTemplate.roofs.add(roof2);
    }
    
}
