/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.puzzle;

import java.util.ArrayList;
import java.util.Iterator;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * PuzzleManager keeps a number of Puzzles in check,
 * eyeing on their conditions and triggering them
 * as need be.
 * @author nikok
 */
public class PuzzleManager implements KryoSerializable {
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
                Iterator<Puzzle> puzzleIterator = this.openPuzzles.iterator();
                while (puzzleIterator.hasNext()) {
                    Puzzle p = (Puzzle)puzzleIterator.next();
                    if (p.isFinished()) puzzleIterator.remove();
                }
            }
        }
    }

	@Override
	public void write(Kryo kryo, Output output) {
		output.writeInt(this.openPuzzles.size());
		for (Puzzle p : this.openPuzzles) {
			kryo.writeObject(output, p);
		}
	}

	@Override
	public void read(Kryo kryo, Input input) {
		int puzzles = input.readInt();
		this.openPuzzles = new ArrayList<>();
		for (int i = 0; i < puzzles; i ++) {
			Puzzle p = kryo.readObject(input, Puzzle.class);
			this.openPuzzles.add(p);
		}
	}
    
}
