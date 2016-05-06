/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.LocationDoorway;
import com.nkoiv.mists.game.gameobject.WorldMapEntrance;
import com.nkoiv.mists.game.gameobject.MapObject;

/**
 *
 * @author nikok
 */
public class LocationEntranceTrigger implements Trigger {
    private LocationDoorway entrance;
    private int targetLocationID;
    private double targetXCoor;
    private double targetYCoor;

    public LocationEntranceTrigger(LocationDoorway entrance, int targetLocationID, double targetXCoordinate, double targetYCoordinate) {
        this.entrance = entrance;
        this.targetLocationID = targetLocationID;
        this.targetXCoor = targetXCoordinate;
        this.targetYCoor = targetYCoordinate;
    }

    public void setEntrance(LocationDoorway entrance) {
        this.entrance = entrance;
    }

    public void setTarget(int locationID, double xCoor, double yCoor) {
        this.targetLocationID = locationID;
        this.targetXCoor = xCoor;
        this.targetYCoor = yCoor;
    }

    @Override
    public boolean toggle(MapObject toggler) {
        toggler.getLocation().changeLocation(targetLocationID, targetXCoor, targetYCoor);
        return true;
    }

    @Override
    public MapObject getTarget() {
        return this.entrance;
    }

    @Override
    public void setTarget(MapObject mob) {
        if(mob instanceof LocationDoorway) this.entrance = (LocationDoorway)mob;
    }

    @Override
    public String getDescription() {
        return "Location entrance to...";
    }

    @Override
    public LocationEntranceTrigger createFromTemplate() {
        LocationEntranceTrigger let = new LocationEntranceTrigger(this.entrance, this.targetLocationID, this.targetXCoor, this.targetYCoor);
        return let;
    }
}
