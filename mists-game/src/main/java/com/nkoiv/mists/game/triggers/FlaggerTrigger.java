/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * FlaggerTrigger flags a target with a given flag
 * upon triggering
 * @author nikok
 */
public class FlaggerTrigger  implements Trigger {
    private MapObject target;
    private String flag;
    private int flagValue;

    public FlaggerTrigger(MapObject target, String flag, int flagValue) {
        this.target = target;
        this.flag = flag;
        this.flagValue = flagValue;
    }
    
    @Override
    public String getDescription() {
        return "Flagger: "+flag+":"+flagValue;
    }

    @Override
    public boolean toggle(MapObject toggler) {
        target.setFlag(flag, flagValue);
        return true;
    }

    @Override
    public MapObject getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.target = mob;
    }

    @Override
    public Trigger createFromTemplate() {
        return new FlaggerTrigger(this.target, this.flag, this.flagValue);
    }
    
}
