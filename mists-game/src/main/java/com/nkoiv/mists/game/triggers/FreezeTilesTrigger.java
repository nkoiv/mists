/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.triggers;

import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.gameobject.PuzzleTile;
import java.util.ArrayList;

/**
 * Freeze a number of PuzzleTiles upon triggering.
 * Most likely to be used in completion of puzzles
 * @author nikok
 */
public class FreezeTilesTrigger implements Trigger {
    private ArrayList<PuzzleTile> tiles;

    public FreezeTilesTrigger() {
        this.tiles = new ArrayList<>();
    }
    
    public void addTile(PuzzleTile tileToBeFrozen) {
        this.tiles.add(tileToBeFrozen);
    }
    
    public void clearTiles() {
        this.tiles.clear();
    }
    
    @Override
    public String getDescription() {
        return "Freeze tiles";
    }

    @Override
    public boolean toggle(MapObject toggler) {
        for (PuzzleTile tile : this.tiles) {
            tile.setFrozen(true);
        }
        return true;
    }

    @Override
    public MapObject getTarget() {
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
}
