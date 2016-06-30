/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.puzzle;

import com.esotericsoftware.kryo.KryoSerializable;

/**
 * PuzzleRequirement is an abstract class
 * that parents various requirements for puzzles.
 * @author nikok
 */
public abstract class PuzzleRequirement implements KryoSerializable {
    protected boolean lockedCompletion;
    protected boolean locksOnCompletion;
    
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
