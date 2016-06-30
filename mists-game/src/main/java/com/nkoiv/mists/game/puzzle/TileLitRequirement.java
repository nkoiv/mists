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
import com.nkoiv.mists.game.gameobject.PuzzleTile;
import com.nkoiv.mists.game.world.Location;

/**
 * A Puzzle Requirement for observing the lit-status of
 * a single PuzzleTile
 * @author nikok
 */
public class TileLitRequirement extends PuzzleRequirement {
    private PuzzleTile tile;
    
    public TileLitRequirement(PuzzleTile tile) {
        this.tile = tile;
    }
    
    @Override
    protected boolean isCompleted() {
        return this.tile.isLit();
    }

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeBoolean(locksOnCompletion);
		output.writeBoolean(lockedCompletion);
		if (tile.getLocation() !=null ) output.writeInt(tile.getLocation().getBaseID());
		else output.writeInt(-1);
		if (tile != null) output.writeInt(tile.getID());
		else output.writeInt(-1);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		this.locksOnCompletion = input.readBoolean();
		this.lockedCompletion = input.readBoolean();
		int locationID = input.readInt();
		int tileID = input.readInt();
		Location location = Mists.MistsGame.getLocation(locationID);
		if (location != null) {
			Object ptile = location.getMapObject(tileID);
			if (ptile instanceof PuzzleTile) this.tile = (PuzzleTile)ptile;
		}
	}
}
