/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.triggers.LocationEntranceTrigger;
import com.nkoiv.mists.game.triggers.Trigger;
import com.nkoiv.mists.game.triggers.WoldMapEntranceTrigger;

/**
 *
 * @author nikok
 */
public class LocationDoorway extends Structure {
    private int targetLocationID;
    private double targetXCoor;
    private double targetYCoor;
    
    public LocationDoorway(String name, MovingGraphics graphics, int collisionLevel, int targetLocationID, double targetXCoor, double targetYCoor) {
        super(name, graphics, collisionLevel);
        this.targetLocationID = targetLocationID;
    }
    
    @Override
    public Trigger[] getTriggers() {
        Trigger[] a = new Trigger[]{new LocationEntranceTrigger(this, targetLocationID, targetXCoor, targetYCoor)};
        return a;
    }
    
    
}
