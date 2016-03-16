/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

/**
 * PuzzleRequirement is an abstract class
 * that parents various requirements for puzzles.
 * @author nikok
 */
public abstract class PuzzleRequirement {
    private boolean lockedCompletion;
    private boolean locksOnCompletion;
    
    /**
     * Call to check if the requirements have been met.
     * @return True if the requirement has been met
     */
    public boolean isMet() {
        if (this.lockedCompletion) return true;
        else if (this.isCompleted()) {
            if (this.locksOnCompletion) this.lockedCompletion = true;
            return true;
        }
        return false;
    }
    
    /**
     * Overwrite in subclass with specific puzzle goals.
     * @return true if the requirements are met
     */
    protected boolean isCompleted() {
        
        return false;
    }
    
}
