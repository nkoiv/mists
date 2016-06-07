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
 * FlaggerTrigger flags a target with a given flag
 * upon triggering
 * @author nikok
 */
public class FlaggerTrigger  implements Trigger {
	private int targetID;
    private MapObject target;
    private String flag;
    private int flagValue;

    public FlaggerTrigger(int targetID, String flag, int flagValue) {
    	this.targetID = targetID;
    	this.flag = flag;
    	this.flagValue = flagValue;
    }
    
    public FlaggerTrigger(MapObject target, String flag, int flagValue) {
        this.target = target;
        this.flag = flag;
        this.flagValue = flagValue;
    }
    
    @Override
    public String getDescription() {
    	if (this.target == null) updateTargetMob(Mists.MistsGame.getCurrentLocation());
        return "Flagger: "+flag+":"+flagValue;
    }

    private void updateTargetMob(Location loc) {
    	this.target = loc.getMapObject(targetID);
    }
    
    @Override
    public boolean toggle(MapObject toggler) {
    	if (this.target == null) updateTargetMob(toggler.getLocation());
        target.setFlag(flag, flagValue);
        return true;
    }

    @Override
    public MapObject getTarget() {
    	if (this.target == null) updateTargetMob(Mists.MistsGame.getCurrentLocation());
        return this.target;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.target = mob;
    }

    @Override
    public Trigger createFromTemplate() {
    	FlaggerTrigger ft = new FlaggerTrigger(this.target, this.flag, this.flagValue);
    	ft.targetID = this.targetID;
        return ft;
    }
    
}
