/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.triggers;

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PuzzleTile;
import com.nkoiv.mists.game.world.Location;

/**
 * Freeze a number of PuzzleTiles upon triggering.
 * Most likely to be used in completion of puzzles
 * @author nikok
 */
public class FreezeTilesTrigger implements Trigger {
	private ArrayList<Integer> tileIDs;
    private ArrayList<PuzzleTile> tiles;

    public FreezeTilesTrigger() {
        this.tiles = new ArrayList<>();
    }
    
    public void addTile(PuzzleTile tileToBeFrozen) {
    	if (this.tiles == null) this.tiles = new ArrayList<>();
    	else this.tiles.add(tileToBeFrozen);
    }
    
    public void clearTiles() {
    	if (this.tiles == null) tiles = new ArrayList<>();
    	else this.tiles.clear();
    }
    
    @Override
    public String getDescription() {
        return "Freeze tiles";
    }

    private void updateTileIDsFromTiles() {
    	this.tileIDs = new ArrayList<>();
    	for (PuzzleTile p : this.tiles) {
    		tileIDs.add(p.getID());
    	}
    }
    
    private void updateTilesFromIDs(Location loc) {
    	this.tiles = new ArrayList<>();
		if (this.tileIDs != null) for (int id : this.tileIDs) {
			MapObject mob = loc.getMapObject(id);
			if (mob instanceof PuzzleTile) this.tiles.add((PuzzleTile)mob);
		}
    }
    
    @Override
    public boolean toggle(MapObject toggler) {
    	if (this.tiles == null) updateTilesFromIDs(toggler.getLocation());
        for (PuzzleTile tile : this.tiles) {
            tile.setFrozen(true);
        }
        return true;
    }

    @Override
    public MapObject getTarget() {
    	if (this.tiles == null) updateTilesFromIDs(Mists.MistsGame.getCurrentLocation());
        return this.tiles.get(0);
    }

    @Override
    public void setTarget(MapObject mob) {
        this.tiles.clear();
        if (mob instanceof PuzzleTile) this.tiles.add((PuzzleTile)mob);
    }

    @Override
    public Trigger createFromTemplate() {
        FreezeTilesTrigger ftt = new FreezeTilesTrigger();
        for (PuzzleTile p : this.tiles) {
            ftt.addTile(p);
        }
        return ftt;
    }

	@Override
	public void write(Kryo kryo, Output output) {
		if (!this.tiles.isEmpty()) this.updateTileIDsFromTiles();
		if (this.tileIDs.size() > this.tiles.size()) {
			output.writeInt(this.tileIDs.size());
			for (int i = 0; i < tileIDs.size(); i++) {
				output.writeInt(this.tileIDs.get(i));
			}
		}
		else {
			output.writeInt(this.tiles.size());
			for (int i = 0; i < tiles.size(); i++) {
				output.writeInt(this.tiles.get(i).getID());
			}
		}
		
	}

	@Override
	public void read(Kryo kryo, Input input) {
		int t = input.readInt();
		this.tileIDs = new ArrayList<>();
		for (int i = 0; i < t; i++) {
			this.tileIDs.add(input.readInt());
		}
	}
}
