/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.triggers.Trigger;
import com.nkoiv.mists.game.gameobject.MapObject;
import java.util.ArrayList;

/**
 * Puzzle is something that has conditions
 * and completion trigger. A puzzle could be
 * to pull (correct) levers to open a door
 * or to kill monsters in the right locations.
 * In a way puzzles are very much like Quests,
 * just invisible.
 * @author nikok
 */
public class Puzzle {
    private ArrayList<PuzzleRequirement> requirements; //Requirements for the puzzle to be complete
    private ArrayList<Trigger> triggers; //Triggers that trigger when puzzle is complete
    private boolean lockedComplete = false;
    private boolean locksOnCompletion = true;
    private int amountOfPossibleTriggerings = 1;
    private int timesTriggered = 0;
    
    public Puzzle() {
        this.requirements = new ArrayList<>();
        this.triggers = new ArrayList<>();
    }
    
    
    /**
     * Check if all the puzzle requirements have
     * been met
     * @return true if puzzle is complete
     */
    public boolean isComplete() {
        if (this.lockedComplete == true) return true;
        for (PuzzleRequirement pr : this.requirements) {
            if (!pr.isMet()) return false;
        }
        if (this.locksOnCompletion) lockedComplete = true;
        return true;
    }
    
    /**
     * Add a requirement for the puzzle to flag complete
     * These are checked by PuzzleManager on default interval
     * @param p Required parameter for 
     */
    public void addRequirement(PuzzleRequirement p) {
        this.requirements.add(p);
    }
    
    /**
     * Adding a trigger to Puzzle will cause it to do
     * something when the puzzle is marked complete.
     * Seriable triggers are allowed
     * @param t Trigger to proc when the puzzle is complete
     */
    public void addTrigger(Trigger t) {
        this.triggers.add(t);
    }
    
    public void setTriggeringCount(int amountOfPossibleTriggerings) {
        this.amountOfPossibleTriggerings = amountOfPossibleTriggerings;
    }
    
    /**
     * Check if the puzzle has triggered the amount of times allowed
     * if so, puzzle can be marked as Finished.
     * @return True if times triggered meets or exceeds amount of possible triggerings
     */
    public boolean isFinished() {
        return (this.timesTriggered >= this.amountOfPossibleTriggerings);
    }
    
    /**
     * Do whatever the puzzle is meant to do when
     * it's complete
     * @return True if something triggered
     */
    public boolean trigger() {
        if (this.triggers.isEmpty()) return false;
        MapObject puzzletriggerer = new MapObject("PuzzleMaster"); //Temporary MapObject to perform the triggering with
        boolean triggered = false;
        for (Trigger t : this.triggers) {
            if (t.toggle(puzzletriggerer)) triggered = true; //TODO: This temp-triggerer might not be the best way to ensure we dont get null-pointers
        }
        timesTriggered++;
        return triggered;
    }
    
}
