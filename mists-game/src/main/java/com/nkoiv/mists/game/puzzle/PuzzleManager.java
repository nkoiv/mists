/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.world.Location;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * PuzzleManager keeps a number of Puzzles in check,
 * eyeing on their conditions and triggering them
 * as need be.
 * @author nikok
 */
public class PuzzleManager {
    private ArrayList<Puzzle> openPuzzles;
    private static final double WAIT_TIME = 250; //How often should we check for puzzle completions, in milliseconds
    private double passedTime;
    
    public PuzzleManager() {
        this.openPuzzles = new ArrayList<>();
        this.passedTime = 0;
    }
    
    /**
     * Add a puzzle to the manager for observing
     * @param puzzle Puzzle to add
     */
    public void addPuzzle(Puzzle puzzle) {
        this.openPuzzles.add(puzzle);
    }
    
    /**
     * Remove all puzzles from the manager
     */
    public void clearPuzzles() {
        this.openPuzzles.clear();
    }
    
    /**
     * Check the puzzles for completion
     * Since there's no need to do this 60 times per second,
     * use the static WAIT_TIME variable to dictate how often the
     * puzzles are checked up on.
     * @param time Game tick time in seconds
     */
    public void tick(double time) {
        if (!this.openPuzzles.isEmpty()) {
            passedTime = passedTime+(time*1000);
            if (passedTime<WAIT_TIME) return; //Don't bother checking puzzles if it's not time yet
            passedTime = 0;
            boolean removals = false;
            for (Puzzle p : this.openPuzzles) {
                if (p.isComplete()) p.trigger();
                if (p.isFinished()) removals = true;
            }
            if (removals) {
                Iterator puzzleIterator = this.openPuzzles.iterator();
                while (puzzleIterator.hasNext()) {
                    Puzzle p = (Puzzle)puzzleIterator.next();
                    if (p.isFinished()) puzzleIterator.remove();
                }
            }
        }
    }
    
}
