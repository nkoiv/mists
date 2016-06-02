/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
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
