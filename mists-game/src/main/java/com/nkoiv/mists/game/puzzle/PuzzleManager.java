/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;

/**
 * PuzzleManager keeps a number of Puzzles in check,
 * eyeing on their conditions and triggering them
 * as need be.
 * @author nikok
 */
public class PuzzleManager {
    private Location location;
    private ArrayList<Puzzle> openPuzzles;
    
    public PuzzleManager(Location location) {
        this.location = location;
    }
    
    public void addPuzzle(Puzzle puzzle) {
        this.openPuzzles.add(puzzle);
    }
    
    /**
     * 
     * @param time 
     */
    public void tick(double time) {
        if (!this.openPuzzles.isEmpty()) {
            for (Puzzle p : this.openPuzzles) {
                
            }
        }
    }
    
}
