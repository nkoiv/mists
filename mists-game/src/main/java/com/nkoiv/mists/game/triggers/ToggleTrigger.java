/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.Location;

/**
 * Toggle a MapObject (trigger ID 0 on the target) upon triggering
 * Basically meant for remote-toggling of things. Like a switch to open
 * door or whatnot.
 * @author nikok
 */
public class ToggleTrigger implements Trigger {
	private int targetID;
    private MapObject target;

    public ToggleTrigger(MapObject mob) {
        this.target = mob;
    }
    
    private void updateTarget(Location loc) {
    	this.target = loc.getMapObject(targetID);
    }

    @Override
    public String getDescription() {
    	if (target == null) updateTarget(Mists.MistsGame.getCurrentLocation());
        String s = "Trigger to toggle "+target.getName();
        return s;
    }
    
    /**
     * Activate the first (ID 0) trigger on the targets
     * Available Triggers list.
     * @param toggler MapObject doing the toggling
     * @return true if toggling was successful
     */
    @Override
    public boolean toggle(MapObject toggler) {
    	if (target == null) updateTarget(toggler.getLocation());
        Trigger[] targetTriggers = target.getTriggers();
        if (targetTriggers.length > 0) { 
            targetTriggers[0].toggle(toggler);
            return true;
        }
        return false;
    }

    @Override
    public ToggleTrigger createFromTemplate() {
        ToggleTrigger tt = new ToggleTrigger(this.target);
        return tt;
    }

    @Override
    public MapObject getTarget() {
    	if (target == null) updateTarget(Mists.MistsGame.getCurrentLocation());
        return this.target;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.target = mob;
    }

	@Override
	public void write(Kryo kryo, Output output) {
		if (this.target != null) this.targetID = target.getID();
 		output.writeInt(this.targetID);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.targetID = input.readInt();
		
	}

}