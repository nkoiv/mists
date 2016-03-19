/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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