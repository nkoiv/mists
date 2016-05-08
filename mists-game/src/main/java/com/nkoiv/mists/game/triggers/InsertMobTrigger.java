/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;

/**
 *
 * @author nikok
 */
public class InsertMobTrigger implements Trigger {
    private MapObject mob;
    private double xCoor;
    private double yCoor;
    
    public InsertMobTrigger(MapObject mob, double xCoor, double yCoor) {
        this.mob = mob;
        this.xCoor = xCoor;
        this.yCoor = yCoor;
    }
    
    @Override
    public String getDescription() {
        return "a trigger that inserts a "+mob.getName();
    }

    @Override
    public boolean toggle(MapObject toggler) {
        if (toggler != null && toggler.getLocation() != null) {
            toggler.getLocation().addMapObject(mob, xCoor, yCoor);
            return true;
        }
        return false;
    }

    @Override
    public MapObject getTarget() {
        return mob;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.mob = mob;
    }

    @Override
    public Trigger createFromTemplate() {
        InsertMobTrigger nt = new InsertMobTrigger(mob, xCoor, yCoor);
        return nt;
    }
    
}
