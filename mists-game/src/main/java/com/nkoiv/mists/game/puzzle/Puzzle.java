/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.puzzle;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.triggers.Trigger;
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
public class Puzzle implements KryoSerializable {
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
        if (this.lockedComplete) return true;
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
            Mists.logger.info("Puzzletrigger triggered");
        }
        timesTriggered++;
        return triggered;
    }


	@Override
	public void write(Kryo kryo, Output output) {
		output.writeInt(requirements.size());
		for (PuzzleRequirement pr : requirements) {
			kryo.writeObject(output, pr);
		}
		output.writeInt(triggers.size());
		for (Trigger t : triggers) {
			kryo.writeClassAndObject(output, t);;
		}
		output.writeBoolean(lockedComplete);
		output.writeBoolean(locksOnCompletion);
		output.writeInt(amountOfPossibleTriggerings);
		output.writeInt(timesTriggered);
	}


	@Override
	public void read(Kryo kryo, Input input) {
		int reqCount = input.readInt();
		for (int i = 0; i < reqCount; i++) {
			PuzzleRequirement pr = kryo.readObject(input, PuzzleRequirement.class);
			this.requirements.add(pr);
		}
		int trigCount = input.readInt();
		for (int i = 0; i < trigCount; i++) {
			Trigger t = (Trigger)kryo.readClassAndObject(input);
			this.triggers.add(t);
		}
		this.lockedComplete = input.readBoolean();
		this.locksOnCompletion = input.readBoolean();
		this.amountOfPossibleTriggerings = input.readInt();
		this.timesTriggered = input.readInt();
	}
    
}
