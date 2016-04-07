/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.CircuitTile;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.logging.Level;

/**
 * Trigger for rotating a CircuitTile
 * @author nikok
 */
public class RotateTrigger implements Trigger{
    private CircuitTile ct;
    private boolean clockwise = true;
    
    public RotateTrigger(CircuitTile ct) {
        this.ct = ct;
    }
    
    @Override
    public String getDescription() {
        return "Rotate "+ct.getName();
    }

    @Override
    public boolean toggle(MapObject toggler) {
        if (clockwise) ct.rotateCW();
        else ct.rotateCCW();
        return true;
    }

    @Override
    public MapObject getTarget() {
        return ct;
    }

    @Override
    public void setTarget(MapObject mob) {
        if (mob instanceof CircuitTile) this.ct = (CircuitTile)mob;
        else Mists.logger.log(Level.WARNING, "Tried to set Non-CircuitTile as a target for RotateTrigger ({0})", mob.toString());
    }

    @Override
    public Trigger createFromTemplate() {
        RotateTrigger rt = new RotateTrigger(ct);
        return rt;
    }
    
}
