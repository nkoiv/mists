/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;

/**
 * KillTrigger destroys (/kills) a mapobject upon triggering
 * @author nikok
 */
public class KillTrigger implements Trigger {
    private MapObject target;
    
    public KillTrigger(MapObject target) {
        this.target = target;
    }
    
    @Override
    public String getDescription() {
        return "Kill trigger for "+target.getName();
    }

    @Override
    public boolean toggle(MapObject toggler) {
        if (target instanceof Creature) {
            ((Creature)target).setHealth(0);
            return true;
        } else {
            target.remove();
            return true;
        }
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
        return new KillTrigger(this.target);
    }
    
}
