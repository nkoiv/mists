/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.MovingGraphics;

/**
 *
 * @author nikok
 */
public class LocationDoorway extends Structure {
    int targetLocationID;
    
    public LocationDoorway(String name, MovingGraphics graphics, int collisionLevel, int targetLocationID) {
        super(name, graphics, collisionLevel);
        this.targetLocationID = targetLocationID;
    }
    
}
