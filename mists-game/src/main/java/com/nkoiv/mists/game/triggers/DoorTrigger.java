/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Door;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.Location;

/**
 * Open/Close door on triggering
 * @author nikok
 */
public class DoorTrigger implements Trigger {
	private int doorID;
    private Door door;
    private boolean unlocks;
    
    public DoorTrigger(int doorID) {
    	this.doorID = doorID;
    }
    
    public DoorTrigger(Door d) {
        this.door = d;
    }
    
    private void updateDoor(Location l) {
    	MapObject mob = l.getMapObject(doorID);
    	if (mob instanceof Door) this.door=(Door)mob;
    }

    @Override
    public boolean toggle(MapObject toggler) {
    	if (this.door == null) updateDoor(toggler.getLocation());
        if (unlocks)door.setLocked(false);
        this.door.toggle();
        return true;
    }

    public void setUnlocking(boolean unlocksOnUse) {
        this.unlocks = unlocksOnUse;
    }
    
    @Override
    public MapObject getTarget() {
    	if (this.door == null) updateDoor(Mists.MistsGame.getCurrentLocation());
        return this.door;
    }

    @Override
    public void setTarget(MapObject mob) {
        if (mob instanceof Door) this.door = (Door)mob;
    }

    @Override
    public String getDescription() {
    	if (this.door == null) updateDoor(Mists.MistsGame.getCurrentLocation());
        if (this.door.isOpen()) return "Close door";
        else return "Open door";
    }

    @Override
    public DoorTrigger createFromTemplate() {
        DoorTrigger d = new DoorTrigger(this.door);
        d.unlocks = this.unlocks;
        d.doorID = this.doorID;
        return d;
    }

}



