/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.CircuitTile;
import com.nkoiv.mists.game.gameobject.ItemContainer;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.Location;

import java.util.logging.Level;

/**
 * Trigger for rotating a CircuitTile
 * @author nikok
 */
public class RotateTrigger implements Trigger{
	private int tileID;
    private CircuitTile ct;
    private boolean clockwise = true;
    
    public RotateTrigger(CircuitTile ct) {
        this.ct = ct;
    }
    
    @Override
    public String getDescription() {
    	if (ct == null) updateCircuitTile(Mists.MistsGame.getCurrentLocation());
        return "Rotate "+ct.getName();
    }

    @Override
    public boolean toggle(MapObject toggler) {
    	if (ct == null) updateCircuitTile(toggler.getLocation());
        if (clockwise) ct.rotateCW();
        else ct.rotateCCW();
        return true;
    }

    private void updateCircuitTile(Location loc) {
 	   MapObject mob = loc.getMapObject(tileID);
 	   if (mob instanceof CircuitTile) this.ct = (CircuitTile)loc.getMapObject(tileID);
    }
    
    @Override
    public MapObject getTarget() {
    	if (ct == null) updateCircuitTile(Mists.MistsGame.getCurrentLocation());
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
