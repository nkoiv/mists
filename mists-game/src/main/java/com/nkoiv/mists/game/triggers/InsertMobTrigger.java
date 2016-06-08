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
 *
 * @author nikok
 */
public class InsertMobTrigger implements Trigger {
	private int mobID;
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
    	if (mob == null) updateMobFromID(Mists.MistsGame.getCurrentLocation());
        return "a trigger that inserts a "+mob.getName();
    }

    private void updateMobFromID(Location loc) {
    	mob = loc.getMapObject(mobID);
    }
    
    @Override
    public boolean toggle(MapObject toggler) {
    	if (mob == null) updateMobFromID(toggler.getLocation());
        if (toggler != null && toggler.getLocation() != null) {
            toggler.getLocation().addMapObject(mob, xCoor, yCoor);
            return true;
        }
        return false;
    }

    @Override
    public MapObject getTarget() {
    	if (mob == null) updateMobFromID(Mists.MistsGame.getCurrentLocation());
        return mob;
    }

    @Override
    public void setTarget(MapObject mob) {
        this.mob = mob;
    }

    @Override
    public Trigger createFromTemplate() {
        InsertMobTrigger nt = new InsertMobTrigger(mob, xCoor, yCoor);
        nt.mobID = this.mobID;
        return nt;
    }

	@Override
	public void write(Kryo kryo, Output output) {
		if (this.mob != null) this.mobID = mob.getID();
		output.writeInt(mobID);
		output.writeDouble(this.xCoor);
		output.writeDouble(this.yCoor);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.mobID = input.readInt();
		this.xCoor = input.readDouble();
		this.yCoor = input.readDouble();
		
	}
    
}
