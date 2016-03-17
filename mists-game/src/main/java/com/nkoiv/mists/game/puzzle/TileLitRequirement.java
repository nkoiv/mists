/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.gameobject.PuzzleTile;

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
}
