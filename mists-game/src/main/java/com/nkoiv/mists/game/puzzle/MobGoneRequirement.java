/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.puzzle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.world.Location;

/**
 * Monitor a mob (by ID) in a location, and return
 * true if the mob is gone.
 * @author nikok
 */
public class MobGoneRequirement extends PuzzleRequirement {
    private Location location;
    private MapObject mobToDestroy;
    
    public MobGoneRequirement(MapObject mob, Location location) {
        this.location = location;
        this.mobToDestroy = mob;
    }
    
    @Override
    protected boolean isCompleted() {
        //Should return false if the mob isnt found in the instance, or if the mob found doesnt match the one on file
        return !location.getMapObject(mobToDestroy.getID()).equals(mobToDestroy);
    }

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeBoolean(locksOnCompletion);
		output.writeBoolean(lockedCompletion);
		if (location !=null ) output.writeInt(location.getBaseID());
		else output.writeInt(-1);
		if (mobToDestroy != null) output.writeInt(mobToDestroy.getID());
		else output.writeInt(-1);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.locksOnCompletion = input.readBoolean();
		this.lockedCompletion = input.readBoolean();
		int locationID = input.readInt();
		int mobID = input.readInt();
		this.location = Mists.MistsGame.getLocation(locationID);
		if (location != null) mobToDestroy = location.getMapObject(mobID);
	}
}
