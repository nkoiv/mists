/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.puzzle;

import com.nkoiv.mists.game.actions.Trigger;
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
    boolean lockedComplete = false;
    boolean locksOnCompletion = true;  //Can the puzzle be triggered several times? If locksOnCompletion = true, then it can't be
    
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
    
    public void addRequirement(PuzzleRequirement p) {
        this.requirements.add(p);
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
        return triggered;
    }
    
}
