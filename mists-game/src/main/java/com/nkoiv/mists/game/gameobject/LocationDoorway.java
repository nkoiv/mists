/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.gameobject;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.sprites.MovingGraphics;
import com.nkoiv.mists.game.sprites.Sprite;
import com.nkoiv.mists.game.triggers.LocationEntranceTrigger;
import com.nkoiv.mists.game.triggers.Trigger;
import com.nkoiv.mists.game.world.Location;

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
    
    public int getTargetLocationID() {
        return this.targetLocationID;
    }
    
    public void setTargetLocation(int targetLocationID, double targetXCoor, double targetYCoor) {
        this.targetLocationID = targetLocationID;
        this.targetXCoor = targetXCoor;
        this.targetYCoor = targetYCoor;
    }
    
    @Override
    public Trigger[] getTriggers() {
        Trigger[] a = new Trigger[]{new LocationEntranceTrigger(this, targetLocationID, targetXCoor, targetYCoor)};
        return a;
    }
    
    @Override
    public String[] getInfoText() {
        String targetLocationName;
        Location l = Mists.MistsGame.getLocation(IDinLocation);
        if (l != null) targetLocationName = l.getName();
        else targetLocationName = "Unspecified";
        String[] s = new String[]{
            this.name,
            "ID "+this.IDinLocation+" @ "+this.location.getName(),
            "X:"+((int)this.getXPos())+" Y:"+((int)this.getYPos()),
            "Doorway to: "+targetLocationName};
        return s;
    }
    
    @Override
    public LocationDoorway createFromTemplate() {
        LocationDoorway ld = new LocationDoorway(this.name, this.getGraphics(), 0, this.targetLocationID, this.targetXCoor, this.targetYCoor);
        if (!this.extraSprites.isEmpty()) {
            for (Sprite s : this.extraSprites) {
                double xOffset = s.getXPos() - this.getXPos();
                double yOffset = s.getYPos() - this.getYPos();
                ld.addExtra(s.getImage(), xOffset, yOffset);
            }
        }
        return ld;
    }
    
}
