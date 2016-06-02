/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * Toggle a MapObject (trigger ID 0 on the target) upon triggering
 * Basically meant for remote-toggling of things. Like a switch to open
 * door or whatnot.
 * @author nikok
 */
public class ToggleTrigger implements Trigger {
    private MapObject targetMob;

    public ToggleTrigger(MapObject mob) {
        this.targetMob = mob;
    }

    @Override
    public String getDescription() {
        String s = "Trigger to toggle "+targetMob.getName();
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
        Trigger[] targetTriggers = targetMob.getTriggers();
        if (targetTriggers.length > 0) { 
            targetTriggers[0].toggle(toggler);
            return true;
        }
        return false;
    }

    @Override
    public ToggleTrigger createFromTemplate() {
        ToggleTrigger tt = new ToggleTrigger(this.targetMob);
        return tt;
    }

    @Override
    public MapObject getTarget() {
        return this.targetMob;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.targetMob = mob;
    }

}