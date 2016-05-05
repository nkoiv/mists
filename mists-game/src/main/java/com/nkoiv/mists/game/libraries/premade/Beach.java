/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.libraries.premade;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.libraries.LocationLibrary.LocationTemplate;
import com.nkoiv.mists.game.world.TileMap;

/**
 * Starting beach, where the player lands in originally
 * @author nikok
 */
public class Beach {
    
    public static LocationTemplate getBeach() {
        Mists.logger.info("Generating template for the beach");
        LocationTemplate beach = new LocationTemplate(5, "Beach", 40*Mists.TILESIZE, 30*Mists.TILESIZE);
        beach.map = new TileMap("/mapdata/beach.map");
        
        return beach;
    }
    
    public static void generateStaticStructures(LocationTemplate beachTemplate) {
        
    }
    
}
